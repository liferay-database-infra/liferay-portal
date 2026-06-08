/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.db.partition;

import com.liferay.counter.kernel.model.Counter;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.ShardedModel;
import com.liferay.portal.kernel.model.VirtualHost;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsValues;

import java.util.Map;

/**
 * @author Luis Ortiz
 */
public class DBPartition {

	public static long[] getSharedPartitionedModelCompanyIds(
		BaseModel<?> baseModel) {

		if (!PropsValues.DATABASE_PARTITION_ENABLED || (baseModel == null) ||
			CompanyThreadLocal.isInitializingPortalInstance() ||
			CompanyThreadLocal.isLocked() ||
			CompanyThreadLocal.isUpgradingPortalInstance()) {

			return null;
		}

		Class<?> modelClass = baseModel.getModelClass();

		if (!Company.class.isAssignableFrom(modelClass) &&
			!VirtualHost.class.isAssignableFrom(modelClass)) {

			return null;
		}

		Map<String, Object> modelAttributes = baseModel.getModelAttributes();

		long companyId = GetterUtil.getLong(modelAttributes.get("companyId"));

		long defaultCompanyId = PortalInstancePool.getDefaultCompanyId();

		if ((companyId == 0) || (companyId == defaultCompanyId)) {
			return new long[] {defaultCompanyId};
		}

		return new long[] {companyId, defaultCompanyId};
	}

	public static boolean isCurrentCompanyRestricted() {
		if (!PropsValues.DATABASE_PARTITION_ENABLED ||
			CompanyThreadLocal.isInitializingPortalInstance() ||
			CompanyThreadLocal.isUpgradingPortalInstance() ||
			(CompanyThreadLocal.getNonsystemCompanyId() ==
				PortalInstancePool.getDefaultCompanyId())) {

			return false;
		}

		return true;
	}

	public static boolean isPartitionedModel(Class<?> clazz) {
		if (PropsValues.DATABASE_PARTITION_ENABLED &&
			(ClassName.class.isAssignableFrom(clazz) ||
			 Company.class.isAssignableFrom(clazz) ||
			 Counter.class.isAssignableFrom(clazz) ||
			 ResourceAction.class.isAssignableFrom(clazz) ||
			 ShardedModel.class.isAssignableFrom(clazz) ||
			 VirtualHost.class.isAssignableFrom(clazz))) {

			return true;
		}

		return false;
	}

}