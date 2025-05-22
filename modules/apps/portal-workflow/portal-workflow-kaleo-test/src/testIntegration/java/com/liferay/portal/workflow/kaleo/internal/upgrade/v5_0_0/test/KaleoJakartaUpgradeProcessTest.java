/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.internal.upgrade.v5_0_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.workflow.constants.WorkflowDefinitionConstants;
import com.liferay.portal.workflow.kaleo.definition.Condition;
import com.liferay.portal.workflow.kaleo.definition.Notification;
import com.liferay.portal.workflow.kaleo.definition.ScriptAssignment;
import com.liferay.portal.workflow.kaleo.definition.Task;
import com.liferay.portal.workflow.kaleo.model.KaleoAction;
import com.liferay.portal.workflow.kaleo.model.KaleoCondition;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinition;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinitionVersion;
import com.liferay.portal.workflow.kaleo.model.KaleoInstance;
import com.liferay.portal.workflow.kaleo.model.KaleoNode;
import com.liferay.portal.workflow.kaleo.model.KaleoNotification;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskAssignment;
import com.liferay.portal.workflow.kaleo.service.KaleoActionLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoConditionLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoDefinitionLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoInstanceLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoNodeLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoNotificationLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoTaskAssignmentLocalService;
import com.liferay.portal.workflow.kaleo.service.test.BaseKaleoLocalServiceTestCase;

import java.io.IOException;
import java.io.InputStream;

import java.util.List;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Luis Ortiz
 */
@RunWith(Arquillian.class)
public class KaleoJakartaUpgradeProcessTest
	extends BaseKaleoLocalServiceTestCase {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		super.setUp();

		_upgradeStepRegistrator.register(
			(fromSchemaVersionString, toSchemaVersionString, upgradeSteps) -> {
				for (UpgradeStep upgradeStep : upgradeSteps) {
					Class<?> clazz = upgradeStep.getClass();

					if (Objects.equals(
							clazz.getName(),
							"com.liferay.portal.workflow.kaleo.internal." +
								"upgrade.v5_0_0.KaleoJakartaUpgradeProcess")) {

						_upgradeProcess = (UpgradeProcess)upgradeStep;
					}
				}
			});
	}

	@Test
	public void testUpgrade() throws Exception {
		KaleoAction kaleoAction = null;
		KaleoCondition kaleoCondition = null;
		KaleoDefinition kaleoDefinition = null;
		KaleoInstance kaleoInstance = null;
		KaleoNode kaleoNode = null;
		KaleoNotification kaleoNotification = null;
		KaleoTaskAssignment kaleoTaskAssignment = null;

		try {
			kaleoInstance = addKaleoInstance();

			kaleoNode = addKaleoNode(
				kaleoInstance, new Task("task", StringPool.BLANK));

			kaleoAction = addKaleoAction(kaleoInstance, kaleoNode);

			kaleoAction = kaleoActionLocalService.getKaleoAction(
				kaleoAction.getKaleoActionId());

			kaleoAction.setScript(_JAVAX_SCRIPT);

			kaleoAction = kaleoActionLocalService.updateKaleoAction(
				kaleoAction);

			Condition condition = new Condition(
				RandomTestUtil.randomString(), StringPool.BLANK, _JAVAX_SCRIPT,
				"java", StringPool.BLANK);

			kaleoCondition = _kaleoConditionLocalService.addKaleoCondition(
				kaleoInstance.getKaleoDefinitionId(),
				kaleoInstance.getKaleoDefinitionVersionId(),
				kaleoNode.getKaleoNodeId(), condition, serviceContext);

			kaleoDefinition = _kaleoDefinitionLocalService.addKaleoDefinition(
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				_read("valid-javax-workflow-definition.xml"),
				WorkflowDefinitionConstants.SCOPE_ALL, 1, serviceContext);

			kaleoNotification =
				_kaleoNotificationLocalService.addKaleoNotification(
					KaleoNode.class.getName(), kaleoInstance.getClassPK(),
					kaleoDefinition.getKaleoDefinitionId(),
					kaleoDefinition.getKaleoDefinitionVersions(
					).get(
						0
					).getKaleoDefinitionVersionId(),
					kaleoNode.getName(),
					new Notification(
						StringUtil.randomString(), StringUtil.randomString(),
						"onTimer", _JAVAX_SCRIPT, "freemarker"),
					serviceContext);

			kaleoTaskAssignment =
				_kaleoTaskAssignmentLocalService.addKaleoTaskAssignment(
					KaleoNode.class.getName(), kaleoInstance.getClassPK(),
					kaleoInstance.getKaleoDefinitionId(),
					kaleoInstance.getKaleoDefinitionVersionId(),
					new ScriptAssignment(
						_JAVAX_SCRIPT, "java", RandomTestUtil.randomString()),
					serviceContext);

			_upgradeProcess.upgrade();

			_multiVMPool.clear();

			KaleoAction updatedKaleoAction =
				kaleoActionLocalService.getKaleoAction(
					kaleoAction.getKaleoActionId());

			Assert.assertNotNull(updatedKaleoAction);

			Assert.assertEquals(
				_JAKARTA_SCRIPT, updatedKaleoAction.getScript());

			KaleoCondition updatedKaleoCondition =
				_kaleoConditionLocalService.getKaleoCondition(
					kaleoCondition.getKaleoConditionId());

			Assert.assertNotNull(updatedKaleoCondition);

			Assert.assertEquals(
				_JAKARTA_SCRIPT, updatedKaleoCondition.getScript());

			KaleoDefinition updatedKaleoDefinition =
				_kaleoDefinitionLocalService.getKaleoDefinition(
					kaleoDefinition.getKaleoDefinitionId());

			Assert.assertNotNull(updatedKaleoDefinition);

			Assert.assertTrue(
				updatedKaleoDefinition.getContentAsXML(
				).contains(
					_JAKARTA_SCRIPT
				));

			List<KaleoDefinitionVersion> kaleoDefinitionVersions =
				kaleoDefinition.getKaleoDefinitionVersions();

			Assert.assertEquals(
				kaleoDefinitionVersions.toString(), 1,
				kaleoDefinitionVersions.size());

			Assert.assertTrue(
				kaleoDefinitionVersions.get(
					0
				).getContentAsXML(
				).contains(
					_JAKARTA_SCRIPT
				));

			KaleoNotification updatedKaleoNotification =
				_kaleoNotificationLocalService.getKaleoNotification(
					kaleoNotification.getKaleoNotificationId());

			Assert.assertNotNull(updatedKaleoNotification);

			Assert.assertEquals(
				_JAKARTA_SCRIPT, updatedKaleoNotification.getTemplate());

			KaleoTaskAssignment updatedKaleoTaskAssignment =
				_kaleoTaskAssignmentLocalService.getKaleoTaskAssignment(
					kaleoTaskAssignment.getKaleoTaskAssignmentId());

			Assert.assertNotNull(updatedKaleoTaskAssignment);

			Assert.assertEquals(
				_JAKARTA_SCRIPT,
				updatedKaleoTaskAssignment.getAssigneeScript());
		}
		finally {
			if (kaleoAction != null) {
				_kaleoActionLocalService.deleteKaleoAction(kaleoAction);
			}

			if (kaleoCondition != null) {
				_kaleoConditionLocalService.deleteKaleoCondition(
					kaleoCondition);
			}

			if (kaleoDefinition != null) {
				_kaleoDefinitionLocalService.deleteKaleoDefinition(
					kaleoDefinition);
			}

			if (kaleoNotification != null) {
				_kaleoNotificationLocalService.deleteKaleoNotification(
					kaleoNotification);
			}

			if (kaleoTaskAssignment != null) {
				_kaleoTaskAssignmentLocalService.deleteKaleoTaskAssignment(
					kaleoTaskAssignment);
			}

			if (kaleoInstance != null) {
				_kaleoInstanceLocalService.deleteKaleoInstance(kaleoInstance);
			}

			if (kaleoNode != null) {
				_kaleoNodeLocalService.deleteKaleoNode(kaleoNode);
			}
		}
	}

	private String _read(String name) throws IOException {
		ClassLoader classLoader =
			KaleoJakartaUpgradeProcessTest.class.getClassLoader();

		try (InputStream inputStream = classLoader.getResourceAsStream(
				"com/liferay/portal/workflow/kaleo/dependencies/" + name)) {

			return StringUtil.read(inputStream);
		}
	}

	private static final String _JAKARTA_SCRIPT =
		"import jakarta.servlet.test.KaleoJakartaUpgradeProcessTest;";

	private static final String _JAVAX_SCRIPT =
		"import javax.servlet.test.KaleoJakartaUpgradeProcessTest;";

	@Inject(
		filter = "component.name=com.liferay.portal.workflow.kaleo.internal.upgrade.registry.KaleoServiceUpgradeStepRegistrator"
	)
	private static UpgradeStepRegistrator _upgradeStepRegistrator;

	@Inject
	private KaleoActionLocalService _kaleoActionLocalService;

	@Inject
	private KaleoConditionLocalService _kaleoConditionLocalService;

	@Inject
	private KaleoDefinitionLocalService _kaleoDefinitionLocalService;

	@Inject
	private KaleoInstanceLocalService _kaleoInstanceLocalService;

	@Inject
	private KaleoNodeLocalService _kaleoNodeLocalService;

	@Inject
	private KaleoNotificationLocalService _kaleoNotificationLocalService;

	@Inject
	private KaleoTaskAssignmentLocalService _kaleoTaskAssignmentLocalService;

	@Inject
	private MultiVMPool _multiVMPool;

	private UpgradeProcess _upgradeProcess;

}