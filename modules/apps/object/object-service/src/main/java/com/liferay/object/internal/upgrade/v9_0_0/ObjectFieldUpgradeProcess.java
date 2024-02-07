/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.upgrade.v9_0_0;

import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.dao.orm.common.SQLTransformer;
import com.liferay.portal.kernel.dao.db.IndexSQLUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.StringUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Pedro Leite
 */
public class ObjectFieldUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				SQLTransformer.transform(
					StringBundler.concat(
						"select ObjectField.dbColumnName, ",
						"ObjectField.dbTableName, ObjectField.localized, ",
						"ObjectDefinition.dbTableName as ",
						"objectDefinitionDBTableName from ObjectField inner ",
						"join ObjectDefinition on ",
						"ObjectDefinition.objectDefinitionId = ",
						"ObjectField.objectDefinitionId inner join ",
						"ObjectFieldSetting on ",
						"ObjectFieldSetting.objectFieldId = ",
						"ObjectField.objectFieldId where ",
						"(ObjectField.businessType = '",
						ObjectFieldConstants.BUSINESS_TYPE_AUTO_INCREMENT,
						"') or (ObjectFieldSetting.name = '",
						ObjectFieldSettingConstants.NAME_UNIQUE_VALUES,
						"' and ObjectFieldSetting.value = 'true')")));
			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				String dbColumnName = resultSet.getString("dbColumnName");
				String dbTableName = resultSet.getString("dbTableName");
				boolean localized = resultSet.getBoolean("localized");

				String[] columnNames = {dbColumnName};

				if (localized) {
					dbTableName =
						resultSet.getString("objectDefinitionDBTableName") +
							"_l";

					columnNames = new String[] {dbColumnName, "languageId"};
				}

				String indexName = _getIndexName(dbTableName, columnNames);

				if (hasIndex(dbTableName, indexName)) {
					runSQL(
						StringBundler.concat(
							"drop index ", indexName, " on ", dbTableName));
				}
			}
		}
	}

	private String _getIndexName(String tableName, String[] columnNames) {
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

		return IndexSQLUtil.INDEX_NAME_PREFIX.concat(specificationHash);
	}

}