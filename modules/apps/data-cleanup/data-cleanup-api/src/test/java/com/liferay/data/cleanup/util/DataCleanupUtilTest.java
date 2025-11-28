/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.cleanup.util;

import com.liferay.data.cleanup.DataCleanup;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Mariano Álvaro Sáiz
 */
public class DataCleanupUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testModuleDataCleanup() {
		DataCleanup dataCleanup = new DataCleanup() {

			@Override
			public String getLabel() {
				return null;
			}

			@Override
			public String getType() {
				return DataCleanup.MODULE_DATA_CLEANUP;
			}

			@Override
			protected void doCleanup() {
			}

		};

		DataCleanupUtil.registerDataCleanup(dataCleanup);

		List<DataCleanup> moduleDataCleanups =
			DataCleanupUtil.getModuleDataCleanups();

		Assert.assertEquals(
			moduleDataCleanups.toString(), 1, moduleDataCleanups.size());

		Assert.assertEquals(dataCleanup, moduleDataCleanups.get(0));

		DataCleanupUtil.unregisterDataCleanup(dataCleanup);

		moduleDataCleanups = DataCleanupUtil.getSystemDataCleanups();

		Assert.assertTrue(moduleDataCleanups.isEmpty());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRegisterWrongTypeDataCleanup() {
		DataCleanupUtil.registerDataCleanup(
			new DataCleanup() {

				@Override
				public String getLabel() {
					return null;
				}

				@Override
				public String getType() {
					return "wrong type";
				}

				@Override
				protected void doCleanup() {
				}

			});
	}

	@Test
	public void testSystemDataCleanup() {
		DataCleanup dataCleanup = new DataCleanup() {

			@Override
			public String getLabel() {
				return null;
			}

			@Override
			public String getType() {
				return DataCleanup.SYSTEM_DATA_CLEANUP;
			}

			@Override
			protected void doCleanup() {
			}

		};

		DataCleanupUtil.registerDataCleanup(dataCleanup);

		List<DataCleanup> systemDataCleanups =
			DataCleanupUtil.getSystemDataCleanups();

		Assert.assertEquals(
			systemDataCleanups.toString(), 1, systemDataCleanups.size());

		Assert.assertEquals(dataCleanup, systemDataCleanups.get(0));

		DataCleanupUtil.unregisterDataCleanup(dataCleanup);

		systemDataCleanups = DataCleanupUtil.getSystemDataCleanups();

		Assert.assertTrue(systemDataCleanups.isEmpty());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testUnregisterWrongTypeDataCleanup() {
		DataCleanupUtil.unregisterDataCleanup(
			new DataCleanup() {

				@Override
				public String getLabel() {
					return null;
				}

				@Override
				public String getType() {
					return "wrong type";
				}

				@Override
				protected void doCleanup() {
				}

			});
	}

}