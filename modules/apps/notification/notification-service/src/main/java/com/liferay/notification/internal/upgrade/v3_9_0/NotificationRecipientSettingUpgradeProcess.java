/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.internal.upgrade.v3_9_0;

import com.liferay.notification.constants.NotificationRecipientSettingConstants;
import com.liferay.notification.internal.upgrade.BaseNotificationRecipientSettingUpgradeProcess;
import com.liferay.petra.string.StringPool;

/**
 * @author Selton Guedes
 */
public class NotificationRecipientSettingUpgradeProcess
	extends BaseNotificationRecipientSettingUpgradeProcess {

	@Override
	protected String getNotificationRecipientSettingName() {
		return NotificationRecipientSettingConstants.NAME_SINGLE_RECIPIENT;
	}

	@Override
	protected String getNotificationRecipientSettingValue() {
		return StringPool.TRUE;
	}

}