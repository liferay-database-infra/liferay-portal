/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.db.index;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Mariano Álvaro Sáiz
 */
public class IndexUpdaterUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetIndexesSQLMapWithUnixLikeSeparator() throws Exception {
		try (AutoCloseable autoCloseable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					System.class, "lineSeparator", StringPool.NEW_LINE)) {

			Map<String, String> indexesMap = ReflectionTestUtil.invoke(
				IndexUpdaterUtil.class, "_getIndexesSQLMap",
				new Class<?>[] {String.class},
				_getSQLFile(StringPool.NEW_LINE));

			Assert.assertEquals(indexesMap.toString(), 2, indexesMap.size());
			Assert.assertNotNull(indexesMap.get("Table1"));
			Assert.assertNotNull(indexesMap.get("Table2"));
		}
	}

	@Test
	public void testGetIndexesSQLMapWithWindowsLikeSeparator()
		throws Exception {

		try (AutoCloseable autoCloseable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					System.class, "lineSeparator",
					StringPool.RETURN_NEW_LINE)) {

			Map<String, String> indexesMap = ReflectionTestUtil.invoke(
				IndexUpdaterUtil.class, "_getIndexesSQLMap",
				new Class<?>[] {String.class},
				_getSQLFile(StringPool.RETURN_NEW_LINE));

			Assert.assertEquals(indexesMap.toString(), 2, indexesMap.size());
			Assert.assertNotNull(indexesMap.get("Table1"));
			Assert.assertNotNull(indexesMap.get("Table2"));
		}
	}

	private String _getSQLFile(String lineSeparator) {
		return StringBundler.concat(
			"create index IX_AAAAAAAA on Table1 (field1);", lineSeparator,
			"create index IX_BBBBBBBB on Table1 (field2);", lineSeparator,
			lineSeparator, "create index IX_CCCCCCCC on Table2 (field1);",
			lineSeparator);
	}

}