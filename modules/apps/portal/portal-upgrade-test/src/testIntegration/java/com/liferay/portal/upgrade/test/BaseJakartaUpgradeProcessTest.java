/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.upgrade.BaseJakartaUpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Luis Ortiz
 */
@RunWith(Arquillian.class)
public class BaseJakartaUpgradeProcessTest extends BaseJakartaUpgradeTestCase {

	@Test
	public void testUpgradeWithCustomSeparators() throws Exception {
		initialString = "import javax$portlet$Portlet";
		resultString = "import jakarta$portlet$Portlet";

		insertInitialData();

		testUpgrade(
			() -> {
				UpgradeProcess upgradeProcess =
					new BaseJakartaUpgradeProcess() {

						@Override
						public char[] getCustomSeparators() {
							return new char[] {'$'};
						}

						@Override
						protected String[][] getTableAndColumnNames() {
							return new String[][] {
								{TABLE_NAME, COLUMN_NAME_1},
								{TABLE_NAME, COLUMN_NAME_2},
								{TABLE_NAME, COLUMN_NAME_3}
							};
						}

					};

				upgradeProcess.upgrade();
			});
	}

	@Test
	public void testUpgradeWithoutCustomSeparators() throws Exception {
		initialString = "import javax.portlet.Portlet";
		resultString = "import jakarta.portlet.Portlet";

		insertInitialData();

		testUpgrade(
			() -> {
				UpgradeProcess upgradeProcess =
					new BaseJakartaUpgradeProcess() {

						@Override
						protected String[][] getTableAndColumnNames() {
							return new String[][] {
								{TABLE_NAME, COLUMN_NAME_1},
								{TABLE_NAME, COLUMN_NAME_2},
								{TABLE_NAME, COLUMN_NAME_3}
							};
						}

					};

				upgradeProcess.upgrade();
			});
	}

}