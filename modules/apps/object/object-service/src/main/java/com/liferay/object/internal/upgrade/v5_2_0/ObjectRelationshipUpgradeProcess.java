/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.upgrade.v5_2_0;

import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.internal.dao.db.ObjectDBManagerUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.dao.orm.common.SQLTransformer;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;

/**
 * @author Mateus Santana
 */
public class ObjectRelationshipUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		processConcurrently(
			SQLTransformer.transform(
				StringBundler.concat(
					"select distinct ",
					"ObjectDefinition.pkObjectFieldDBColumnName, ",
					"ObjectRelationship.dbTableName, ",
					"ObjectRelationship.objectDefinitionId1, ",
					"ObjectRelationship.objectDefinitionId2 from ",
					"ObjectDefinition inner join ObjectRelationship on ",
					"ObjectRelationship.type_ = '",
					ObjectRelationshipConstants.TYPE_MANY_TO_MANY, "' where ",
					"ObjectDefinition.objectDefinitionId = ",
					"ObjectRelationship.objectDefinitionId1 and ",
					"ObjectDefinition.active_ = [$TRUE$]")),
			resultSet -> new Object[] {
				resultSet.getString(1), resultSet.getString(2),
				resultSet.getLong(3), resultSet.getLong(4)
			},
			values -> _createIndex(
				String.valueOf(values[0]), String.valueOf(values[1]),
				(long)values[2], (long)values[3]),
			null);
	}

	private void _createIndex(
			String columnName, String tableName, long objectDefinitionId1,
			long objectDefinitionId2)
		throws Exception {

		if (objectDefinitionId1 != objectDefinitionId2) {
			ObjectDBManagerUtil.createIndexMetadata(
				connection, tableName, false, columnName);
		}
		else {
			ObjectDBManagerUtil.createIndexMetadata(
				connection, tableName, false, columnName.concat("1"));
			ObjectDBManagerUtil.createIndexMetadata(
				connection, tableName, false, columnName.concat("2"));
		}
	}

}