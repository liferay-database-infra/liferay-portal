/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.verify;

import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.util.PropsValues;
import com.liferay.portal.verify.util.PreupgradeDLStoreVerifyUtil;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author István András Dézsi
 */
public class PreupgradeVerifyDLStoreWritePermission
	extends PreupgradeVerifyProcess {

	@Override
	protected void doVerify() throws Exception {
		if (PropsValues.UPGRADE_DATABASE_DL_STORAGE_CHECK_DISABLED ||
			!PreupgradeDLStoreVerifyUtil.isFileSystemStore()) {

			return;
		}

		_verifyDLStoreWritePermission();
	}

	@Override
	protected boolean isSkipDBPartitions() {
		return true;
	}

	private void _verifyDLStoreWritePermission() throws Exception {
		Path fileSystemStoreRootDir =
			PreupgradeDLStoreVerifyUtil.getFileSystemStoreRootDir(connection);

		if (fileSystemStoreRootDir == null) {
			throw new VerifyException(
				"Unable to determine file system store root directory path");
		}

		String errorMessage = null;

		if (!Files.exists(fileSystemStoreRootDir)) {
			errorMessage =
				"File system store directory does not exist: " +
					fileSystemStoreRootDir.toString();
		}
		else if (!Files.isDirectory(fileSystemStoreRootDir)) {
			errorMessage =
				"File system store path is not a directory: " +
					fileSystemStoreRootDir.toString();
		}
		else {
			Path testFile = fileSystemStoreRootDir.resolve(
				StringBundler.concat(
					"liferay_upgrade_test_",
					String.valueOf(System.currentTimeMillis()), ".tmp"));

			try {
				Files.createFile(testFile);
				Files.delete(testFile);
			}
			catch (Exception exception) {
				errorMessage = StringBundler.concat(
					"Cannot write to file system store directory: ",
					fileSystemStoreRootDir.toString(), ". ",
					exception.getClass(
					).getName(),
					": ", exception.getMessage());
			}
		}

		if (errorMessage != null) {
			throw new VerifyException(errorMessage);
		}
	}

}