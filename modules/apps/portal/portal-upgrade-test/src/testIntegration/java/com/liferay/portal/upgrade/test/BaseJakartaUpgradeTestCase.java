/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.test;

import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.db.partition.DBPartition;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.upgrade.BaseJakartaUpgradeProcess;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;

/**
 * @author Luis Ortiz
 */
public abstract class BaseJakartaUpgradeTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		db = DBManagerUtil.getDB();

		companyLocalService.forEachCompany(
			company -> db.runSQL(
				StringBundler.concat(
					"create table ", TABLE_NAME,
					" (mvccVersion LONG default 0 not null, uuid_ VARCHAR(75) ",
					"not null, ", COLUMN_NAME_1, " TEXT null, ", COLUMN_NAME_2,
					" VARCHAR(255) null, ", COLUMN_NAME_3, " STRING null, ",
					COLUMN_NAME_4,
					" TEXT null, primary key (mvccVersion, uuid_))")));
	}

	@After
	public void tearDown() throws Exception {
		companyLocalService.forEachCompany(
			company -> db.runSQL("drop table " + TABLE_NAME));
	}

	protected void insertInitialData() throws Exception {
		companyLocalService.forEachCompany(
			company -> {
				db.runSQL(
					StringBundler.concat(
						"insert into ", TABLE_NAME, " (mvccVersion, uuid_, ",
						COLUMN_NAME_1, ", ", COLUMN_NAME_2, ", ", COLUMN_NAME_3,
						") values (0, 'uuid1', '", initialString, "', '",
						initialString, "', '", initialString, "')"));

				db.runSQL(
					StringBundler.concat(
						"insert into ", TABLE_NAME, " (mvccVersion, uuid_, ",
						COLUMN_NAME_1, ", ", COLUMN_NAME_2,
						") values (1, 'uuid2', '", initialString, "', '",
						initialString, "')"));
			});
	}

	protected void testUpgrade(UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				BaseJakartaUpgradeProcess.class.getName(),
				LoggerTestUtil.INFO)) {

			unsafeRunnable.run();

			try (Connection connection = DataAccess.getConnection();
				PreparedStatement preparedStatement =
					connection.prepareStatement(
						"select * from " + TABLE_NAME + " order by uuid_ asc");
				ResultSet resultSet = preparedStatement.executeQuery()) {

				Assert.assertTrue(resultSet.next());

				Assert.assertEquals(0, resultSet.getLong(1));
				Assert.assertEquals("uuid1", resultSet.getString(2));
				Assert.assertEquals(
					resultString, resultSet.getString(COLUMN_NAME_1));
				Assert.assertEquals(
					resultString, resultSet.getString(COLUMN_NAME_2));
				Assert.assertEquals(
					resultString, resultSet.getString(COLUMN_NAME_3));
				Assert.assertNull(resultSet.getString(COLUMN_NAME_4));

				Assert.assertTrue(resultSet.next());

				Assert.assertEquals(1, resultSet.getLong(1));
				Assert.assertEquals("uuid2", resultSet.getString(2));
				Assert.assertEquals(
					resultString, resultSet.getString(COLUMN_NAME_1));
				Assert.assertEquals(
					resultString, resultSet.getString(COLUMN_NAME_2));
				Assert.assertNull(resultSet.getString(COLUMN_NAME_3));
				Assert.assertNull(resultSet.getString(COLUMN_NAME_4));

				Assert.assertFalse(resultSet.next());
			}

			List<LogEntry> logEntries = logCapture.getLogEntries();

			int logEntriesSize = 4;

			if (DBPartition.isPartitionEnabled()) {
				logEntriesSize =
					logEntriesSize * PortalInstancePool.getCompanyIds().length;
			}

			Assert.assertEquals(
				logEntries.toString(), logEntriesSize, logEntries.size());

			AtomicInteger i = new AtomicInteger();

			long[] companyIds = ReflectionTestUtil.invoke(
				PortalInstancePool.class, "_getCompanyIdsBySQL", null);

			String companyIdMessage = "";

			for (long companyId : companyIds) {
				if (DBPartition.isPartitionEnabled()) {
					companyIdMessage = " for company " + companyId;
				}

				_assertLogEntry(
					StringBundler.concat(
						"Table/column ", TABLE_NAME, "/", COLUMN_NAME_1,
						companyIdMessage, " has been upgraded for next IDs:"),
					new HashSet<>(Arrays.asList("(0, uuid1)", "(1, uuid2)")),
					logEntries.get(
						i.getAndIncrement()
					).toString());

				_assertLogEntry(
					StringBundler.concat(
						"Table/column ", TABLE_NAME, "/", COLUMN_NAME_2,
						companyIdMessage, " has been upgraded for next IDs: "),
					new HashSet<>(Arrays.asList("(0, uuid1)", "(1, uuid2)")),
					logEntries.get(
						i.getAndIncrement()
					).toString());

				_assertLogEntry(
					StringBundler.concat(
						"Table/column ", TABLE_NAME, "/", COLUMN_NAME_3,
						companyIdMessage, " has been upgraded for next IDs: "),
					new HashSet<>(Arrays.asList("(0, uuid1)")),
					logEntries.get(
						i.getAndIncrement()
					).toString());

				_assertLogEntry(
					StringBundler.concat(
						"Table/column ", TABLE_NAME, "/", COLUMN_NAME_4,
						companyIdMessage, " has not been upgraded for any ID"),
					new HashSet<>(),
					logEntries.get(
						i.getAndIncrement()
					).toString());
			}
		}
	}

	protected static final String COLUMN_NAME_1 = "script1";

	protected static final String COLUMN_NAME_2 = "script2";

	protected static final String COLUMN_NAME_3 = "script3";

	protected static final String COLUMN_NAME_4 = "script4";

	protected static final String TABLE_NAME = "BaseJakartaUpgradeProcessTest";

	@Inject
	protected static CompanyLocalService companyLocalService;

	protected static DB db;

	protected String initialString;
	protected String resultString;

	private void _assertLogEntry(
		String expectedMessage, Set<String> expectedKeys, String logEntry) {

		Assert.assertTrue(logEntry, logEntry.contains(expectedMessage));

		for (String key : expectedKeys) {
			Assert.assertTrue(logEntry, logEntry.contains(key));
		}
	}

}