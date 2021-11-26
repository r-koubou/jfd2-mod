package com.nullfish.app.jfd2.command.embed;

import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;

import com.nullfish.app.jfd2.JFDComponent;
import com.nullfish.app.jfd2.command.Command;
import com.nullfish.app.jfd2.ui.tree.TreeDialog;
import com.nullfish.lib.ui.UIUtilities;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;

/**
 * ツリー表示コマンド
 * 
 * @author shunji
 */
public class TreeCommand extends Command {
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nullfish.lib.vfs.manipulation.abst.AbstractManipulation#doExecute()
	 */
	public void doExecute() throws VFSException {
		Container owner = UIUtilities.getTopLevelOwner((Container)((JFDComponent)getJFD()).getComponent());
		TreeDialog dialog = null;
		try {
			if(owner instanceof Frame) {
				dialog = new TreeDialog((Frame)owner, getJFD());
			} else {
				dialog = new TreeDialog((Dialog)owner, getJFD());
			}
			dialog.setSelectedFile(getJFD().getModel().getCurrentDirectory());
			dialog.pack();
			dialog.setLocationRelativeTo(owner);
			dialog.setVisible(true);
			
			VFile dir = dialog.getSelectedFile();
			if(dir != null) {
				VFile selected = dir.getParent();
				if(selected != null) {
					getJFD().getModel().setDirectoryAsynchIfNecessary(dir, selected, getJFD());
				} else {
					getJFD().getModel().setDirectoryAsynchIfNecessary(dir, 1, getJFD());
				}
			}
		} finally {
			try {
				dialog.dispose();
			} catch (Exception e) {}
		}
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nullfish.app.jfd2.command.Command#closesUnusingFileSystem()
	 */
	public boolean closesUnusingFileSystem() {
		return false;
	}
}
