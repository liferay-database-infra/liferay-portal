/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.db.schema.info.internal.test.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.InfrastructureUtil;

import java.io.File;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.junit.Assert;

/**
 * @author Mariano Álvaro Sáiz
 */
public class DatabaseValidationTestUtil {

	public static void assertDatabaseDumpMirrorsCurrentDatabase(String dumpPath)
		throws Exception {

		DatabaseTestUtil.createSchema(_COPY_SCHEMA_NAME);

		try {
			DatabaseTestUtil.importFileTo(
				new File(dumpPath, "tables.sql"), _COPY_SCHEMA_NAME);
			DatabaseTestUtil.importFileTo(
				new File(dumpPath, "indexes.sql"), _COPY_SCHEMA_NAME);
			DatabaseTestUtil.importFileTo(
				new File(dumpPath, "sequences.sql"), _COPY_SCHEMA_NAME);

			_assertSameIndexesStructure(_COPY_SCHEMA_NAME);
			_assertSameTablesStructure(_COPY_SCHEMA_NAME);
		}
		finally {
			DatabaseTestUtil.dropSchema(_COPY_SCHEMA_NAME);
		}
	}

	private static void _assertSameIndexesStructure(String copiedSchema)
		throws Exception {

		DataSource dataSource = InfrastructureUtil.getDataSource();

		try (Connection connection = dataSource.getConnection()) {
			try (PreparedStatement preparedStatement =
					connection.prepareStatement(
						StringBundler.concat(
							"select count(distinct(table_name)) total from ",
							"information_schema.statistics where table_schema ",
							" = ?"))) {

				preparedStatement.setString(1, copiedSchema);

				try (ResultSet resultSet = preparedStatement.executeQuery()) {
					resultSet.next();

					Assert.assertNotEquals(0, resultSet.getInt("total"));
				}
			}

			try (PreparedStatement preparedStatement =
					connection.prepareStatement(
						StringBundler.concat(
							"select * FROM information_schema.statistics ",
							"target where table_schema = ? and not exists",
							"(select 1 from information_schema.statistics ",
							"source where table_schema = database() and ",
							"source.table_name = target.table_name and ",
							"source.index_name = target.index_name and ",
							"source.column_name = target.column_name and ",
							"source.seq_in_index = target.seq_in_index and ",
							"source.non_unique = target.non_unique)"))) {

				preparedStatement.setString(1, copiedSchema);

				try (ResultSet resultSet = preparedStatement.executeQuery()) {
					Assert.assertFalse(resultSet.next());
				}
			}
		}
	}

	private static void _assertSameTablesStructure(String copiedSchema)
		throws Exception {

		DataSource dataSource = InfrastructureUtil.getDataSource();

		try (Connection connection = dataSource.getConnection()) {
			try (PreparedStatement preparedStatement =
					connection.prepareStatement(
						StringBundler.concat(
							"select count(distinct(table_name)) total from ",
							"information_schema.columns where table_schema = ",
							"?"))) {

				preparedStatement.setString(1, copiedSchema);

				try (ResultSet resultSet = preparedStatement.executeQuery()) {
					resultSet.next();

					Assert.assertNotEquals(0, resultSet.getInt("total"));
				}
			}

			try (PreparedStatement preparedStatement =
					connection.prepareStatement(
						StringBundler.concat(
							"select * from information_schema.columns target ",
							"where table_schema = ? and not exists (select 1 ",
							"from information_schema.columns source where ",
							"table_schema = database() and source.table_name ",
							"= target.table_name AND source.column_name = ",
							"target.column_name AND ((source.column_default ",
							"is null and target.column_default is null) or (",
							"source.column_default = target.column_default)) ",
							"AND source.is_nullable = target.is_nullable AND ",
							"source.data_type = target.data_type AND (( ",
							"source.character_maximum_length is null and ",
							"target.character_maximum_length is null) or ",
							"(source.character_maximum_length = ",
							"target.character_maximum_length)) AND ((",
							"source.numeric_precision is null and ",
							"target.numeric_precision is null) or (",
							"source.numeric_precision = ",
							"target.numeric_precision)) AND ((",
							"source.numeric_scale is null and ",
							"target.numeric_scale is null) or (",
							"source.numeric_scale = target.numeric_scale)) ",
							"AND source.column_type = target.column_type )"))) {

				preparedStatement.setString(1, copiedSchema);

				try (ResultSet resultSet = preparedStatement.executeQuery()) {
					Assert.assertFalse(resultSet.next());
				}
			}
		}
	}

	private static final String _COPY_SCHEMA_NAME = "copyschema";

}