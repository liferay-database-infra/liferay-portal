/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.dao.db;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author James Lefeu
 * @author Peter Shin
 * @author Shuyang Zhou
 */
public class IndexMetadataFactoryUtil {

	public static IndexMetadata createIndexMetadata(
			Connection connection, boolean unique, String tableName,
			String... columnNames)
		throws SQLException {

		return _createIndexMetadata(
			connection, unique, tableName, columnNames, _INDEX_NAME_PREFIX);
	}

	public static IndexMetadata createIndexMetadata(String createSQL) {
		boolean unique = createSQL.contains("unique");

		int start = createSQL.indexOf(_INDEX_NAME_PREFIX);

		if (start < 0) {
			throw new IllegalArgumentException(
				"Unable to find index name start " + createSQL);
		}

		int end = createSQL.indexOf(CharPool.SPACE, start + 3);

		String indexName = createSQL.substring(start, end);

		start = createSQL.indexOf("on ", end + 1);

		if (start < 0) {
			throw new IllegalArgumentException(
				"Unable to find table name start " + createSQL);
		}

		start += 3;

		end = createSQL.indexOf(CharPool.SPACE, start + 1);

		if (end < 0) {
			throw new IllegalArgumentException(
				"Unable to find table name end " + createSQL);
		}

		String tableName = createSQL.substring(start, end);

		start = createSQL.indexOf(CharPool.OPEN_PARENTHESIS, end + 1);

		if (start < 0) {
			throw new IllegalArgumentException(
				"Unable to find column names start " + createSQL);
		}

		start += 1;

		end = createSQL.indexOf(CharPool.CLOSE_PARENTHESIS, start + 1);

		if (end < 0) {
			throw new IllegalArgumentException(
				"Unable to find column names end " + createSQL);
		}

		String[] columnNames = StringUtil.split(
			createSQL.substring(start, end), StringPool.COMMA_AND_SPACE);

		return new IndexMetadata(
			indexName, tableName, unique, columnNames, createSQL);
	}

	public static IndexMetadata createTempIndexMetadata(
			Connection connection, boolean unique, String tableName,
			String... columnNames)
		throws SQLException {

		return _createIndexMetadata(
			connection, unique, tableName, columnNames, "IX_TEMP_");
	}

	private static IndexMetadata _createIndexMetadata(
			Connection connection, boolean unique, String tableName,
			String[] columnNames, String indexPrefix)
		throws SQLException {

		if (columnNames == null) {
			throw new NullPointerException("Column names are missing");
		}

		String sqlCreate = IndexSQLUtil.getCreateSQL(
			connection, tableName, unique, columnNames, indexPrefix);

		String substring = sqlCreate.substring(sqlCreate.indexOf(indexPrefix));

		return new IndexMetadata(
			substring.substring(0, substring.indexOf(StringPool.SPACE)),
			tableName, unique, columnNames, sqlCreate);
	}

	private static final String _INDEX_NAME_PREFIX = "IX_";

}