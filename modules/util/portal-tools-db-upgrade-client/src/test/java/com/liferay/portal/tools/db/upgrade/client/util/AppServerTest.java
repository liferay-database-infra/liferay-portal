/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.db.upgrade.client.util;

import com.liferay.portal.tools.db.upgrade.client.AppServer;
import com.liferay.portal.tools.db.upgrade.client.DBUpgradeClient;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Jorge Avalos
 */
public class AppServerTest {

	@ClassRule
	public static TemporaryFolder temporaryFolder = new TemporaryFolder();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_liferayHomeDir = temporaryFolder.getRoot(
		).getAbsolutePath();

		_tomcatAppDir = temporaryFolder.newFolder("tomcat");

		_tomcatAppDirPath = _tomcatAppDir.getAbsolutePath();

		System.setProperty("user.dir", _liferayHomeDir);

		_dbUpgradeClientMockedStatic.when(
			DBUpgradeClient::getAppServerDir
		).thenReturn(
			_tomcatAppDirPath
		);

		_dbUpgradeClientMockedStatic.when(
			DBUpgradeClient::getLiferayHome
		).thenReturn(
			_liferayHomeDir
		);
	}

	@Test
	public void testGetAppServer() {
		AppServer tomcatAppServer = AppServer.getTomcatAppServer();

		File tomcatAppDir = new File(_tomcatAppDirPath);

		Assert.assertEquals(tomcatAppServer.getDir(), tomcatAppDir);

		File extraLibDir = new File(_tomcatAppDirPath + File.separator + "bin");

		File globalLibDir = new File(
			_tomcatAppDirPath + File.separator + "lib");

		File portalClassesDir = new File(
			_tomcatAppDirPath + File.separator + "webapps" + File.separator +
				"ROOT" + File.separator + "WEB-INF" + File.separator +
					"classes");

		File portalDir = new File(
			_tomcatAppDirPath + File.separator + "webapps" + File.separator +
				"ROOT");

		File portalLibDir = new File(
			_tomcatAppDirPath + File.separator + "webapps" + File.separator +
				"ROOT" + File.separator + "WEB-INF" + File.separator + "lib");

		File portalShieldContainerLibDir = new File(
			_tomcatAppDirPath + File.separator + "webapps" + File.separator +
				"ROOT" + File.separator + "WEB-INF" + File.separator +
					"shielded-container-lib");

		Assert.assertEquals(
			"tomcat", tomcatAppServer.getServerDetectorServerId());

		Assert.assertEquals(portalLibDir, tomcatAppServer.getPortalLibDir());

		Assert.assertEquals(
			portalShieldContainerLibDir,
			tomcatAppServer.getPortalShieldedContainerLibDir());

		Assert.assertEquals(portalDir, tomcatAppServer.getPortalDir());

		Assert.assertEquals(
			portalClassesDir, tomcatAppServer.getPortalClassesDir());

		Assert.assertEquals(globalLibDir, tomcatAppServer.getGlobalLibDir());

		Assert.assertEquals(
			extraLibDir,
			tomcatAppServer.getExtraLibDirs(
			).get(
				0
			));
	}

	@Test
	public void testSetDirName() throws IOException {
		AppServer tomcat = AppServer.getTomcatAppServer();

		tomcat.setDirName(_liferayHomeDir);

		Assert.assertEquals(
			tomcat.getDir(
			).getCanonicalPath(),
			_liferayHomeDir);

		_dbUpgradeClientMockedStatic.when(
			DBUpgradeClient::getAppServerDir
		).thenReturn(
			""
		);

		tomcat.setDirName("tomcat");

		Assert.assertEquals(
			tomcat.getDir(
			).getCanonicalPath(),
			_tomcatAppDirPath);

		_tomcatAppDir.delete();

		File tomcatVersionAppDir = temporaryFolder.newFolder("tomcat-x.y.z");

		tomcat.setDirName("tomcat");

		Assert.assertEquals(
			tomcat.getDir(
			).getCanonicalPath(),
			tomcatVersionAppDir.getAbsolutePath());
	}

	@Test
	public void testSetDirectoryNames() {
		AppServer tomcatAppServer = AppServer.getTomcatAppServer();

		tomcatAppServer.setExtraLibDirNames(_liferayHomeDir);

		Assert.assertEquals(
			tomcatAppServer.getExtraLibDirNames(), _liferayHomeDir);

		tomcatAppServer.setGlobalLibDirName(_liferayHomeDir);

		Assert.assertEquals(
			tomcatAppServer.getGlobalLibDirName(), _liferayHomeDir);

		tomcatAppServer.setPortalDirName(_liferayHomeDir);

		Assert.assertEquals(
			tomcatAppServer.getPortalDirName(), _liferayHomeDir);
	}

	private static final MockedStatic<DBUpgradeClient>
		_dbUpgradeClientMockedStatic = Mockito.mockStatic(
			DBUpgradeClient.class);
	private static String _liferayHomeDir;
	private static File _tomcatAppDir;
	private static String _tomcatAppDirPath;

}