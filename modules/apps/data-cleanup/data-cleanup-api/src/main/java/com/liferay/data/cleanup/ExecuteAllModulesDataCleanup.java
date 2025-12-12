/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.cleanup;

import com.liferay.data.cleanup.util.DataCleanupUtil;

/**
 * @author István András Dézsi
 */
public class ExecuteAllModulesDataCleanup extends DataCleanup {

	@Override
	public String getLabel() {
		return "clean-up-all-module-data";
	}

	@Override
	public String getType() {
		return MODULE_DATA_CLEANUP;
	}

	@Override
	protected void doCleanup() throws Exception {
		for (DataCleanup dataCleanup :
				DataCleanupUtil.getModuleDataCleanups()) {

			dataCleanup.cleanup();
		}
	}

}