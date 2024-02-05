/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.dao.db;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;

/**
 * @author Mariano Álvaro Sáiz
 */
public class IndexSQLUtil {

	public static String getCreateSQL(
		IndexMetadata indexMetadata, int[] lengths) {

		String[] columnNames = indexMetadata.getColumnNames();

		int sbSize = 8 + (columnNames.length * 2);

		if (lengths != null) {
			sbSize += columnNames.length * 3;
		}

		StringBundler sb = new StringBundler(sbSize);

		if (indexMetadata.isUnique()) {
			sb.append("create unique ");
		}
		else {
			sb.append("create ");
		}

		sb.append("index ");
		sb.append(indexMetadata.getIndexName());
		sb.append(" on ");
		sb.append(indexMetadata.getTableName());

		sb.append(StringPool.SPACE);
		sb.append(StringPool.OPEN_PARENTHESIS);

		for (int i = 0; i < columnNames.length; i++) {
			sb.append(columnNames[i]);

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

}