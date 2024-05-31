/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.db.schema.info.internal.sql;

/**
 * @author Mariano Álvaro Sáiz
 */
public class SequencesSQLRecorder extends SQLRecorder {

	public SequencesSQLRecorder() {
		super(sql -> sql.contains("create sequence"), "sequences");
	}

}