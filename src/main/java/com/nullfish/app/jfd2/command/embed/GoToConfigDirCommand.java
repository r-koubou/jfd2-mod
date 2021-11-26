/*
 * Created on 2004/05/31
 *
 */
package com.nullfish.app.jfd2.command.embed;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.command.Command;
import com.nullfish.lib.vfs.VFS;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;

/**
 * 親ディレクトリ移動コマンド
 * 
 * @author shunji
 */
public class GoToConfigDirCommand extends Command {
	public static final String CONFIG_NAME = "config_name";
	
	/* (non-Javadoc)
	 * @see com.nullfish.lib.vfs.manipulation.abst.AbstractManipulation#doExecute()
	 */
	public void doExecute() throws VFSException {
		JFD jfd = getJFD();
		String path = (String)jfd.getCommonConfiguration().getParam((String)getParameter(CONFIG_NAME), null);
System.out.println(path);
		if(path == null) {
			return;
		}
		VFile dir = VFS.getInstance(jfd).getFile(path);
		jfd.getModel().setDirectoryAsynchIfNecessary(dir, dir.getParent(), jfd);
	}

	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.command.Command#closesUnusingFileSystem()
	 */
	public boolean closesUnusingFileSystem() {
		return true;
	}
}
