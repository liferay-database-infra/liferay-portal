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

	public String getCreateSQL(int[] lengths) {
		int sbSize = 8 + (_columnNames.length * 2);

		if (lengths != null) {
			sbSize += _columnNames.length * 3;
		}

		StringBundler sb = new StringBundler(sbSize);

		if (isUnique()) {
			sb.append("create unique ");
		}
		else {
			sb.append("create ");
		}

		sb.append("index ");
		sb.append(getIndexName());
		sb.append(" on ");
		sb.append(getTableName());

		sb.append(StringPool.SPACE);
		sb.append(StringPool.OPEN_PARENTHESIS);

		for (int i = 0; i < _columnNames.length; i++) {
			sb.append(_columnNames[i]);

			if ((lengths != null) && (lengths[i] > 0)) {
				sb.append("[$COLUMN_LENGTH:");
				sb.append(lengths[i]);
				sb.append("$]");
			}

			sb.append(StringPool.COMMA_AND_SPACE);
		}

		sb.setIndex(sb.index() - 1);

		sb.append(StringPool.CLOSE_PARENTHESIS);
		sb.append(StringPool.SEMICOLON);

		return sb.toString();
	}

	public String getDropSQL() {
		return _dropSQL;
	}

	private final String[] _columnNames;
	private final String _dropSQL;

}