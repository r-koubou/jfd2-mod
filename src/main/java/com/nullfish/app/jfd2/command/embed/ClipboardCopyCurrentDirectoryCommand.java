/*
 * Created on 2004/05/31
 *
 */
package com.nullfish.app.jfd2.command.embed;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.command.Command;
import com.nullfish.app.jfd2.dnd.TransferableVFileList;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;

/**
 * クリップボードにカレントディレクトリをコピーするコマンド
 * 
 * @author shunji
 */
public class ClipboardCopyCurrentDirectoryCommand extends Command implements
		ClipboardOwner {
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nullfish.lib.vfs.manipulation.abst.AbstractManipulation#doExecute()
	 */
	public void doExecute() throws VFSException {
		JFD jfd = getJFD();
		VFile currentDir = jfd.getModel().getCurrentDirectory();
		VFile[] files = { currentDir };
		TransferableVFileList list = new TransferableVFileList(files, jfd, false);
		Toolkit.getDefaultToolkit().getSystemClipboard()
				.setContents(list, this);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.datatransfer.ClipboardOwner#lostOwnership(java.awt.datatransfer.Clipboard,
	 *      java.awt.datatransfer.Transferable)
	 */
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		// TODO Auto-generated method stub

	}
	
	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.command.Command#closesUnusingFileSystem()
	 */
	public boolean closesUnusingFileSystem() {
		return false;
	}

}
