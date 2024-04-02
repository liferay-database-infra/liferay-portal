/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.db.partition.internal.operation;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.db.partition.internal.configuration.DBPartitionInsertVirtualInstanceConfiguration;
import com.liferay.portal.instances.service.PortalInstancesLocalService;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.CompanyLocalService;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mariano Álvaro Sáiz
 */
@Component(
	configurationPid = "com.liferay.portal.db.partition.internal.configuration.DBPartitionInsertVirtualInstanceConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE, enabled = false,
	service = {}
)
public class DBPartitionInsertVirtualInstanceOperation
	extends BaseVirtualInstanceOperation {

	@Activate
	protected void activate(Map<String, Object> properties) {
		onVirtualInstance(
			() -> {
				_log.info("DBPartitionInsertVirtualInstanceOperation activate");

				DBPartitionInsertVirtualInstanceConfiguration
					dBPartitionInsertVirtualInstanceConfiguration =
						ConfigurableUtil.createConfigurable(
							DBPartitionInsertVirtualInstanceConfiguration.class,
							properties);

				long companyId =
					dBPartitionInsertVirtualInstanceConfiguration.
						partitionCompanyId();

				_log.info("DBPartitionInsertVirtualInstanceOperation companyId " + companyId);

				if (_companyLocalService.fetchCompany(companyId) != null) {

					_log.info("DBPartitionInsertVirtualInstanceOperation null ");
					return null;
				}

				Company company = _companyLocalService.addDBPartitionCompany(
					companyId,
					dBPartitionInsertVirtualInstanceConfiguration.newName(),
					dBPartitionInsertVirtualInstanceConfiguration.
						newVirtualHostname(),
					dBPartitionInsertVirtualInstanceConfiguration.newWebId());

				_log.info("DBPartitionInsertVirtualInstanceOperation added ");

				_portalInstancesLocalService.synchronizePortalInstances();

				_log.info("DBPartitionInsertVirtualInstanceOperation sync ");

				return company;
			},
			properties);
	}

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private PortalInstancesLocalService _portalInstancesLocalService;

	private static final Log _log = LogFactoryUtil.getLog(
			DBPartitionInsertVirtualInstanceOperation.class);

}