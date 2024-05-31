/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.db.schema.info.internal.sql;

import com.liferay.portal.dao.jdbc.util.ConnectionWrapper;
import com.liferay.portal.dao.jdbc.util.DataSourceWrapper;
import com.liferay.portal.dao.jdbc.util.StatementWrapper;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Mariano Álvaro Sáiz
 */
public class DataSourceSQLSniffer extends DataSourceWrapper {

	public DataSourceSQLSniffer(SQLRecorder[] sqlRecorders) {
		super(null);

		_sqlRecorders = sqlRecorders;
	}

	@Override
	public Connection getConnection() throws SQLException {
		return _getConnectionWrapper();
	}

	@Override
	public Connection getConnection(String userName, String password)
		throws SQLException {

		return _getConnectionWrapper();
	}

	private Connection _getConnectionWrapper() {
		return new ConnectionWrapper(null) {

			@Override
			public void close() {
			}

			@Override
			public Statement createStatement() throws SQLException {
				return _wrapStatement(null);
			}

			@Override
			public Statement createStatement(
					int resultSetType, int resultSetConcurrency)
				throws SQLException {

				return _wrapStatement(null);
			}

			@Override
			public Statement createStatement(
					int resultSetType, int resultSetConcurrency,
					int resultSetHoldability)
				throws SQLException {

				return _wrapStatement(null);
			}

		};
	}

	private Statement _wrapStatement(Statement statement) {
		return new StatementWrapper(statement) {

			@Override
			public void close() {
			}

			@Override
			public int executeUpdate(String sql) throws SQLException {
				for (SQLRecorder sqlRecorder : _sqlRecorders) {
					sqlRecorder.record(sql);
				}

				return 0;
			}

		};
	}

	private final SQLRecorder[] _sqlRecorders;

}