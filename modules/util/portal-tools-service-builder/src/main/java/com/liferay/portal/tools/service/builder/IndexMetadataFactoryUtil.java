/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder;

/**
 * @author Mariano Álvaro Sáiz
 */
public class IndexMetadataFactoryUtil {

	public static IndexMetadata createIndexMetadata(
		boolean unique, String tableName, String... columnNames) {

		if (columnNames == null) {
			throw new NullPointerException("Column names are missing");
		}

		return new IndexMetadata(unique, tableName, columnNames);
	}

	public static IndexMetadata createIndexMetadata(String sql) {
		com.liferay.portal.kernel.dao.db.IndexMetadata indexMetadata =
			com.liferay.portal.kernel.dao.db.IndexMetadataFactoryUtil.
				createIndexMetadata(sql);

		return new IndexMetadata(
			indexMetadata.isUnique(), indexMetadata.getTableName(),
			indexMetadata.getColumnNames());
	}

}