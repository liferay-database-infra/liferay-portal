/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.db.schema.info.internal.processor;

import com.liferay.portal.db.DBResourceUtil;
import com.liferay.portal.db.schema.info.internal.sql.DataSourceSQLSniffer;
import com.liferay.portal.db.schema.info.internal.sql.FakeDBFactory;
import com.liferay.portal.db.schema.info.internal.sql.SQLRecorder;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.upgrade.release.SchemaCreator;

import java.sql.Connection;

import java.util.Collection;

import javax.sql.DataSource;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * @author Mariano Álvaro Sáiz
 */
public class DBSchemaToSQLProcessor {

	public DBSchemaToSQLProcessor(DBType dbType, SQLRecorder[] sqlRecorders) {
		_dbType = dbType;

		_dataSourceSQLSniffer = new DataSourceSQLSniffer(sqlRecorders);
		_fakeDB = FakeDBFactory.getDB(dbType);
	}

	public void process() throws Exception {
		_generatePortalSQL();
		_generateMiscellaneousSQL();
		_generateModulesSQL();
	}

	private void _generateMiscellaneousSQL() throws Exception {
		try (Connection connection = _dataSourceSQLSniffer.getConnection()) {
			for (String sqlTemplate :
					DBResourceUtil.getMiscellaneousSQLTemplates()) {

				_fakeDB.runSQLTemplateString(connection, sqlTemplate, true);
			}
		}
	}

	private void _generateModulesSQL() throws Exception {
		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		Collection<ServiceReference<SchemaCreator>> serviceReferences =
			bundleContext.getServiceReferences(SchemaCreator.class, null);

		try (Connection connection = _dataSourceSQLSniffer.getConnection()) {
			for (ServiceReference<SchemaCreator> serviceReference :
					serviceReferences) {

				SchemaCreator schemaCreator = bundleContext.getService(
					serviceReference);

				try {
					_fakeDB.runSQLTemplateString(
						connection, schemaCreator.getBundleSQL(), true);
				}
				finally {
					bundleContext.ungetService(serviceReference);
				}
			}
		}
	}

	private void _generatePortalSQL() throws Exception {
		try (Connection connection = _dataSourceSQLSniffer.getConnection()) {
			_fakeDB.runSQLTemplateString(
				connection, DBResourceUtil.getPortalTablesSQL(), true);
			_fakeDB.runSQLTemplateString(
				connection, DBResourceUtil.getPortalIndexesSQL(), true);
			_fakeDB.runSQLTemplateString(
				connection, DBResourceUtil.getPortalSequencesSQL(), true);
		}
	}

	private final DataSource _dataSourceSQLSniffer;
	private final DBType _dbType;
	private final DB _fakeDB;

}