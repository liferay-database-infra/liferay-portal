/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.verify.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.verify.PreupgradeVerifyCloudStore;
import com.liferay.portal.verify.VerifyProcess;
import com.liferay.portal.verify.test.util.BaseVerifyProcessTestCase;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.RunWith;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author István András Dézsi
 */
@RunWith(Arquillian.class)
public class PreupgradeVerifyCloudStoreTest extends BaseVerifyProcessTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_dlStoreImplSafeCloseable = PropsValuesTestUtil.swapWithSafeCloseable(
			"DL_STORE_IMPL", _GCS_STORE);

		_configuration = _configurationAdmin.getConfiguration(
			"com.liferay.portal.store.gcs.configuration.GCSStoreConfiguration",
			StringPool.QUESTION);

		ConfigurationTestUtil.saveConfiguration(
			_configuration,
			HashMapDictionaryBuilder.<String, Object>put(
				"aes256Key", ""
			).put(
				"bucketName", "test"
			).put(
				"initialRetryDelay", "400"
			).put(
				"initialRPCTimeout", "120000"
			).put(
				"maxRetryAttempts", "5"
			).put(
				"maxRetryDelay", "10000"
			).put(
				"maxRPCTimeout", "600000"
			).put(
				"retryDelayMultiplier", "1.5"
			).put(
				"retryJitter", "false"
			).put(
				"rpcTimeoutMultiplier", "1.0"
			).put(
				"serviceAccountKey", ""
			).build());
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		ConfigurationTestUtil.deleteConfiguration(_configuration);

		_dlStoreImplSafeCloseable.close();
	}

	@Override
	protected VerifyProcess getVerifyProcess() {
		return new PreupgradeVerifyCloudStore();
	}

	private static final String _GCS_STORE =
		"com.liferay.portal.store.gcs.GCSStore";

	private static Configuration _configuration;

	@Inject
	private static ConfigurationAdmin _configurationAdmin;

	private static SafeCloseable _dlStoreImplSafeCloseable;

}