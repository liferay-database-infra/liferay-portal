/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.data.cleanup;

import com.liferay.portal.db.index.PrimaryKeyUpdaterUtil;
import com.liferay.portal.events.StartupHelperUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ReleaseConstants;
import com.liferay.portal.kernel.upgrade.data.cleanup.DataCleanupPreupgradeProcess;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.upgrade.PortalUpgradeProcess;

import java.sql.Connection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author Luis Ortiz
 */
public class DataCleanupPreupgradeProcessSuite {

	public void cleanUp() throws Exception {
		try (Connection connection = DataAccess.getConnection()) {
			if (StartupHelperUtil.isDBNew() ||
				PortalUpgradeProcess.isInLatestSchemaVersion(connection) ||
				(PortalUpgradeProcess.getCurrentState(connection) !=
					ReleaseConstants.STATE_GOOD)) {

				return;
			}
		}

		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			for (List<DataCleanupPreupgradeProcess> wave :
					getWavedDataCleanupPreupgradeProcesses()) {

				if (wave.size() == 1) {
					_runProcess(wave.get(0));

					continue;
				}

				ExecutorService executorService = Executors.newFixedThreadPool(
					wave.size());

				try {
					List<Future<Void>> futures = new ArrayList<>();

					for (DataCleanupPreupgradeProcess process : wave) {
						futures.add(
							executorService.submit(
								(Callable<Void>)() -> {
									_runProcess(process);

									return null;
								}));
					}

					List<Exception> exceptions = new ArrayList<>();

					for (Future<Void> future : futures) {
						try {
							future.get();
						}
						catch (ExecutionException executionException) {
							Throwable throwable = executionException.getCause();

							exceptions.add(
								throwable instanceof Exception ?
									(Exception)throwable :
										new RuntimeException(throwable));
						}
						catch (InterruptedException interruptedException) {
							Thread.currentThread(
							).interrupt();

							exceptions.add(
								new RuntimeException(interruptedException));
						}
					}

					if (!exceptions.isEmpty()) {
						Exception exception = exceptions.get(0);

						for (int i = 1; i < exceptions.size(); i++) {
							exception.addSuppressed(exceptions.get(i));
						}

						throw exception;
					}
				}
				finally {
					executorService.shutdownNow();
				}
			}
		}
	}

	public List<DataCleanupPreupgradeProcess>
		getSortedDataCleanupPreupgradeProcesses() {

		return DataCleanupPreupgradeProcess.
			getSortedDataCleanupPreupgradeProcesses(
				_dataCleanupPreupgradeProcessesMap);
	}

	public List<List<DataCleanupPreupgradeProcess>>
		getWavedDataCleanupPreupgradeProcesses() {

		return DataCleanupPreupgradeProcess.
			getWavedDataCleanupPreupgradeProcesses(
				_dataCleanupPreupgradeProcessesMap);
	}

	private Map
		<DataCleanupPreupgradeProcess, List<DataCleanupPreupgradeProcess>>
			_createDataCleanupPreupgradeProcessesMap() {

		DataCleanupPreupgradeProcess
			analyticsMessageDataCleanupPreupgradeProcess =
				new AnalyticsMessageDataCleanupPreupgradeProcess();
		DataCleanupPreupgradeProcess companyDataCleanupPreupgradeProcess =
			new CompanyDataCleanupPreupgradeProcess();
		DataCleanupPreupgradeProcess configurationDataCleanupPreupgradeProcess =
			new ConfigurationDataCleanupPreupgradeProcess();
		DataCleanupPreupgradeProcess contactDataCleanupPreupgradeProcess =
			new ContactDataCleanupPreupgradeProcess();
		DataCleanupPreupgradeProcess
			databaseTableAndColumnCaseDataCleanupPreupgradeProcess =
				new DatabaseTableAndColumnCaseDataCleanupPreupgradeProcess();
		DataCleanupPreupgradeProcess ddmDataCleanupPreupgradeProcess =
			new DDMDataCleanupPreupgradeProcess();
		DataCleanupPreupgradeProcess
			ddmStorageLinkDataCleanupPreupgradeProcess =
				new DDMStorageLinkDataCleanupPreupgradeProcess();
		DataCleanupPreupgradeProcess dlFileEntryDataCleanupPreupgradeProcess =
			new DLFileEntryDataCleanupPreupgradeProcess();
		DataCleanupPreupgradeProcess groupDataCleanupPreupgradeProcess =
			new GroupDataCleanupPreupgradeProcess();
		DataCleanupPreupgradeProcess
			illegalCharactersContentDataCleanupPreupgradeProcess =
				new IllegalCharactersContentDataCleanupPreupgradeProcess();
		DataCleanupPreupgradeProcess journalDataCleanupPreupgradeProcess =
			new JournalDataCleanupPreupgradeProcess();
		DataCleanupPreupgradeProcess layoutDataCleanupPreupgradeProcess =
			new LayoutDataCleanupPreupgradeProcess();
		DataCleanupPreupgradeProcess
			portalPreferencesDataCleanupPreupgradeProcess =
				new PortalPreferencesDataCleanupPreupgradeProcess();
		DataCleanupPreupgradeProcess
			portletPreferencesDataCleanupPreupgradeProcess =
				new PortletPreferencesDataCleanupPreupgradeProcess();
		DataCleanupPreupgradeProcess
			quartzJobDetailsDataCleanupPreupgradeProcess =
				new QuartzJobDetailsDataCleanupPreupgradeProcess();
		DataCleanupPreupgradeProcess roleDataCleanupPreupgradeProcess =
			new RoleDataCleanupPreupgradeProcess();
		DataCleanupPreupgradeProcess
			resourcePermissionDataCleanupPreupgradeProcess =
				new ResourcePermissionDataCleanupPreupgradeProcess();
		DataCleanupPreupgradeProcess
			updateAllPrimaryKeysDataCleanupPreupgradeProcess =
				new DataCleanupPreupgradeProcess() {

					@Override
					protected void doUpgrade() throws Exception {
						PrimaryKeyUpdaterUtil.updateAllPrimaryKeys();
					}

				};
		DataCleanupPreupgradeProcess userDataCleanupPreupgradeProcess =
			new UserDataCleanupPreupgradeProcess();

		return LinkedHashMapBuilder.
			<DataCleanupPreupgradeProcess, List<DataCleanupPreupgradeProcess>>
				put(
					analyticsMessageDataCleanupPreupgradeProcess,
					DataCleanupPreupgradeProcess.dependsOn(
						databaseTableAndColumnCaseDataCleanupPreupgradeProcess)
			).put(
				companyDataCleanupPreupgradeProcess,
				DataCleanupPreupgradeProcess.dependsOn(
					analyticsMessageDataCleanupPreupgradeProcess,
					databaseTableAndColumnCaseDataCleanupPreupgradeProcess,
					updateAllPrimaryKeysDataCleanupPreupgradeProcess)
			).put(
				configurationDataCleanupPreupgradeProcess,
				DataCleanupPreupgradeProcess.dependsOn(
					companyDataCleanupPreupgradeProcess,
					databaseTableAndColumnCaseDataCleanupPreupgradeProcess,
					userDataCleanupPreupgradeProcess)
			).put(
				contactDataCleanupPreupgradeProcess,
				DataCleanupPreupgradeProcess.dependsOn(
					userDataCleanupPreupgradeProcess)
			).put(
				new CounterDataCleanupPreupgradeProcess(),
				DataCleanupPreupgradeProcess.dependsOn(
					analyticsMessageDataCleanupPreupgradeProcess,
					companyDataCleanupPreupgradeProcess,
					configurationDataCleanupPreupgradeProcess,
					contactDataCleanupPreupgradeProcess,
					databaseTableAndColumnCaseDataCleanupPreupgradeProcess,
					ddmDataCleanupPreupgradeProcess,
					ddmStorageLinkDataCleanupPreupgradeProcess,
					dlFileEntryDataCleanupPreupgradeProcess,
					groupDataCleanupPreupgradeProcess,
					journalDataCleanupPreupgradeProcess,
					layoutDataCleanupPreupgradeProcess,
					illegalCharactersContentDataCleanupPreupgradeProcess,
					portalPreferencesDataCleanupPreupgradeProcess,
					portletPreferencesDataCleanupPreupgradeProcess,
					quartzJobDetailsDataCleanupPreupgradeProcess,
					resourcePermissionDataCleanupPreupgradeProcess,
					roleDataCleanupPreupgradeProcess,
					updateAllPrimaryKeysDataCleanupPreupgradeProcess,
					userDataCleanupPreupgradeProcess)
			).put(
				databaseTableAndColumnCaseDataCleanupPreupgradeProcess,
				DataCleanupPreupgradeProcess.dependsOn()
			).put(
				ddmDataCleanupPreupgradeProcess,
				DataCleanupPreupgradeProcess.dependsOn(
					databaseTableAndColumnCaseDataCleanupPreupgradeProcess,
					groupDataCleanupPreupgradeProcess)
			).put(
				ddmStorageLinkDataCleanupPreupgradeProcess,
				DataCleanupPreupgradeProcess.dependsOn(
					databaseTableAndColumnCaseDataCleanupPreupgradeProcess,
					ddmDataCleanupPreupgradeProcess,
					dlFileEntryDataCleanupPreupgradeProcess,
					journalDataCleanupPreupgradeProcess)
			).put(
				dlFileEntryDataCleanupPreupgradeProcess,
				DataCleanupPreupgradeProcess.dependsOn(
					databaseTableAndColumnCaseDataCleanupPreupgradeProcess,
					groupDataCleanupPreupgradeProcess)
			).put(
				groupDataCleanupPreupgradeProcess,
				DataCleanupPreupgradeProcess.dependsOn(
					databaseTableAndColumnCaseDataCleanupPreupgradeProcess,
					userDataCleanupPreupgradeProcess)
			).put(
				illegalCharactersContentDataCleanupPreupgradeProcess,
				DataCleanupPreupgradeProcess.dependsOn(
					databaseTableAndColumnCaseDataCleanupPreupgradeProcess,
					ddmDataCleanupPreupgradeProcess)
			).put(
				journalDataCleanupPreupgradeProcess,
				DataCleanupPreupgradeProcess.dependsOn(
					databaseTableAndColumnCaseDataCleanupPreupgradeProcess,
					ddmDataCleanupPreupgradeProcess)
			).put(
				layoutDataCleanupPreupgradeProcess,
				DataCleanupPreupgradeProcess.dependsOn(
					companyDataCleanupPreupgradeProcess,
					groupDataCleanupPreupgradeProcess,
					userDataCleanupPreupgradeProcess)
			).put(
				portalPreferencesDataCleanupPreupgradeProcess,
				DataCleanupPreupgradeProcess.dependsOn(
					companyDataCleanupPreupgradeProcess,
					groupDataCleanupPreupgradeProcess,
					userDataCleanupPreupgradeProcess)
			).put(
				portletPreferencesDataCleanupPreupgradeProcess,
				DataCleanupPreupgradeProcess.dependsOn(
					layoutDataCleanupPreupgradeProcess)
			).put(
				quartzJobDetailsDataCleanupPreupgradeProcess,
				DataCleanupPreupgradeProcess.dependsOn(
					databaseTableAndColumnCaseDataCleanupPreupgradeProcess)
			).put(
				resourcePermissionDataCleanupPreupgradeProcess,
				DataCleanupPreupgradeProcess.dependsOn(
					analyticsMessageDataCleanupPreupgradeProcess,
					companyDataCleanupPreupgradeProcess,
					configurationDataCleanupPreupgradeProcess,
					contactDataCleanupPreupgradeProcess,
					databaseTableAndColumnCaseDataCleanupPreupgradeProcess,
					ddmDataCleanupPreupgradeProcess,
					ddmStorageLinkDataCleanupPreupgradeProcess,
					dlFileEntryDataCleanupPreupgradeProcess,
					groupDataCleanupPreupgradeProcess,
					journalDataCleanupPreupgradeProcess,
					layoutDataCleanupPreupgradeProcess,
					illegalCharactersContentDataCleanupPreupgradeProcess,
					portalPreferencesDataCleanupPreupgradeProcess,
					portletPreferencesDataCleanupPreupgradeProcess,
					quartzJobDetailsDataCleanupPreupgradeProcess,
					roleDataCleanupPreupgradeProcess,
					updateAllPrimaryKeysDataCleanupPreupgradeProcess,
					userDataCleanupPreupgradeProcess)
			).put(
				roleDataCleanupPreupgradeProcess,
				DataCleanupPreupgradeProcess.dependsOn(
					companyDataCleanupPreupgradeProcess,
					userDataCleanupPreupgradeProcess)
			).put(
				updateAllPrimaryKeysDataCleanupPreupgradeProcess,
				DataCleanupPreupgradeProcess.dependsOn(
					databaseTableAndColumnCaseDataCleanupPreupgradeProcess)
			).put(
				userDataCleanupPreupgradeProcess,
				DataCleanupPreupgradeProcess.dependsOn(
					companyDataCleanupPreupgradeProcess,
					databaseTableAndColumnCaseDataCleanupPreupgradeProcess)
			).build();
	}

	private void _runProcess(
			DataCleanupPreupgradeProcess dataCleanupPreupgradeProcess)
		throws Exception {

		Class<?> clazz = dataCleanupPreupgradeProcess.getClass();

		if (ArrayUtil.contains(
				PropsValues.UPGRADE_DATABASE_PREUPGRADE_DATA_CLEANUP_BLACKLIST,
				clazz.getName())) {

			if (_log.isInfoEnabled()) {
				_log.info(
					"Skipping blacklisted data cleanup process: " +
						clazz.getName());
			}

			return;
		}

		String processName = clazz.getSimpleName();

		if (processName.isEmpty()) {
			processName = clazz.getName();
		}

		try (LoggingTimer loggingTimer = new LoggingTimer(processName)) {
			dataCleanupPreupgradeProcess.upgrade();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DataCleanupPreupgradeProcessSuite.class);

	private final Map
		<DataCleanupPreupgradeProcess, List<DataCleanupPreupgradeProcess>>
			_dataCleanupPreupgradeProcessesMap =
				_createDataCleanupPreupgradeProcessesMap();

}