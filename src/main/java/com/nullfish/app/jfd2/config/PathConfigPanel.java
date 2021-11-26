package com.nullfish.app.jfd2.config;

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.jdom.JDOMException;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.command.embed.EditCommand;
import com.nullfish.app.jfd2.command.embed.ExtensionMapCommand;
import com.nullfish.app.jfd2.resource.JFDResource;
import com.nullfish.lib.tablelayout.HtmlTablePanel;
import com.nullfish.lib.vfs.VFS;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;

public class PathConfigPanel extends JPanel implements ConfigPanel {
	private VFile configDir;

	public static final String DIRECTORY_LAYOUT = "classpath:///resources/option_layout_directory_tab.xml";

	//
	//	ディレクトリタブ
	private HtmlTablePanel directoryPanel;

	private PathConfig editorPathConfig = new PathConfig("editor", "editor", JFileChooser.FILES_ONLY);
	private JCheckBox editorUseShellCheckBox = new JCheckBox(JFDResource.LABELS.getString("use_shell"));
//	private PathConfig shellPathConfig = new PathConfig("shell", "shell", JFileChooser.FILES_ONLY);
	private PathConfig scriptDirConfig = new PathConfig("script_dir", "script_dir", JFileChooser.DIRECTORIES_ONLY);
	private PathConfig shortcutDirConfig = new PathConfig("shortcut_dir", "shortcut_dir", JFileChooser.DIRECTORIES_ONLY);
	private PathConfig tempDirConfig = new PathConfig("temp_dir", "temp_dir", JFileChooser.DIRECTORIES_ONLY);
	private PathConfig userConfigConfig = new PathConfig("user_conf_dir", "user_conf_dir", JFileChooser.DIRECTORIES_ONLY);
	private PathConfig pluginDirConfig = new PathConfig("plugin_dir", "plugin_dir", JFileChooser.DIRECTORIES_ONLY);
	private PathConfig libDirConfig = new PathConfig("lib_dir", "lib_dir", JFileChooser.DIRECTORIES_ONLY);

	private JLabel extensionMapperConfigLabel = new JLabel(JFDResource.LABELS.getString("extension_mapper"));
	private JTextField extensionMapperConfig = new JTextField(30);
	
	private JLabel shellConfigLabel = new JLabel(JFDResource.LABELS.getString("shell"));
	private JTextArea shellConfig = new JTextArea(3, 25);

	private JLabel appShellConfigLabel = new JLabel(JFDResource.LABELS.getString("app_shell"));
	private JTextArea appShellConfig = new JTextArea(3, 25);

	private JLabel openDirConfigLabel = new JLabel(JFDResource.LABELS.getString("open_dir_command"));
	private JTextArea openDirConfig = new JTextArea(3, 25);

	private JLabel pathNotice = new JLabel(JFDResource.LABELS.getString("path_notice"));
	private JLabel shellNotice = new JLabel(JFDResource.LABELS.getString("shell_notice"));
	private JLabel appshellNotice = new JLabel(JFDResource.LABELS.getString("appshell_notice"));

	private JLabel consoleConfigLabel = new JLabel(JFDResource.LABELS.getString("console"));
	private JTextArea consoleConfig = new JTextArea(3, 25);

	public PathConfigPanel() {
		super(new BorderLayout());
		try {
			initGui();
			layoutComponent();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void initGui() throws Exception {
		scriptDirConfig.setEscapesQuate(false);
		shortcutDirConfig.setEscapesQuate(false);
		tempDirConfig.setEscapesQuate(false);
		userConfigConfig.setEscapesQuate(false);
		pluginDirConfig.setEscapesQuate(false);
		libDirConfig.setEscapesQuate(false);
		
		directoryPanel = new HtmlTablePanel(VFS.getInstance().getFile(
				DIRECTORY_LAYOUT).getInputStream());
	}

	public void layoutComponent() {
		add(directoryPanel);
		directoryPanel.addComponent(editorPathConfig, "editor");
		directoryPanel.addComponent(editorUseShellCheckBox, "editor_use_shell");
//		directoryPanel.addComponent(shellPathConfig, "shell");
		directoryPanel.addComponent(scriptDirConfig, "script_dir");
		directoryPanel.addComponent(pluginDirConfig, "plugin_dir");
		directoryPanel.addComponent(shortcutDirConfig, "shortcut_dir");
		directoryPanel.addComponent(libDirConfig, "lib_dir");
		directoryPanel.addComponent(tempDirConfig, "temp_dir");
		directoryPanel.addComponent(userConfigConfig, "user_conf_dir");
		directoryPanel.addComponent(pathNotice, "path_notice");
		directoryPanel.addComponent(extensionMapperConfigLabel, "extension_mapper_label");
		directoryPanel.addComponent(extensionMapperConfig, "extension_mapper_input");
		directoryPanel.addComponent(shellConfigLabel, "shell_label");
		directoryPanel.addComponent(new JScrollPane(shellConfig), "shell_input");
		directoryPanel.addComponent(shellNotice, "shell_notice");
		directoryPanel.addComponent(appshellNotice, "appshell_notice");
		directoryPanel.addComponent(openDirConfigLabel, "open_dir_label");
		directoryPanel.addComponent(new JScrollPane(openDirConfig), "open_dir_input");

		directoryPanel.addComponent(appShellConfigLabel, "app_shell_label");
		directoryPanel.addComponent(new JScrollPane(appShellConfig), "app_shell_input");
		
		directoryPanel.addComponent(consoleConfigLabel, "console_label");
		directoryPanel.addComponent(new JScrollPane(consoleConfig), "console_input");
	}

	/**
	 * 設定を読み込む
	 * @param configDir
	 * @throws JDOMException
	 * @throws IOException
	 * @throws VFSException
	 */
	public void loadPreference(VFile configDir) throws Exception {
		this.configDir = configDir;
		Configuration commonConfig = Configuration.getInstance(configDir
				.getChild(JFD.COMMON_PARAM_FILE));
		String appShell = (String)commonConfig.getParam("app_shell", DefaultConfig.getDefaultConfig().getAppShell());
		editorPathConfig.setEscapesQuate(appShell.indexOf("\n") == -1);
		editorPathConfig.setConfiguration(commonConfig);
		editorUseShellCheckBox.setSelected(((Boolean)commonConfig.getParam(EditCommand.PARAM_USE_SHELL, Boolean.valueOf(DefaultConfig.getDefaultConfig().isEditorUseShell()))).booleanValue());
//		shellPathConfig.setConfiguration(commonConfig);
		scriptDirConfig.setConfiguration(commonConfig);
		pluginDirConfig.setConfiguration(commonConfig);
		libDirConfig.setConfiguration(commonConfig);
		shortcutDirConfig.setConfiguration(commonConfig);
		tempDirConfig.setConfiguration(commonConfig);
		userConfigConfig.setConfiguration(commonConfig);
		
		extensionMapperConfig.setText((String)commonConfig.getParam("extension_mapper", DefaultConfig.getDefaultConfig().getExtensionMapping()));
		shellConfig.setText((String)commonConfig.getParam("shell", DefaultConfig.getDefaultConfig().getShell()));
		appShellConfig.setText((String)commonConfig.getParam("app_shell", DefaultConfig.getDefaultConfig().getAppShell()));
		consoleConfig.setText((String)commonConfig.getParam("open_console_command", DefaultConfig.getDefaultConfig().getConsole()));
		openDirConfig.setText((String)commonConfig.getParam(ExtensionMapCommand.PARAM_DIR_OPEN, DefaultConfig.getDefaultConfig().getOpenDirCommand()));
	}

	/***
	 * 入力を設定に反映する。
	 * @throws JDOMException
	 * @throws IOException
	 * @throws VFSException
	 */
	public void apply() throws JDOMException, IOException, VFSException {
		Configuration commonConfig = Configuration.getInstance(configDir
				.getChild(JFD.COMMON_PARAM_FILE));
		editorPathConfig.apply(commonConfig);
		commonConfig.setParam(EditCommand.PARAM_USE_SHELL, Boolean.valueOf(editorUseShellCheckBox.isSelected()));
//		shellPathConfig.apply(commonConfig);
		scriptDirConfig.apply(commonConfig);
		pluginDirConfig.apply(commonConfig);
		shortcutDirConfig.apply(commonConfig);
		libDirConfig.apply(commonConfig);
		tempDirConfig.apply(commonConfig);
		userConfigConfig.apply(commonConfig);

		commonConfig.setParam("extension_mapper", extensionMapperConfig.getText());
		commonConfig.setParam("shell", shellConfig.getText());
		commonConfig.setParam("app_shell", appShellConfig.getText());
		commonConfig.setParam("open_console_command", consoleConfig.getText());
		commonConfig.setParam(ExtensionMapCommand.PARAM_DIR_OPEN, openDirConfig.getText());
	}
	
	public 	String getTitle() {
		return JFDResource.LABELS.getString("path");
	}
}
