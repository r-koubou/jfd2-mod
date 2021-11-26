/*
 * Created on 2005/01/22
 *
 */
package com.nullfish.app.jfd2.command.embed;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.command.Command;
import com.nullfish.app.jfd2.ui.container2.ContainerPosition;
import com.nullfish.app.jfd2.ui.container2.JFD2TitleUpdater;
import com.nullfish.app.jfd2.ui.container2.JFDOwner;
import com.nullfish.app.jfd2.ui.container2.NumberedJFD2;
import com.nullfish.lib.vfs.exception.VFSException;

/**
 * @author shunji
 * 
 * Preferences - Java - Code Style - Code Templates
 */
public class ChangePositionCommand extends Command {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nullfish.app.jfd2.command.Command#closesUnusingFileSystem()
	 */
	public boolean closesUnusingFileSystem() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nullfish.lib.vfs.manipulation.abst.AbstractManipulation#doExecute()
	 */
	public void doExecute() throws VFSException {
		JFD jfd = getJFD();
		JFDOwner owner = jfd.getJFDOwner();

		if (owner == null) {
			return;
		}

		ContainerPosition position = owner.getComponentPosition(jfd);
		owner.removeComponent(jfd);
		owner.addComponent(jfd, position == ContainerPosition.MAIN_PANEL ? 
						ContainerPosition.SUB_PANEL
						: ContainerPosition.MAIN_PANEL, new JFD2TitleUpdater((NumberedJFD2)jfd));
	}

}
