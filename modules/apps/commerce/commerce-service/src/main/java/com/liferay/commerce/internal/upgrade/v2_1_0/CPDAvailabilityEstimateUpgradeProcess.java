/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.upgrade.v2_1_0;

import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.kernel.upgrade.UpgradeStep;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author Alec Sloan
 */
public class CPDAvailabilityEstimateUpgradeProcess extends UpgradeProcess {

	public CPDAvailabilityEstimateUpgradeProcess(
		CPDefinitionLocalService cpDefinitionLocalService) {

		_cpDefinitionLocalService = cpDefinitionLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"update CPDAvailabilityEstimate set CProductId = ? where " +
					"CPDefinitionId = ?");
			Statement s = connection.createStatement();
			ResultSet resultSet = s.executeQuery(
				"select distinct CPDefinitionId from " +
					"CPDAvailabilityEstimate")) {

			while (resultSet.next()) {
				long cpDefinitionId = resultSet.getLong("CPDefinitionId");

				CPDefinition cpDefinition =
					_cpDefinitionLocalService.getCPDefinition(cpDefinitionId);

				preparedStatement.setLong(1, cpDefinition.getCProductId());

				preparedStatement.setLong(2, cpDefinitionId);

				preparedStatement.execute();
			}
		}
	}

	@Override
	protected UpgradeStep[] getPreUpgradeSteps() {
		return new UpgradeStep[] {
			UpgradeProcessFactory.addColumns(
				"CPDAvailabilityEstimate", "CProductId LONG")
		};
	}

	private final CPDefinitionLocalService _cpDefinitionLocalService;

}