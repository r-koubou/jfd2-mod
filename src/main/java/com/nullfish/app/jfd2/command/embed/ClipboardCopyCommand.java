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
import com.nullfish.app.jfd2.JFDModel;
import com.nullfish.app.jfd2.command.Command;
import com.nullfish.app.jfd2.dnd.TransferableVFileList;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;

/**
 * クリップボードにカレントディレクトリをコピーするコマンド
 * 
 * @author shunji
 */
public class ClipboardCopyCommand extends Command implements
		ClipboardOwner {
	public static final String MODE = "mode";
	
	public static final String MODE_COPY = "copy";
	
	public static final String MODE_CUT = "cut";
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nullfish.lib.vfs.manipulation.abst.AbstractManipulation#doExecute()
	 */
	public void doExecute() throws VFSException {
		JFD jfd = getJFD();
		JFDModel model = jfd.getModel();
		VFile[] markedFiles = model.getMarkedFiles();
		
		VFile[] files;
		if(markedFiles.length > 0) {
			files = markedFiles;
		} else {
			files = new VFile[1];
			files[0] = model.getSelectedFile();
		}

		boolean cut = MODE_CUT.equals(getParameter(MODE));
		
		TransferableVFileList list = new TransferableVFileList(files, jfd, cut);
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
