/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder;

import com.liferay.portal.kernel.dao.db.IndexSQLUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * @author Mariano Álvaro Sáiz
 */
public class IndexMetadata implements Comparable<IndexMetadata> {

	public IndexMetadata(
		boolean unique, String tableName, String... columnNames) {

		if (columnNames == null) {
			throw new NullPointerException("Column names are missing");
		}

		_unique = unique;
		_tableName = tableName;
		_columnNames = columnNames;

		_dbColumnNames = _trimColumnNames(columnNames);
	}

	@Override
	public int compareTo(IndexMetadata indexMetadata) {
		String columnNames = StringUtil.merge(getColumnNames());

		String indexMetadataColumnNames = StringUtil.merge(
			indexMetadata.getColumnNames());

		return columnNames.compareTo(indexMetadataColumnNames);
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof IndexMetadata)) {
			return false;
		}

		IndexMetadata indexMetadata = (IndexMetadata)object;

		if (Objects.equals(_tableName, indexMetadata._tableName) &&
			Arrays.equals(_columnNames, indexMetadata._columnNames)) {

			return true;
		}

		return false;
	}

	public String[] getColumnNames() {
		return _columnNames;
	}

	public String getCreateSQL(int[] columnSizes) {
		if ((columnSizes == null) || (columnSizes.length == 0)) {
			return IndexSQLUtil.getCreateSQL(
				_unique, _tableName, _columnNames, columnSizes,
				IndexSQLUtil.INDEX_NAME_PREFIX);
		}

		return IndexSQLUtil.getCreateSQL(
			_unique, _tableName, _dbColumnNames, columnSizes,
			IndexSQLUtil.INDEX_NAME_PREFIX);
	}

	public String[] getDBColumnNames() {
		return _dbColumnNames;
	}

	public String getTableName() {
		return _tableName;
	}

	@Override
	public int hashCode() {
		int hashCode = _hash(0, _tableName);

		for (String columnName : _columnNames) {
			hashCode = _hash(hashCode, columnName);
		}

		return hashCode;
	}

	public boolean isUnique() {
		return _unique;
	}

	public void optimizeColumns(Map<String, IntegerWrapper> frequencyMap) {
		Arrays.sort(
			_columnNames,
			(columnName1, columnName2) -> {
				IntegerWrapper count1 = frequencyMap.get(columnName1);

				IntegerWrapper count2 = frequencyMap.get(columnName2);

				return count2.compareTo(count1);
			});

		_dbColumnNames = _trimColumnNames(_columnNames);
	}

	public Boolean redundantTo(IndexMetadata indexMetadata) {
		String[] indexMetadataColumnNames = indexMetadata._columnNames;

		if (indexMetadata._unique && _unique) {
			if ((_columnNames.length <= indexMetadataColumnNames.length) &&
				ArrayUtil.containsAll(indexMetadataColumnNames, _columnNames)) {

				return Boolean.FALSE;
			}

			if ((_columnNames.length > indexMetadataColumnNames.length) &&
				ArrayUtil.containsAll(_columnNames, indexMetadataColumnNames)) {

				return Boolean.TRUE;
			}
		}

		if (_columnNames.length <= indexMetadataColumnNames.length) {
			for (int i = 0; i < _columnNames.length; i++) {
				if (!_columnNames[i].equals(indexMetadataColumnNames[i])) {
					return null;
				}
			}

			if (_unique) {
				return Boolean.FALSE;
			}

			return Boolean.TRUE;
		}

		Boolean redundant = indexMetadata.redundantTo(this);

		if (redundant == null) {
			return null;
		}

		return !redundant;
	}

	private int _hash(int seed, Object value) {
		return (seed * 11) + ((value == null) ? 0 : value.hashCode());
	}

	private String _trimColumnName(String columnName) {
		int index = columnName.indexOf("[$COLUMN_LENGTH:");

		if (index > 0) {
			columnName = columnName.substring(0, index);
		}

		return columnName;
	}

	private String[] _trimColumnNames(String[] columnNames) {
		String[] trimmedColumnNames = columnNames.clone();

		for (int i = 0; i < trimmedColumnNames.length; i++) {
			trimmedColumnNames[i] = _trimColumnName(trimmedColumnNames[i]);
		}

		return trimmedColumnNames;
	}

	private final String[] _columnNames;
	private String[] _dbColumnNames;
	private final String _tableName;
	private final boolean _unique;

}