/*
 * Created on 2004/05/31
 *
 */
package com.nullfish.app.jfd2.command.embed;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.JFDModel;
import com.nullfish.app.jfd2.command.Command;
import com.nullfish.lib.vfs.FileSystem;
import com.nullfish.lib.vfs.VFS;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;

/**
 * 親ディレクトリ移動コマンド
 * 
 * @author shunji
 */
public class GoParentCommand extends Command {
	/* (non-Javadoc)
	 * @see com.nullfish.lib.vfs.manipulation.abst.AbstractManipulation#doExecute()
	 */
	public void doExecute() throws VFSException {
		JFD jfd = getJFD();
		JFDModel model = jfd.getModel();
		VFile currentDirectory = model.getCurrentDirectory();
		if(!currentDirectory.isRoot()) {
			VFile parent = currentDirectory.getParent();
			model.setDirectoryAsynchIfNecessary(parent, currentDirectory, jfd);
		} else {
			VFile mountPoint = currentDirectory.getFileSystem().getMountPoint();
			if(mountPoint != null) {
				model.setDirectoryAsynchIfNecessary(mountPoint.getParent(), mountPoint, jfd);
				return;
			}
//			model.setDirectory(currentDirectory, 0);
			model.setDirectory(VFS.getInstance(jfd).getFile("root:///"), currentDirectory);
		}
	}

	public FileSystem[] getUsingFileSystems() {
		if(currentDir.isRoot() && currentDir.getFileSystem().getMountPoint() != null) {
			FileSystem[] rtn = {
					currentDir.getFileSystem(),
					currentDir.getFileSystem().getMountPoint().getFileSystem()
			};
			
			return rtn;
		}
		
		return null;
	}
	
	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.command.Command#closesUnusingFileSystem()
	 */
	public boolean closesUnusingFileSystem() {
		return true;
	}
}
