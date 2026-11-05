/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.schema.manifest.builder;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONArrayImpl;
import com.liferay.portal.json.JSONObjectImpl;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.tools.ArgumentsUtil;
import com.liferay.portal.tools.ToolsUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.framework.Version;

/**
 * @author Luis Ortiz
 */
public class SchemaManifestBuilder {

	public static void main(String[] args) throws Exception {
		Map<String, String> arguments = ArgumentsUtil.parseArguments(args);

		String baseDirName = GetterUtil.getString(
			arguments.get("schema.manifest.base.dir"), ".");
		String manifestFileName = GetterUtil.getString(
			arguments.get("schema.manifest.file"),
			"src/main/resources/META-INF/schema/manifest.json");
		String module = arguments.get("schema.manifest.module");

		try {
			SchemaManifestBuilder schemaManifestBuilder =
				new SchemaManifestBuilder(
					baseDirName, manifestFileName, module);

			schemaManifestBuilder.build();
		}
		catch (Exception exception) {
			ArgumentsUtil.processMainException(arguments, exception);
		}
	}

	public SchemaManifestBuilder(
		String baseDirName, String manifestFileName, String module) {

		_module = module;

		_baseDir = new File(baseDirName);

		_manifestFile = new File(_baseDir, manifestFileName);
	}

	public void build() throws IOException {
		JSONArray historyJSONArray = getHistoryJSONArray(_baseDir);

		String module = _module;

		if (Validator.isNull(module)) {
			module = getBundleSymbolicName(new File(_baseDir, "bnd.bnd"));
		}

		write(
			_manifestFile,
			getManifestJSONObject(
				historyJSONArray, isInitializationRegistered(_baseDir),
				module));
	}

	protected String getBundleSymbolicName(File bndFile) throws IOException {
		if (!bndFile.exists()) {
			throw new IllegalArgumentException(
				"Unable to find " + bndFile.getAbsolutePath());
		}

		Properties bndProperties = new Properties();

		try (InputStream inputStream = Files.newInputStream(bndFile.toPath())) {
			bndProperties.load(inputStream);
		}

		String bundleSymbolicName = bndProperties.getProperty(
			"Bundle-SymbolicName");

		if (Validator.isNull(bundleSymbolicName)) {
			throw new IllegalArgumentException(
				"Unable to find \"Bundle-SymbolicName\" in " +
					bndFile.getAbsolutePath());
		}

		return bundleSymbolicName;
	}

	protected JSONArray getHistoryJSONArray(File baseDir) throws IOException {
		JSONArray historyJSONArray = new JSONArrayImpl();

		for (File upgradeStepRegistratorFile :
				_getUpgradeStepRegistratorFiles(baseDir)) {

			_addHistory(historyJSONArray, upgradeStepRegistratorFile);
		}

		return historyJSONArray;
	}

	protected JSONObject getManifestJSONObject(
		JSONArray historyJSONArray, boolean initializationRegistered,
		String module) {

		JSONObject manifestJSONObject = new JSONObjectImpl();

		manifestJSONObject.put(
			"description",
			StringBundler.concat(
				"Database changes manifest of one Liferay module, generated ",
				"by Liferay Schema Manifest Builder from its upgrade steps. ",
				"Do not edit it by hand. Top level keys: 'history' stores the ",
				"upgrade steps that move a database between schema versions; ",
				"'module' is the module's Bundle-SymbolicName, or 'portal' ",
				"for portal core; 'schemaVersion' is the current schema ",
				"version; 'timestamp' is when the content last changed. ",
				"Rules: 'history' is a SET of edges, not a sequence: array ",
				"order is not meaningful, several edges may share a ",
				"'toSchemaVersion', and only one applies to any given ",
				"database. Compute the upgrade path by following ",
				"'fromSchemaVersion'/'toSchemaVersion'. 'history' only holds ",
				"the steps registered with 'register': a fresh install is ",
				"created directly at 'schemaVersion'. 'changes' inside an ",
				"edge IS ordered, in execution order. Schema versions are ",
				"opaque strings: never compare or sort them. A change's ",
				"'className' is the class that implements the step; for an ",
				"anonymous class it is its binary name, '<registrator>$<n>'; ",
				"for a step returned by a method call it is the class or type ",
				"the method is called on, such as UpgradeProcessFactory; for ",
				"a lambda, or a step whose class cannot be resolved, it is ",
				"the registrator. The edge's 'registrator' plus the change's ",
				"'registratorLine' is where the step is declared; for a step ",
				"created by UpgradeProcessFactory directly in the 'register' ",
				"call, an upgrade log entry '<class>:<line> - ...' matches ",
				"the change whose edge 'registrator' is <class> and whose ",
				"'registratorLine' is <line>. 'customChange' means the step's ",
				"effect could NOT be determined, not that it made no change. ",
				"A database's own schemaVersion for this module is in its ",
				"Release_ table, in the row where servletContextName equals ",
				"'module'.")
		).put(
			"history", historyJSONArray
		).put(
			"module", module
		);

		String schemaVersion = getSchemaVersion(
			historyJSONArray, initializationRegistered);

		if (schemaVersion != null) {
			manifestJSONObject.put("schemaVersion", schemaVersion);
		}

		return manifestJSONObject;
	}

	protected String getSchemaVersion(
		JSONArray historyJSONArray, boolean initializationRegistered) {

		Version schemaVersion = null;

		for (int i = 0; i < historyJSONArray.length(); i++) {
			JSONObject edgeJSONObject = historyJSONArray.getJSONObject(i);

			Version toSchemaVersion = Version.parseVersion(
				edgeJSONObject.getString("toSchemaVersion"));

			if ((schemaVersion != null) &&
				(schemaVersion.compareTo(toSchemaVersion) >= 0)) {

				continue;
			}

			schemaVersion = toSchemaVersion;
		}

		if (schemaVersion != null) {
			return schemaVersion.toString();
		}

		if (initializationRegistered) {
			return "1.0.0";
		}

		return null;
	}

	protected String getTimestamp(String manifestContent) {
		Matcher matcher = _timestampPattern.matcher(manifestContent);

		if (matcher.find()) {
			return matcher.group(1);
		}

		return null;
	}

	protected boolean isInitializationRegistered(File baseDir)
		throws IOException {

		for (File upgradeStepRegistratorFile :
				_getUpgradeStepRegistratorFiles(baseDir)) {

			String upgradeStepRegistratorContent = _read(
				upgradeStepRegistratorFile);

			Matcher matcher = _upgradeStepRegistratorPattern.matcher(
				upgradeStepRegistratorContent);

			if (!matcher.find()) {
				continue;
			}

			matcher = _registerInitializationPattern.matcher(
				upgradeStepRegistratorContent);

			if (matcher.find()) {
				return true;
			}
		}

		return false;
	}

	protected void write(File manifestFile, JSONObject manifestJSONObject)
		throws IOException {

		Path manifestPath = manifestFile.toPath();

		if (manifestFile.exists()) {
			String manifestContent = StringUtil.replace(
				new String(
					Files.readAllBytes(manifestPath), StandardCharsets.UTF_8),
				StringPool.RETURN_NEW_LINE, StringPool.NEW_LINE);

			String manifestTimestamp = getTimestamp(manifestContent);

			if (manifestTimestamp != null) {
				manifestJSONObject.put("timestamp", manifestTimestamp);

				if (manifestContent.equals(
						JSONUtil.toString(manifestJSONObject))) {

					return;
				}
			}
		}

		Instant instant = Instant.now();

		manifestJSONObject.put(
			"timestamp",
			String.valueOf(instant.truncatedTo(ChronoUnit.SECONDS)));

		Files.createDirectories(manifestPath.getParent());

		String json = JSONUtil.toString(manifestJSONObject);

		Files.write(manifestPath, json.getBytes(StandardCharsets.UTF_8));

		System.out.println("Writing " + manifestFile);
	}

	private void _addHistory(
			JSONArray historyJSONArray, File upgradeStepRegistratorFile)
		throws IOException {

		String upgradeStepRegistratorContent = _read(
			upgradeStepRegistratorFile);

		Matcher matcher = _upgradeStepRegistratorPattern.matcher(
			upgradeStepRegistratorContent);

		if (!matcher.find()) {
			return;
		}

		String upgradeStepRegistratorFileName =
			upgradeStepRegistratorFile.getName();

		String upgradeStepRegistratorPackageName = ToolsUtil.getPackagePath(
			upgradeStepRegistratorFile);

		String upgradeStepRegistratorClassName =
			upgradeStepRegistratorPackageName + StringPool.PERIOD +
				StringUtil.removeLast(upgradeStepRegistratorFileName, ".java");

		List<Integer> anonymousClassPositions = _getAnonymousClassPositions(
			upgradeStepRegistratorContent);
		Map<String, String> importedClassNames = _getImportedClassNames(
			upgradeStepRegistratorContent);

		int registerPosition = -1;

		while (true) {
			registerPosition = upgradeStepRegistratorContent.indexOf(
				".register(", registerPosition + 1);

			if (registerPosition == -1) {
				break;
			}

			List<String> registerParameters = _getParameters(
				upgradeStepRegistratorContent.substring(registerPosition));

			if (registerParameters.size() < 3) {
				continue;
			}

			JSONArray changesJSONArray = new JSONArrayImpl();

			int registerParameterPosition =
				upgradeStepRegistratorContent.indexOf(
					registerParameters.get(1), registerPosition);

			for (int i = 2; i < registerParameters.size(); i++) {
				String registerParameter = registerParameters.get(i);

				registerParameterPosition =
					upgradeStepRegistratorContent.indexOf(
						registerParameter, registerParameterPosition);

				int newLinesCount = StringUtil.count(
					upgradeStepRegistratorContent, 0, registerParameterPosition,
					CharPool.NEW_LINE);

				changesJSONArray.put(
					new JSONObjectImpl(
					).put(
						"className",
						_getClassName(
							anonymousClassPositions, importedClassNames,
							registerParameter, registerParameterPosition,
							upgradeStepRegistratorClassName,
							upgradeStepRegistratorContent,
							upgradeStepRegistratorPackageName)
					).put(
						"op", "customChange"
					).put(
						"registratorLine", newLinesCount + 1
					));

				registerParameterPosition += registerParameter.length();
			}

			historyJSONArray.put(
				new JSONObjectImpl(
				).put(
					"changes", changesJSONArray
				).put(
					"fromSchemaVersion",
					StringUtil.unquote(registerParameters.get(0))
				).put(
					"registrator", upgradeStepRegistratorClassName
				).put(
					"toSchemaVersion",
					StringUtil.unquote(registerParameters.get(1))
				));
		}
	}

	private List<Integer> _getAnonymousClassPositions(
		String upgradeStepRegistratorContent) {

		List<Integer> anonymousClassPositions = new ArrayList<>();

		Matcher matcher = _constructorPattern.matcher(
			upgradeStepRegistratorContent);

		int closeCurlyBraceIndex = -1;

		while (matcher.find()) {
			int constructorPosition = matcher.start();

			if ((constructorPosition < closeCurlyBraceIndex) ||
				ToolsUtil.isInsideQuotes(
					upgradeStepRegistratorContent, constructorPosition)) {

				continue;
			}

			int openParenthesisIndex = matcher.end() - 1;

			String parametersString = _getParametersString(
				upgradeStepRegistratorContent.substring(openParenthesisIndex));

			int trailingContentIndex =
				openParenthesisIndex + parametersString.length() + 2;

			String trailingContent = StringUtil.trim(
				upgradeStepRegistratorContent.substring(trailingContentIndex));

			if (!trailingContent.startsWith(StringPool.OPEN_CURLY_BRACE)) {
				continue;
			}

			anonymousClassPositions.add(constructorPosition);

			int openCurlyBraceIndex = upgradeStepRegistratorContent.indexOf(
				CharPool.OPEN_CURLY_BRACE, trailingContentIndex);

			closeCurlyBraceIndex = openCurlyBraceIndex;

			while (true) {
				closeCurlyBraceIndex = upgradeStepRegistratorContent.indexOf(
					CharPool.CLOSE_CURLY_BRACE, closeCurlyBraceIndex + 1);

				String anonymousClassBody =
					upgradeStepRegistratorContent.substring(
						openCurlyBraceIndex, closeCurlyBraceIndex + 1);

				if (ToolsUtil.getLevel(anonymousClassBody, "{", "}") == 0) {
					break;
				}
			}
		}

		return anonymousClassPositions;
	}

	private String _getClassName(
		List<Integer> anonymousClassPositions,
		Map<String, String> importedClassNames, String upgradeStepExpression,
		int upgradeStepExpressionPosition,
		String upgradeStepRegistratorClassName,
		String upgradeStepRegistratorContent,
		String upgradeStepRegistratorPackageName) {

		Matcher matcher = _lambdaPattern.matcher(upgradeStepExpression);

		if (matcher.find()) {
			return upgradeStepRegistratorClassName;
		}

		matcher = _castPattern.matcher(upgradeStepExpression);

		if (matcher.find()) {
			upgradeStepExpression = upgradeStepExpression.substring(
				matcher.end());
			upgradeStepExpressionPosition += matcher.end();
		}

		int openParenthesisIndex = upgradeStepExpression.indexOf(
			CharPool.OPEN_PARENTHESIS);

		if (upgradeStepExpression.startsWith("new ")) {
			String parametersString = _getParametersString(
				upgradeStepExpression);

			String trailingContent = StringUtil.trim(
				upgradeStepExpression.substring(
					openParenthesisIndex + parametersString.length() + 2));

			if (trailingContent.startsWith(StringPool.OPEN_CURLY_BRACE)) {
				int index = anonymousClassPositions.indexOf(
					upgradeStepExpressionPosition);

				if (index == -1) {
					throw new IllegalStateException(
						"Unable to find the anonymous class at position " +
							upgradeStepExpressionPosition);
				}

				return upgradeStepRegistratorClassName + StringPool.DOLLAR +
					(index + 1);
			}

			String className = upgradeStepExpression.substring(
				4, openParenthesisIndex);

			int index = className.indexOf(CharPool.LESS_THAN);

			if (index != -1) {
				className = className.substring(0, index);
			}

			return _getClassName(
				className, importedClassNames,
				upgradeStepRegistratorPackageName);
		}

		if (openParenthesisIndex != -1) {
			String qualifiedMethodName = upgradeStepExpression.substring(
				0, openParenthesisIndex);

			int index = qualifiedMethodName.lastIndexOf(CharPool.PERIOD);

			if (index == -1) {
				return upgradeStepRegistratorClassName;
			}

			String receiverName = StringUtil.trim(
				qualifiedMethodName.substring(0, index));

			if (_isVariableName(receiverName)) {
				String variableTypeName = _getVariableTypeName(
					receiverName, upgradeStepRegistratorContent);

				if (variableTypeName == null) {
					return upgradeStepRegistratorClassName;
				}

				return _getClassName(
					variableTypeName, importedClassNames,
					upgradeStepRegistratorPackageName);
			}

			return _getClassName(
				receiverName, importedClassNames,
				upgradeStepRegistratorPackageName);
		}

		int initializerPosition = _getVariableInitializerPosition(
			upgradeStepExpression, upgradeStepRegistratorContent);

		if (initializerPosition == -1) {
			String variableTypeName = _getVariableTypeName(
				upgradeStepExpression, upgradeStepRegistratorContent);

			if (variableTypeName == null) {
				return upgradeStepRegistratorClassName;
			}

			return _getClassName(
				variableTypeName, importedClassNames,
				upgradeStepRegistratorPackageName);
		}

		return _getClassName(
			anonymousClassPositions, importedClassNames,
			_getVariableInitializer(
				initializerPosition, upgradeStepRegistratorContent),
			initializerPosition, upgradeStepRegistratorClassName,
			upgradeStepRegistratorContent, upgradeStepRegistratorPackageName);
	}

	private String _getClassName(
		String className, Map<String, String> importedClassNames,
		String packageName) {

		className = StringUtil.removeChars(
			className, CharPool.NEW_LINE, CharPool.SPACE, CharPool.TAB);

		if (className.contains(StringPool.PERIOD)) {
			return className;
		}

		String fullyQualifiedClassName = importedClassNames.get(className);

		if (fullyQualifiedClassName != null) {
			return fullyQualifiedClassName;
		}

		return packageName + StringPool.PERIOD + className;
	}

	private Map<String, String> _getImportedClassNames(
		String upgradeStepRegistratorContent) {

		Map<String, String> importedClassNames = new HashMap<>();

		Matcher matcher = _importPattern.matcher(upgradeStepRegistratorContent);

		while (matcher.find()) {
			importedClassNames.put(matcher.group(2), matcher.group(1));
		}

		return importedClassNames;
	}

	private List<String> _getParameters(String methodCall) {
		List<String> parameters = new ArrayList<>();

		String parametersString = StringUtil.trim(
			_getParametersString(methodCall));

		if (parametersString.equals(StringPool.BLANK)) {
			return parameters;
		}

		int index = -1;

		while (true) {
			index = parametersString.indexOf(StringPool.COMMA, index + 1);

			if (index == -1) {
				parameters.add(StringUtil.trim(parametersString));

				return parameters;
			}

			if (ToolsUtil.isInsideQuotes(parametersString, index)) {
				continue;
			}

			String parameter = StringUtil.replace(
				parametersString.substring(0, index), "->",
				_LAMBDA_SYMBOL_PLACEHOLDER);

			if ((ToolsUtil.getLevel(parameter, "(", ")") != 0) ||
				(ToolsUtil.getLevel(parameter, "<", ">") != 0) ||
				(ToolsUtil.getLevel(parameter, "{", "}") != 0)) {

				continue;
			}

			parameter = StringUtil.replace(
				parameter, _LAMBDA_SYMBOL_PLACEHOLDER, "->");

			parameters.add(StringUtil.trim(parameter));

			parametersString = parametersString.substring(index + 1);

			index = -1;
		}
	}

	private String _getParametersString(String methodCall) {
		int index = -1;
		String parametersString = null;

		while (true) {
			index = methodCall.indexOf(StringPool.CLOSE_PARENTHESIS, index + 1);

			parametersString = methodCall.substring(0, index + 1);

			if ((ToolsUtil.getLevel(parametersString, "(", ")") == 0) &&
				(ToolsUtil.getLevel(parametersString, "{", "}") == 0)) {

				break;
			}
		}

		index = parametersString.indexOf(StringPool.OPEN_PARENTHESIS);

		return parametersString.substring(
			index + 1, parametersString.length() - 1);
	}

	private List<File> _getUpgradeStepRegistratorFiles(File baseDir)
		throws IOException {

		List<File> upgradeStepRegistratorFiles = new ArrayList<>();

		File javaDir = new File(baseDir, "src/main/java");

		if (!javaDir.isDirectory()) {
			return upgradeStepRegistratorFiles;
		}

		FileSystem fileSystem = FileSystems.getDefault();

		PathMatcher pathMatcher = fileSystem.getPathMatcher(
			"glob:**/upgrade/registry/*UpgradeStepRegistrator.java");

		Files.walkFileTree(
			javaDir.toPath(),
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult visitFile(
					Path filePath, BasicFileAttributes basicFileAttributes) {

					if (pathMatcher.matches(filePath)) {
						upgradeStepRegistratorFiles.add(filePath.toFile());
					}

					return FileVisitResult.CONTINUE;
				}

			});

		Collections.sort(upgradeStepRegistratorFiles);

		return upgradeStepRegistratorFiles;
	}

	private String _getVariableInitializer(
		int position, String upgradeStepRegistratorContent) {

		int index = position;

		while (true) {
			index = upgradeStepRegistratorContent.indexOf(
				CharPool.SEMICOLON, index + 1);

			String initializer = upgradeStepRegistratorContent.substring(
				position, index);

			if ((ToolsUtil.getLevel(initializer, "(", ")") == 0) &&
				(ToolsUtil.getLevel(initializer, "{", "}") == 0)) {

				return StringUtil.trim(initializer);
			}
		}
	}

	private int _getVariableInitializerPosition(
		String name, String upgradeStepRegistratorContent) {

		Pattern pattern = Pattern.compile(
			"\\b" + Pattern.quote(name) + "\\s*=(?!=)\\s*");

		Matcher matcher = pattern.matcher(upgradeStepRegistratorContent);

		if (!matcher.find()) {
			return -1;
		}

		return matcher.end();
	}

	private String _getVariableTypeName(
		String name, String upgradeStepRegistratorContent) {

		Pattern pattern = Pattern.compile(
			"([\\w.]+)(?:<[^;=]*>)?\\s+" + Pattern.quote(name) + "\\s*[;=]");

		Matcher matcher = pattern.matcher(upgradeStepRegistratorContent);

		if (!matcher.find()) {
			return null;
		}

		return matcher.group(1);
	}

	private boolean _isVariableName(String name) {
		char c = name.charAt(0);

		if (((c == CharPool.UNDERLINE) || Character.isLowerCase(c)) &&
			!name.contains(StringPool.PERIOD)) {

			return true;
		}

		return false;
	}

	private String _read(File file) throws IOException {
		String content = new String(
			Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);

		content = StringUtil.replace(
			content, StringPool.RETURN_NEW_LINE, StringPool.NEW_LINE);

		return _removeComments(content);
	}

	private String _removeComments(String content) {
		int index = 0;

		while (true) {
			Matcher matcher = _commentPattern.matcher(content);

			if (!matcher.find(index)) {
				return content;
			}

			int start = matcher.start();

			if (ToolsUtil.isInsideQuotes(content, start)) {
				index = start + 1;

				continue;
			}

			char[] chars = content.toCharArray();

			for (int i = start; i < matcher.end(); i++) {
				if (chars[i] == CharPool.NEW_LINE) {
					continue;
				}

				chars[i] = CharPool.SPACE;
			}

			content = new String(chars);

			index = matcher.end();
		}
	}

	private static final String _LAMBDA_SYMBOL_PLACEHOLDER =
		"_LAMBDA_SYMBOL_PLACEHOLDER_";

	private static final Pattern _castPattern = Pattern.compile(
		"^\\(\\s*[\\w.]+\\s*\\)\\s*");
	private static final Pattern _commentPattern = Pattern.compile(
		"//[^\\n]*|/\\*[\\s\\S]*?\\*/");
	private static final Pattern _constructorPattern = Pattern.compile(
		"\\bnew\\s+[\\w.\\s]+(?:<[^;{]*?>)?\\s*\\(");
	private static final Pattern _importPattern = Pattern.compile(
		"^import ([\\w.]+\\.(\\w+));", Pattern.MULTILINE);
	private static final Pattern _lambdaPattern = Pattern.compile(
		"^(\\([\\w\\s,]*\\)|\\w+)\\s*->");
	private static final Pattern _registerInitializationPattern =
		Pattern.compile("\\.registerInitialization\\(\\s*\\)");
	private static final Pattern _timestampPattern = Pattern.compile(
		"^\t\"timestamp\": \"([^\"]*)\"", Pattern.MULTILINE);
	private static final Pattern _upgradeStepRegistratorPattern =
		Pattern.compile("\\bimplements\\s[^{]*\\bUpgradeStepRegistrator\\b");

	private final File _baseDir;
	private final File _manifestFile;
	private final String _module;

}