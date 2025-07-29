/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.data.cleanup;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upgrade.data.cleanup.DataCleanupPreupgradeProcess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.List;

/**
 * @author István András Dézsi
 */
public class QuartzJobDetailsDataCleanupPreupgradeProcess
	extends DataCleanupPreupgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				"select JOB_NAME from QUARTZ_JOB_DETAILS where JOB_NAME not " +
					"like '%@%' and JOB_DATA is null");
			ResultSet resultSet = preparedStatement1.executeQuery()) {

			List<String> jobNames = new ArrayList<>();

			while (resultSet.next()) {
				jobNames.add(resultSet.getString("JOB_NAME"));
			}

			if (jobNames.isEmpty()) {
				return;
			}

			if (jobNames.size() == 1) {
				try (PreparedStatement preparedStatement2 =
						connection.prepareStatement(
							"delete from QUARTZ_JOB_DETAILS where JOB_NAME = " +
								"?")) {

					preparedStatement2.setString(1, jobNames.get(0));

					preparedStatement2.execute();

					if (_log.isInfoEnabled()) {
						_log.info(
							StringBundler.concat(
								"Deleted Quartz job detail for job '",
								jobNames.get(0),
								"' because it had no job data and was not a ",
								"Liferay-managed job"));
					}
				}
			}
			else {
				if (_log.isInfoEnabled()) {
					_log.info(
						StringBundler.concat(
							"Found ", jobNames.size(),
							" Quartz job details with null JOB_DATA that are ",
							"not Liferay-managed jobs:"));

					for (String jobName : jobNames) {
						_log.info("Job name: " + jobName);
					}
				}
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		QuartzJobDetailsDataCleanupPreupgradeProcess.class);

}