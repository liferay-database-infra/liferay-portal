/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.runtime.internal.action.executor;

import com.liferay.osgi.service.tracker.collections.map.ServiceReferenceMapperFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerCustomizerFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerCustomizerFactory.ServiceWrapper;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.ClassUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.workflow.kaleo.definition.ActionType;
import com.liferay.portal.workflow.kaleo.definition.ScriptLanguage;
import com.liferay.portal.workflow.kaleo.model.KaleoAction;
import com.liferay.portal.workflow.kaleo.runtime.ExecutionContext;
import com.liferay.portal.workflow.kaleo.runtime.action.ActionExecutorManager;
import com.liferay.portal.workflow.kaleo.runtime.action.executor.ActionExecutor;

import java.util.List;
import java.util.Objects;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Leonardo Barros
 */
@Component(service = ActionExecutorManager.class)
public class ActionExecutorManagerImpl implements ActionExecutorManager {

	@Override
	public void executeKaleoAction(
			KaleoAction kaleoAction, ExecutionContext executionContext)
		throws PortalException {

		String actionExecutorKey = _getActionExecutorKey(kaleoAction);

		ActionExecutor actionExecutor = null;

		List<ServiceWrapper<ActionExecutor>> actionExecutorServiceWrappers =
			_serviceTrackerMap.getService(actionExecutorKey);

		if (actionExecutorServiceWrappers != null) {
			if (Objects.equals(
					String.valueOf(ScriptLanguage.JAVA),
					kaleoAction.getScriptLanguage())) {

				ActionExecutor innerActionExecutor = null;

				String className = kaleoAction.getScript();

				for (ServiceWrapper<ActionExecutor>
						innerActionExecutorServiceWrapper :
							actionExecutorServiceWrappers) {

					innerActionExecutor =
						innerActionExecutorServiceWrapper.getService();

					if (Objects.equals(
							ClassUtil.getClassName(innerActionExecutor),
							className)) {

						actionExecutor = innerActionExecutor;

						break;
					}
				}
			}
			else {
				actionExecutor = actionExecutorServiceWrappers.get(
					0
				).getService();
			}
		}

		if (actionExecutor == null) {
			throw new PortalException(
				"No action executor for " + actionExecutorKey);
		}

		actionExecutor.execute(kaleoAction, executionContext);
	}

	@Override
	public String[] getFunctionActionExecutorKeys() {
		return TransformUtil.transformToArray(
			_serviceTrackerMap.keySet(),
			key -> {
				if (key.startsWith("function")) {
					List<ServiceWrapper<ActionExecutor>>
						actionExecutorServiceWrappers =
							_serviceTrackerMap.getService(key);

					if (!actionExecutorServiceWrappers.isEmpty()) {
						ServiceWrapper<ActionExecutor>
							actionExecutorServiceWrapper =
								actionExecutorServiceWrappers.get(0);

						long companyId = GetterUtil.getLong(
							actionExecutorServiceWrapper.getProperties(
							).get(
								"companyId"
							));

						if (companyId == CompanyThreadLocal.getCompanyId()) {
							return key;
						}
					}
				}

				return null;
			},
			String.class);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openMultiValueMap(
			bundleContext, ActionExecutor.class, null,
			ServiceReferenceMapperFactory.create(
				bundleContext,
				(actionExecutor, emitter) -> emitter.emit(
					actionExecutor.getActionExecutorKey())),
			ServiceTrackerCustomizerFactory.<ActionExecutor>serviceWrapper(
				bundleContext));
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private String _getActionExecutorKey(KaleoAction kaleoAction) {
		ActionType actionType = ActionType.valueOf(kaleoAction.getType());

		if (Objects.equals(actionType, ActionType.UPDATE_STATUS)) {
			return actionType.name();
		}

		return kaleoAction.getScriptLanguage();
	}

	private ServiceTrackerMap<String, List<ServiceWrapper<ActionExecutor>>>
		_serviceTrackerMap;

}