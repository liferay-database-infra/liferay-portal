/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_4_x.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.util.UpgradeVersionTreeMap;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.v7_4_x.PortalUpgradeProcessRegistryImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
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

	@After
	public void tearDown() throws Exception {
		if (_dlFileEntryType != null) {
			DLFileEntryType dlFileEntryType =
				_dlFileEntryTypeLocalService.fetchDLFileEntryType(
					_dlFileEntryType.getFileEntryTypeId());

			if (dlFileEntryType != null) {
				_dlFileEntryTypeLocalService.deleteFileEntryType(
					dlFileEntryType);
			}
		}

		if (_ddmStructure != null) {
			DDMStructure ddmStructure =
				_ddmStructureLocalService.fetchDDMStructure(
					_ddmStructure.getStructureId());

			if (ddmStructure != null) {
				_ddmStructureLocalService.deleteStructure(ddmStructure);
			}
		}
	}

	@Test
	public void testUpgrade() throws Exception {
		_ddmStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), DLFileEntryMetadata.class.getName());

		_dlFileEntryType = _dlFileEntryTypeLocalService.addFileEntryType(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			_ddmStructure.getStructureId(), null,
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId()));

		_testUpgrade(
			"name", _dlFileEntryType.getFileEntryTypeId(), "fileEntryTypeId",
			"DLFileEntryType", _dlFileEntryType.getName());

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		_testUpgrade(
			"name", layout.getPlid(), "plid", "Layout", layout.getName());

		_role = _roleLocalService.addRole(
			null, TestPropsValues.getUserId(), null, 0,
			RandomTestUtil.randomString(),
			RandomTestUtil.randomLocaleStringMap(), null,
			RoleConstants.TYPE_REGULAR, null, null);

		_testUpgrade(
			"title", _role.getRoleId(), "roleId", "Role_", _role.getTitle());
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

			UpgradeVersionTreeMap upgradeVersionTreeMap =
				new UpgradeVersionTreeMap();

			PortalUpgradeProcessRegistryImpl portalUpgradeProcessRegistryImpl =
				new PortalUpgradeProcessRegistryImpl();

			portalUpgradeProcessRegistryImpl.registerUpgradeProcesses(
				upgradeVersionTreeMap);

			for (UpgradeProcess upgradeProcess :
					upgradeVersionTreeMap.get(new Version(39, 3, 1))) {

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

	private DDMStructure _ddmStructure;

	@Inject
	private DDMStructureLocalService _ddmStructureLocalService;

	private DLFileEntryType _dlFileEntryType;

	@Inject
	private DLFileEntryTypeLocalService _dlFileEntryTypeLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private Role _role;

	@Inject
	private RoleLocalService _roleLocalService;

}