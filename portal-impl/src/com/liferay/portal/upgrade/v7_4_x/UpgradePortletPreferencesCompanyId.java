/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_4_x;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.PortletKeys;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.List;

/**
 * @author István András Dézsi
 */
public class UpgradePortletPreferencesCompanyId extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		runSQL(
			StringBundler.concat(
				"update PortletPreferences inner join PortletPreferenceValue ",
				"on PortletPreferences.portletPreferencesId = ",
				"PortletPreferenceValue.portletPreferencesId set ",
				"PortletPreferences.companyId = (select companyId from ",
				"Company where Company.companyId = ",
				"PortletPreferences.ownerId), ",
				"PortletPreferenceValue.companyId = (select companyId from ",
				"Company where Company.companyId = ",
				"PortletPreferences.ownerId) where ",
				"PortletPreferences.ownerType = ",
				PortletKeys.PREFS_OWNER_TYPE_COMPANY,
				" and (PortletPreferences.companyId is null or ",
				"PortletPreferences.companyId = 0)"));

		if (_log.isWarnEnabled()) {
			try (PreparedStatement preparedStatement =
					connection.prepareStatement(
						"select * from PortletPreferences where companyId is " +
							"null or companyId = 0")) {

				ResultSet resultSet = preparedStatement.executeQuery();

				List<Long> portletPreferencesIds = new ArrayList<>();

				while (resultSet.next()) {
					portletPreferencesIds.add(
						resultSet.getLong("portletPreferencesId"));
				}

				if (!portletPreferencesIds.isEmpty()) {
					_log.warn(
						"There are " + portletPreferencesIds.size() +
							" entries where companyId could not be modified:");

					for (Long portletPreferencesId : portletPreferencesIds) {
						_log.warn(
							"PortletPreferencesId: " + portletPreferencesId);
					}
				}
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UpgradePortletPreferencesCompanyId.class);

}