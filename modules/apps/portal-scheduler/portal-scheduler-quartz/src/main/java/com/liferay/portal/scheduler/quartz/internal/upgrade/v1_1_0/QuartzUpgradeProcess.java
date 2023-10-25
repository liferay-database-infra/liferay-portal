/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.scheduler.quartz.internal.upgrade.v1_1_0;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.db.partition.DBPartition;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.scheduler.SchedulerEngine;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;

import java.io.ObjectInputStream;

import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.HashMap;
import java.util.Map;

import org.quartz.JobDataMap;

/**
 * @author Kevin Lee
 */
public class QuartzUpgradeProcess extends UpgradeProcess {

	public QuartzUpgradeProcess(JSONFactory jsonFactory) {
		_jsonFactory = jsonFactory;
	}

	@Override
	protected void doUpgrade() throws Exception {
		Map<String, String> jobNamesMap = new HashMap<>();

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select job_name, job_data from QUARTZ_JOB_DETAILS where " +
					"job_name not like '%@%'");
			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				String jobName = resultSet.getString("job_name");

				long companyId = CompanyThreadLocal.getCompanyId();

				JobDataMap jobDataMap = _deserializeJobData(
					resultSet.getBlob("job_data"));

				if (jobDataMap != null) {
					Message message = (Message)_jsonFactory.deserialize(
						jobDataMap.getString(SchedulerEngine.MESSAGE));

					if (message.contains("companyId")) {
						companyId = message.getLong("companyId");
					}
				}

				jobNamesMap.put(
					jobName, jobName.concat(StringPool.AT + companyId));
			}
		}

		if (jobNamesMap.isEmpty()) {
			return;
		}

		_updateQuartzTables(
			jobNamesMap, "job_name",
			new String[] {
				"QUARTZ_FIRED_TRIGGERS", "QUARTZ_JOB_DETAILS", "QUARTZ_TRIGGERS"
			});

		_updateQuartzTables(
			jobNamesMap, "trigger_name",
			new String[] {
				"QUARTZ_BLOB_TRIGGERS", "QUARTZ_CRON_TRIGGERS",
				"QUARTZ_FIRED_TRIGGERS", "QUARTZ_SIMPLE_TRIGGERS",
				"QUARTZ_SIMPROP_TRIGGERS", "QUARTZ_TRIGGERS"
			});
	}

	@Override
	protected boolean isSkipUpgradeProcess() {
		return !DBPartition.isPartitionEnabled();
	}

	private JobDataMap _deserializeJobData(Blob blob) throws Exception {
		if (blob == null) {
			return null;
		}

		ObjectInputStream objectInputStream = new ObjectInputStream(
			blob.getBinaryStream());

		return (JobDataMap)objectInputStream.readObject();
	}

	private void _updateQuartzTables(
			Map<String, String> jobNamesMap, String columnName,
			String[] tableNames)
		throws Exception {

		for (String tableName : tableNames) {
			try (PreparedStatement preparedStatement =
					AutoBatchPreparedStatementUtil.autoBatch(
						connection,
						StringBundler.concat(
							"update ", tableName, " set ", columnName,
							" = ? where ", columnName, " = ?"))) {

				for (Map.Entry<String, String> entry : jobNamesMap.entrySet()) {
					preparedStatement.setString(1, entry.getValue());
					preparedStatement.setString(2, entry.getKey());
					preparedStatement.addBatch();
				}

				preparedStatement.executeBatch();
			}
		}
	}

	private final JSONFactory _jsonFactory;

}