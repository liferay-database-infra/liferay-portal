/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz.group;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import java.util.Collections;
import java.util.Properties;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Jorge Avalos
 */
public class PlaywrightBatchTestClassGroupTest
	extends com.liferay.jenkins.results.parser.Test {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		setShellCommandOutput(
			"git remote -v", mockShell(),
			"upstream\tgit@github.com:liferay/liferay-portal.git (fetch)\n" +
				"upstream\tgit@github.com:liferay/liferay-portal.git (push)\n");

		_workingDirectory = temporaryFolder.newFolder();

		File playwrightDir = new File(
			_workingDirectory, "modules/test/playwright");

		_writeProject(
			playwrightDir, "tests/multiple-database-types-project",
			"multiple-database-types-project",
			"tests/multiple-database-types-project",
			"database.types=\\\n    db2,\\\n    mysql,\\\n    oracle");
		_writeProject(
			playwrightDir, "tests/mysql-project", "mysql-project",
			"tests/mysql-project", "database.types=mysql");
		_writeProject(
			playwrightDir, "tests/no-database-types-project",
			"no-database-types-project", "tests/no-database-types-project",
			"analytics.cloud.enabled=false");
		_writeProject(
			playwrightDir, "tests/no-test-properties-project",
			"no-test-properties-project", "tests/no-test-properties-project",
			null);
		_writeProject(
			playwrightDir, "tests/shared-spec-project", "shared-spec-project",
			"tests/shared", null);
		_writeProject(
			playwrightDir, "tests/unknown-database-type-project",
			"unknown-database-type-project",
			"tests/unknown-database-type-project",
			"database.types=mysq,postgresql");

		JenkinsResultsParserUtil.write(
			new File(playwrightDir, "tests/shared/test.properties"),
			"database.types=mysql,postgresql");
	}

	@Test
	public void testIsDatabaseTypeSupported() throws Exception {
		_testIsDatabaseTypeSupportedDefaultProjects();
		_testIsDatabaseTypeSupportedIncludesWildcard();
		_testIsDatabaseTypeSupportedMultipleDatabaseTypes();
		_testIsDatabaseTypeSupportedNoDatabaseType();
		_testIsDatabaseTypeSupportedSuffixedBatchName();
		_testIsDatabaseTypeSupportedUnknownDatabaseType();
	}

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private PlaywrightBatchTestClassGroup _newPlaywrightBatchTestClassGroup(
		String batchName, Properties jobProperties) {

		jobProperties.setProperty("test.relevant.changes", "false");

		return new PlaywrightBatchTestClassGroup(
			batchName,
			BatchTestClassGroupTestUtil.getPortalTestClassJob(
				jobProperties, Collections.emptyList(), _workingDirectory)) {

			@Override
			protected void setTestClasses() {
			}

		};
	}

	private void _testIsDatabaseTypeSupportedDefaultProjects() {
		mockEnvironment(
			Collections.singletonMap(
				"PLAYWRIGHT_PROJECT_NAME",
				"multiple-database-types-project,mysql-project," +
					"no-database-types-project," +
						"no-test-properties-project,shared-spec-project"));

		PlaywrightBatchTestClassGroup playwrightBatchTestClassGroup =
			_newPlaywrightBatchTestClassGroup(
				"playwright-js-smoke-tomcat101-postgresql163",
				new Properties());

		testEquals(
			false,
			playwrightBatchTestClassGroup.isDatabaseTypeSupported(
				"mysql-project"));
		testEquals(
			true,
			playwrightBatchTestClassGroup.isDatabaseTypeSupported(
				"no-database-types-project"));
		testEquals(
			true,
			playwrightBatchTestClassGroup.isDatabaseTypeSupported(
				"no-test-properties-project"));
		testEquals(
			true,
			playwrightBatchTestClassGroup.isDatabaseTypeSupported(
				"shared-spec-project"));

		PlaywrightBatchTestClassGroup db2PlaywrightBatchTestClassGroup =
			_newPlaywrightBatchTestClassGroup(
				"playwright-js-smoke-tomcat101-db2111", new Properties());

		testEquals(
			false,
			db2PlaywrightBatchTestClassGroup.isDatabaseTypeSupported(
				"shared-spec-project"));
	}

	private void _testIsDatabaseTypeSupportedIncludesWildcard() {
		Properties jobProperties = new Properties();

		jobProperties.setProperty(
			"playwright.projects.includes[playwright-js-upgrade-tomcat101-*]",
			"mysql-project,no-database-types-project");

		PlaywrightBatchTestClassGroup db2PlaywrightBatchTestClassGroup =
			_newPlaywrightBatchTestClassGroup(
				"playwright-js-upgrade-tomcat101-db2111", jobProperties);

		testEquals(
			false,
			db2PlaywrightBatchTestClassGroup.isDatabaseTypeSupported(
				"mysql-project"));
		testEquals(
			true,
			db2PlaywrightBatchTestClassGroup.isDatabaseTypeSupported(
				"no-database-types-project"));

		PlaywrightBatchTestClassGroup mySQLPlaywrightBatchTestClassGroup =
			_newPlaywrightBatchTestClassGroup(
				"playwright-js-upgrade-tomcat101-mysql84", jobProperties);

		testEquals(
			true,
			mySQLPlaywrightBatchTestClassGroup.isDatabaseTypeSupported(
				"mysql-project"));
	}

	private void _testIsDatabaseTypeSupportedMultipleDatabaseTypes() {
		Properties jobProperties = new Properties();

		jobProperties.setProperty(
			"playwright.projects.includes[playwright-js-upgrade-tomcat101-*]",
			"multiple-database-types-project");

		for (String batchName :
				new String[] {
					"playwright-js-upgrade-tomcat101-db2111",
					"playwright-js-upgrade-tomcat101-oracle193"
				}) {

			PlaywrightBatchTestClassGroup playwrightBatchTestClassGroup =
				_newPlaywrightBatchTestClassGroup(batchName, jobProperties);

			testEquals(
				true,
				playwrightBatchTestClassGroup.isDatabaseTypeSupported(
					"multiple-database-types-project"));
		}

		PlaywrightBatchTestClassGroup playwrightBatchTestClassGroup =
			_newPlaywrightBatchTestClassGroup(
				"playwright-js-upgrade-tomcat101-postgresql163", jobProperties);

		testEquals(
			false,
			playwrightBatchTestClassGroup.isDatabaseTypeSupported(
				"multiple-database-types-project"));
	}

	private void _testIsDatabaseTypeSupportedNoDatabaseType() {
		mockEnvironment(
			Collections.singletonMap(
				"PLAYWRIGHT_PROJECT_NAME",
				"mysql-project,no-database-types-project"));

		PlaywrightBatchTestClassGroup playwrightBatchTestClassGroup =
			_newPlaywrightBatchTestClassGroup(
				"playwright-js-tomcat101", new Properties());

		testEquals(
			false,
			playwrightBatchTestClassGroup.isDatabaseTypeSupported(
				"mysql-project"));
		testEquals(
			true,
			playwrightBatchTestClassGroup.isDatabaseTypeSupported(
				"no-database-types-project"));
	}

	private void _testIsDatabaseTypeSupportedSuffixedBatchName() {
		Properties jobProperties = new Properties();

		jobProperties.setProperty(
			"playwright.projects.includes[playwright-js-tomcat101-*]",
			"mysql-project");

		for (String batchName :
				new String[] {
					"playwright-js-tomcat101-mysql84-jdk21_zulu",
					"playwright-js-tomcat101-mysql84_stable"
				}) {

			PlaywrightBatchTestClassGroup playwrightBatchTestClassGroup =
				_newPlaywrightBatchTestClassGroup(batchName, jobProperties);

			testEquals(
				true,
				playwrightBatchTestClassGroup.isDatabaseTypeSupported(
					"mysql-project"));
		}

		PlaywrightBatchTestClassGroup playwrightBatchTestClassGroup =
			_newPlaywrightBatchTestClassGroup(
				"playwright-js-tomcat101-postgresql163_stable", jobProperties);

		testEquals(
			false,
			playwrightBatchTestClassGroup.isDatabaseTypeSupported(
				"mysql-project"));
	}

	private void _testIsDatabaseTypeSupportedUnknownDatabaseType() {
		Properties jobProperties = new Properties();

		jobProperties.setProperty(
			"playwright.projects.includes[playwright-js-upgrade-tomcat101-*]",
			"unknown-database-type-project");

		PrintStream printStream = System.err;

		ByteArrayOutputStream byteArrayOutputStream =
			new ByteArrayOutputStream();

		System.setErr(new PrintStream(byteArrayOutputStream, true));

		try {
			PlaywrightBatchTestClassGroup mySQLPlaywrightBatchTestClassGroup =
				_newPlaywrightBatchTestClassGroup(
					"playwright-js-upgrade-tomcat101-mysql84", jobProperties);

			testEquals(
				false,
				mySQLPlaywrightBatchTestClassGroup.isDatabaseTypeSupported(
					"unknown-database-type-project"));

			PlaywrightBatchTestClassGroup
				postgreSQLPlaywrightBatchTestClassGroup =
					_newPlaywrightBatchTestClassGroup(
						"playwright-js-upgrade-tomcat101-postgresql163",
						jobProperties);

			testEquals(
				true,
				postgreSQLPlaywrightBatchTestClassGroup.isDatabaseTypeSupported(
					"unknown-database-type-project"));
		}
		finally {
			System.setErr(printStream);
		}

		String errorOutput = byteArrayOutputStream.toString();

		testEquals(
			true,
			errorOutput.contains(
				"Ignoring unknown database type mysq in Playwright project " +
					"unknown-database-type-project"));
	}

	private void _writeProject(
			File playwrightDir, String projectDirPath, String projectName,
			String testDirPath, String testProperties)
		throws Exception {

		File projectDir = new File(playwrightDir, projectDirPath);

		JenkinsResultsParserUtil.write(
			new File(projectDir, "config.ts"),
			JenkinsResultsParserUtil.combine(
				"export const config = {\n\tname: '", projectName,
				"',\n\ttestDir: '", testDirPath, "',\n};"));

		if (testProperties != null) {
			JenkinsResultsParserUtil.write(
				new File(projectDir, "test.properties"), testProperties);
		}
	}

	private File _workingDirectory;

}