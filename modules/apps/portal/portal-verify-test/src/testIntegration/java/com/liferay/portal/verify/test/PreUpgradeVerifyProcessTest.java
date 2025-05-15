/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.verify.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.verify.PreUpgradeVerifyProperties;
import com.liferay.portal.verify.VerifyException;
import com.liferay.portal.verify.VerifyProperties;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author István András Dézsi
 */
@RunWith(Arquillian.class)
public class PreUpgradeVerifyProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testPreUpgradeVerifyProperties() throws Exception {
		String migratedPortalKey = getFirstPortalPropertyKey();

		String[][] originalMigratedPortalKeys = _setPropertyKeys(
			"_MIGRATED_PORTAL_KEYS",
			new String[][] {{migratedPortalKey, migratedPortalKey}});

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				VerifyProperties.class.getName(), LoggerTestUtil.ERROR)) {

			PreUpgradeVerifyProperties preUpgradeVerifyProperties =
				new PreUpgradeVerifyProperties();

			try {
				preUpgradeVerifyProperties.verify();
			}
			catch (VerifyException verifyException) {
			}

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 2, logEntries.size());

			LogEntry logEntry1 = logEntries.get(0);

			LogEntry logEntry2 = logEntries.get(1);

			Assert.assertEquals(
				StringBundler.concat(
					"Portal property \"", migratedPortalKey,
					"\" was migrated to the system property \"",
					migratedPortalKey, "\""),
				logEntry1.getMessage());

			Assert.assertEquals(
				StringBundler.concat(
					"Stopping the server due to incorrect use of migrated ",
					"portal properties [", migratedPortalKey, "]"),
				logEntry2.getMessage());
		}
		finally {
			_setPropertyKeys(
				"_MIGRATED_PORTAL_KEYS", originalMigratedPortalKeys);
		}
	}

	protected String getFirstPortalPropertyKey() {
		Properties portalProperties = ReflectionTestUtil.invoke(
			VerifyProperties.class, "loadPortalProperties", new Class<?>[0]);

		Set<String> propertyNames = portalProperties.stringPropertyNames();

		Assert.assertFalse(propertyNames.toString(), propertyNames.isEmpty());

		Iterator<String> iterator = propertyNames.iterator();

		return iterator.next();
	}

	protected String getFirstSystemPropertyKey() {
		Set<String> propertyNames = SystemProperties.getPropertyNames();

		Assert.assertFalse(propertyNames.toString(), propertyNames.isEmpty());

		Iterator<String> iterator = propertyNames.iterator();

		return iterator.next();
	}

	private <T> T _setPropertyKeys(String fieldName, T value) {
		T orignalValue = ReflectionTestUtil.getFieldValue(
			VerifyProperties.class, fieldName);

		ReflectionTestUtil.setFieldValue(
			VerifyProperties.class, fieldName, value);

		return orignalValue;
	}

}