/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.data.cleanup.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.data.cleanup.ConfigurationDataCleanupPreupgradeProcess;

import java.sql.Connection;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author István András Dézsi
 */
@RunWith(Arquillian.class)
public class ConfigurationDataCleanupPreupgradeProcessTest
	extends ConfigurationDataCleanupPreupgradeProcess {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_connection = DataAccess.getConnection();

		_originalCacheEnabled = ReflectionTestUtil.getAndSetFieldValue(
			PortalInstancePool.class, "_cacheEnabled", false);
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		ReflectionTestUtil.setFieldValue(
			PortalInstancePool.class, "_cacheEnabled", _originalCacheEnabled);
	}

	@Test
	public void testUpgradeWithCompanyScopedConfigurations() throws Exception {
		connection = _connection;

		_test(
			TestPropsValues.getCompanyId(), _getNonexistingCompanyId(),
			"companyId", Company.class.getSimpleName());
	}

	@Test
	public void testUpgradeWithGroupScopedConfigurations() throws Exception {
		connection = _connection;

		_test(
			TestPropsValues.getGroupId(), _getNonexistingGroupId(), "groupId",
			Group.class.getSimpleName());
	}

	private long _getNonexistingCompanyId() throws Exception {
		Set<Long> companyIds = SetUtil.fromArray(
			PortalInstancePool.getCompanyIds());
		Random random = new Random();

		while (true) {
			long nonexistingCompanyId = random.nextLong(1, Long.MAX_VALUE);

			if (!companyIds.contains(nonexistingCompanyId)) {
				return nonexistingCompanyId;
			}
		}
	}

	private long _getNonexistingGroupId() throws Exception {
		Set<Long> groupIds = SetUtil.fromArray(getGroupIds());
		Random random = new Random();

		while (true) {
			long nonexistingGroupId = random.nextLong(1, Long.MAX_VALUE);

			if (!groupIds.contains(nonexistingGroupId)) {
				return nonexistingGroupId;
			}
		}
	}

	private void _test(
			long existingEntityId, long nonexistingEntityId, String entityKey,
			String entityName)
		throws Exception {

		String existingConfigurationId = null;
		String nonexistingConfigurationId = null;

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				ConfigurationDataCleanupPreupgradeProcess.class.getName(),
				LoggerTestUtil.INFO)) {

			existingConfigurationId =
				ConfigurationTestUtil.createFactoryConfiguration(
					ConfigurationDataCleanupPreupgradeProcessTest.class.
						getName(),
					HashMapDictionaryBuilder.<String, Object>put(
						entityKey, existingEntityId
					).build());

			nonexistingConfigurationId =
				ConfigurationTestUtil.createFactoryConfiguration(
					ConfigurationDataCleanupPreupgradeProcessTest.class.
						getName(),
					HashMapDictionaryBuilder.<String, Object>put(
						entityKey, nonexistingEntityId
					).build());

			upgrade();

			List<LogEntry> logEntries = logCapture.getLogEntries();

			List<String> logMessages = new ArrayList<>();

			for (LogEntry logEntry : logEntries) {
				logMessages.add(logEntry.getMessage());
			}

			Assert.assertFalse(
				logMessages.contains(
					StringBundler.concat(
						"Deleted configuration ", existingConfigurationId,
						" because of nonexisting ", entityName, ": ",
						existingEntityId)));

			Assert.assertTrue(
				logMessages.contains(
					StringBundler.concat(
						"Deleted configuration ", nonexistingConfigurationId,
						" because of nonexisting ", entityName, ": ",
						nonexistingEntityId)));
		}
		finally {
			ConfigurationTestUtil.deleteConfiguration(existingConfigurationId);
			ConfigurationTestUtil.deleteConfiguration(
				nonexistingConfigurationId);
		}
	}

	private static Connection _connection;
	private static boolean _originalCacheEnabled;

}