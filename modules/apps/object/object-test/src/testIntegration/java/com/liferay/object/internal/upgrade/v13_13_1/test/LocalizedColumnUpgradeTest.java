/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.upgrade.v13_13_1.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.HashMap;
import java.util.Map;

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

	@Test
	public void testUpgrade() throws Exception {
		_objectDefinition =
			ObjectDefinitionTestUtil.addCustomObjectDefinition();

		_testUpgrade(
			"label", _objectDefinition.getObjectDefinitionId(),
			"objectDefinitionId", "ObjectDefinition",
			_objectDefinition.getLabel());
		_testUpgrade(
			"pluralLabel", _objectDefinition.getObjectDefinitionId(),
			"objectDefinitionId", "ObjectDefinition",
			_objectDefinition.getPluralLabel());

		ObjectField objectField = ObjectFieldUtil.addCustomObjectField(
			new TextObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"a" + RandomTestUtil.randomString()
			).objectDefinitionId(
				_objectDefinition.getObjectDefinitionId()
			).userId(
				TestPropsValues.getUserId()
			).build());

		_testUpgrade(
			"label", objectField.getObjectFieldId(), "objectFieldId",
			"ObjectField", objectField.getLabel());
	}

	private Map<Long, String> _getValues(
			String columnName, String primaryKeyColumnName, String tableName)
		throws Exception {

		Map<Long, String> values = new HashMap<>();

		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select ", primaryKeyColumnName, ", ", columnName, " from ",
					tableName))) {

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					values.put(
						resultSet.getLong(primaryKeyColumnName),
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

		Map<Long, String> values = _getValues(
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
				_upgradeStepRegistrator, new Version(13, 13, 1));

			for (UpgradeProcess upgradeProcess : upgradeProcesses) {
				upgradeProcess.upgrade();
			}

			try (Connection connection = DataAccess.getConnection()) {
				DBInspector dbInspector = new DBInspector(connection);

				Assert.assertTrue(
					dbInspector.hasColumnType(
						tableName, columnName, "TEXT null"));
			}

			Map<Long, String> upgradedValues = _getValues(
				columnName, primaryKeyColumnName, tableName);

			Assert.assertEquals(value, upgradedValues.get(primaryKey));
			Assert.assertEquals(values, upgradedValues);
		}
		finally {
			_restoreColumnType(columnName, tableName);
		}
	}

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition;

	@Inject(
		filter = "component.name=com.liferay.object.internal.upgrade.registry.ObjectServiceUpgradeStepRegistrator"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}