/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.dao.db;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;

/**
 * @author James Lefeu
 * @author Peter Shin
 * @author Shuyang Zhou
 */
public class IndexMetadata extends Index {

	public IndexMetadata(
		String indexName, boolean unique, String tableName,
		String[] columnNames, String createSQL) {

		super(indexName, tableName, unique);

		if (columnNames == null) {
			throw new NullPointerException("Column names are missing");
		}

		_columnNames = columnNames;

		_createSQL = createSQL;

		_dropSQL = StringBundler.concat(
			"drop index ", indexName, " on ", tableName, StringPool.SEMICOLON);
	}

	public String[] getColumnNames() {
		return _columnNames;
	}

	public String getCreateSQL() {
		return _createSQL;
	}

	public String getDropSQL() {
		return _dropSQL;
	}

	private final String[] _columnNames;
	private final String _createSQL;
	private final String _dropSQL;

}