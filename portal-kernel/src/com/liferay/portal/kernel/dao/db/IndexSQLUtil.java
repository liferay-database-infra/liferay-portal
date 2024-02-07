/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.dao.db;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Mariano Álvaro Sáiz
 */
public class IndexSQLUtil {

	public static final String INDEX_NAME_PREFIX = "IX_";

	public static String getCreateSQL(
		boolean unique, String tableName, String[] columnNames,
		int[] columnSizes, String indexPrefix) {

		int sbSize = 8 + (columnNames.length * 2);

		if (columnSizes != null) {
			sbSize += columnNames.length * 3;
		}

		StringBundler sb = new StringBundler(sbSize);

		if (unique) {
			sb.append("create unique ");
		}
		else {
			sb.append("create ");
		}

		String[] fullColumnNames = new String[columnNames.length];

		for (int i = 0; i < columnNames.length; i++) {
			fullColumnNames[i] = columnNames[i];

			if ((columnSizes != null) && (columnSizes[i] > 0)) {
				fullColumnNames[i] = StringBundler.concat(
					fullColumnNames[i], "[$COLUMN_LENGTH:", columnSizes[i],
					"$]");
			}
		}

		sb.append("index ");
		sb.append(_createIndexName(tableName, fullColumnNames, indexPrefix));
		sb.append(" on ");
		sb.append(tableName);

		sb.append(StringPool.SPACE);
		sb.append(StringPool.OPEN_PARENTHESIS);

		for (String fullColumnName : fullColumnNames) {
			sb.append(fullColumnName);
			sb.append(StringPool.COMMA_AND_SPACE);
		}

		sb.setIndex(sb.index() - 1);

		sb.append(StringPool.CLOSE_PARENTHESIS);
		sb.append(StringPool.SEMICOLON);

		return sb.toString();
	}

	public static String getCreateSQL(
			Connection connection, boolean unique, String tableName,
			String[] columnNames, String indexPrefix)
		throws SQLException {

		return getCreateSQL(
			unique, tableName, columnNames,
			_getColumnSizes(connection, tableName, columnNames), indexPrefix);
	}

	private static String _createIndexName(
		String tableName, String[] fullColumnNames, String indexPrefix) {

		StringBundler sb = new StringBundler(4 + (fullColumnNames.length * 2));

		sb.append(tableName);
		sb.append(StringPool.SPACE);
		sb.append(StringPool.OPEN_PARENTHESIS);

		for (String columnName : fullColumnNames) {
			sb.append(columnName);
			sb.append(StringPool.COMMA_AND_SPACE);
		}

		sb.setIndex(sb.index() - 1);

		sb.append(StringPool.CLOSE_PARENTHESIS);
		sb.append(StringPool.SEMICOLON);

		String specification = sb.toString();

		String specificationHash = StringUtil.toHexString(
			specification.hashCode());

		specificationHash = StringUtil.toUpperCase(specificationHash);

		return indexPrefix.concat(specificationHash);
	}

	private static int[] _getColumnSizes(
			Connection connection, String tableName, String[] columnNames)
		throws SQLException {

		DatabaseMetaData databaseMetaData = connection.getMetaData();

		DB db = DBManagerUtil.getDB();

		DBInspector dbInspector = new DBInspector(connection);

		int[] columnSizes = new int[columnNames.length];

		try (ResultSet resultSet = databaseMetaData.getColumns(
				dbInspector.getCatalog(), dbInspector.getSchema(),
				dbInspector.normalizeName(tableName), null)) {

			Map<String, Integer> columnSizeMap = new HashMap<>();

			while (resultSet.next()) {
				int columnType = resultSet.getInt("DATA_TYPE");

				if (!db.isVarchar(columnType)) {
					continue;
				}

				columnSizeMap.put(
					dbInspector.normalizeName(
						resultSet.getString("COLUMN_NAME"), databaseMetaData),
					resultSet.getInt("COLUMN_SIZE"));
			}

			for (int i = 0; i < columnNames.length; i++) {
				columnSizes[i] = MapUtil.getInteger(
					columnSizeMap, columnNames[i], 0);
			}
		}

		return columnSizes;
	}

}