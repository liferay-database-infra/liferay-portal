/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.object.validation.rule;

import com.liferay.object.validation.rule.ObjectValidationRuleEngine;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;

import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Crescenzo Rega
 */
@Component(service = ObjectValidationRuleEngine.class)
public class CommerceReturnCommerceOrderIdObjectValidationRuleEngineImpl
	extends BaseObjectValidationRuleEngineImpl {

	@Override
	protected String getObjectDefinitionName() {
		return "CommerceReturn";
	}

	@Override
	protected String getObjectFieldName() {
		return "commerceOrderId";
	}

	@Override
	protected boolean hasValidationCriteriaMet(
		Map<String, Object> inputObjects) {

		Map<String, Object> originalEntryDTO =
			(Map<String, Object>)inputObjects.get("originalEntryDTO");

		if (MapUtil.isEmpty(originalEntryDTO)) {
			return true;
		}

		Map<String, Object> entryDTO = (Map<String, Object>)inputObjects.get(
			"entryDTO");

		Map<String, Object> properties = (Map<String, Object>)entryDTO.get(
			"properties");

		long commerceOrderId = GetterUtil.getLong(
			properties.get("r_commerceOrderToCommerceReturns_commerceOrderId"));

		Map<String, Object> originalProperties =
			(Map<String, Object>)originalEntryDTO.get("properties");

		long originalCommerceOrderId = GetterUtil.getLong(
			originalProperties.get(
				"r_commerceOrderToCommerceReturns_commerceOrderId"));

		if (commerceOrderId == originalCommerceOrderId) {
			return true;
		}

		return false;
	}

}