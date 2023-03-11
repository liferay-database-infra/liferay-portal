/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.petra.string.StringBundler;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class CompanyIterationCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.LITERAL_FOR, TokenTypes.METHOD_DEF};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		String absolutePath = getAbsolutePath();

		if (absolutePath.contains("com/liferay/portal/") &&
			absolutePath.contains("/upgrade/")) {

			return;
		}

		if (detailAST.getType() == TokenTypes.METHOD_DEF) {
			List<String> importNames = getImportNames(detailAST);

			if (importNames.contains("java.sql.PreparedStatement")) {
				System.out.println(getAbsolutePath());

				_checkMethodCalls(
					detailAST, "connection", "callStatement",
					"prepareStatement");
				_checkMethodCalls(
					detailAST, "AutoBatchPreparedStatementUtil", "autoBatch",
					"concurrentAutoBatch");
			}

			return;
		}

		DetailAST forEachClauseDetailAST = detailAST.findFirstToken(
			TokenTypes.FOR_EACH_CLAUSE);

		if (forEachClauseDetailAST == null) {
			return;
		}

		DetailAST variableDefinitionDetailAST =
			forEachClauseDetailAST.findFirstToken(TokenTypes.VARIABLE_DEF);

		String typeName = getTypeName(
			variableDefinitionDetailAST.findFirstToken(TokenTypes.TYPE), true);

		DetailAST nameDetailAST = variableDefinitionDetailAST.findFirstToken(
			TokenTypes.IDENT);

		String variableName = nameDetailAST.getText();

		if (typeName.equals("Company")) {
			log(
				detailAST, _MSG_USE_COMPANY_LOCAL_SERVICE_1, "forEachCompany",
				typeName + " " + variableName);
		}
		else if ((typeName.equals("Long") || typeName.equals("long")) &&
				 variableName.equals("companyId")) {

			log(
				detailAST, _MSG_USE_COMPANY_LOCAL_SERVICE_1, "forEachCompanyId",
				typeName + " " + variableName);
		}
	}

	private void _checkMethodCalls(
		DetailAST detailAST, String className, String... methodNames) {

		for (DetailAST methodCallDetailAST :
				getMethodCalls(detailAST, className, methodNames)) {

			DetailAST elistDetailAST = methodCallDetailAST.findFirstToken(
				TokenTypes.ELIST);

			for (DetailAST exprDetailAST :
					getAllChildTokens(elistDetailAST, false, TokenTypes.EXPR)) {

				String sqlQuery = _extractSQLQuery(exprDetailAST);

				if (sqlQuery != null) {
					Matcher matcher = _selectSQLPattern.matcher(sqlQuery);

					if (matcher.find()) {
						log(
							methodCallDetailAST,
							_MSG_USE_COMPANY_LOCAL_SERVICE_2);

						break;
					}
				}
			}
		}
	}

	private String _extractSQLQuery(DetailAST detailAST) {
		List<DetailAST> stringLiteralDetailASTs = getAllChildTokens(
			detailAST, true, TokenTypes.STRING_LITERAL);

		if (stringLiteralDetailASTs.isEmpty()) {
			return null;
		}

		StringBundler sb = new StringBundler(
			(2 * stringLiteralDetailASTs.size()) - 1);

		for (DetailAST stringLiteralDetailAST : stringLiteralDetailASTs) {
			String stringLiteral = stringLiteralDetailAST.getText();

			sb.append(stringLiteral.substring(1, stringLiteral.length() - 1));
		}

		return sb.toString();
	}

	private static final String _MSG_USE_COMPANY_LOCAL_SERVICE_1 =
		"company.local.service.use.1";

	private static final String _MSG_USE_COMPANY_LOCAL_SERVICE_2 =
		"company.local.service.use.2";

	private static final Pattern _selectSQLPattern = Pattern.compile(
		"select\\s+.+\\s+from\\s+Company", Pattern.CASE_INSENSITIVE);

}