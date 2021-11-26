package com.nullfish.app.jfd2.command.owner;

import com.nullfish.app.jfd2.JFDComponent;
import com.nullfish.app.jfd2.ui.container2.ContainerPosition;
import com.nullfish.app.jfd2.ui.container2.JFDOwner;
import com.nullfish.lib.vfs.exception.VFSException;

/**
 * 反対側コンポーネントフォーカスコマンド
 * 
 * @author shunji
 */
public class FocusOpponentCommand extends OwnerCommand {
	/* (non-Javadoc)
	 * @see com.nullfish.lib.vfs.manipulation.abst.AbstractManipulation#doExecute()
	 */
	public void doExecute() throws VFSException {
		JFDOwner owner = getJFDOwner();
		
		JFDComponent c = owner.getActiveComponent();
		if(c == null) {
			return;
		}
		
		ContainerPosition position = owner.getComponentPosition(c);
		JFDComponent component = owner.getComponent(position.getOpponent());
		if(component != null) {
			owner.setActiveComponent(component);
		}
    }

	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.command.Command#closesUnusingFileSystem()
	 */
	public boolean closesUnusingFileSystem() {
		return false;
	}
}
