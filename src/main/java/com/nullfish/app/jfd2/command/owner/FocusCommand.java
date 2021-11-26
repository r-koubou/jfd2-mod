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
public class FocusCommand extends OwnerCommand {
	public static final String POSITION = "position";
	
	/* (non-Javadoc)
	 * @see com.nullfish.lib.vfs.manipulation.abst.AbstractManipulation#doExecute()
	 */
	public void doExecute() throws VFSException {
		JFDOwner owner = getJFDOwner();
		
		ContainerPosition position = null;
		if("main".equals(getParameter(POSITION))) {
			position = ContainerPosition.MAIN_PANEL;
		} else if("sub".equals(getParameter(POSITION))) {
			position = ContainerPosition.SUB_PANEL;
		}
		
		JFDComponent component = owner.getComponent(position);
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
