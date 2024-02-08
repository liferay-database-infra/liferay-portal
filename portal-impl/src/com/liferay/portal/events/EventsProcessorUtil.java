/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.events;

import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.events.LifecycleAction;
import com.liferay.portal.kernel.events.LifecycleEvent;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author Brian Wing Shun Chan
 * @author Michael Young
 * @author Raymond Augé
 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
 *             com.liferay.portal.kernel.events.EventsProcessorUtil}
 */
@Deprecated
public class EventsProcessorUtil {

	public static void process(String key, String[] classes)
		throws ActionException {

		com.liferay.portal.kernel.events.EventsProcessorUtil.process(
			key, classes);
	}

	public static void process(
			String key, String[] classes, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws ActionException {

		com.liferay.portal.kernel.events.EventsProcessorUtil.process(
			key, classes, httpServletRequest, httpServletResponse);
	}

	public static void process(
			String key, String[] classes, HttpSession httpSession)
		throws ActionException {

		com.liferay.portal.kernel.events.EventsProcessorUtil.process(
			key, classes, httpSession);
	}

	public static void process(
			String key, String[] classes, LifecycleEvent lifecycleEvent)
		throws ActionException {

		com.liferay.portal.kernel.events.EventsProcessorUtil.process(
			key, classes, lifecycleEvent);
	}

	public static void process(String key, String[] classes, String[] ids)
		throws ActionException {

		com.liferay.portal.kernel.events.EventsProcessorUtil.process(
			key, classes, ids);
	}

	public static void processEvent(
			LifecycleAction lifecycleAction, LifecycleEvent lifecycleEvent)
		throws ActionException {

		com.liferay.portal.kernel.events.EventsProcessorUtil.processEvent(
			lifecycleAction, lifecycleEvent);
	}

	protected EventsProcessorUtil() {
	}

}