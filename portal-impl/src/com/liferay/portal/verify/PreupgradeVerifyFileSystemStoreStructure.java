/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.verify;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.util.PropsValues;
import com.liferay.portal.verify.util.PreupgradeDLStoreVerifyUtil;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author István András Dézsi
 */
public class PreupgradeVerifyFileSystemStoreStructure
	extends PreupgradeVerifyProcess {

	@Override
	protected void doVerify() throws Exception {
		if (PropsValues.UPGRADE_DATABASE_DL_STORAGE_CHECK_DISABLED ||
			!PreupgradeDLStoreVerifyUtil.isFileSystemStore()) {

			return;
		}

		_verifyFileSystemStoreStructure();
	}

	@Override
	protected boolean isSkipDBPartitions() {
		return true;
	}

	private boolean _hasAdvancedFileSystemPattern(Path directory) {
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(
				directory)) {

			for (Path item : stream) {
				String itemName = item.getFileName(
				).toString();

				if (StringUtil.equals(itemName, _ADAPTIVE_MEDIA_FOLDER_NAME)) {
					continue;
				}

				if (!Files.isDirectory(item)) {
					_log.error(
						"Found file in advanced file system pattern " +
							"directory (only directories expected): " + item);

					return false;
				}

				if (!_validateSubdirectories(item)) {
					return false;
				}
			}

			return true;
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to check advanced file system pattern in: " +
						directory,
					exception);
			}

			return false;
		}
	}

	private boolean _hasBasicFileSystemPattern(Path directory) {
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(
				directory)) {

			for (Path item : stream) {
				String itemName = item.getFileName(
				).toString();

				if (StringUtil.equals(itemName, _ADAPTIVE_MEDIA_FOLDER_NAME)) {
					continue;
				}

				if (!Files.isDirectory(item)) {
					_log.error(
						"Found file in basic file system pattern directory " +
							"(only directories expected): " + item);

					return false;
				}

				try (DirectoryStream<Path> subStream = Files.newDirectoryStream(
						item)) {

					for (Path subitem : subStream) {
						if (!Files.isDirectory(subitem)) {
							_log.error(
								"Found file in basic file system pattern " +
									"directory (only directories expected): " +
										subitem);

							return false;
						}

						String subitemName = subitem.getFileName(
						).toString();

						if (subitemName.contains(".")) {
							_log.error(
								StringBundler.concat(
									"Found directory with extension in basic ",
									"file system pattern (no extensions ",
									"expected): ", subitem.toString()));

							return false;
						}

						if (!_hasVersionFile(subitem)) {
							_log.error(
								StringBundler.concat(
									"Directory does not contain valid version ",
									"files as expected in basic file system ",
									"pattern: ", subitem.toString()));

							return false;
						}
					}
				}
			}

			return true;
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to check basic file system pattern in: " +
						directory,
					exception);
			}

			return false;
		}
	}

	private boolean _hasItemsWithExtensions(Path directory) {
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(
				directory)) {

			for (Path item : stream) {
				if (!Files.isDirectory(item)) {
					_log.error(
						"Found file in advanced file system pattern " +
							"directory (only directories expected): " + item);

					return false;
				}

				String itemName = item.getFileName(
				).toString();

				if (!itemName.contains(".")) {
					_log.error(
						"Found directory without extension (extension " +
							"expected): " + item);

					return false;
				}
			}

			return true;
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to check for items with extensions in: " +
						directory,
					exception);
			}

			return false;
		}
	}

	private boolean _hasNoSubdirectories(Path directory) {
		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(
				directory)) {

			for (Path path : directoryStream) {
				if (Files.isDirectory(path)) {
					return false;
				}
			}

			return true;
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to check for subdirectories in: " + directory,
					exception);
			}

			return false;
		}
	}

	private boolean _hasVersionFile(Path directory) {
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(
				directory)) {

			for (Path item : stream) {
				if (!Files.isDirectory(item)) {
					String fileName = item.getFileName(
					).toString();

					if (!fileName.matches("\\d+\\.\\d+.*")) {
						_log.error(
							"Found file that does not match version pattern " +
								"(expected \\d+\\.\\d+.*): " + item);

						return false;
					}
				}
			}

			return true;
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to check for version file in: " + directory,
					exception);
			}

			return false;
		}
	}

	private boolean _validateSubdirectories(Path directory) {
		try (DirectoryStream<Path> subStream = Files.newDirectoryStream(
				directory)) {

			for (Path subitem : subStream) {
				if (!Files.isDirectory(subitem)) {
					_log.error(
						"Found file in advanced file system pattern " +
							"directory (only directories expected): " +
								subitem);

					return false;
				}

				String subitemName = subitem.getFileName(
				).toString();

				if (subitemName.equals("DLFE")) {
					if (!_validateSubdirectories(subitem)) {
						return false;
					}
				}
				else {
					if (!_hasItemsWithExtensions(subitem)) {
						_log.error(
							StringBundler.concat(
								"Directory does not contain items with ",
								"extensions as expected in advanced file ",
								"system pattern: ", subitem.toString()));

						return false;
					}
				}
			}

			return true;
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to validate subdirectories in: " + directory,
					exception);
			}

			return false;
		}
	}

	private void _verifyFileSystemStoreStructure() throws Exception {
		Path fileSystemStoreRootDir =
			PreupgradeDLStoreVerifyUtil.getFileSystemStoreRootDir(connection);

		if ((fileSystemStoreRootDir == null) ||
			!Files.exists(fileSystemStoreRootDir) ||
			!Files.isDirectory(fileSystemStoreRootDir) ||
			_hasNoSubdirectories(fileSystemStoreRootDir)) {

			return;
		}

		boolean advancedFileSystemStore = StringUtil.equals(
			PropsValues.DL_STORE_IMPL, _ADVANCED_FILE_SYSTEM_STORE);
		boolean fileSystemStore = StringUtil.equals(
			PropsValues.DL_STORE_IMPL, _FILE_SYSTEM_STORE);

		if (!advancedFileSystemStore && !fileSystemStore) {
			return;
		}

		try (DirectoryStream<Path> companyDirStream = Files.newDirectoryStream(
				fileSystemStoreRootDir)) {

			for (Path companyDir : companyDirStream) {
				String companyDirName = companyDir.getFileName(
				).toString();

				if (!Files.isDirectory(companyDir) ||
					StringUtil.equals(
						companyDirName, _ADAPTIVE_MEDIA_FOLDER_NAME) ||
					!Validator.isNumber(companyDirName)) {

					continue;
				}

				boolean validStructure = true;

				if (advancedFileSystemStore) {
					validStructure = _hasAdvancedFileSystemPattern(companyDir);
				}
				else if (fileSystemStore) {
					validStructure = _hasBasicFileSystemPattern(companyDir);
				}

				if (!validStructure) {
					String expectedType =
						advancedFileSystemStore ? "AdvancedFileSystemStore" :
							"FileSystemStore";

					throw new VerifyException(
						StringBundler.concat(
							"File system store directory structure mismatch. ",
							"Expected ", expectedType, " structure but found ",
							"invalid structure in: ",
							fileSystemStoreRootDir.toString()));
				}
			}
		}
	}

	private static final String _ADAPTIVE_MEDIA_FOLDER_NAME = "0";

	private static final String _ADVANCED_FILE_SYSTEM_STORE =
		"com.liferay.portal.store.file.system.AdvancedFileSystemStore";

	private static final String _FILE_SYSTEM_STORE =
		"com.liferay.portal.store.file.system.FileSystemStore";

	private static final Log _log = LogFactoryUtil.getLog(
		PreupgradeVerifyFileSystemStoreStructure.class);

}