/*
 * Created on 2004/05/31
 *
 */
package com.nullfish.app.jfd2.command.embed;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.command.Command;
import com.nullfish.app.jfd2.dialog.DialogUtilities;
import com.nullfish.app.jfd2.dialog.JFDDialog;
import com.nullfish.app.jfd2.resource.JFDResource;
import com.nullfish.lib.vfs.VFS;
import com.nullfish.lib.vfs.exception.VFSException;

/**
 * タグ名検索コマンド
 * 
 * @author shunji
 */
public class CleanTagDbCommand extends Command {
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nullfish.lib.vfs.manipulation.abst.AbstractManipulation#doExecute()
	 */
	public void doExecute() throws VFSException {
		JFDDialog dialog = null;
		try {
			JFD jfd = getJFD();

			dialog = DialogUtilities.createOkCancelDialog(jfd);

			dialog.addMessage(JFDResource.MESSAGES.getString("message_clean_tag_db"));
			dialog.setTitle("jFD2");
			dialog.pack();
			dialog.setVisible(true);
			
			if(!JFDDialog.OK.equals(dialog.getButtonAnswer())) {
				return;
			}
			
			VFS.getInstance(jfd).getTagDataBase().clean();
			DialogUtilities.showMessageDialog(jfd, JFDResource.MESSAGES.getString("message_clean_tag_db_finished"), "jFD2");
		} finally {
			if (dialog != null) {
				dialog.dispose();
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.command.Command#closesUnusingFileSystem()
	 */
	public boolean closesUnusingFileSystem() {
		return true;
	}
}
