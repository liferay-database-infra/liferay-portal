/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.db.partition.internal.configuration.persistence.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.Objects;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mariano Álvaro Sáiz
 */
@RunWith(Arquillian.class)
public class DBPartitionVirtualInstanceInsertionConfigurationModelListenerTest
	extends BaseConfigurationModelListenerTestCase {

	@Override
	public String getListenerName() {
		return "DBPartitionVirtualInstanceInsertionConfigurationModelListener";
	}

	@Test
	public void testDeployConfiguration() throws Exception {
		try (AutoCloseable autoCloseable = swapCompanyLocalService(
				(proxy, method, args) -> {
					if (Objects.equals(method.getName(), "insertCompany")) {
						Assert.assertEquals(
							COMPANY_IDS[0], GetterUtil.getLong(args[0]));

						_calledInsertCompany = true;
					}

					return null;
				})) {

			deployConfiguration(
				_PID,
				"newWebId=T\"testNewWebId\"\ncompanyId=L\"" + COMPANY_IDS[0] +
					"\"\n");

			Assert.assertTrue(_calledInsertCompany);

			verifyConfigurationIsDeletedAfterDeploy(_PID);
		}
	}

	private static final String _PID =
		"com.liferay.portal.db.partition.internal.configuration." +
			"DBPartitionVirtualInstanceInsertionConfiguration";

	private boolean _calledInsertCompany;

}