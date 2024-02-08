/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.util;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.exception.NoSuchVirtualHostException;
import com.liferay.portal.kernel.model.Company;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Brian Wing Shun Chan
 * @author Jose Oliver
 * @author Atul Patel
 * @author Mika Koivisto
 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link com.liferay.portal.kernel.util.PortalInstances}
 */
@Deprecated
public class PortalInstances {

	public static long getCompanyId(HttpServletRequest httpServletRequest) {
		return com.liferay.portal.kernel.util.PortalInstances.getCompanyId(
			httpServletRequest);
	}

	public static long getCompanyId(
			HttpServletRequest httpServletRequest, boolean strict)
		throws NoSuchVirtualHostException {

		return com.liferay.portal.kernel.util.PortalInstances.getCompanyId(
			httpServletRequest, strict);
	}

	public static long[] getCompanyIds() {
		return com.liferay.portal.kernel.util.PortalInstances.getCompanyIds();
	}

	public static long[] getCompanyIdsBySQL() throws SQLException {
		return com.liferay.portal.kernel.util.PortalInstances.
			getCompanyIdsBySQL();
	}

	public static long getDefaultCompanyId() {
		return com.liferay.portal.kernel.util.PortalInstances.
			getDefaultCompanyId();
	}

	public static long getDefaultCompanyIdBySQL() throws SQLException {
		return com.liferay.portal.kernel.util.PortalInstances.
			getDefaultCompanyIdBySQL();
	}

	public static String[] getWebIds() {
		return com.liferay.portal.kernel.util.PortalInstances.getWebIds();
	}

	public static long initCompany(Company company) {
		return com.liferay.portal.kernel.util.PortalInstances.initCompany(
			company);
	}

	public static long initCompany(Company company, boolean skipCheck) {
		return com.liferay.portal.kernel.util.PortalInstances.initCompany(
			company, skipCheck);
	}

	public static boolean isAutoLoginIgnoreHost(String host) {
		return com.liferay.portal.kernel.util.PortalInstances.
			isAutoLoginIgnoreHost(host);
	}

	public static boolean isAutoLoginIgnorePath(String path) {
		return com.liferay.portal.kernel.util.PortalInstances.
			isAutoLoginIgnorePath(path);
	}

	public static boolean isCompanyActive(long companyId) {
		return com.liferay.portal.kernel.util.PortalInstances.isCompanyActive(
			companyId);
	}

	public static boolean isCompanyInDeletionProcess(long companyId) {
		return com.liferay.portal.kernel.util.PortalInstances.
			isCompanyInDeletionProcess(companyId);
	}

	public static boolean isCurrentCompanyInDeletionProcess() {
		return com.liferay.portal.kernel.util.PortalInstances.
			isCurrentCompanyInDeletionProcess();
	}

	public static boolean isVirtualHostsIgnoreHost(String host) {
		return com.liferay.portal.kernel.util.PortalInstances.
			isVirtualHostsIgnoreHost(host);
	}

	public static boolean isVirtualHostsIgnorePath(String path) {
		return com.liferay.portal.kernel.util.PortalInstances.
			isVirtualHostsIgnorePath(path);
	}

	public static void removeCompany(long companyId) {
		com.liferay.portal.kernel.util.PortalInstances.remove(companyId);
	}

	public static SafeCloseable setCompanyInDeletionProcess(long companyId) {
		return com.liferay.portal.kernel.util.PortalInstances.
			setCompanyInDeletionProcess(companyId);
	}

}