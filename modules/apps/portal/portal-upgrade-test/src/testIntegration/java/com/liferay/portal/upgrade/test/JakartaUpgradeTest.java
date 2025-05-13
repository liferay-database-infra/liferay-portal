/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.util.PropsValues;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.felix.cm.PersistenceManager;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Luis Ortiz
 */
@RunWith(Arquillian.class)
public class JakartaUpgradeTest extends BaseJakartaUpgradeTestCase {

	@Test
	public void testUpgradeWithCustomSeparators() throws Exception {
		initialString = "import javax$portlet$Portlet";
		resultString = "import jakarta$portlet$Portlet";

		insertInitialData();

		testUpgrade(
			() -> _deployConfigurationFile(
				_PID,
				StringBundler.concat(
					"tableAndColumnNames=[ \\\n\"", COLUMN_NAME_1, "@",
					TABLE_NAME, "\", \\\n\"", COLUMN_NAME_2, "@", TABLE_NAME,
					"\", \\\n\"", COLUMN_NAME_3, "@", TABLE_NAME,
					"\", \\\n]\ncustomSeparators=[ \\\n\"$\", \\\n]")));

		_assertConfigurationFileIsDeletedAfterDeploy(_PID);
	}

	@Test
	public void testUpgradeWithoutCustomSeparators() throws Exception {
		initialString = "import javax.portlet.Portlet";
		resultString = "import jakarta.portlet.Portlet";

		insertInitialData();

		testUpgrade(
			() -> _deployConfigurationFile(
				_PID,
				StringBundler.concat(
					"tableAndColumnNames=[ \\\n\"", COLUMN_NAME_1, "@",
					TABLE_NAME, "\", \\\n\"", COLUMN_NAME_2, "@", TABLE_NAME,
					"\", \\\n\"", COLUMN_NAME_3, "@", TABLE_NAME,
					"\", \\\n]")));

		_assertConfigurationFileIsDeletedAfterDeploy(_PID);
	}

	private void _assertConfigurationFileIsDeletedAfterDeploy(String pid)
		throws Exception {

		Assert.assertNull(
			_configurationAdmin.listConfigurations(
				"(service.pid=" + pid + ")"));
		Assert.assertFalse(Files.exists(_configurationPath));
		Assert.assertNull(
			ReflectionTestUtil.invoke(
				_persistenceManager, "_getDictionary",
				new Class<?>[] {String.class}, pid));
	}

	private Configuration _createConfigurationFile(
			String configurationPid, String content)
		throws Exception {

		return ConfigurationTestUtil.updateConfiguration(
			configurationPid,
			() -> Files.write(
				_configurationPath,
				content.getBytes(Charset.defaultCharset())));
	}

	private void _deployConfigurationFile(String pid, String content)
		throws Exception {

		Assert.assertNull(
			_configurationAdmin.listConfigurations(
				"(service.pid=" + pid + ")"));

		_configurationPath = Paths.get(
			PropsValues.MODULE_FRAMEWORK_CONFIGS_DIR, pid.concat(".config"));

		try (AutoCloseable autoCloseable =
				_registerOnAfterDeleteConfigurationModelListener(pid)) {

			_createConfigurationFile(pid, content);

			Assert.assertNotNull(
				_configurationAdmin.listConfigurations(
					"(service.pid=" + pid + ")"));

			_countDownLatch.await(180, TimeUnit.SECONDS);
		}
	}

	private AutoCloseable _registerOnAfterDeleteConfigurationModelListener(
		String pid) {

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		_countDownLatch = new CountDownLatch(1);

		ServiceRegistration<?> serviceRegistration =
			bundleContext.registerService(
				ConfigurationModelListener.class,
				new ConfigurationModelListener() {

					@Override
					public void onAfterDelete(String pid) {
						_countDownLatch.countDown();
					}

				},
				HashMapDictionaryBuilder.put(
					"model.class.name", pid
				).build());

		return serviceRegistration::unregister;
	}

	private static final String _PID =
		"com.liferay.portal.upgrade.internal.configuration." +
			"JakartaUpgradeConfiguration";

	@Inject
	private ConfigurationAdmin _configurationAdmin;

	private Path _configurationPath;
	private CountDownLatch _countDownLatch;

	@Inject
	private PersistenceManager _persistenceManager;

}