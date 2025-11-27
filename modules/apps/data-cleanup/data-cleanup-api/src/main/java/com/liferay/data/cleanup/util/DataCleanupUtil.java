/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.cleanup.util;

import com.liferay.data.cleanup.DataCleanup;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Maríano Álvaro Sáiz
 */
public class DataCleanupUtil {

	public static List<DataCleanup> getModuleDataCleanups() {
		return _moduleDataCleanups;
	}

	public static List<DataCleanup> getSystemDataCleanups() {
		return _systemDataCleanups;
	}

	public static void registerDataCleanup(DataCleanup dataCleanup) {
		String type = dataCleanup.getType();

		if (StringUtil.equalsIgnoreCase(
				type, DataCleanup.MODULE_DATA_CLEANUP)) {

			_moduleDataCleanups.add(dataCleanup);
		}
		else if (StringUtil.equalsIgnoreCase(
					type, DataCleanup.SYSTEM_DATA_CLEANUP)) {

			_systemDataCleanups.add(dataCleanup);
		}
		else {
			throw new IllegalArgumentException(
				"Type : " + type + " is not allowed");
		}
	}

	public static void unregisterDataCleanup(DataCleanup dataCleanup) {
		String type = dataCleanup.getType();

		if (StringUtil.equalsIgnoreCase(
				type, DataCleanup.MODULE_DATA_CLEANUP)) {

			_moduleDataCleanups.remove(dataCleanup);
		}
		else if (StringUtil.equalsIgnoreCase(
					type, DataCleanup.SYSTEM_DATA_CLEANUP)) {

			_systemDataCleanups.remove(dataCleanup);
		}
		else {
			throw new IllegalArgumentException(
				"Type : " + type + " is not allowed");
		}
	}

	private static final List<DataCleanup> _moduleDataCleanups =
		new ArrayList<>();
	private static final List<DataCleanup> _systemDataCleanups =
		new ArrayList<>();

}