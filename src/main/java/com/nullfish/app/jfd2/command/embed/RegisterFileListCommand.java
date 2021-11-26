/*
 * Created on 2004/05/31
 *
 */
package com.nullfish.app.jfd2.command.embed;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.command.Command;
import com.nullfish.app.jfd2.dialog.DialogUtilities;
import com.nullfish.app.jfd2.dialog.JFDDialog;
import com.nullfish.app.jfd2.resource.JFDResource;
import com.nullfish.lib.vfs.VFS;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;
import com.nullfish.lib.vfs.impl.filelist.FileListFileFactory;
import com.nullfish.lib.vfs.impl.filelist.FileListFileSystem;

/**
 * ファイルリスト登録コマンド
 * 
 * @author shunji
 */
public class RegisterFileListCommand extends Command {
	public static final String FILELIST_NAME = "filelist_name";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nullfish.lib.vfs.manipulation.abst.AbstractManipulation#doExecute()
	 */
	public void doExecute() throws VFSException {
		VFS vfs = VFS.getInstance(getJFD());
		JFD jfd = getJFD();

		String shortCutDirPath = (String) jfd.getCommonConfiguration()
				.getParam(QuickAccessCommand.SHORTCUT_DIR, QuickAccessCommand.DEFAULT_SHORTCUT_DIR);
		VFile shortCutDir = vfs.getFile(shortCutDirPath);
		if (!shortCutDir.exists(this)) {
			shortCutDir.createDirectory(this);
		}

		VFile current = jfd.getModel().getCurrentDirectory();
		String defaultName = current.getName();

		VFile filelistFile = null;
		while (true) {
			String filelistName = getTargetFileName(defaultName);
			if (filelistName == null) {
				return;
			}
			filelistFile = shortCutDir.getChild(filelistName + "." + FileListFileFactory.EXTENSION_FILE_LIST);
			if (!filelistFile.exists(this)) {
				break;
			} else {
				JFDDialog dialog = DialogUtilities.createYesNoDialog(jfd, JFDDialog.YES, "jFD2");
				dialog.addMessage(JFDResource.MESSAGES
						.getString("filelist_exists"));
				dialog.pack();
				dialog.setVisible(true);
				
				if(JFDDialog.YES.equals(dialog.getButtonAnswer())) {
					appendFileList(filelistFile); 
					return;
				} else {
					defaultName = filelistName;
				}
			}
		}

		createFileList(filelistFile);
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nullfish.app.jfd2.command.Command#closesUnusingFileSystem()
	 */
	public boolean closesUnusingFileSystem() {
		return false;
	}

	private String getTargetFileName(String defaultName) {
		JFDDialog dialog = null;
		try {
			dialog = DialogUtilities.createOkCancelDialog(getJFD());
			dialog.addMessage(JFDResource.MESSAGES.getString("input_filelist_name"));
			dialog.addTextField(FILELIST_NAME, defaultName, true);
			dialog.pack();
			dialog.setVisible(true);

			String button = dialog.getButtonAnswer();
			if (button == null || button.equals(JFDDialog.CANCEL)) {
				return null;
			}

			String rtn = dialog.getTextFieldAnswer(FILELIST_NAME);
			if (rtn.length() == 0) {
				return null;
			}

			return rtn;
		} finally {
			if (dialog != null) {
				dialog.dispose();
			}
		}
	}
	
	private void createFileList(VFile file) throws VFSException {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(file.getOutputStream()));
			
			VFile[] markedFiles = getJFD().getModel().getMarkedFiles();
			if(markedFiles.length == 0) {
				markedFiles = new VFile[1];
				markedFiles[0] = getJFD().getModel().getSelectedFile();
			}
			
			for(int i=0; i<markedFiles.length; i++) {
				writer.write(markedFiles[i].getAbsolutePath());
				if(i != markedFiles.length - 1) {
					writer.write("\n");
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				writer.flush();
			} catch (Exception e) {}
			try {
				writer.close();
			} catch (Exception e) {}
		}
	}
	
	private void appendFileList(VFile file) throws VFSException {
		FileListFileSystem fileSystem = (FileListFileSystem)file.getInnerRoot().getFileSystem();
		fileSystem.initFileTree(this);

		VFile[] markedFiles = getJFD().getModel().getMarkedOrSelectedFiles();
		
		for(int i=0; i<markedFiles.length; i++) {
			fileSystem.addFile(markedFiles[i]);
		}
		fileSystem.save();
		
		DialogUtilities.showMessageDialog(getJFD(), JFDResource.MESSAGES.getString("filelist_created"), "jFD2");
	}
}
