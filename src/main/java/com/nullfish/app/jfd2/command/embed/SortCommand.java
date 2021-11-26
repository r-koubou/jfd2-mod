/*
 * Created on 2004/05/31
 *
 */
package com.nullfish.app.jfd2.command.embed;


import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.JFDModel;
import com.nullfish.app.jfd2.command.Command;
import com.nullfish.app.jfd2.comparator.FileComparator;
import com.nullfish.app.jfd2.comparator.JFDComparator;
import com.nullfish.app.jfd2.dialog.ConfigurationInfo;
import com.nullfish.app.jfd2.dialog.DialogUtilities;
import com.nullfish.app.jfd2.dialog.JFDDialog;
import com.nullfish.app.jfd2.resource.JFDResource;
import com.nullfish.app.jfd2.util.SortUtility;
import com.nullfish.lib.ui.Choice;
import com.nullfish.lib.vfs.exception.VFSException;

/**
 * ソートコマンド
 * 
 * @author shunji
 */
public class SortCommand extends Command {
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nullfish.lib.vfs.manipulation.abst.AbstractManipulation#doExecute()
	 */
	public void doExecute() throws VFSException {
		JFD jfd = getJFD();
		JFDModel model = jfd.getModel();

		JFDDialog dialog = DialogUtilities.createOkCancelDialog(jfd, "jFD2 - Sort");

		//	メッセージ
		dialog.addMessage(JFDResource.MESSAGES.getString("message_sort"));

		//	ソート条件
		Choice[] conditions = {
				new Choice(SortUtility.NAME, JFDResource.LABELS.getString("name"), 'n'),
				new Choice(SortUtility.EXTENSION,
						JFDResource.LABELS.getString("extension"), 'e'),
				new Choice(SortUtility.SIZE, JFDResource.LABELS.getString("size"), 's'),
				new Choice(SortUtility.DATE, JFDResource.LABELS.getString("date"), 't') };

		dialog.addChooser(SortUtility.CONDITION, JFDResource.LABELS.getString("condition"),
				conditions, 2, SortUtility.NAME, new ConfigurationInfo(jfd
						.getLocalConfiguration(), SortUtility.CONDITION), false);

		//	昇順降順
		Choice[] orders = {
				new Choice(SortUtility.ASCEND, JFDResource.LABELS.getString("ascend"), 'u'),
				new Choice(SortUtility.DESCEND, JFDResource.LABELS.getString("descend"),
						'd') };

		dialog.addChooser(SortUtility.ORDER, JFDResource.LABELS.getString("order"), orders,
				1, SortUtility.ASCEND, new ConfigurationInfo(jfd.getLocalConfiguration(),
						SortUtility.ORDER), true);

		//	チェックボックス
		//	属性ソート
		dialog.addCheckBox(SortUtility.NO_ATTRIBUTE_SORT, JFDResource.LABELS
				.getString("no_attribute_sort"), 'a', false, new ConfigurationInfo(
				jfd.getLocalConfiguration(), SortUtility.NO_ATTRIBUTE_SORT), false);

		//	ディレクトリ優先
		dialog.addCheckBox(SortUtility.DIRECTORY_FIRST, JFDResource.LABELS
				.getString("directory_first"), 'p', false, new ConfigurationInfo(
				jfd.getLocalConfiguration(), SortUtility.DIRECTORY_FIRST), false);

		//	ファイルとディレクトリの区別
		dialog.addCheckBox(SortUtility.NO_DISTINCT_FILE_DIRECTORY, JFDResource.LABELS
				.getString("no_file_directory_distinction"), 'i', false, new ConfigurationInfo(
				jfd.getLocalConfiguration(), SortUtility.NO_DISTINCT_FILE_DIRECTORY), false);

		//	数値ソート
		dialog.addCheckBox(SortUtility.SORT_NAME_NUMBER, JFDResource.LABELS
				.getString("sort_numbers"), 'm', false, new ConfigurationInfo(
				jfd.getLocalConfiguration(), SortUtility.SORT_NAME_NUMBER), false);

		//	ソートしない
		dialog.addCheckBox(SortUtility.NO_SORT, JFDResource.LABELS
				.getString("no_auto_sort"), 'r', false, new ConfigurationInfo(
				jfd.getLocalConfiguration(), SortUtility.NO_SORT), false);

		dialog.pack();
		dialog.setVisible(true);

		String answer = dialog.getButtonAnswer();
		if (answer == null || JFDDialog.CANCEL.equals(answer)) {
			return;
		}

		dialog.applyConfig();

		FileComparator[] comparators = SortUtility.createComparators(jfd);

		model.setComparator(new JFDComparator(comparators));
		model.setFiles(model.getCurrentDirectory(), model.getFiles(), model
				.getSelectedFile());
	}

	/**
	 * 作業経過メッセージを取得する。
	 * 
	 * @return
	 */
	public String getProgressMessage() {
		return JFDResource.MESSAGES.getString("progress_sort");
	}

	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.command.Command#closesUnusingFileSystem()
	 */
	public boolean closesUnusingFileSystem() {
		return false;
	}
}
