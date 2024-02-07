/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.db.IndexSQLUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.StringUtil;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Mariano Álvaro Sáiz
 */
public class IndexMetadataTest {

	@Test
	public void testIndexNameCanChangeAfterOptimization() throws Exception {
		IndexMetadata indexMetadata = new IndexMetadata(
			false, "Table1", "column1", "column2");

		indexMetadata.optimizeColumns(
			HashMapBuilder.<String, IntegerWrapper>put(
				"column1", new IntegerWrapper(2)
			).put(
				"column2", new IntegerWrapper(1)
			).build());

		String indexName = (String)ArrayUtil.getValue(
			StringUtil.split(
				IndexSQLUtil.getCreateSQL(
					indexMetadata.isUnique(), indexMetadata.getTableName(),
					indexMetadata.getColumnNames(), new int[] {10, 20}, "IX_"),
				StringPool.SPACE),
			2);

		indexMetadata.optimizeColumns(
			HashMapBuilder.<String, IntegerWrapper>put(
				"column1", new IntegerWrapper(1)
			).put(
				"column2", new IntegerWrapper(2)
			).build());

		Assert.assertNotEquals(
			indexName,
			(String)ArrayUtil.getValue(
				StringUtil.split(
					IndexSQLUtil.getCreateSQL(
						indexMetadata.isUnique(), indexMetadata.getTableName(),
						indexMetadata.getColumnNames(), new int[] {10, 20},
						"IX_"),
					StringPool.SPACE),
				2));
	}

	@Test
	public void testIndexNameGenerationCanTakeColumnLengthIntoAccount()
		throws Exception {

		IndexMetadata indexMetadata = new IndexMetadata(
			true, "Company", "webId");

		Assert.assertEquals(
			"IX_EC00543C",
			(String)ArrayUtil.getValue(
				StringUtil.split(
					IndexSQLUtil.getCreateSQL(
						indexMetadata.isUnique(), indexMetadata.getTableName(),
						indexMetadata.getColumnNames(), null, "IX_"),
					StringPool.SPACE),
				3));

		Assert.assertEquals(
			"IX_B5134427",
			(String)ArrayUtil.getValue(
				StringUtil.split(
					IndexSQLUtil.getCreateSQL(
						indexMetadata.isUnique(), indexMetadata.getTableName(),
						indexMetadata.getColumnNames(), new int[] {75}, "IX_"),
					StringPool.SPACE),
				3));
	}

}