/*
 * Created on 2004/07/06
 *
 */
package com.nullfish.app.jfd2.command.groovy;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import org.codehaus.groovy.control.CompilationFailedException;

import com.nullfish.app.jfd2.command.Command;
import com.nullfish.app.jfd2.ext_command.window.ConsoleFrame;
import com.nullfish.app.jfd2.util.GroovyScriptCache;
import com.nullfish.lib.vfs.Manipulation;
import com.nullfish.lib.vfs.VFS;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;

/**
 * @author shunji
 *
 */
public class GroovyCommand extends Command {
	/**
	 * スクリプトファイルパス
	 */
	public static final String SCRIPT_PATH = "script";
	
	private static GroovyScriptCache cache = new GroovyScriptCache();
	
	/* (non-Javadoc)
	 * @see com.nullfish.lib.vfs.manipulation.abst.AbstractManipulation#doExecute()
	 */
	public void doExecute() throws VFSException {
		try {
			VFS vfs = VFS.getInstance(getJFD());
			Binding binding = new Binding();
			binding.setVariable("jfd", getJFD());
			binding.setVariable("command", this);
			binding.setVariable("console", ConsoleFrame.getInstance());
			String scriptDirPath = (String)getJFD().getCommonConfiguration().getParam(GroovySelectCommand.SCRIPT_DIR_PATH, GroovySelectCommand.DEFAULT_SCRIPT_DIR);
			VFile scriptDir = vfs.getFile(scriptDirPath);
			boolean found = false;
			String path = (String)getParameter(SCRIPT_PATH);
			VFile script =null;
			try {
				script = scriptDir.getChild(path);
				if(script.exists(this)) {
					found = true;
				}
			} catch (VFSException e) {
			}
			if(!found) {
				script = vfs.getFile(path);
			}
			cache.makeScript(script, binding).run(); 
		} catch (CompilationFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (VFSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 子操作を設定する。
	 * @param children
	 */
	public void setChildManipulations(Manipulation[] children) {
		super.setChildManipulations(children);
	}
	
	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.command.Command#closesUnusingFileSystem()
	 */
	public boolean closesUnusingFileSystem() {
		return false;
	}
	
}
