/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.db.schema.info.internal.test.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.dao.jdbc.DataSourceFactoryUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.util.PropsValues;

import java.io.File;

import java.sql.Connection;

import javax.sql.DataSource;

/**
 * @author Mariano Álvaro Sáiz
 */
public class DatabaseTestUtil {

	public static void createSchema(String schemaName) throws Exception {
		DB db = DBManagerUtil.getDB();

		db.runSQL("create schema " + schemaName + " character set utf8");
	}

	public static void dropSchema(String schemaName) throws Exception {
		DB db = DBManagerUtil.getDB();

		db.runSQL("drop schema " + schemaName);
	}

	public static String getSchemaURL(String schemaName) {
		String jdbcURL = PropsValues.JDBC_DEFAULT_URL;

		int index = jdbcURL.indexOf("?");

		if (index == -1) {
			return jdbcURL.substring(0, jdbcURL.lastIndexOf("/") + 1) +
				schemaName;
		}

		String baseJDBCURL = jdbcURL.substring(0, index);

		return StringBundler.concat(
			jdbcURL.substring(0, baseJDBCURL.lastIndexOf("/") + 1), schemaName,
			jdbcURL.substring(index));
	}

	public static void importFileTo(File file, String schemaName)
		throws Exception {

		DataSource dataSource = DataSourceFactoryUtil.initDataSource(
			PropsValues.JDBC_DEFAULT_DRIVER_CLASS_NAME,
			getSchemaURL(schemaName), PropsValues.JDBC_DEFAULT_USERNAME,
			PropsValues.JDBC_DEFAULT_PASSWORD, StringPool.BLANK);

		DB db = DBManagerUtil.getDB(DBType.MYSQL, dataSource);

		try (Connection connection = dataSource.getConnection()) {
			db.runSQLTemplateString(connection, FileUtil.read(file), true);
		}
		finally {
			DataSourceFactoryUtil.destroyDataSource(dataSource);
		}
	}

}