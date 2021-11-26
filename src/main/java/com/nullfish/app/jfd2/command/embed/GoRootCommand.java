/*
 * Created on 2004/05/31
 *
 */
package com.nullfish.app.jfd2.command.embed;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.JFDModel;
import com.nullfish.app.jfd2.command.Command;
import com.nullfish.lib.vfs.VFS;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;
import com.nullfish.lib.vfs.impl.root.RootFileName;

/**
 * ルートディレクトリ移動コマンド
 * 
 * @author shunji
 */
public class GoRootCommand extends Command {
	/* (non-Javadoc)
	 * @see com.nullfish.lib.vfs.manipulation.abst.AbstractManipulation#doExecute()
	 */
	public void doExecute() throws VFSException {
		JFD jfd = getJFD();
		JFDModel model = jfd.getModel();
		VFile current = model.getCurrentDirectory();
		
		if(current.isRoot()) {
			VFile mountPoint = current.getFileSystem().getMountPoint();
			if(mountPoint != null) {
				model.setDirectoryAsynchIfNecessary(mountPoint.getParent(), mountPoint, jfd);
			} else {
//				model.setDirectory(current, 0);
				model.setDirectory(VFS.getInstance(jfd).getFile(RootFileName.getInstance()), current);
			}
			return;
		}
		
		VFile root = current.getFileSystem().getVFS().getFile( current.getFileSystem().getRootName() );
		
		model.setDirectoryAsynchIfNecessary(root, root, jfd);
	}

	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.command.Command#closesUnusingFileSystem()
	 */
	public boolean closesUnusingFileSystem() {
		return true;
	}
}
