/*
 * Created on 2004/05/31
 *
 */
package com.nullfish.app.jfd2.command.embed;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.JFDModel;
import com.nullfish.app.jfd2.command.Command;
import com.nullfish.app.jfd2.config.Configuration;
import com.nullfish.app.jfd2.dialog.JFDDialog;
import com.nullfish.app.jfd2.resource.JFDResource;
import com.nullfish.lib.vfs.FileUtil;
import com.nullfish.lib.vfs.Manipulation;
import com.nullfish.lib.vfs.VFS;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;
import com.nullfish.lib.vfs.manipulation.MoveManipulation;

/**
 * 移動コマンド
 * 
 * @author shunji
 */
public class MoveCommand extends Command {
	/**
	 * OKボタン
	 */
	public static final String OK = "ok";

	/**
	 * キャンセルボタン
	 */
	public static final String CANCEL = "cancel";

	/**
	 * ディレクトリテキストフィールド
	 */
	public static final String DIRECTORY = "directory";

	/**
	 * doExecuteのオーバーライド。
	 */
	public void doExecute() throws VFSException {
		JFDDialog dialog = null;

		JFD jfd = getJFD();
		JFDModel model = jfd.getModel();
		
		VFile dest = null
		;
		try {
			model.lockAutoUpdate(this);

			VFile currentDir = model.getCurrentDirectory();
			VFile selectedFile = model.getSelectedFile();
			VFile[] markedFiles = model.getMarkedFiles();

			if (markedFiles.length == 0
					&& (selectedFile.equals(currentDir) || selectedFile
							.equals(currentDir.getParent()))) {
				return;
			}

			dialog = jfd.createDialog();

			dialog.setTitle(JFDResource.LABELS.getString("title_move"));
			
			//	メッセージ
			dialog.addMessage(JFDResource.MESSAGES.getString("message_move"));

			//	ボタン
			dialog.addButton(OK, JFDResource.LABELS.getString("ok"), 'o', true);
			dialog.addButton(CANCEL, JFDResource.LABELS.getString("cancel"),
					'c', false);

			//	ファイル構造を無視してのコピー
			dialog.addCheckBox(CopyCommand.IGNORE_FILE_STRUCTURE, JFDResource.LABELS
					.getString(CopyCommand.IGNORE_FILE_STRUCTURE), 'i', !jfd.showsRelativePath(), null, false);

			dialog
					.addFileComboBoxWithHistory(
							DIRECTORY,
							getParameter(CopyCommand.SELF_FIRST_HISTORY) != null
									&& ((Boolean) getParameter(CopyCommand.SELF_FIRST_HISTORY))
											.booleanValue(), true, true, jfd,
							null);

			dialog.pack();
			dialog.setVisible(true);

			String answer = dialog.getButtonAnswer();
			String fileName = dialog.getTextFieldAnswer(DIRECTORY);
			if (answer == null || CANCEL.equals(answer) || fileName == null
					|| fileName.length() == 0) {
				return;
			}
			
			boolean ignoresFileStructure = dialog.isChecked(CopyCommand.IGNORE_FILE_STRUCTURE);
			
			dialog.applyConfig();

			dest = VFS.getInstance(jfd).getFile(fileName);

			dest.getFileSystem().registerUser(this);

			model.getHistory().add(dest);
			model.getNoOverwrapHistory().add(dest);
			
			showProgress(1000);
			
			jfd.getModel().clearMark();
			
			Manipulation[] moveManipulations = initManipulations(selectedFile, markedFiles, dest, ignoresFileStructure);
			setChildManipulations(moveManipulations);

			for(int i=0; i<moveManipulations.length; i++) {
				moveManipulations[i].start();
			}
			model.refresh();
		} finally {
			model.unlockAutoUpdate(this);
			if (dialog != null) {
				dialog.dispose();
			}
			
			if(dest != null) {
				dest.getFileSystem().removeUser(this);
			}
		}
	}

	/**
	 * 子コピー操作を初期化する。
	 * @param selectedFile
	 * @param markedFiles
	 * @param dest
	 * @return
	 * @throws VFSException
	 */
	private MoveManipulation[] initManipulations(VFile selectedFile, VFile[] markedFiles,
			VFile dest, boolean ignoresFileStructure) throws VFSException {
		Configuration config = getJFD().getLocalConfiguration();

		CopyOverwritePolicy policy = new CopyOverwritePolicy(this);
		MoveManipulation[] rtn;
		if (markedFiles.length == 0) {
			VFile target = ignoresFileStructure ? dest : dest.getRelativeFile(getJFD().getModel().getCurrentDirectory().getRelation(selectedFile.getParent()));
			
			rtn = new MoveManipulation[1];
			rtn[0] = FileUtil.prepareMoveTo(selectedFile, target
					.getChild(selectedFile.getName()), policy, this);
		} else {
			rtn = new MoveManipulation[markedFiles.length];
			for (int i = 0; i < markedFiles.length; i++) {
				VFile target = ignoresFileStructure ? dest : dest.getRelativeFile(getJFD().getModel().getCurrentDirectory().getRelation(markedFiles[i].getParent()));
				rtn[i] = FileUtil.prepareMoveTo(markedFiles[i], target
						.getChild(markedFiles[i].getName()), policy, this);
			}
		}

		for(int i=0; i<rtn.length; i++) {
			rtn[i].prepare();
		}
		
		return rtn;
	}

	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.command.Command#closesUnusingFileSystem()
	 */
	public boolean closesUnusingFileSystem() {
		return true;
	}
}
