/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.cleanup;

import com.liferay.data.cleanup.util.DataCleanupUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author István András Dézsi
 */
public class ExecuteAllSystemDataCleanup extends DataCleanup {

	@Override
	public String getLabel() {
		return "clean-up-all-system-data";
	}

	@Override
	public String getType() {
		return SYSTEM_DATA_CLEANUP;
	}

	@Override
	protected void doCleanup() throws Exception {
		List<Exception> exceptions = new ArrayList<>();

		for (DataCleanup dataCleanup :
				DataCleanupUtil.getSystemDataCleanups()) {

			try {
				dataCleanup.cleanup();
			}
			catch (Exception exception) {
				_log.error(
					"Failed to execute cleanup: " + dataCleanup.getLabel(),
					exception);

				exceptions.add(exception);
			}
		}

		if (!exceptions.isEmpty()) {
			throw new Exception("One or more system data cleanup tasks failed");
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ExecuteAllSystemDataCleanup.class);

}