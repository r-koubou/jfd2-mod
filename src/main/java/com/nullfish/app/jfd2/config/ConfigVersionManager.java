package com.nullfish.app.jfd2.config;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.jdom.Document;
import org.jdom.JDOMException;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.resource.JFDResource;
import com.nullfish.app.jfd2.util.DomCache;
import com.nullfish.app.jfd2.util.PropertiesCache;
import com.nullfish.lib.vfs.VFS;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;

public class ConfigVersionManager {
	public static final double MIN_VERSION_COMMON_CONFIG = 0;
	public static final String COMMON_CONFIG_FILE = "common_config.xml";
	public static final String LOCAL_CONFIG_FILE = "local_config.xml";

	public static boolean isWindows = System.getProperty("os.name").indexOf(
			"Windows") != -1;

	private static final FileVersion[] ETC_FILES_XML_WIN = {
			new FileVersion("aliase.xml", "aliase_win.xml", 0),
			new FileVersion("command.xml", "command.xml", 20101207),
			new FileVersion("external_command.xml", "external_command.xml", 0),
			new FileVersion("keys.xml", "keys.xml", 20091022),
			new FileVersion("menubar.xml", "menubar.xml", 20101226),
			new FileVersion("owner_command.xml", "owner_command.xml", 20101226),
			new FileVersion("owner_keys.xml", "owner_keys.xml", 20101226),
			new FileVersion("popup.xml", "popup.xml", 20101226),
			new FileVersion("function.ini", "function.ini", 20090127) };

	private static final FileVersion[] ETC_FILES_XML_UNIX = {
			new FileVersion("aliase.xml", "aliase_unix.xml", 0.3),
			new FileVersion("command.xml", "command.xml", 20101207),
			new FileVersion("external_command.xml", "external_command.xml", 0),
			new FileVersion("keys.xml", "keys.xml", 20091022),
			new FileVersion("menubar.xml", "menubar.xml", 20101226),
			new FileVersion("owner_command.xml", "owner_command.xml", 20101226),
			new FileVersion("owner_keys.xml", "owner_keys.xml", 20101226),
			new FileVersion("popup.xml", "popup.xml", 20101226),
			new FileVersion("function.ini", "function.ini", 20090127) };

	public void checkVersion(VFile configDir) throws VFSException,
			JDOMException, IOException {
		String language = Locale.getDefault().getLanguage();
		VFile configSourceDir = VFS.getInstance().getFile(
				"classpath:///conf/" + language);
		if (!configSourceDir.exists()) {
			configSourceDir = VFS.getInstance().getFile(
					"classpath:///conf/default");
		}

		checkCommonConfigVersion(configDir, configSourceDir);
		checkLocalConfigVersion(configDir, configSourceDir);
		checkEtcFilesVersion(configDir, configSourceDir);
	}

	/**
	 * 共通設定のバージョン管理
	 * 
	 * @param configDir
	 * @param sourceDir
	 * @throws VFSException
	 * @throws JDOMException
	 * @throws IOException
	 */
	private void checkCommonConfigVersion(VFile configDir, VFile sourceDir)
			throws VFSException, JDOMException, IOException {
		VFile commonConfigFile = configDir.getChild(JFD.COMMON_PARAM_FILE);
		VFile userConfigDir = configDir.getParent().getChild(".jfd2_user");

		String editorPath = null;
		String shell = null;
		String appShell = null;

		if (!commonConfigFile.exists()) {
			sourceDir.getChild(COMMON_CONFIG_FILE).copyTo(
					configDir.getChild(COMMON_CONFIG_FILE));
		}

		Configuration config = null;
		try {
			config = Configuration.getInstance(commonConfigFile);
			editorPath = (String) config.getParam("editor", null);
			initConfig(config, "temp_dir", configDir.getChild("temp")
					.getAbsolutePath());
			initConfig(config, "script_dir", userConfigDir.getChild("script")
					.getAbsolutePath());
			initConfig(config, "shortcut_dir", userConfigDir.getChild(
					"shortcut").getAbsolutePath());
			initConfig(config, "user_conf_dir", userConfigDir.getChild("conf")
					.getAbsolutePath());
			shell = (String) config.getParam("shell", null);
			appShell = (String) config.getParam("app_shell", null);
			initConfig(config, "open_console_command", DefaultConfig
					.getDefaultConfig().getConsole());
			initConfig(config, "plugin_dir", userConfigDir.getChild("plugin")
					.getAbsolutePath());
			initConfig(config, "tag_db_dir", configDir.getChild("tags")
					.getAbsolutePath());
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (editorPath == null || editorPath.length() == 0) {
			FileInputPanel panel = new FileInputPanel(DefaultConfig
					.getDefaultConfig().getEditor(), JFDResource.MESSAGES
					.getString("input_editor_path"),
					JFileChooser.FILES_AND_DIRECTORIES);
			JOptionPane.showConfirmDialog(null, panel, "jFD2",
					JOptionPane.OK_OPTION);
			editorPath = panel.getText();
			config.setParam("editor", editorPath);
		}

		if (shell == null || shell.length() == 0) {
			JTextArea text = new JTextArea(3, 15);
			text.setText(DefaultConfig.getDefaultConfig().getShell());

			Object[] messages = {
					JFDResource.MESSAGES.getString("input_shell"),
					new JScrollPane(text) };
			JOptionPane.showMessageDialog(null, messages);
			shell = text.getText();
			config.setParam("shell", shell);
		}

		if (appShell == null || appShell.length() == 0) {
			JTextArea text = new JTextArea(3, 15);
			text.setText(DefaultConfig.getDefaultConfig().getAppShell());

			Object[] messages = {
					JFDResource.MESSAGES.getString("input_app_shell"),
					new JScrollPane(text) };
			JOptionPane.showMessageDialog(null, messages);
			appShell = text.getText();
			config.setParam("app_shell", appShell);
		}
	}

	private void initConfig(Configuration config, String key, Object value) {
		if ("".equals(config.getParam(key, value))) {
			config.setParam(key, value);
		}
	}

	/**
	 * ローカル設定のバージョン管理
	 * 
	 * @param configDir
	 * @param sourceDir
	 * @throws VFSException
	 * @throws JDOMException
	 * @throws IOException
	 */
	private void checkLocalConfigVersion(VFile configDir, VFile sourceDir)
			throws VFSException, JDOMException, IOException {
		VFile configFile = configDir.getChild(JFD.LOCAL_PARAM_FILE);

		if (!configFile.exists()) {
			sourceDir.getChild(LOCAL_CONFIG_FILE).copyTo(
					configDir.getChild(LOCAL_CONFIG_FILE));
		}
	}

	private void checkEtcFilesVersion(VFile configDir, VFile sourceDir)
			throws VFSException {
		List toUpdate = new ArrayList();

		FileVersion[] fileVersions = isWindows ? ETC_FILES_XML_WIN
				: ETC_FILES_XML_UNIX;

		for (int i = 0; i < fileVersions.length; i++) {
			VFile configFile = configDir
					.getChild(fileVersions[i].getFileName());
			if (configFile.exists()) {
				double currentVersion = -1;
				if ("xml".equals(configFile.getFileName().getExtension())) {
					currentVersion = getXmlVersion(configFile);
				} else {
					currentVersion = getPropertyVersion(configFile);
				}

				if (currentVersion < fileVersions[i].getVersion()
				// TODO: バージョン番号付け間違え対応。将来（ベータ20くらい？）消す。
						|| currentVersion == 200801014) {
					toUpdate.add(fileVersions[i]);
				}
			} else {
				sourceDir.getChild(fileVersions[i].getSourceFileName()).copyTo(
						configFile);
			}
		}

		if (toUpdate.size() == 0) {
			return;
		}

		Object[] values = { fileVersionsList2Str(toUpdate) };
		String message = MessageFormat.format(JFDResource.MESSAGES
				.getString("update_config_files"), values);
		int answer = JOptionPane.showConfirmDialog(null, message, "jFD2",
				JOptionPane.YES_NO_OPTION);
		if (answer == JOptionPane.NO_OPTION) {
			return;
		}

		for (int i = 0; i < toUpdate.size(); i++) {
			VFile toFile = configDir.getChild(((FileVersion) toUpdate.get(i))
					.getFileName());
			VFile sourceFile = sourceDir.getChild(((FileVersion) toUpdate
					.get(i)).getSourceFileName());
			toFile.copyTo(toFile.getParent()
					.getChild(toFile.getName() + ".old"));
			sourceFile.copyTo(toFile);
		}
	}

	public static double getXmlVersion(VFile file) throws VFSException {
		double currentVersion = -1;

		try {
			Document doc = DomCache.getInstance().getDocument(file);
			String versionStr = doc.getRootElement().getAttributeValue(
					"version");
			if (versionStr != null && versionStr.length() > 0) {
				currentVersion = Double.parseDouble(versionStr);
			} else {
				currentVersion = 0;
			}
		} catch (JDOMException e) {
		} catch (IOException e) {
		}

		return currentVersion;
	}

	public static double getPropertyVersion(VFile file) throws VFSException {
		double currentVersion = -1;

		try {
			Properties prop = PropertiesCache.getInstance().getDocument(file);
			String versionStr = prop.getProperty("version");
			if (versionStr != null && versionStr.length() > 0) {
				currentVersion = Double.parseDouble(versionStr);
			} else {
				currentVersion = 0;
			}
		} catch (IOException e) {
		}

		return currentVersion;
	}

	private static class FileVersion {
		private String fileName;
		private String sourceFileName;
		private double version;

		public FileVersion(String fileName, String sourceFileName,
				double version) {
			this.fileName = fileName;
			this.sourceFileName = sourceFileName;
			this.version = version;
		}

		public String getFileName() {
			return fileName;
		}

		public String getSourceFileName() {
			return sourceFileName;
		}

		public double getVersion() {
			return version;
		}
	}

	private String fileVersionsList2Str(List files) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < files.size(); i++) {
			buffer.append(((FileVersion) files.get(i)).getFileName());
			if (i != files.size() - 1) {
				buffer.append(", ");
			}
		}

		return buffer.toString();
	}
}
