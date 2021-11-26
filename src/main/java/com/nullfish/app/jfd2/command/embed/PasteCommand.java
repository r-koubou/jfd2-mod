/*
 * Created on 2004/05/31
 *
 */
package com.nullfish.app.jfd2.command.embed;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.JFDModel;
import com.nullfish.app.jfd2.command.Command;
import com.nullfish.app.jfd2.dnd.TransferableVFileList;
import com.nullfish.lib.vfs.Manipulation;
import com.nullfish.lib.vfs.VFS;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;
import com.nullfish.lib.vfs.exception.VFSIOException;
import com.nullfish.lib.vfs.manipulation.CopyFileManipulation;
import com.nullfish.lib.vfs.manipulation.MoveManipulation;

/**
 * ペーストコマンド
 * 
 * @author shunji
 */
public class PasteCommand extends Command {
	/**
	 * doExecuteのオーバーライド。
	 */
	public void doExecute() throws VFSException {
		VFile[] froms = null;
		try {
			showProgress(1000);
			
			JFD jfd = getJFD();
			JFDModel model = jfd.getModel();

			Clipboard clipboard = Toolkit.getDefaultToolkit()
					.getSystemClipboard();
			Transferable data = clipboard.getContents(this);
			boolean cut = false;
			
			VFS vfs = VFS.getInstance(jfd);
			
			if (data.isDataFlavorSupported(TransferableVFileList.LOCAL_VFILELIST_FLAVOR)) {
				TransferableVFileList fileList = (TransferableVFileList)data.getTransferData(TransferableVFileList.LOCAL_VFILELIST_FLAVOR);
				
				froms = fileList.getFiles();
				cut = fileList.isCut();
			} else if (data.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				java.util.List fileList = (java.util.List) (data
						.getTransferData(DataFlavor.javaFileListFlavor));

				froms = new VFile[fileList.size()];

				for (int i = 0; i < fileList.size(); i++) {
					File file = (File) fileList.get(i);
					froms[i] = vfs.getFile(file.getAbsolutePath());
				}
			} else {
				String text = (String) data
						.getTransferData(DataFlavor.stringFlavor);

				String[] paths = text.split("[\r\n]");
				froms = new VFile[paths.length];

				for (int i = 0; i < paths.length; i++) {
					froms[i] = vfs.getFile(paths[i]);
				}
			}
			
			for(int i=0; froms != null && i<froms.length; i++) {
				froms[i].getFileSystem().registerUser(this);
			}

			Manipulation[] copyManipulations = initCopyManipulations(froms,
						model.getCurrentDirectory(), cut);
			
			setChildManipulations(copyManipulations);

			for (int i = 0; i < copyManipulations.length; i++) {
				copyManipulations[i].execute();
			}
		} catch (UnsupportedFlavorException e) {
		} catch (IOException e) {
			new VFSIOException(e);
		} finally {
			for(int i=0; froms != null && i<froms.length; i++) {	
				froms[i].getFileSystem().removeUser(this);
			}
		}
	}

	/**
	 * 子操作を初期化する。
	 */
	private Manipulation[] initCopyManipulations(VFile[] from,
			VFile dest, boolean cut) throws VFSException {
		CopyOverwritePolicy policy = new CopyOverwritePolicy(this);
		
		if(!cut) {
			CopyFileManipulation[] rtn = new CopyFileManipulation[from.length];
			for (int i = 0; i < from.length; i++) {
				rtn[i] = from[i].getManipulationFactory().getCopyFileManipulation(
						from[i]);
				rtn[i].setParentManipulation(this);
				rtn[i].setDest(dest.getChild(from[i].getName()));
				rtn[i].setOverwritePolicy(policy);
			}
			for (int i = 0; i < rtn.length; i++) {
				rtn[i].prepare();
			}
			
			return rtn;
		} else {
			MoveManipulation[] rtn = new MoveManipulation[from.length];
			for (int i = 0; i < from.length; i++) {
				rtn[i] = from[i].getManipulationFactory().getMoveManipulation(
						from[i]);
				rtn[i].setParentManipulation(this);
				rtn[i].setDest(dest.getChild(from[i].getName()));
				rtn[i].setOverwritePolicy(policy);
			}
			for (int i = 0; i < rtn.length; i++) {
				rtn[i].prepare();
			}
			
			return rtn;
		}
	}

	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.command.Command#closesUnusingFileSystem()
	 */
	public boolean closesUnusingFileSystem() {
		return true;
	}
}
