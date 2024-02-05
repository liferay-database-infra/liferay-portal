/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.upgrade.v5_3_1;

import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.internal.dao.db.ObjectDBManagerUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.dao.orm.common.SQLTransformer;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;

/**
 * @author Murilo Stodolni
 */
public class SchemaUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		processConcurrently(
			SQLTransformer.transform(
				StringBundler.concat(
					"select ObjectField.dbColumnName, ObjectField.dbTableName ",
					"from ObjectField inner join ObjectDefinition on ",
					"ObjectField.businessType = '",
					ObjectFieldConstants.BUSINESS_TYPE_RELATIONSHIP,
					"' and ObjectDefinition.active_ = [$TRUE$] where ",
					"ObjectField.objectDefinitionId = ",
					"ObjectDefinition.objectDefinitionId")),
			resultSet -> new Object[] {
				resultSet.getString(1), resultSet.getString(2)
			},
			values -> ObjectDBManagerUtil.createIndexMetadata(
				connection, String.valueOf(values[1]), false,
				String.valueOf(values[0])),
			null);
	}

}