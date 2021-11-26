/*
 * Created on 2004/05/31
 *
 */
package com.nullfish.app.jfd2.command.embed;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.JFDModel;
import com.nullfish.app.jfd2.command.Command;
import com.nullfish.app.jfd2.dialog.JFDDialog;
import com.nullfish.app.jfd2.resource.JFDResource;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;

/**
 * ソートコマンド
 * 
 * @author shunji
 */
public class CreateFileCommand extends Command {
	public static final String OK = "ok";

	public static final String CANCEL = "cancel";

	public static final String FILE = "file";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nullfish.lib.vfs.manipulation.abst.AbstractManipulation#doExecute()
	 */
	public void doExecute() throws VFSException {
		JFDDialog dialog = null;

		JFD jfd = getJFD();
		JFDModel model = jfd.getModel();
		try {
			model.lockAutoUpdate(this);
			
			dialog = jfd.createDialog();
			dialog.setTitle(JFDResource.LABELS.getString("title_newfile"));
	
			//	メッセージ
			dialog.addMessage(JFDResource.MESSAGES.getString("message_create_file"));
	
			//	ボタン
			dialog.addButton(OK, JFDResource.LABELS.getString("ok"), 'o', true);
			dialog.addButton(CANCEL, JFDResource.LABELS.getString("cancel"), 'c',
					false);
	
			VFile selectedFile = model.getSelectedFile();
			dialog.addTextField(FILE, selectedFile.getName(), true);

			dialog.pack();
			dialog.setVisible(true);
	
			String answer = dialog.getButtonAnswer();
			String fileName = dialog.getTextFieldAnswer(FILE);
			if (answer == null || CANCEL.equals(answer) 
					|| fileName == null || fileName.length() == 0) {
				return;
			}
	
			VFile currentDir = jfd.getModel().getCurrentDirectory();
			VFile newFile = currentDir.getChild(fileName);
			if(newFile.exists(this)) {
				showAlreadyExistsDialog(jfd, newFile);
				return;
			}
			newFile.createFile(this);
			model.setDirectoryAsynchIfNecessary(model.getCurrentDirectory(), newFile, jfd);
		} finally {
			model.unlockAutoUpdate(this);
			
			if(dialog != null) {
				dialog.dispose();
			}
		}
	}
	
	public void showAlreadyExistsDialog(JFD jfd, VFile newFile) {
		JFDDialog dialog = jfd.createDialog();
		String[] messages = {
			JFDResource.MESSAGES.getString("file_already_exists"),
			newFile.getName()
		};
		dialog.setMessage(messages);
		dialog.addButton(OK, JFDResource.LABELS.getString("ok"), 'o', true);
		dialog.pack();
		dialog.setVisible(true);
		
		return;
	}

	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.command.Command#closesUnusingFileSystem()
	 */
	public boolean closesUnusingFileSystem() {
		return false;
	}
}
