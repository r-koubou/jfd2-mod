/*
 * Created on 2004/05/31
 *
 */
package com.nullfish.app.jfd2.command.embed;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.JFDModel;
import com.nullfish.app.jfd2.command.Command;
import com.nullfish.app.jfd2.dialog.DialogUtilities;
import com.nullfish.app.jfd2.dialog.JFDDialog;
import com.nullfish.app.jfd2.resource.JFDResource;
import com.nullfish.lib.vfs.VFS;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;
/**
 * ディレクトリ変更コマンド
 * 
 * @author shunji
 */
public class LogicalDiskCommand extends Command {
	public static final String OK = "ok";

	public static final String CANCEL = "cancel";

	public static final String DIRECTORY = "directory";

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
			dialog.setTitle(JFDResource.LABELS.getString("title_logdsk"));
	
			//	メッセージ
			dialog.addMessage(JFDResource.MESSAGES.getString("message_logical_disk"));
	
			//	ボタン
			dialog.addButton(OK, JFDResource.LABELS.getString("ok"), 'o', true);
			dialog.addButton(CANCEL, JFDResource.LABELS.getString("cancel"), 'c',
					false);
	
			//dialog.addFileTextField(DIRECTORY, model.getCurrentDirectory(), true);
			dialog.addFileComboBox(DIRECTORY, model.getNoOverwrapHistory().toArray(), true, true, jfd);
			
			dialog.pack();
			dialog.setVisible(true);
	
			String answer = dialog.getButtonAnswer();
			if (answer == null || CANCEL.equals(answer)) {
				return;
			}
	
			String dirStr = dialog.getTextFieldAnswer(DIRECTORY);
			try {
				VFile dir = jfd.getAliaseManager().getFile(dirStr);
				
				if(dir == null) {
					dir = VFS.getInstance(jfd).getFile(dirStr);
				}
				
				VFile selectedFile = dir.getParent();
				if(dir.isFile(this)) {
					selectedFile = dir;
					dir = dir.getParent();
				}
				
				model.setDirectoryAsynchIfNecessary(dir, selectedFile, jfd);
			} catch (VFSException e) {
				String[] messages = {
					JFDResource.MESSAGES.getString("wrong_path"),
					e.getErrorMessage(),
					dialog.getTextFieldAnswer(DIRECTORY)
				};
				
				DialogUtilities.showMessageDialog(jfd, messages, JFDResource.LABELS.getString("title_logdsk"));
				
				return;
			}
		} finally {
			model.unlockAutoUpdate(this);

			if(dialog != null) {
				dialog.dispose();
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
