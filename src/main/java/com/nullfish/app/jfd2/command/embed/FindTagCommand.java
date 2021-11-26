/*
 * Created on 2004/05/31
 *
 */
package com.nullfish.app.jfd2.command.embed;

import java.util.List;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.JFDModel;
import com.nullfish.app.jfd2.command.Command;
import com.nullfish.app.jfd2.comparator.JFDComparator;
import com.nullfish.app.jfd2.dialog.DialogUtilities;
import com.nullfish.app.jfd2.dialog.JFDDialog;
import com.nullfish.app.jfd2.resource.JFDResource;
import com.nullfish.app.jfd2.util.SortUtility;
import com.nullfish.app.jfd2.util.StringHistory;
import com.nullfish.lib.vfs.VFS;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;

/**
 * タグ名検索コマンド
 * 
 * @author shunji
 */
public class FindTagCommand extends Command {
	/**
	 * タグ名コンボボックス
	 */
	public static final String TAG_NAME = "tag_name";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nullfish.lib.vfs.manipulation.abst.AbstractManipulation#doExecute()
	 */
	public void doExecute() throws VFSException {
		JFDDialog dialog = null;
		try {
			JFD jfd = getJFD();
			JFDModel model = jfd.getModel();

			dialog = DialogUtilities.createOkCancelDialog(jfd);

			dialog.setTitle(JFDResource.LABELS.getString("title_find"));
			
			dialog.addMessage(JFDResource.MESSAGES.getString("message_tag_find"));
			StringHistory history = (StringHistory) jfd.getLocalConfiguration()
					.getParam("find_tag_history", null);
			if (history == null) {
				history = new StringHistory(50, true);
				jfd.getLocalConfiguration().setParam("find_tag_history", history);
			}

			List allTags = VFS.getInstance(jfd).getTagDataBase().findAllTags();
			allTags.removeAll(history.toList());
			allTags.addAll(0, history.toList());
			dialog.addComboBox(TAG_NAME, allTags, null, true, true);

			dialog.pack();
			dialog.setVisible(true);

			String answer = dialog.getButtonAnswer();
			if (answer == null || JFDDialog.CANCEL.equals(answer)) {
				return;
			}

			dialog.applyConfig();
			String tagName = dialog.getTextFieldAnswer(TAG_NAME);
			tagName = tagName != null ? tagName : "";

			if(tagName.length() == 0) {
				return;
			}
			history.add(tagName);
			showProgress(1000);

			tagName = tagName.replace('*', '%');
			List filesList = VFS.getInstance(jfd).getFileByTag(tagName);
			
			model.setComparator(new JFDComparator(SortUtility.createComparators(jfd)));

			VFile[] files = new VFile[filesList.size()];
			files = (VFile[]) filesList.toArray(files);
			model.setFiles(model.getCurrentDirectory(), files,
					model.getSelectedFile());
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
