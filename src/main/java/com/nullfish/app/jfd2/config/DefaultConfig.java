package com.nullfish.app.jfd2.config;


public class DefaultConfig {
	private String editor;

	private boolean editorUseShell;
	
	private String extensionMapping;

	private String shell;

	private String appShell;

	private String console;

	private String openDirCommand;
	
	private boolean swapCtrlMeta = false;

	private boolean useCustomComboBox;
	
	private static DefaultConfig instance;

	public static DefaultConfig WINDOWS = new DefaultConfig(
			"notepad.exe", true, "{0}", "cmd.exe /C $Q", "cmd.exe /C \"$A $BQ\"", "start cmd.exe", "explorer /root,{0}", false, false);

	public static DefaultConfig MAC = new DefaultConfig(
			"/Applications/TextEdit.app", true,
			"{0}", "/usr/bin/open\n$C", "/usr/bin/open\n-W\n-a\n$A\n$BN", "/Applications/Utilities/Terminal.app", "{0}", true, true);

	public static DefaultConfig OTHER_UNIX = new DefaultConfig("gedit", true,
			"/usr/bin/gnome-open \"{0}\"", "/bin/sh\n-c\n$C", "/bin/sh\n-c\n$A $BQ", "gnome-terminal", "/usr/bin/gnome-open {0}", false, false);

	static  {
		String osName = System.getProperty("os.name");
		if(osName.indexOf("Windows") >= 0) {
			instance = WINDOWS;
		} else if(osName.indexOf("Mac OS") >= 0) {
			instance = MAC;
		} else{
			instance = OTHER_UNIX;
		}
	}

	private DefaultConfig(String editor, boolean editorUseShell,
			String extensionMapping, String shell, String appShell,
			String console, String openDirCommand, boolean swapCtrlMeta, boolean useCustomComboBox) {
		this.editor = editor;
		this.editorUseShell = editorUseShell;
		this.extensionMapping = extensionMapping;
		this.shell = shell;
		this.appShell = appShell;
		this.console = console;
		this.openDirCommand = openDirCommand;
		this.swapCtrlMeta = swapCtrlMeta;
		this.useCustomComboBox = useCustomComboBox;
	}

	public static DefaultConfig getDefaultConfig() {
		return instance;
	}
	                                                
	public String getEditor() {
		return editor;
	}

	public boolean isEditorUseShell() {
		return editorUseShell;
	}

	public String getExtensionMapping() {
		return extensionMapping;
	}

	public String getShell() {
		return shell;
	}

	public String getAppShell() {
		return appShell;
	}

	public String getConsole() {
		return console;
	}

	public boolean isSwapCtrlMeta() {
		return swapCtrlMeta;
	}

	public boolean isUseCustomComboBox() {
		return useCustomComboBox;
	}

	public String getOpenDirCommand() {
		return openDirCommand;
	}
}
