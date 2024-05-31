/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.db.schema.info.internal.sql;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.function.Predicate;

/**
 * @author Mariano Álvaro Sáiz
 */
public abstract class SQLRecorder {

	public SQLRecorder(Predicate<String> predicate, String type) {
		_predicate = predicate;
		_type = type;
	}

	public String getSQL() {
		return _sb.toString();
	}

	public String getType() {
		return _type;
	}

	public void record(String sql) {
		if (_isRecordedSQL(sql)) {
			_sb.append(sql + StringPool.SEMICOLON + StringPool.NEW_LINE);
		}
	}

	private boolean _isRecordedSQL(String sql) {
		sql = StringUtil.toLowerCase(sql);

		return _predicate.test(sql);
	}

	private final Predicate<String> _predicate;
	private final StringBundler _sb = new StringBundler();
	private final String _type;

}