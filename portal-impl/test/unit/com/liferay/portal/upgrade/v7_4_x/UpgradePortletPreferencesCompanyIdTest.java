/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_4_x;

import com.liferay.portal.kernel.upgrade.MockPortletPreferences;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import javax.portlet.PortletPreferences;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author István András Dézsi
 */
public class UpgradePortletPreferencesCompanyIdTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_portletPreferences = new MockPortletPreferences();
		_upgradePortletPreferencesCompanyId =
			new UpgradePortletPreferencesCompanyId();
	}

	@Test
	public void testUpgrade() throws Exception {
		_upgradePortletPreferencesCompanyId.upgrade();
	}

	private PortletPreferences _portletPreferences;
	private UpgradePortletPreferencesCompanyId
		_upgradePortletPreferencesCompanyId;

}