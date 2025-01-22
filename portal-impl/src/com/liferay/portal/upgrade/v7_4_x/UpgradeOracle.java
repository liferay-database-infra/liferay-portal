/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_4_x;

import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.upgrade.BaseDBColumnSizeUpgradeProcess;

/**
 * @author Mariano Álvaro Sáiz
 */
public class UpgradeOracle extends BaseDBColumnSizeUpgradeProcess {

	public UpgradeOracle() {
		super(DBType.ORACLE, "number", 30, 20);
	}

	@Override
	protected void upgradeColumn(String tableName, String columnName)
		throws Exception {

		alterColumnType(tableName, columnName, "binary_double");
	}

}