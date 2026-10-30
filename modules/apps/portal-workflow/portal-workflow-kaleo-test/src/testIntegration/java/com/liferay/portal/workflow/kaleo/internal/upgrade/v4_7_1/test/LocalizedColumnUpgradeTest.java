/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.internal.upgrade.v4_7_1.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.LocalizationUtil;
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

import org.junit.After;
import org.junit.Assert;
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

	@After
	public void tearDown() throws Exception {
		_deleteRow(
			_KALEO_DEFINITION_ID, "kaleoDefinitionId", "KaleoDefinition");
		_deleteRow(
			_KALEO_DEFINITION_VERSION_ID, "kaleoDefinitionVersionId",
			"KaleoDefinitionVersion");
	}

	@Test
	public void testUpgrade() throws Exception {
		String kaleoDefinitionTitle = LocalizationUtil.updateLocalization(
			StringPool.BLANK, "Title", RandomTestUtil.randomString());

		_addRow(
			_KALEO_DEFINITION_ID, "kaleoDefinitionId", "KaleoDefinition",
			kaleoDefinitionTitle);

		_testUpgrade(
			"title", _KALEO_DEFINITION_ID, "kaleoDefinitionId",
			"KaleoDefinition", kaleoDefinitionTitle);

		String kaleoDefinitionVersionTitle =
			LocalizationUtil.updateLocalization(
				StringPool.BLANK, "Title", RandomTestUtil.randomString());

		_addRow(
			_KALEO_DEFINITION_VERSION_ID, "kaleoDefinitionVersionId",
			"KaleoDefinitionVersion", kaleoDefinitionVersionTitle);

		_testUpgrade(
			"title", _KALEO_DEFINITION_VERSION_ID, "kaleoDefinitionVersionId",
			"KaleoDefinitionVersion", kaleoDefinitionVersionTitle);
	}

	private void _addRow(
			long primaryKey, String primaryKeyColumnName, String tableName,
			String title)
		throws Exception {

		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"insert into ", tableName, " (", primaryKeyColumnName,
					", companyId, title) values (?, ?, ?)"))) {

			preparedStatement.setLong(1, primaryKey);
			preparedStatement.setLong(2, RandomTestUtil.randomLong());
			preparedStatement.setString(3, title);

			preparedStatement.executeUpdate();
		}
	}

	private void _deleteRow(
			long primaryKey, String primaryKeyColumnName, String tableName)
		throws Exception {

		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"delete from ", tableName, " where ", primaryKeyColumnName,
					" = ?"))) {

			preparedStatement.setLong(1, primaryKey);

			preparedStatement.executeUpdate();
		}
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
				_upgradeStepRegistrator, new Version(4, 7, 1));

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

	private static final long _KALEO_DEFINITION_ID =
		RandomTestUtil.randomLong();

	private static final long _KALEO_DEFINITION_VERSION_ID =
		RandomTestUtil.randomLong();

	@Inject(
		filter = "component.name=com.liferay.portal.workflow.kaleo.internal.upgrade.registry.KaleoServiceUpgradeStepRegistrator"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}