/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.object.validation.rule;

import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.object.validation.rule.ObjectValidationRuleEngine;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Crescenzo Rega
 */
@Component(service = ObjectValidationRuleEngine.class)
public class CommerceReturnCommerceOrderStatusObjectValidationRuleEngineImpl
	extends BaseObjectValidationRuleEngineImpl {

	@Override
	protected String getObjectDefinitionName() {
		return "CommerceReturn";
	}

	@Override
	protected String getObjectFieldName() {
		return "commerceOrderStatus";
	}

	@Override
	protected boolean hasValidationCriteriaMet(
		Map<String, Object> inputObjects) {

		Map<String, Object> entryDTO = (Map<String, Object>)inputObjects.get(
			"entryDTO");

		Map<String, Object> properties = (Map<String, Object>)entryDTO.get(
			"properties");

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.fetchCommerceOrder(
				GetterUtil.getLong(
					properties.get(
						"r_commerceOrderToCommerceReturns_commerceOrderId")));

		if (commerceOrder == null) {
			return false;
		}

		if (commerceOrder.getOrderStatus() ==
				CommerceOrderConstants.ORDER_STATUS_COMPLETED) {

			return true;
		}

		return false;
	}

	@Reference
	private CommerceOrderLocalService _commerceOrderLocalService;

}