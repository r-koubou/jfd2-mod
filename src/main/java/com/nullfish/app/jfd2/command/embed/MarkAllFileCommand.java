/*
 * Created on 2004/05/31
 *
 */
package com.nullfish.app.jfd2.command.embed;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.JFDModel;
import com.nullfish.app.jfd2.command.Command;
import com.nullfish.lib.vfs.exception.VFSException;

/**
 * 全ファイルマークコマンド（Homeボタンのアレ)
 * 
 * @author shunji
 */
public class MarkAllFileCommand extends Command {
	/* (non-Javadoc)
	 * @see com.nullfish.lib.vfs.manipulation.abst.AbstractManipulation#doExecute()
	 */
	public void doExecute() throws VFSException {
		JFD jfd = getJFD();
		JFDModel model = jfd.getModel();
		try {
			model.setAutoNotify(false);
			
			if(isMarked(model)) {
				clearAll(model);
			} else {
				markOnlyFile(model);
			}
		} finally {
			model.setAutoNotify(true);
		}
	}
	
	private boolean isMarked(JFDModel model) {
		int length = model.getFilesCount();
		for(int i=0; i<length; i++) {
			if(model.isMarked(i)) {
				return true;
			}
		}
		
		return false;
	}
	
	private void clearAll(JFDModel model) {
		int length = model.getFilesCount();
		for(int i=0; i<length; i++) {
			model.setMarked(i, false);
		}
	}
	
	private void markOnlyFile(JFDModel model) throws VFSException {
		int length = model.getFilesCount();
		for(int i=0; i<length; i++) {
			if(model.getFileAt(i).isFile(this)) {
				model.setMarked(i, true);
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.command.Command#closesUnusingFileSystem()
	 */
	public boolean closesUnusingFileSystem() {
		return false;
	}
}
