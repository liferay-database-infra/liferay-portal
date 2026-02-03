/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.data.cleanup;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upgrade.data.cleanup.DataCleanupPreupgradeProcess;
import com.liferay.portal.kernel.upgrade.data.cleanup.FilterableAllTablesOrphanReferencesDataCleanupPreupgradeProcess;
import com.liferay.portal.kernel.upgrade.data.cleanup.TableOrphanReferencesDataCleanupPreupgradeProcess;
import com.liferay.portal.kernel.upgrade.data.cleanup.util.DataCleanupLoggingUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Luis Ortiz
 */
public class DDMDataCleanupPreupgradeProcess
	extends DataCleanupPreupgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		DataCleanupPreupgradeProcess ddmStructureDataCleanupPreupgradeProcess =
			_getDDMStructureDataCleanupPreupgradeProcess();
		DataCleanupPreupgradeProcess
			ddmStructureVersionDataCleanupPreupgradeProcess =
				_getDDMStructureVersionDataCleanupPreupgradeProcess();
		DataCleanupPreupgradeProcess ddmTemplateDataCleanupPreupgradeProcess =
			_getDDMTemplateDataCleanupPreupgradeProcess();

		Map<DataCleanupPreupgradeProcess, List<DataCleanupPreupgradeProcess>>
			dataCleanupPreupgradeProcessesMap =
				LinkedHashMapBuilder.
					<DataCleanupPreupgradeProcess,
					 List<DataCleanupPreupgradeProcess>>put(
						_getDDMFieldDataCleanupPreupgradeProcess(),
						dependsOn(
							ddmStructureVersionDataCleanupPreupgradeProcess)
					).put(
						_getDDMFormInstanceDataCleanupPreupgradeProcess(),
						dependsOn(ddmStructureDataCleanupPreupgradeProcess)
					).put(
						ddmStructureDataCleanupPreupgradeProcess, dependsOn()
					).put(
						ddmStructureVersionDataCleanupPreupgradeProcess,
						dependsOn(ddmStructureDataCleanupPreupgradeProcess)
					).put(
						ddmTemplateDataCleanupPreupgradeProcess,
						dependsOn(ddmStructureDataCleanupPreupgradeProcess)
					).put(
						_getJournalPointingOrphanDDMStructureCleanupPreupgradeProcess62(),
						dependsOn(ddmTemplateDataCleanupPreupgradeProcess)
					).put(
						_getJournalPointingOrphanDDMStructureCleanupPreupgradeProcess70to73(),
						dependsOn(ddmTemplateDataCleanupPreupgradeProcess)
					).put(
						_getJournalPointingOrphanNonancestorDDMStructureCleanupPreupgradeProcess70to73(),
						dependsOn(ddmTemplateDataCleanupPreupgradeProcess)
					).put(
						_getJournalPointingOrphanDDMStructureCleanupPreupgradeProcess74(),
						dependsOn(ddmTemplateDataCleanupPreupgradeProcess)
					).build();

		List<DataCleanupPreupgradeProcess> dataCleanupPreupgradeProcesses =
			getSortedDataCleanupPreupgradeProcesses(
				dataCleanupPreupgradeProcessesMap);

		for (DataCleanupPreupgradeProcess dataCleanupPreupgradeProcess :
				dataCleanupPreupgradeProcesses) {

			dataCleanupPreupgradeProcess.upgrade();
		}
	}

	private DataCleanupPreupgradeProcess
		_getDDMFieldDataCleanupPreupgradeProcess() {

		return new DataCleanupPreupgradeProcess(
			new TableOrphanReferencesDataCleanupPreupgradeProcess(
				null, null, "fieldId", "DDMFieldAttribute", "fieldId",
				"DDMField"));
	}

	private DataCleanupPreupgradeProcess
		_getDDMFormInstanceDataCleanupPreupgradeProcess() {

		return new DataCleanupPreupgradeProcess(
			new TableOrphanReferencesDataCleanupPreupgradeProcess(
				null, null, "formInstanceId", "DDMFormInstanceRecord",
				"formInstanceId", "DDMFormInstance"),
			new TableOrphanReferencesDataCleanupPreupgradeProcess(
				null, null, "formInstanceId", "DDMFormInstanceRecordVersion",
				"formInstanceId", "DDMFormInstance"),
			new TableOrphanReferencesDataCleanupPreupgradeProcess(
				null, null, "formInstanceId", "DDMFormInstanceReport",
				"formInstanceId", "DDMFormInstance"),
			new TableOrphanReferencesDataCleanupPreupgradeProcess(
				null, null, "formInstanceId", "DDMFormInstanceReportVersion",
				"formInstanceId", "DDMFormInstance"));
	}

	private DataCleanupPreupgradeProcess
		_getDDMStructureDataCleanupPreupgradeProcess() {

		return new DataCleanupPreupgradeProcess(
			new FilterableAllTablesOrphanReferencesDataCleanupPreupgradeProcess(
				StringBundler.concat(
					"[$SOURCE_TABLE_ALIAS$].classNameId = (select classNameId ",
					"from ClassName_ where value = 'com.liferay.dynamic.data.",
					"mapping.model.DDMStructure')"),
				new String[] {"classNameId"}, "classPK",
				new String[] {"structureId"}, "DDMStructure"),
			new TableOrphanReferencesDataCleanupPreupgradeProcess(
				null, null, "structureId", "DDMFormInstance", "structureId",
				"DDMStructure"),
			new TableOrphanReferencesDataCleanupPreupgradeProcess(
				null, null, "structureId", "DDMStorageLink", "structureId",
				"DDMStructure"),
			new TableOrphanReferencesDataCleanupPreupgradeProcess(
				null, null, "structureId", "DDMStructureLink", "structureId",
				"DDMStructure"),
			new TableOrphanReferencesDataCleanupPreupgradeProcess(
				null, null, "structureId", "DDMStructureVersion", "structureId",
				"DDMStructure"));
	}

	private DataCleanupPreupgradeProcess
		_getDDMStructureVersionDataCleanupPreupgradeProcess() {

		return new DataCleanupPreupgradeProcess(
			new TableOrphanReferencesDataCleanupPreupgradeProcess(
				null, null, "structureVersionId", "DDMField",
				"structureVersionId", "DDMStructureVersion"),
			new TableOrphanReferencesDataCleanupPreupgradeProcess(
				null, null, "structureVersionId", "DDMStructureLayout",
				"structureVersionId", "DDMStructureVersion"));
	}

	private DataCleanupPreupgradeProcess
		_getDDMTemplateDataCleanupPreupgradeProcess() {

		return new DataCleanupPreupgradeProcess(
			new FilterableAllTablesOrphanReferencesDataCleanupPreupgradeProcess(
				StringBundler.concat(
					"[$SOURCE_TABLE_ALIAS$].classNameId = (select classNameId ",
					"from ClassName_ where value = 'com.liferay.dynamic.data.",
					"mapping.model.DDMTemplate')"),
				new String[] {"classNameId"}, "classPK",
				new String[] {"templateId"}, "DDMTemplate"),
			new TableOrphanReferencesDataCleanupPreupgradeProcess(
				null, null, "templateId", "DDMTemplateLink", "templateId",
				"DDMTemplate"),
			new TableOrphanReferencesDataCleanupPreupgradeProcess(
				null, null, "templateId", "DDMTemplateVersion", "templateId",
				"DDMTemplate"));
	}

	private DataCleanupPreupgradeProcess
		_getJournalPointingOrphanDDMStructureCleanupPreupgradeProcess62() {

		return new DataCleanupPreupgradeProcess(
			new TableOrphanReferencesDataCleanupPreupgradeProcess(
				null, null, "structureId", "JournalArticle", "structureKey",
				"DDMStructure"),
			new TableOrphanReferencesDataCleanupPreupgradeProcess(
				null, null, "structureId", "JournalFeed", "structureKey",
				"DDMStructure"));
	}

	private DataCleanupPreupgradeProcess
		_getJournalPointingOrphanDDMStructureCleanupPreupgradeProcess70to73() {

		return new DataCleanupPreupgradeProcess(
			new TableOrphanReferencesDataCleanupPreupgradeProcess(
				null, null, "DDMStructureKey", "JournalArticle", "structureKey",
				"DDMStructure"),
			new TableOrphanReferencesDataCleanupPreupgradeProcess(
				null, null, "DDMStructureKey", "JournalFeed", "structureKey",
				"DDMStructure"));
	}

	private DataCleanupPreupgradeProcess
		_getJournalPointingOrphanDDMStructureCleanupPreupgradeProcess74() {

		return new DataCleanupPreupgradeProcess(
			new TableOrphanReferencesDataCleanupPreupgradeProcess(
				null, null, "DDMStructureId", "JournalArticle", "structureId",
				"DDMStructure"),
			new TableOrphanReferencesDataCleanupPreupgradeProcess(
				null, null, "DDMStructureId", "JournalFeed", "structureId",
				"DDMStructure"));
	}

	private DataCleanupPreupgradeProcess
		_getJournalPointingOrphanNonancestorDDMStructureCleanupPreupgradeProcess70to73() {

		return new DataCleanupPreupgradeProcess() {

			@Override
			protected void doUpgrade() throws Exception {
				DBInspector dbInspector = new DBInspector(connection);

				if (!dbInspector.hasColumn(
						"JournalArticle", "DDMStructureKey")) {

					return;
				}

				List<Long> globalGroupIds = new ArrayList<>();

				Map<Long, Long> groupParents = new HashMap<>();

				try (PreparedStatement preparedStatement =
						connection.prepareStatement(
							"select friendlyURL, groupId, parentGroupId from " +
								"Group_");
					ResultSet resultSet = preparedStatement.executeQuery()) {

					while (resultSet.next()) {
						String friendlyURL = resultSet.getString("friendlyURL");

						long groupId = resultSet.getLong("groupId");
						long parentGroupId = resultSet.getLong("parentGroupId");

						if (Objects.equals(friendlyURL, "/global")) {
							globalGroupIds.add(groupId);
						}

						if (parentGroupId > 0) {
							groupParents.put(groupId, parentGroupId);
						}
					}
				}

				Set<String> structureKeys = new HashSet<>();

				try (PreparedStatement preparedStatement =
						connection.prepareStatement(
							"select groupId, structureKey from DDMStructure");
					ResultSet resultSet = preparedStatement.executeQuery()) {

					while (resultSet.next()) {
						structureKeys.add(
							resultSet.getLong("groupId") + StringPool.POUND +
								resultSet.getString("structureKey"));
					}
				}

				try (PreparedStatement preparedStatement1 =
						connection.prepareStatement(
							StringBundler.concat(
								"select groupId, DDMStructureKey, count(1) ",
								"from JournalArticle where DDMStructureKey is ",
								"not null group by groupId, ",
								"DDMStructureKey"));
					PreparedStatement preparedStatement2 =
						connection.prepareStatement(
							"delete from JournalArticle where groupId = ? " +
								"and DDMStructureKey = ?");
					ResultSet resultSet = preparedStatement1.executeQuery()) {

					while (resultSet.next()) {
						long groupId = resultSet.getLong("groupId");

						String structureKey = resultSet.getString(
							"DDMStructureKey");

						if (_hasStructure(
								globalGroupIds, groupId, groupParents,
								structureKey, structureKeys)) {

							continue;
						}

						if (_log.isInfoEnabled()) {
							DataCleanupLoggingUtil.logDelete(
								_log, resultSet.getLong(3),
								dbInspector.normalizeName("JournalArticle"),
								StringBundler.concat(
									dbInspector.normalizeName(
										"DDMStructureKey"),
									StringPool.SPACE, structureKey,
									" was not found in ",
									dbInspector.normalizeName("groupId"),
									StringPool.SPACE, groupId,
									" or its ancestors"));
						}

						preparedStatement2.setLong(1, groupId);
						preparedStatement2.setString(2, structureKey);

						preparedStatement2.addBatch();
					}

					preparedStatement2.executeBatch();
				}
			}

			private boolean _hasStructure(
				List<Long> globalGroupIds, long groupId,
				Map<Long, Long> groupParents, String structureKey,
				Set<String> structureKeys) {

				long currentGroupId = groupId;

				while (true) {
					if (structureKeys.contains(
							currentGroupId + StringPool.POUND + structureKey)) {

						return true;
					}

					if (!groupParents.containsKey(currentGroupId)) {
						break;
					}

					currentGroupId = groupParents.get(currentGroupId);
				}

				for (Long globalGroupId : globalGroupIds) {
					if (structureKeys.contains(
							globalGroupId + StringPool.POUND + structureKey)) {

						return true;
					}
				}

				return false;
			}

		};
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DDMDataCleanupPreupgradeProcess.class);

}