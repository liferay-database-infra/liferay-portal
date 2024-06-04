/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.db.schema.info.internal.processor;

import com.liferay.portal.db.schema.info.internal.sql.IndexesSQLRecorder;
import com.liferay.portal.db.schema.info.internal.sql.SQLRecorder;
import com.liferay.portal.db.schema.info.internal.sql.SequencesSQLRecorder;
import com.liferay.portal.db.schema.info.internal.sql.TablesSQLRecorder;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.util.FileUtil;

import java.io.File;

/**
 * @author Mariano Álvaro Sáiz
 */
public class DBSchemaToFilesProcessor {

	public DBSchemaToFilesProcessor(DBType dbType) {
		_dbType = dbType;
	}

	public void processTo(String path) throws Exception {
		new DBSchemaToSQLProcessor(
			_dbType, _sqlRecorders
		).process();

		for (SQLRecorder sqlRecorder : _sqlRecorders) {
			FileUtil.write(
				new File(path, sqlRecorder.getType() + _DEFAULT_EXTENSION),
				sqlRecorder.getSQL());
		}
	}

	private static final String _DEFAULT_EXTENSION = ".sql";

	private final DBType _dbType;
	private final SQLRecorder[] _sqlRecorders = {
		new IndexesSQLRecorder(), new SequencesSQLRecorder(),
		new TablesSQLRecorder()
	};

}