/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.db.schema.importer;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.io.unsync.UnsyncBufferedReader;
import com.liferay.portal.kernel.io.unsync.UnsyncStringReader;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.sql.Connection;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import javax.sql.DataSource;

/**
 * @author Mariano Álvaro Sáiz
 */
public class SQLConcurrentExecutor {

	public SQLConcurrentExecutor(Predicate<String> synchronousPredicate) {
		_synchronousPredicate = synchronousPredicate;

		_executorService = Executors.newFixedThreadPool(5);
	}

	public void runSQLTemplateConcurrently(
			DataSource dataSource, String sqlTemplate)
		throws Exception {

		_preprocessSQL(sqlTemplate);

		List<Future<?>> futures = new ArrayList<>();

		for (String sql : _asyncSQLs) {
			futures.add(
				_executorService.submit(
					() -> {
						try (Connection connection = dataSource.getConnection();
							Statement statement =
								connection.createStatement()) {

							statement.executeUpdate(sql);
						}
						catch (Exception exception) {
							_log.error(exception);
						}
					}));
		}

		_asyncSQLs.clear();

		for (Future<?> future : futures) {
			future.get();
		}

		for (String sql : _syncSQLs) {
			try (Connection connection = dataSource.getConnection();
				Statement statement = connection.createStatement()) {

				statement.executeUpdate(sql);
			}
		}

		_syncSQLs.clear();
	}

	public void stop() throws Exception {
		_executorService.shutdownNow();
		_executorService.awaitTermination(10, TimeUnit.SECONDS);
	}

	private void _preprocessSQL(String sqlTemplate) throws Exception {
		sqlTemplate = StringUtil.trim(sqlTemplate);

		if ((sqlTemplate == null) || sqlTemplate.isEmpty()) {
			return;
		}

		if (!sqlTemplate.endsWith(StringPool.SEMICOLON)) {
			sqlTemplate += StringPool.SEMICOLON;
		}

		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(new UnsyncStringReader(sqlTemplate))) {

			StringBundler sb = new StringBundler();

			String line = null;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				if (line.isEmpty() || line.startsWith("##")) {
					continue;
				}

				sb.append(line);
				sb.append(StringPool.NEW_LINE);

				if (line.endsWith(";")) {
					String sql = sb.toString();

					sb.setIndex(0);

					if (_synchronousPredicate.test(sql)) {
						_syncSQLs.add(sql);
					}
					else {
						_asyncSQLs.add(sql);
					}
				}
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SQLConcurrentExecutor.class);

	private final List<String> _asyncSQLs = new ArrayList<>();
	private final ExecutorService _executorService;
	private final Predicate<String> _synchronousPredicate;
	private final List<String> _syncSQLs = new ArrayList<>();

}