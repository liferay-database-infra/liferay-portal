/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.dao.db;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;

import java.util.function.Function;

/**
 * @author Mariano Álvaro Sáiz
 */
public class IndexSQLUtil {

	public static String getCreateSQL(
		String tableName, boolean unique, String[] columnNames,
		Function<String[], String> nameGenerator, int[] lengths) {

		int sbSize = 8 + (columnNames.length * 2);

		if (lengths != null) {
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

			if ((lengths != null) && (lengths[i] > 0)) {
				fullColumnNames[i] = StringBundler.concat(
					fullColumnNames[i], "[$COLUMN_LENGTH:", lengths[i], "$]");
			}
		}

		sb.append("index ");
		sb.append(nameGenerator.apply(fullColumnNames));
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

}