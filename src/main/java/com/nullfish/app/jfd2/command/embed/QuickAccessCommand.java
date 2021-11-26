/*
 * Created on 2004/05/31
 *
 */
package com.nullfish.app.jfd2.command.embed;

import java.io.File;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.command.Command;
import com.nullfish.lib.vfs.VFS;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;

/**
 * ショートカットディレクトリオープンコマンド
 * 
 * @author shunji
 */
public class QuickAccessCommand extends Command {
	public static final String SHORTCUT_DIR = "shortcut_dir";
	
	public static final String DEFAULT_SHORTCUT_DIR = new File(new File(new File(System.getProperty("user.home")), ".jfd_user"), "shortcut").getAbsolutePath();
	
	/* (non-Javadoc)
	 * @see com.nullfish.lib.vfs.manipulation.abst.AbstractManipulation#doExecute()
	 */
	public void doExecute() throws VFSException {
		VFS vfs = VFS.getInstance(getJFD());
		JFD jfd = getJFD();
		String shortCutDirPath = (String)jfd.getCommonConfiguration().getParam(SHORTCUT_DIR, DEFAULT_SHORTCUT_DIR);
		VFile shortCutDir = vfs.getFile(shortCutDirPath);
		if(!shortCutDir.exists(this)) {
			shortCutDir.createDirectory(this);
		}
		
		jfd.getModel().setDirectoryAsynchIfNecessary(shortCutDir, shortCutDir.getParent(), jfd);
    }

	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.command.Command#closesUnusingFileSystem()
	 */
	public boolean closesUnusingFileSystem() {
		return false;
	}
}
