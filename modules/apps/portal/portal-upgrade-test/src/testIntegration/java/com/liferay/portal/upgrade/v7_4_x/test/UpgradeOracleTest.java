/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_4_x.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.AssumeTestRule;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.v7_4_x.UpgradeOracle;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mariano Álvaro Sáiz
 */
@RunWith(Arquillian.class)
public class UpgradeOracleTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new AssumeTestRule("assume"), new LiferayIntegrationTestRule());

	public static void assume() {
		Assume.assumeTrue(DBManagerUtil.getDBType() == DBType.ORACLE);
	}

	@Before
	public void setUp() throws Exception {
		try (Connection connection = DataAccess.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				"create table TestTable (testTableId int not null primary " +
					"key, testValue number(30,20) null)")) {

			preparedStatement.executeUpdate();
		}
	}

	@After
	public void tearDown() throws Exception {
		try (Connection connection = DataAccess.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				"drop table TestTable")) {

			preparedStatement.executeUpdate();
		}
	}

	@Test
	public void testUpgrade() throws Exception {
		new UpgradeOracle(
		).upgrade();

		try (Connection connection = DataAccess.getConnection()) {
			DatabaseMetaData databaseMetaData = connection.getMetaData();

			DBInspector dbInspector = new DBInspector(connection);

			try (ResultSet resultSet = databaseMetaData.getColumns(
					dbInspector.getCatalog(), dbInspector.getSchema(),
					dbInspector.normalizeName("TestTable"),
					dbInspector.normalizeName("testValue"))) {

				Assert.assertTrue(resultSet.next());

				Assert.assertEquals(
					dbInspector.normalizeName("binary_double"),
					resultSet.getString("TYPE_NAME"));

				Assert.assertFalse(resultSet.next());
			}
		}
	}

}