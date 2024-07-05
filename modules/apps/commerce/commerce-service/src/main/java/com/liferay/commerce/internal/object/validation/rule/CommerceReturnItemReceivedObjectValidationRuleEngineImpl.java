/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.object.validation.rule;

import com.liferay.object.validation.rule.ObjectValidationRuleEngine;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.GetterUtil;

import java.math.BigDecimal;

import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Crescenzo Rega
 */
@Component(service = ObjectValidationRuleEngine.class)
public class CommerceReturnItemReceivedObjectValidationRuleEngineImpl
	extends BaseObjectValidationRuleEngineImpl {

	@Override
	protected String getObjectDefinitionName() {
		return "CommerceReturnItem";
	}

	@Override
	protected String getObjectFieldName() {
		return "received";
	}

	@Override
	protected boolean hasValidationCriteriaMet(
		Map<String, Object> inputObjects) {

		Map<String, Object> entryDTO = (Map<String, Object>)inputObjects.get(
			"entryDTO");

		Map<String, Object> properties = (Map<String, Object>)entryDTO.get(
			"properties");

		return BigDecimalUtil.lte(
			BigDecimal.valueOf(GetterUtil.getLong(properties.get("received"))),
			BigDecimal.valueOf(
				GetterUtil.getLong(properties.get("authorized"))));
	}

}