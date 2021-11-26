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
import com.nullfish.app.jfd2.util.ShortCutFile;
import com.nullfish.lib.vfs.VFS;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;

/**
 * ショートカット登録オープンコマンド
 * 
 * @author shunji
 */
public class RegisterQuickAccessCommand extends Command {
	public static final String SHORTCUT_NAME = "shortcut_name";

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

		VFile shortCutFile = null;
		while (true) {
			String shortCutName = getTargetFileName(defaultName);
			if (shortCutName == null) {
				return;
			}
			shortCutFile = shortCutDir.getChild(shortCutName + "." + ShortCutFile.EXTENSION);
			if (!shortCutFile.exists(this)) {
				break;
			} else {
				DialogUtilities.showMessageDialog(jfd, JFDResource.MESSAGES
						.getString("shortcut_exists"), "jFD");
				defaultName = shortCutName;
			}
		}

		ShortCutFile shortCut = new ShortCutFile(shortCutFile);
		shortCut.setTarget(current);
		shortCut.save(this);
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
			dialog.addMessage(JFDResource.MESSAGES.getString("input_shortcut_name"));
			dialog.addTextField(SHORTCUT_NAME, defaultName, true);
			dialog.pack();
			dialog.setVisible(true);

			String button = dialog.getButtonAnswer();
			if (button == null || button.equals(JFDDialog.CANCEL)) {
				return null;
			}

			String rtn = dialog.getTextFieldAnswer(SHORTCUT_NAME);
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
}
