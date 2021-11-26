/*
 * Created on 2004/05/31
 *
 */
package com.nullfish.app.jfd2.command.embed;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.JFDComponent;
import com.nullfish.app.jfd2.command.Command;
import com.nullfish.app.jfd2.ui.container2.JFDOwner;
import com.nullfish.lib.vfs.VFS;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;

/**
 * 反対側jFD2のカレントディレクトリを同じにするコマンド
 * 
 * @author shunji
 */
public class SameDirectoryCommand extends Command {
	public static final String DIRECTION = "direction";
	
	/* (non-Javadoc)
	 * @see com.nullfish.lib.vfs.manipulation.abst.AbstractManipulation#doExecute()
	 */
	public void doExecute() throws VFSException {
		JFD jfd = getJFD();
		JFDOwner owner = jfd.getJFDOwner();
		JFDComponent opponent = owner.getComponent(owner.getComponentPosition(jfd).getOpponent());
		
		if(opponent == null || !(opponent instanceof JFD)) {
			return;
		}
		
		String direction = (String)getParameter(DIRECTION);
		
		JFD other = (JFD)opponent;
		
		if(direction == null || "this2opposit".equals(direction)) {
			copyCurrentDir(jfd, other);
		} else {
			copyCurrentDir(other, jfd);
		}
	}
	
	private void copyCurrentDir(JFD from, JFD to) throws VFSException {
		VFile currentDir = VFS.getInstance(to).getFile(from.getModel().getCurrentDirectory().getAbsolutePath());
		VFile selected = VFS.getInstance(to).getFile(from.getModel().getSelectedFile().getAbsolutePath());
		to.getModel().setDirectoryAsynchIfNecessary(currentDir, selected, to);
	}
	
	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.command.Command#closesUnusingFileSystem()
	 */
	public boolean closesUnusingFileSystem() {
		return false;
	}
}

