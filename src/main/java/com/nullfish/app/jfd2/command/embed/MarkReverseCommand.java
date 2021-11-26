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
 * ファイルマーク反転コマンド（Endボタンのアレ)
 * 
 * @author shunji
 */
public class MarkReverseCommand extends Command {
	/* (non-Javadoc)
	 * @see com.nullfish.lib.vfs.manipulation.abst.AbstractManipulation#doExecute()
	 */
	public void doExecute() throws VFSException {
		JFD jfd = getJFD();
		JFDModel model = jfd.getModel();
		try {
			model.setAutoNotify(false);
			
			int length = model.getFilesCount();
			for(int i=0; i<length; i++) {
				model.setMarked(i, !model.isMarked(i));
			}
		} finally {
			model.setAutoNotify(true);
		}
	}

	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.command.Command#closesUnusingFileSystem()
	 */
	public boolean closesUnusingFileSystem() {
		return false;
	}
}
