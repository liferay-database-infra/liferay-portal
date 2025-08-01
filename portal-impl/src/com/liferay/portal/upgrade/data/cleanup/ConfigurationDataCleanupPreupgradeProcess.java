/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.data.cleanup;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.upgrade.data.cleanup.DataCleanupPreupgradeProcess;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author István András Dézsi
 */
public class ConfigurationDataCleanupPreupgradeProcess
	extends DataCleanupPreupgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		DBInspector dbInspector = new DBInspector(connection);

		if (!dbInspector.hasTable(
				dbInspector.normalizeName("Configuration_"))) {

			if (_log.isDebugEnabled()) {
				_log.debug("The Configuration_ table does not exist");
			}

			return;
		}

		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				"select configurationId, dictionary from Configuration_");
			PreparedStatement preparedStatement2 = connection.prepareStatement(
				"delete from Configuration_ where configurationId = ?");
			ResultSet resultSet = preparedStatement1.executeQuery()) {

			while (resultSet.next()) {
				String dictionary = resultSet.getString("dictionary");

				long companyId = _extractId(dictionary, _companyIdPattern);
				long groupId = _extractId(dictionary, _groupIdPattern);

				boolean orphanCompanyScopedConfiguration = false;
				boolean orphanGroupScopedConfiguration = false;

				if ((companyId != -1) &&
					!ArrayUtil.contains(
						PortalInstancePool.getCompanyIds(), companyId)) {

					orphanCompanyScopedConfiguration = true;
				}

				if ((groupId != -1) &&
					!ArrayUtil.contains(getGroupIds(), groupId)) {

					orphanGroupScopedConfiguration = true;
				}

				String entityName = null;

				if (orphanCompanyScopedConfiguration &&
					((groupId == -1) || orphanGroupScopedConfiguration)) {

					entityName = Company.class.getSimpleName();
				}
				else if (orphanGroupScopedConfiguration && (companyId == -1)) {
					entityName = Group.class.getSimpleName();
				}

				if (entityName != null) {
					String configurationId = resultSet.getString(
						"configurationId");

					preparedStatement2.setString(1, configurationId);

					preparedStatement2.addBatch();

					long entityId = 0;

					if (entityName.equals(Company.class.getSimpleName())) {
						entityId = companyId;
					}
					else {
						entityId = groupId;
					}

					if (_log.isInfoEnabled()) {
						_log.info(
							StringBundler.concat(
								"Deleted configuration ", configurationId,
								" because of nonexisting ", entityName, ": ",
								entityId));
					}
				}
			}

			preparedStatement2.executeBatch();
		}
	}

	protected long[] getGroupIds() throws Exception {
		List<Long> groupIds = new ArrayList<>();

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select groupId from Group_");
			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				groupIds.add(resultSet.getLong("groupId"));
			}
		}

		return ArrayUtil.toArray(groupIds.toArray(new Long[0]));
	}

	private long _extractId(String dictionary, Pattern pattern) {
		Matcher matcher = pattern.matcher(dictionary);

		if (matcher.find()) {
			String id =
				(matcher.group(1) != null) ? matcher.group(1) :
					matcher.group(2);

			return GetterUtil.getLong(id);
		}

		return -1;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ConfigurationDataCleanupPreupgradeProcess.class);

	private static final Pattern _companyIdPattern = Pattern.compile(
		"companyId=\\s*(?:[A-Z]\"(\\d+)\"|(\\d+))");
	private static final Pattern _groupIdPattern = Pattern.compile(
		"groupId=\\s*(?:[A-Z]\"(\\d+)\"|(\\d+))");

}