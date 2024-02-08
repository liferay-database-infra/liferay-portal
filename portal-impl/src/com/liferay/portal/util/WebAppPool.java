/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.util;

/**
 * @author Brian Wing Shun Chan
 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
 *      com.liferay.portal.kernel.util.WebAppPool}
 */
@Deprecated
public class WebAppPool {

	public static void clear() {
		com.liferay.portal.kernel.util.WebAppPool.clear();
	}

	public static Object get(Long webAppId, String key) {
		return com.liferay.portal.kernel.util.WebAppPool.get(webAppId, key);
	}

	public static void put(Long webAppId, String key, Object object) {
		com.liferay.portal.kernel.util.WebAppPool.put(webAppId, key, object);
	}

	public static Object remove(Long webAppId, String key) {
		return com.liferay.portal.kernel.util.WebAppPool.remove(webAppId, key);
	}

}