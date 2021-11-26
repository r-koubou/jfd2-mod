/*
 * Created on 2004/08/26
 *
 */
package com.nullfish.app.jfd2.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.command.Command;
import com.nullfish.app.jfd2.command.embed.DragAndDropCopyCommand;
import com.nullfish.app.jfd2.command.embed.DragAndDropMoveCommand;
import com.nullfish.lib.vfs.FileSystem;
import com.nullfish.lib.vfs.VFS;
import com.nullfish.lib.vfs.VFile;

/**
 * @author shunji
 * 
 * Preferences - Java - Code Style - Code Templates
 */
public abstract class AbstractJFDDropTargetListener extends DropTargetAdapter {
	public AbstractJFDDropTargetListener() {
		super();
	}

	public abstract JFD getJFD(DropTargetDropEvent dtde);

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.dnd.DropTargetListener#drop(java.awt.dnd.DropTargetDropEvent)
	 */
	public void drop(DropTargetDropEvent dtde) {
		JFD jfd = getJFD(dtde);
		if (jfd == null) {
			return;
		}
		VFS vfs = VFS.getInstance(jfd);
		try {
			int action = dtde.getDropAction();

			if ((action | DnDConstants.ACTION_COPY_OR_MOVE) == 0) {
				dtde.rejectDrop();
				return;
			}

			dtde.acceptDrop(action);
			VFile[] froms;
			Transferable t = dtde.getTransferable();

			if (dtde.isDataFlavorSupported(TransferableVFileList.LOCAL_VFILELIST_FLAVOR)) {
				TransferableVFileList fileList = (TransferableVFileList)t.getTransferData(TransferableVFileList.LOCAL_VFILELIST_FLAVOR);
				if(fileList.getFrom() == jfd) {
					return;
				}
				
				froms = fileList.getFiles();
			} else if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				java.util.List fileList = (java.util.List) (t
						.getTransferData(DataFlavor.javaFileListFlavor));

				dtde.dropComplete(true);

				froms = new VFile[fileList.size()];

				for (int i = 0; i < fileList.size(); i++) {
					File file = (File) fileList.get(i);
					froms[i] = vfs.getFile(file.getAbsolutePath());
				}
			} else {
				String data = (String) t
						.getTransferData(DataFlavor.stringFlavor);
				dtde.dropComplete(true);

				String[] paths = data.split("\n");

				froms = new VFile[paths.length];
				for (int i = 0; i < paths.length; i++) {
					froms[i] = vfs.getFile(paths[i]);
				}
			}
			VFile to = jfd.getModel().getCurrentDirectory();

			FileSystem fromFileSystem = froms[0].getFileSystem();
			FileSystem toFileSystem = to.getFileSystem();
			
			Command command;
			if (action == DnDConstants.ACTION_COPY) {
				if(fromFileSystem.equals(toFileSystem)) {
					command = new DragAndDropCopyCommand(froms, to);
				} else {
					command = new DragAndDropMoveCommand(froms, to);
				}
			} else {
				if(fromFileSystem.equals(toFileSystem)) {
					command = new DragAndDropMoveCommand(froms, to);
				} else {
					command = new DragAndDropCopyCommand(froms, to);
				}
			}

			command.setJFD(jfd);
			command.startAsync();
		} catch (Exception e) {
			e.printStackTrace();
			dtde.dropComplete(false);
		}
	}

}
