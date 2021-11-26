/*
 * Created on 2004/05/31
 *
 */
package com.nullfish.app.jfd2.command.embed;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.command.Command;
import com.nullfish.app.jfd2.command.JFDException;
import com.nullfish.app.jfd2.ui.container2.JFDFrame;
import com.nullfish.app.jfd2.ui.container2.JFDOwner;
import com.nullfish.lib.vfs.exception.VFSException;

/**
 * 終了コマンド
 * 
 * @author shunji
 */
public class ExitCommand extends Command {
	/* (non-Javadoc)
	 * @see com.nullfish.lib.vfs.manipulation.abst.AbstractManipulation#doExecute()
	 */
	public void doExecute() throws VFSException {
		JFD jfd = getJFD();
		JFDOwner owner = jfd.getJFDOwner();
		if(owner != null) {
			if(owner instanceof JFDFrame) {
				try {
					JFDFrame.saveSizeTabConfig(owner.getConfigDirectory());
				} catch (Exception e) {
					throw new JFDException(e, "Config error", new Object[0]);
				}
			}
			
			owner.removeComponent(jfd);
		}

		try {
			jfd.save(owner.getConfigDirectory());
		} catch (VFSException e) {
			e.printStackTrace();
		}

		jfd.dispose();
		
		if(owner.getCount() == 0) {
			owner.dispose();
		}
	}

	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.command.Command#closesUnusingFileSystem()
	 */
	public boolean closesUnusingFileSystem() {
		return false;
	}
}
