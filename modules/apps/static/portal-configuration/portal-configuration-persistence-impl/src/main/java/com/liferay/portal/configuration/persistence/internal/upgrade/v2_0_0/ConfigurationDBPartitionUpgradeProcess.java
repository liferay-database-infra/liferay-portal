/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.configuration.persistence.internal.upgrade.v2_0_0;

import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.db.partition.util.DBPartitionUtil;
import com.liferay.portal.kernel.db.partition.DBPartition;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.GetterUtil;

import java.io.Serializable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Dictionary;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.felix.cm.file.ConfigurationHandler;

/**
 * @author Luis Ortiz
 */
public class ConfigurationDBPartitionUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {

		System.out.println("Entering upgrade process for company " + CompanyThreadLocal.getCompanyId());

		if (PortalInstancePool.getDefaultCompanyId() ==
				CompanyThreadLocal.getCompanyId()) {

			try (PreparedStatement preparedStatement =
					connection.prepareStatement(
						"select configurationId, dictionary from " +
							"Configuration_");
				ResultSet resultSet = preparedStatement.executeQuery()) {

				while (resultSet.next()) {
					ScopeConfiguration scopeConfiguration =
						_getScopeConfiguration(
							resultSet.getString(1), resultSet.getString(2));

					System.out.println("There is one configuration in company " + CompanyThreadLocal.getCompanyId() + " to process");

					if (scopeConfiguration != null) {

						System.out.println("The configuration is scoped");

						if (Objects.equals(
								scopeConfiguration.getScope(),
								ExtendedObjectClassDefinition.Scope.
									PORTLET_INSTANCE)) {

							System.out.println("Inserting configuration because is PORLTLET_INSTANCE scoped");

							_scopeConfigurations.add(scopeConfiguration);

							continue;
						}

						if (!_isApplicable(
								scopeConfiguration,
								PortalInstancePool.getDefaultCompanyId())) {

							System.out.println("Inserting configuration is not applicable to the current company");

							_scopeConfigurations.add(scopeConfiguration);

							_removeConfiguration(
								scopeConfiguration.getConfigurationId());
						}
					}
				}
			}

			long[] companyIds = PortalInstancePool.getCompanyIds();

			System.out.println("There are " + (companyIds.length - 1) + " companies to process");

			_atomicInteger.set(companyIds.length - 1);

			System.out.println("Finished upgrade for company " + CompanyThreadLocal.getCompanyId());

			return;
		}

		DBPartitionUtil.replaceByTable(connection, false, "Configuration_");

		for (ScopeConfiguration scopeConfiguration : _scopeConfigurations) {
			if (_isApplicable(
					scopeConfiguration, CompanyThreadLocal.getCompanyId())) {

				_insertConfiguration(
					scopeConfiguration.getConfigurationId(),
					scopeConfiguration.getDictionary());

				if (!Objects.equals(
						scopeConfiguration.getScope(),
						ExtendedObjectClassDefinition.Scope.PORTLET_INSTANCE)) {

					System.out.println("Removed scoped configuration due to company " + CompanyThreadLocal.getCompanyId());

					_scopeConfigurations.remove(scopeConfiguration);
				}
			}
		}

		int remainingCompanies = _atomicInteger.decrementAndGet();

		System.out.println("There are " + remainingCompanies + " pending to process");

		if (remainingCompanies == 0) {
			System.out.println("There are remaining scoped configurations to remove: " + _scopeConfigurations.size());
		}

		if ((remainingCompanies == 0) && _log.isWarnEnabled()) {
			System.out.println("Entering to removal section");

			for (ScopeConfiguration scopeConfiguration : _scopeConfigurations) {
				if (Objects.equals(
						scopeConfiguration.getScope(),
						ExtendedObjectClassDefinition.Scope.COMPANY)) {

					System.out.println("Removed company scope configuration with ID " + scopeConfiguration.getConfigurationId() + " because the company ID " + scopeConfiguration.getScopePK() + " does not exist");

					_log.warn(
						StringBundler.concat(
							"Company scope configuration with ID ",
							scopeConfiguration.getConfigurationId(),
							" has been removed because the company ID ",
							scopeConfiguration.getScopePK(),
							" does not exist"));
				}

				if (Objects.equals(
						scopeConfiguration.getScope(),
						ExtendedObjectClassDefinition.Scope.GROUP)) {

					System.out.println("Removed group scope configuration with ID " + scopeConfiguration.getConfigurationId() + " because the group ID " + scopeConfiguration.getScopePK() + " does not exist");

					_log.warn(
						StringBundler.concat(
							"Group scope configuration with ID ",
							scopeConfiguration.getConfigurationId(),
							" has been removed because the group ID ",
							scopeConfiguration.getScopePK(),
							" does not exist"));
				}
			}

			_scopeConfigurations.clear();
		}

		System.out.println("Finished upgrade for company " + CompanyThreadLocal.getCompanyId());
	}

	@Override
	protected boolean isSkipUpgradeProcess() {
		if (!DBPartition.isPartitionEnabled()) {
			return true;
		}

		return false;
	}

	private ScopeConfiguration _getScopeConfiguration(
			String configurationId, String dictionary)
		throws Exception {

		Dictionary<String, String> dictionaryMap = ConfigurationHandler.read(
			new UnsyncByteArrayInputStream(
				dictionary.getBytes(StringPool.UTF8)));

		Object value = dictionaryMap.get(
			ExtendedObjectClassDefinition.Scope.COMPANY.getPropertyKey());

		if (value != null) {
			return new ScopeConfiguration(
				configurationId, dictionary, GetterUtil.getLong(value),
				ExtendedObjectClassDefinition.Scope.COMPANY);
		}

		value = dictionaryMap.get(
			ExtendedObjectClassDefinition.Scope.GROUP.getPropertyKey());

		if (value != null) {
			return new ScopeConfiguration(
				configurationId, dictionary, GetterUtil.getLong(value),
				ExtendedObjectClassDefinition.Scope.GROUP);
		}

		value = dictionaryMap.get(
			ExtendedObjectClassDefinition.Scope.PORTLET_INSTANCE.
				getPropertyKey());

		if (value != null) {
			return new ScopeConfiguration(
				configurationId, dictionary, GetterUtil.getString(value),
				ExtendedObjectClassDefinition.Scope.PORTLET_INSTANCE);
		}

		return null;
	}

	private void _insertConfiguration(String configurationId, String dictionary)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"insert into Configuration_ (configurationId, dictionary" +
					") values (?, ?)")) {

			preparedStatement.setString(1, configurationId);
			preparedStatement.setString(2, dictionary);

			preparedStatement.executeUpdate();
		}
	}

	private boolean _isApplicable(
			ScopeConfiguration scopeConfiguration, long companyId)
		throws Exception {

		if (Objects.equals(
				scopeConfiguration.getScope(),
				ExtendedObjectClassDefinition.Scope.COMPANY)) {

			if (companyId == (long)scopeConfiguration.getScopePK()) {
				return true;
			}

			return false;
		}

		if (Objects.equals(
				scopeConfiguration.getScope(),
				ExtendedObjectClassDefinition.Scope.GROUP)) {

			try (PreparedStatement preparedStatement =
					connection.prepareStatement(
						"select groupId from Group_ where groupId = ?")) {

				preparedStatement.setLong(
					1, (long)scopeConfiguration.getScopePK());

				try (ResultSet resultSet = preparedStatement.executeQuery()) {
					if (resultSet.next()) {
						return true;
					}
				}
			}

			return false;
		}

		return true;
	}

	private void _removeConfiguration(String configurationId) throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"delete from Configuration_ where configurationId = ?")) {

			preparedStatement.setString(1, configurationId);

			preparedStatement.executeUpdate();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ConfigurationDBPartitionUpgradeProcess.class);

	private static final AtomicInteger _atomicInteger = new AtomicInteger();
	private static final CopyOnWriteArrayList<ScopeConfiguration>
		_scopeConfigurations = new CopyOnWriteArrayList<>();

	private class ScopeConfiguration {

		public ScopeConfiguration(
			String configurationId, String dictionary, Serializable scopePK,
			ExtendedObjectClassDefinition.Scope scope) {

			_configurationId = configurationId;
			_dictionary = dictionary;
			_scopePK = scopePK;
			_scope = scope;
		}

		public String getConfigurationId() {
			return _configurationId;
		}

		public String getDictionary() {
			return _dictionary;
		}

		public ExtendedObjectClassDefinition.Scope getScope() {
			return _scope;
		}

		public Object getScopePK() {
			return _scopePK;
		}

		private final String _configurationId;
		private final String _dictionary;
		private final ExtendedObjectClassDefinition.Scope _scope;
		private final Object _scopePK;

	}

}