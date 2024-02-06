/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.dao.db;

import com.liferay.petra.string.CharPool;
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

		int start = createSQL.indexOf("IX_");

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

	public static String createIndexName(
		String tableName, String[] columnNames, String indexPrefix) {

		StringBundler sb = new StringBundler(4 + (columnNames.length * 2));

		sb.append(tableName);
		sb.append(StringPool.SPACE);
		sb.append(StringPool.OPEN_PARENTHESIS);

		for (String columnName : columnNames) {
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

		int[] columnSizes = _getColumnSizes(connection, tableName, columnNames);

		String[] fullColumnNames = new String[columnNames.length];

		for (int i = 0; i < columnNames.length; i++) {
			fullColumnNames[i] = columnNames[i];

			if ((columnSizes != null) && (columnSizes[i] > 0)) {
				fullColumnNames[i] = StringBundler.concat(
					fullColumnNames[i], "[$COLUMN_LENGTH:", columnSizes[i],
					"$]");
			}
		}

		String indexName = createIndexName(
			tableName, fullColumnNames, indexPrefix);

		return new IndexMetadata(
			indexName, tableName, unique, columnNames,
			_getCreateSQL(
				indexName, tableName, unique, fullColumnNames, columnSizes));
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

	private static String _getCreateSQL(
		String indexName, String tableName, boolean unique,
		String[] fullColumnNames, int[] columnSizes) {

		int sbSize = 8 + (fullColumnNames.length * 2);

		if (columnSizes != null) {
			sbSize += fullColumnNames.length * 3;
		}

		StringBundler sb = new StringBundler(sbSize);

		if (unique) {
			sb.append("create unique ");
		}
		else {
			sb.append("create ");
		}

		sb.append("index ");
		sb.append(indexName);
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

	private static final String _INDEX_NAME_PREFIX = "IX_";

}