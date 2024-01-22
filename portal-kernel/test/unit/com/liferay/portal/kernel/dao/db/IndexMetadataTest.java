/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.dao.db;

import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.IntegerWrapper;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Mariano Álvaro Sáiz
 */
public class IndexMetadataTest {

	@Test
	public void testIndexNameCanChangeAfterOptimization() throws Exception {
		IndexMetadata indexMetadata = new IndexMetadata(
			"IX_XXXXXXXX", "Table1", false, "column1", "column2");

		indexMetadata.optimizeColumns(
			HashMapBuilder.<String, IntegerWrapper>put(
				"column1", new IntegerWrapper(2)
			).put(
				"column2", new IntegerWrapper(1)
			).build());

		Assert.assertEquals(
			IndexMetadataFactoryUtil.createIndexName(
				"Table1", "column1", "column2"),
			indexMetadata.getIndexName());

		indexMetadata.optimizeColumns(
			HashMapBuilder.<String, IntegerWrapper>put(
				"column1", new IntegerWrapper(1)
			).put(
				"column2", new IntegerWrapper(2)
			).build());

		Assert.assertEquals(
			IndexMetadataFactoryUtil.createIndexName(
				"Table1", "column2", "column1"),
			indexMetadata.getIndexName());
	}

	@Test
	public void testIndexNameDoesNotChangeDueToColumnLength() throws Exception {
		IndexMetadata indexMetadata = new IndexMetadata(
			"IX_B5134427", "Company", true, "webId[$COLUMN_LENGTH:75$]");

		indexMetadata.optimizeColumns(
			HashMapBuilder.<String, IntegerWrapper>put(
				"webId", new IntegerWrapper(1)
			).build());

		Assert.assertEquals("IX_B5134427", indexMetadata.getIndexName());
	}

}