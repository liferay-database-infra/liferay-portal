/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.dispatch.internal.messaging.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.scheduler.SchedulerEngineHelper;
import com.liferay.portal.kernel.scheduler.SchedulerException;
import com.liferay.portal.kernel.scheduler.StorageType;
import com.liferay.portal.kernel.scheduler.messaging.SchedulerResponse;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Luis Ortiz
 */
@RunWith(Arquillian.class)
public class SchedulerResponseTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testStartupScheduleJobCompanyId() throws SchedulerException {
		SchedulerResponse schedulerResponse =
			_schedulerEngineHelper.getScheduledJob(
				_STARTUP_LISTENER_CLASS, _STARTUP_LISTENER_CLASS,
				StorageType.MEMORY_CLUSTERED);

		Message message = schedulerResponse.getMessage();

		Assert.assertEquals(
			CompanyConstants.SYSTEM, message.getLong("companyId"));
	}

	private static final String _STARTUP_LISTENER_CLASS =
		"com.liferay.journal.web.internal.messaging." +
			"CheckArticleMessageListener";

	@Inject
	private SchedulerEngineHelper _schedulerEngineHelper;

}