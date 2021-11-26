/*
 * Created on 2005/01/04
 *
 */
package com.nullfish.app.jfd2.command.groovy;

import groovy.lang.Binding;

import java.io.File;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.command.Command;
import com.nullfish.app.jfd2.ext_command.window.ConsoleFrame;
import com.nullfish.lib.vfs.Manipulation;
import com.nullfish.lib.vfs.VFS;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;

/**
 * @author shunji
 *
 */
public abstract class AbstractGroovyCommand extends Command {
	public static final String DEFAULT_SCRIPT_DIR = new File(new File(new File(System.getProperty("user.home")), ".jfd_user"), "script").getAbsolutePath();

	/**
	 * スクリプトファイルのディレクトリ
	 */
	public static final String SCRIPT_DIR_PATH = "script_dir";
	/**
	 * スクリプトファイル
	 */
	public static final String SCRIPT = "script";
	/**
	 * スクリプトディレクトリオープンコマンド
	 */
	public static final String OPEN_SCRIPT_DIR = "openScriptDir";
	/**
	 * 実行後、不要ファイルシステムをクローズするかのフラグ
	 */
	private boolean closesUnusingFileSystem = false;

	/**
	 * 子操作を設定する。
	 * @param children
	 */
	public void setChildManipulations(Manipulation[] children) {
		super.setChildManipulations(children);
	}

	public void setClosesUnusingFileSystem(boolean closesUnusingFileSystem) {
		this.closesUnusingFileSystem = closesUnusingFileSystem;
	}

	public boolean closesUnusingFileSystem() {
		return closesUnusingFileSystem;
	}

	protected VFile getScriptDirectory() throws VFSException {
		VFS vfs = VFS.getInstance(getJFD());
		JFD jfd = getJFD();
		String scriptDirPath = (String)jfd.getCommonConfiguration().getParam(SCRIPT_DIR_PATH, DEFAULT_SCRIPT_DIR);
		return vfs.getFile(scriptDirPath);
	}
	
	protected Binding getBinding() {
		Binding binding = new Binding();
		binding.setVariable("jfd", getJFD());
		binding.setVariable("command", this);
		binding.setVariable("vfs", VFS.getInstance(getJFD()));
		binding.setVariable("console", ConsoleFrame.getInstance());
		
		return binding;
	}

	protected void showErrorMessage(Exception e) {
//		ConsoleFrame.getInstance().println(e);
		ConsoleFrame.getInstance().printStackTrace(e);
	}
}
