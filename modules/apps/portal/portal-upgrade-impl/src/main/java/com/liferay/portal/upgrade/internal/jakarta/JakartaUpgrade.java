/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.internal.jakarta;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upgrade.BaseJakartaUpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.upgrade.internal.configuration.JakartaUpgradeConfiguration;
import com.liferay.portal.util.PropsValues;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Map;

import org.osgi.framework.Constants;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luis Ortiz
 */
@Component(
	configurationPid = "com.liferay.portal.upgrade.internal.configuration.JakartaUpgradeConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE, service = {}
)
public class JakartaUpgrade {

	@Activate
	protected void activate(Map<String, Object> properties) {
		try {
			JakartaUpgradeConfiguration jakartaUpgradeConfiguration =
				ConfigurableUtil.createConfigurable(
					JakartaUpgradeConfiguration.class, properties);

			char[] customSeparators = _getCustomSeparators(
				jakartaUpgradeConfiguration);

			String[][] tableAndColumnNames = _getTableAndColumnNames(
				jakartaUpgradeConfiguration);

			UpgradeProcess upgradeProcess = new BaseJakartaUpgradeProcess() {

				@Override
				protected char[] getCustomSeparators() {
					return customSeparators;
				}

				@Override
				protected String[][] getTableAndColumnNames() {
					return tableAndColumnNames;
				}

			};

			upgradeProcess.upgrade();
		}
		catch (Exception exception) {
			_log.error("Unable to upgrade Jakarta references: ", exception);
		}
		finally {
			_deleteConfiguration((String)properties.get("service.pid"));
		}
	}

	private void _deleteConfiguration(String pid) {
		try {
			Path path = Paths.get(
				PropsValues.MODULE_FRAMEWORK_CONFIGS_DIR,
				pid.concat(".config"));

			if (Files.exists(path)) {
				Files.delete(path);
			}
			else {
				Configuration[] configurations =
					_configurationAdmin.listConfigurations(
						StringBundler.concat(
							"(", Constants.SERVICE_PID, StringPool.EQUAL, pid,
							"*)"));

				if (configurations == null) {
					return;
				}

				for (Configuration configuration : configurations) {
					configuration.delete();
				}
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	private char[] _getCustomSeparators(
			JakartaUpgradeConfiguration jakartaUpgradeConfiguration)
		throws Exception {

		if ((jakartaUpgradeConfiguration.customSeparators() == null) ||
			(jakartaUpgradeConfiguration.customSeparators().length == 0)) {

			return new char[0];
		}

		char[] separators =
			new char[jakartaUpgradeConfiguration.customSeparators().length];

		int i = 0;

		for (String separator :
				jakartaUpgradeConfiguration.customSeparators()) {

			if (separator.length() != 1) {
				throw new Exception(
					"All custom separators must be one single character");
			}

			separators[i++] = separator.charAt(0);
		}

		return separators;
	}

	private String[][] _getTableAndColumnNames(
			JakartaUpgradeConfiguration jakartaUpgradeConfiguration)
		throws Exception {

		String[][] tableAndColumnNames =
			new String
				[jakartaUpgradeConfiguration.tableAndColumnNames().length][2];

		int row = 0;

		for (String tableAndColumnName :
				jakartaUpgradeConfiguration.tableAndColumnNames()) {

			int index = tableAndColumnName.indexOf(StringPool.AT);

			if (index == -1) {
				throw new Exception(
					"Invalid table and column format. Expected format " +
						"{columnName}@{tableName}.");
			}

			tableAndColumnNames[row][0] = tableAndColumnName.substring(
				index + 1);

			tableAndColumnNames[row][1] = tableAndColumnName.substring(
				0, index);

			row++;
		}

		return tableAndColumnNames;
	}

	private static final Log _log = LogFactoryUtil.getLog(JakartaUpgrade.class);

	@Reference
	private ConfigurationAdmin _configurationAdmin;

}