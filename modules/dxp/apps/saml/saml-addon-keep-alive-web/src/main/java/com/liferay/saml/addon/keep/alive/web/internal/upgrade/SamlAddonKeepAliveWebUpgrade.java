/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */

package com.liferay.saml.addon.keep.alive.web.internal.upgrade;

import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.osgi.service.component.annotations.Component;

/**
 * @author Carlos Sierra Andrés
 */
@Component(immediate = true, service = UpgradeStepRegistrator.class)
public class SamlAddonKeepAliveWebUpgrade implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.register(
			"0.0.0", "1.0.0",
			UpgradeProcessFactory.runSQL(
				"delete from Portlet where portletId like " +
					"'%1_WAR_samlportlet%'"),
			UpgradeProcessFactory.runSQL(
				"delete from PortletPreferences where portletId like " +
					"'%1_WAR_samlportlet%'"),
			UpgradeProcessFactory.runSQL(
				"delete from ResourceAction where name like " +
					"'%1_WAR_samlportlet%'"),
			UpgradeProcessFactory.runSQL(
				"delete from ResourcePermission where name like " +
					"'%1_WAR_samlportlet%'"));
	}

}