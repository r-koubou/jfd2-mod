/*
 * Created on 2004/05/31
 *
 */
package com.nullfish.app.jfd2.command.embed;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;

import javax.swing.SwingUtilities;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.command.Command;
import com.nullfish.app.jfd2.dialog.DialogUtilities;
import com.nullfish.app.jfd2.dialog.JFDDialog;
import com.nullfish.app.jfd2.filelist.SmartFileListEditorDialog;
import com.nullfish.app.jfd2.resource.JFDResource;
import com.nullfish.lib.vfs.VFS;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;
import com.nullfish.lib.vfs.impl.filelist.FileListFileFactory;

/**
 * スマートファイルリスト登録コマンド
 * 
 * @author shunji
 */
public class RegisterSmartFileListCommand extends Command {
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
			filelistFile = shortCutDir.getChild(filelistName + "." + FileListFileFactory.EXTENSION_SMART_FILE_LIST);
			if (!filelistFile.exists(this)) {
				break;
			} else {
				DialogUtilities.showMessageDialog(jfd, JFDResource.MESSAGES
						.getString("smartfilelist_exists"), "jFD");
				defaultName = filelistName;
			}
		}

		editAsSmartFileList(filelistFile);
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
			dialog.addMessage(JFDResource.MESSAGES.getString("input_smartfilelist_name"));
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
	
	private boolean editAsSmartFileList(VFile file) {
		Window window = SwingUtilities.getWindowAncestor(getJFD().getComponent());
		
		SmartFileListEditorDialog frame;
		if(window instanceof Frame) {
			frame = new SmartFileListEditorDialog(getJFD(), (Frame)window, file);
		} else {
			frame = new SmartFileListEditorDialog(getJFD(), (Dialog)window, file);
		}
		
		frame.setLocationRelativeTo(getJFD().getComponent());
		frame.setVisible(true);
		
		return true;
	}

}
