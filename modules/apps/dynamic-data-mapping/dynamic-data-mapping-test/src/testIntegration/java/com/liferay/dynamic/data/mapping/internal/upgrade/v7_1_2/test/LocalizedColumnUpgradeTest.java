/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.upgrade.v7_1_2.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.lists.model.DDLRecordSet;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersion;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jorge Avalos
 */
@RunWith(Arquillian.class)
public class LocalizedColumnUpgradeTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testUpgrade() throws Exception {
		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), DDLRecordSet.class.getName());

		_testUpgrade(
			"name", ddmStructure.getStructureId(), "structureId",
			"DDMStructure", ddmStructure.getName());

		DDMStructureVersion ddmStructureVersion =
			ddmStructure.getStructureVersion();

		_testUpgrade(
			"name", ddmStructureVersion.getStructureVersionId(),
			"structureVersionId", "DDMStructureVersion",
			ddmStructureVersion.getName());
	}

	private Map<String, String> _getValues(
			String columnName, String primaryKeyColumnName, String tableName)
		throws Exception {

		Map<String, String> values = new HashMap<>();

		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select ", primaryKeyColumnName, ", ctCollectionId, ",
					columnName, " from ", tableName))) {

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					values.put(
						StringBundler.concat(
							resultSet.getLong(primaryKeyColumnName), "_",
							resultSet.getLong("ctCollectionId")),
						resultSet.getString(columnName));
				}
			}
		}

		return values;
	}

	private void _restoreColumnType(String columnName, String tableName)
		throws Exception {

		try (Connection connection = DataAccess.getConnection()) {
			DBInspector dbInspector = new DBInspector(connection);

			if (dbInspector.hasColumnType(tableName, columnName, "TEXT null")) {
				return;
			}

			DB db = DBManagerUtil.getDB();

			db.alterColumnType(connection, tableName, columnName, "TEXT null");
		}
	}

	private void _testUpgrade(
			String columnName, long primaryKey, String primaryKeyColumnName,
			String tableName, String value)
		throws Exception {

		Map<String, String> values = _getValues(
			columnName, primaryKeyColumnName, tableName);

		try {
			try (Connection connection = DataAccess.getConnection()) {
				DB db = DBManagerUtil.getDB();

				db.alterColumnType(
					connection, tableName, columnName, "STRING null");

				DBInspector dbInspector = new DBInspector(connection);

				Assert.assertTrue(
					dbInspector.hasColumnType(
						tableName, columnName, "STRING null"));
			}

			UpgradeProcess[] upgradeProcesses = UpgradeTestUtil.getUpgradeSteps(
				_upgradeStepRegistrator, new Version(7, 1, 2));

			for (UpgradeProcess upgradeProcess : upgradeProcesses) {
				upgradeProcess.upgrade();
			}

			try (Connection connection = DataAccess.getConnection()) {
				DBInspector dbInspector = new DBInspector(connection);

				Assert.assertTrue(
					dbInspector.hasColumnType(
						tableName, columnName, "TEXT null"));
			}

			Map<String, String> upgradedValues = _getValues(
				columnName, primaryKeyColumnName, tableName);

			Assert.assertEquals(
				value,
				upgradedValues.get(StringBundler.concat(primaryKey, "_", 0L)));
			Assert.assertEquals(values, upgradedValues);
		}
		finally {
			_restoreColumnType(columnName, tableName);
		}
	}

	@DeleteAfterTestRun
	private Group _group;

	@Inject(
		filter = "component.name=com.liferay.dynamic.data.mapping.internal.upgrade.registry.DDMServiceUpgradeStepRegistrator"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}