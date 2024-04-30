/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_4_x;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.upgrade.v7_0_0.UpgradeCompanyId;

import java.io.IOException;

import java.sql.SQLException;

/**
 * @author István András Dézsi
 */
public class UpgradePortletPreferencesCompanyId extends UpgradeCompanyId {

	@Override
	protected TableUpdater[] getTableUpdaters() {
		return new TableUpdater[] {
			new PortletPreferencesValueTableUpdater("PortletPreferences")
		};
	}

	protected class PortletPreferencesValueTableUpdater
		extends PortletPreferencesTableUpdater {

		public PortletPreferencesValueTableUpdater(String tableName) {
			super(tableName);
		}

		@Override
		protected String getUpdateSQL(String selectSQL) {
			return StringBundler.concat(
				"update PortletPreferences inner join PortletPreferenceValue ",
				"on PortletPreferences.portletPreferencesId = ",
				"PortletPreferenceValue.portletPreferencesId set ",
				"PortletPreferences.companyId = (", selectSQL, "), ",
				"PortletPreferenceValue.companyId = (", selectSQL, ")");
		}

		@Override
		protected String getUpdateSQL(
				String foreignTableName, String foreignColumnName,
				String columnName, int ownerType)
			throws IOException, SQLException {

			return StringBundler.concat(
				getUpdateSQL(
					getSelectSQL(
						foreignTableName, foreignColumnName, columnName)),
				" where ownerType = ", ownerType,
				" and (PortletPreferences.companyId is null or ",
				"PortletPreferences.companyId = 0)");
		}

	}

}