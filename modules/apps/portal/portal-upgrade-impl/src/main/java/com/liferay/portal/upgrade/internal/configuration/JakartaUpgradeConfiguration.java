/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Luis Ortiz
 */
@ExtendedObjectClassDefinition(category = "upgrades")
@Meta.OCD(
	description = "jakarta-upgrade-configuration-description",
	id = "com.liferay.portal.upgrade.internal.configuration.JakartaUpgradeConfiguration",
	localization = "content/Language",
	name = "jakarta-upgrade-configuration-name"
)
public interface JakartaUpgradeConfiguration {

	@Meta.AD(name = "custom-separators", required = false)
	public String[] customSeparators();

	@Meta.AD(name = "table-column-names")
	public String[] tableAndColumnNames();

}