/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.upgrade.v2_12_3.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.model.CTMessage;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTMessageLocalService;
import com.liferay.portal.kernel.cache.CacheRegistryUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.messaging.Destination;
import com.liferay.portal.kernel.messaging.DestinationConfiguration;
import com.liferay.portal.kernel.messaging.DestinationFactory;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.DestinationWrapper;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.SubscriptionSender;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author István András Dézsi
 */
@RunWith(Arquillian.class)
public class UpgradeCTMessageCompanyIdTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_ctCollection = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, UpgradeCTMessageCompanyIdTest.class.getSimpleName(), null);

		_testDestination = new TestDestination(
			_destinationFactory.createDestination(
				new DestinationConfiguration(
					DestinationConfiguration.DESTINATION_TYPE_SYNCHRONOUS,
					DestinationNames.SUBSCRIPTION_SENDER)));

		_serviceRegistration = _bundleContext.registerService(
			Destination.class, _testDestination,
			HashMapDictionaryBuilder.<String, Object>put(
				"destination.name", DestinationNames.SUBSCRIPTION_SENDER
			).put(
				"service.ranking", Integer.MAX_VALUE
			).build());

		_upgradeStepRegistrator.register(
			new UpgradeStepRegistrator.Registry() {

				@Override
				public void register(
					String fromSchemaVersionString,
					String toSchemaVersionString, UpgradeStep... upgradeSteps) {

					for (UpgradeStep upgradeStep : upgradeSteps) {
						if (fromSchemaVersionString.equals("2.12.2")) {
							UpgradeProcess upgradeProcess =
								(UpgradeProcess)upgradeStep;

							for (UpgradeStep innerUpgradeStep :
									upgradeProcess.getUpgradeSteps()) {

								_upgradeSteps.add(
									(UpgradeProcess)innerUpgradeStep);
							}
						}
					}
				}

			});
	}

	@After
	public void tearDown() throws Exception {
		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();

			_serviceRegistration = null;
		}
	}

	@Test
	public void testUpgrade() throws Exception {
		long companyId = TestPropsValues.getCompanyId();

		Message message = new Message();

		message.setDestinationName(DestinationNames.SUBSCRIPTION_SENDER);

		SubscriptionSender subscriptionSender = new SubscriptionSender();

		subscriptionSender.setCompanyId(companyId);

		subscriptionSender.setMailId(
			UpgradeCTMessageCompanyIdTest.class.getName(), "test");

		message.setPayload(subscriptionSender);

		long ctMessageId = RandomTestUtil.nextLong();

		CTMessage ctMessage = _ctMessageLocalService.createCTMessage(
			ctMessageId);

		ctMessage.setCompanyId(companyId);

		ctMessage.setCtCollectionId(_ctCollection.getCtCollectionId());

		String messageContent = _jsonFactory.serialize(message);

		Assert.assertTrue(messageContent.contains("\"companyId\""));

		ctMessage.setMessageContent(messageContent);

		_ctMessageLocalService.updateCTMessage(ctMessage);

		for (UpgradeProcess upgradeProcess : _upgradeSteps) {
			upgradeProcess.upgrade();
		}

		CacheRegistryUtil.clear();

		ctMessage = _ctMessageLocalService.getCTMessage(ctMessageId);

		messageContent = ctMessage.getMessageContent();

		Assert.assertFalse(messageContent.contains("\"companyId\""));
	}

	private static final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();

	@Inject
	private static CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private static CTMessageLocalService _ctMessageLocalService;

	@Inject
	private static DestinationFactory _destinationFactory;

	@Inject(
		filter = "(&(component.name=com.liferay.change.tracking.internal.upgrade.registry.ChangeTrackingServiceUpgradeStepRegistrator))"
	)
	private static UpgradeStepRegistrator _upgradeStepRegistrator;

	@DeleteAfterTestRun
	private CTCollection _ctCollection;

	@Inject
	private JSONFactory _jsonFactory;

	private ServiceRegistration<Destination> _serviceRegistration;
	private TestDestination _testDestination;
	private final List<UpgradeProcess> _upgradeSteps = new ArrayList<>();

	private static class TestDestination extends DestinationWrapper {

		public TestDestination(Destination destination) {
			super(destination);
		}

		public Message getReceivedMessage() {
			return _message;
		}

		@Override
		public void send(Message message) {
			_message = message;
		}

		private Message _message;

	}

}