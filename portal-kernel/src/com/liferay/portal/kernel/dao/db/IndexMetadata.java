/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.dao.db;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.function.Function;

/**
 * @author James Lefeu
 * @author Peter Shin
 * @author Shuyang Zhou
 */
public class IndexMetadata extends Index {

	public IndexMetadata(
		String indexName, String tableName, boolean unique,
		String... columnNames) {

		super(indexName, tableName, unique);

		if (columnNames == null) {
			throw new NullPointerException("Column names are missing");
		}

		_columnNames = columnNames;

		_dropSQL = StringBundler.concat(
			"drop index ", indexName, " on ", tableName, StringPool.SEMICOLON);
	}

	public String[] getColumnNames() {
		return _columnNames;
	}

	public String getDropSQL() {
		return _dropSQL;
	}

	public Function<String[], String> getIndexNameRebuildFunction() {
		if (_isTempIndex()) {
			return columnNames -> getIndexName();
		}

		return columnNames -> IndexMetadataFactoryUtil.createIndexName(
			getTableName(), columnNames);
	}

	private boolean _isTempIndex() {
		return StringUtil.startsWith(getIndexName(), "IX_TEMP_");
	}

	private final String[] _columnNames;
	private final String _dropSQL;

}