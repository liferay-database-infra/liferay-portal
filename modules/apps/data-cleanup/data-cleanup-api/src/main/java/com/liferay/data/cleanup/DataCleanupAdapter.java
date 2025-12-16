/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.cleanup;

import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.verify.VerifyProcess;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Maríano Álvaro Sáiz
 */
public class DataCleanupAdapter {

	public static DataCleanup create(
		String label, String servletContextName, String type,
		UpgradeProcess upgradeProcess) {

		return _create(
			label, servletContextName, type, upgradeProcess::upgrade);
	}

	public static DataCleanup create(
		String label, String servletContextName, String type,
		VerifyProcess verifyProcess) {

		return _create(label, servletContextName, type, verifyProcess::verify);
	}

	private static DataCleanup _create(
		String label, String servletContextName, String type,
		UnsafeRunnable<Exception> unsafeRunnable) {

		return new DataCleanup() {

			@Override
			public String getLabel() {
				return label;
			}

			@Override
			public String getServletContextName() {
				return servletContextName;
			}

			@Override
			public String getType() {
				return type;
			}

			@Override
			public boolean isEnabled() {
				return _isEnabled.get();
			}

			@Override
			protected void doCleanup() throws Exception {
				unsafeRunnable.run();

				if (StringUtil.equals(getType(), MODULE_DATA_CLEANUP)) {
					_isEnabled.set(false);
				}
			}

			private final AtomicBoolean _isEnabled = new AtomicBoolean(true);

		};
	}

}