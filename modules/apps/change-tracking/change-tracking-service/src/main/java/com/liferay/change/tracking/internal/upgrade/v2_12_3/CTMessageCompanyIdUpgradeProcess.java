/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.upgrade.v2_12_3;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LoggingTimer;

/**
 * @author István András Dézsi
 */
public class CTMessageCompanyIdUpgradeProcess extends UpgradeProcess {

	public CTMessageCompanyIdUpgradeProcess(JSONFactory jsonFactory) {
		_jsonFactory = jsonFactory;
	}

	@Override
	protected void doUpgrade() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			processConcurrently(
				"select ctMessageId, messageContent from CTMessage where " +
					"messageContent like '%companyId%'",
				"update CTMessage set messageContent = ? where ctMessageId = ?",
				resultSet -> new Object[] {
					resultSet.getLong("ctMessageId"),
					GetterUtil.getString(resultSet.getString("messageContent"))
				},
				(values, preparedStatement) -> {
					long ctMessageId = (Long)values[0];

					String messageContent = (String)values[1];

					preparedStatement.setString(
						1, _removeCompanyId(messageContent));

					preparedStatement.setLong(2, ctMessageId);

					preparedStatement.addBatch();
				},
				"Failed to clear companyId from messageContent");
		}
	}

	private JSONObject _removeCompanyId(JSONObject jsonObject) {
		if (jsonObject == null) {
			return null;
		}

		jsonObject.remove("companyId");

		for (String key : jsonObject.keySet()) {
			Object value = jsonObject.get(key);

			if (value instanceof JSONObject) {
				_removeCompanyId((JSONObject)value);
			}
			else if (value instanceof JSONArray) {
				JSONArray jsonArray = (JSONArray)value;

				for (int i = 0; i < jsonArray.length(); i++) {
					Object arrayElement = jsonArray.get(i);

					if (arrayElement instanceof JSONObject) {
						_removeCompanyId((JSONObject)arrayElement);
					}
				}
			}
		}

		return jsonObject;
	}

	private String _removeCompanyId(String json) {
		try {
			JSONObject jsonObject = _jsonFactory.createJSONObject(json);

			json = String.valueOf(_removeCompanyId(jsonObject));
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug(jsonException);
			}
		}

		return json;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CTMessageCompanyIdUpgradeProcess.class);

	private final JSONFactory _jsonFactory;

}