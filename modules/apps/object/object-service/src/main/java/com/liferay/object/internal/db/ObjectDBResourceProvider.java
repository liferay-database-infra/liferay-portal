/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.db;

import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.petra.sql.dsl.DynamicObjectDefinitionLocalizationTable;
import com.liferay.object.petra.sql.dsl.DynamicObjectDefinitionLocalizationTableFactory;
import com.liferay.object.petra.sql.dsl.DynamicObjectDefinitionTable;
import com.liferay.object.petra.sql.dsl.DynamicObjectDefinitionTableFactory;
import com.liferay.object.relationship.util.ObjectRelationshipUtil;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectFieldLocalServiceUtil;
import com.liferay.object.service.ObjectRelationshipLocalServiceUtil;
import com.liferay.portal.db.DBResourceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Mariano Álvaro Sáiz
 */
@Component(service = DBResourceUtil.DBResourceProvider.class)
public class ObjectDBResourceProvider
	implements DBResourceUtil.DBResourceProvider {

	@Override
	public Map<String, String[]> getTablesPrimaryKeyColumnNames(long companyId)
		throws PortalException {

		Map<String, String[]> objectTablesPrimaryKeyColumnNames =
			new HashMap<>();

		List<ObjectDefinition> objectDefinitions =
			ObjectDefinitionLocalServiceUtil.getObjectDefinitions(
				companyId, WorkflowConstants.STATUS_APPROVED);

		for (ObjectDefinition objectDefinition : objectDefinitions) {
			DynamicObjectDefinitionLocalizationTable
				dynamicObjectDefinitionLocalizationTable =
					DynamicObjectDefinitionLocalizationTableFactory.create(
						objectDefinition,
						ObjectFieldLocalServiceUtil.getService());

			if (dynamicObjectDefinitionLocalizationTable != null) {
				objectTablesPrimaryKeyColumnNames.put(
					objectDefinition.getLocalizationDBTableName(),
					dynamicObjectDefinitionLocalizationTable.
						getPrimaryKeyColumnNames());
			}

			if (!objectDefinition.isUnmodifiableSystemObject()) {
				DynamicObjectDefinitionTable dynamicObjectDefinitionTable =
					DynamicObjectDefinitionTableFactory.create(
						objectDefinition,
						ObjectFieldLocalServiceUtil.getService());

				objectTablesPrimaryKeyColumnNames.put(
					dynamicObjectDefinitionTable.getTableName(),
					new String[] {
						dynamicObjectDefinitionTable.getPrimaryKeyColumnName()
					});
			}

			DynamicObjectDefinitionTable dynamicObjectDefinitionTable =
				DynamicObjectDefinitionTableFactory.createExtension(
					objectDefinition, ObjectFieldLocalServiceUtil.getService());

			objectTablesPrimaryKeyColumnNames.put(
				dynamicObjectDefinitionTable.getTableName(),
				new String[] {
					dynamicObjectDefinitionTable.getPrimaryKeyColumnName()
				});

			objectTablesPrimaryKeyColumnNames.putAll(
				_getObjectRelationshipTablesPrimaryKeyColumnNames(
					objectDefinition));
		}

		return objectTablesPrimaryKeyColumnNames;
	}

	private Map<String, String[]>
			_getObjectRelationshipTablesPrimaryKeyColumnNames(
				ObjectDefinition objectDefinition)
		throws PortalException {

		Map<String, String[]> objectRelationshipTablesPrimaryKeyColumnNames =
			new HashMap<>();

		List<ObjectRelationship> objectRelationships =
			ObjectRelationshipLocalServiceUtil.getAllObjectRelationships(
				objectDefinition.getObjectDefinitionId());

		for (ObjectRelationship objectRelationship : objectRelationships) {
			if (!StringUtil.equalsIgnoreCase(
					objectRelationship.getType(),
					ObjectRelationshipConstants.TYPE_MANY_TO_MANY)) {

				continue;
			}

			Map<String, String> pkObjectFieldDBColumnNames =
				ObjectRelationshipUtil.getPKObjectFieldDBColumnNames(
					ObjectDefinitionLocalServiceUtil.getObjectDefinition(
						objectRelationship.getObjectDefinitionId1()),
					ObjectDefinitionLocalServiceUtil.getObjectDefinition(
						objectRelationship.getObjectDefinitionId2()),
					false);

			String pkObjectFieldDBColumnName1 = pkObjectFieldDBColumnNames.get(
				"pkObjectFieldDBColumnName1");
			String pkObjectFieldDBColumnName2 = pkObjectFieldDBColumnNames.get(
				"pkObjectFieldDBColumnName2");

			objectRelationshipTablesPrimaryKeyColumnNames.put(
				objectRelationship.getDBTableName(),
				new String[] {
					pkObjectFieldDBColumnName1, pkObjectFieldDBColumnName2
				});
		}

		return objectRelationshipTablesPrimaryKeyColumnNames;
	}

}