/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.upgrade.v3_0_0;

import com.liferay.portal.kernel.upgrade.BaseJakartaUpgradeProcess;

/**
 * @author Luis Ortiz
 */
public class FragmentJakartaUpgradeProcess extends BaseJakartaUpgradeProcess {

	@Override
	protected String[][] getTableAndColumnNames() {
		return new String[][] {
			{"FragmentEntry", "html"}, {"FragmentEntry", "configuration"},
			{"FragmentEntryLink", "html"},
			{"FragmentEntryLink", "configuration"},
			{"FragmentEntryLink", "editableValues"},
			{"FragmentEntryVersion", "html"},
			{"FragmentEntryVersion", "configuration"}
		};
	}

}