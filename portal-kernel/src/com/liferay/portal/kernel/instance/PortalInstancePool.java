/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.instance;

import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.util.PortalInstances;

/**
 * @author Tina Tian
 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link PortalInstances}
 */
@Deprecated
public class PortalInstancePool {

	public static void add(Company company) {
		PortalInstances.add(company);
	}

	public static long[] getCompanyIds() {
		return PortalInstances.getCompanyIds();
	}

	public static long getDefaultCompanyId() {
		return PortalInstances.getDefaultCompanyId();
	}

	public static String getWebId(long companyId) {
		return PortalInstances.getWebId(companyId);
	}

	public static String[] getWebIds() {
		return PortalInstances.getWebIds();
	}

	public static void remove(long companyId) {
		PortalInstances.remove(companyId);
	}

}