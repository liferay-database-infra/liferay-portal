/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.db.partition.upgrade.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.db.partition.test.util.BaseDBPartitionTestCase;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.upgrade.util.UpgradePartitionedControlTableView;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author István András Dézsi
 */
@RunWith(Arquillian.class)
public class UpgradePartitionedControlTableViewTest
	extends BaseDBPartitionTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		BaseDBPartitionTestCase.setUpClass();

		BaseDBPartitionTestCase.setUpDBPartitions();
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		BaseDBPartitionTestCase.tearDownDBPartitions();
	}

	@Test
	public void testUpgrade() throws Exception {
		_removeViewFilter(COMPANY_IDS[0]);

		Assert.assertTrue(_isCompanyVisible(COMPANY_IDS[0], COMPANY_IDS[1]));

		UpgradeProcess upgradeProcess = new UpgradePartitionedControlTableView(
			"Company", "VirtualHost");

		upgradeProcess.upgrade();

		Assert.assertFalse(_isCompanyVisible(COMPANY_IDS[0], COMPANY_IDS[1]));
		Assert.assertTrue(_isCompanyVisible(COMPANY_IDS[0], COMPANY_IDS[0]));
	}

	private boolean _isCompanyVisible(long partitionCompanyId, long companyId)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select companyId from ",
					getPartitionName(partitionCompanyId),
					".Company where companyId = ?"))) {

			preparedStatement.setLong(1, companyId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				return resultSet.next();
			}
		}
	}

	private void _removeViewFilter(long companyId) throws Exception {
		try (Statement statement = connection.createStatement()) {
			statement.execute(
				StringBundler.concat(
					"create or replace view ", getPartitionName(companyId),
					".Company as select * from ", defaultPartitionName,
					".Company"));
		}
	}

}