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
import com.nullfish.lib.vfs.FileUtil;
import com.nullfish.lib.vfs.Manipulation;
import com.nullfish.lib.vfs.VFS;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.ManipulationStoppedException;
import com.nullfish.lib.vfs.exception.VFSException;
import com.nullfish.lib.vfs.impl.filelist.FileListFileSystem;
import com.nullfish.lib.vfs.manipulation.CopyFileManipulation;

/**
 * コピーコマンド
 * 
 * @author shunji
 */
public class CopyCommand extends Command {
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
	 * ヒストリで自ディレクトリをトップにする
	 */
	public static final String SELF_FIRST_HISTORY = "self_first";

	/**
	 * ディレクトリの相対構造を無視する
	 */
	public static final String IGNORE_FILE_STRUCTURE = "ignore_file_structure";

	/**
	 * doExecuteのオーバーライド。
	 */
	public void doExecute() throws VFSException {
		JFDDialog dialog = null;

		JFD jfd = getJFD();
		JFDModel model = jfd.getModel();
		VFile currentDir = model.getCurrentDirectory();
		VFile selectedFile = model.getSelectedFile();
		VFile[] markedFiles = model.getMarkedFiles();

		VFile dest = null;
		
		try {
			model.lockAutoUpdate(this);

			if (markedFiles.length == 0
					&& (selectedFile.equals(currentDir) || selectedFile
							.equals(currentDir.getParent()))) {
				return;
			}

			dialog = jfd.createDialog();
			
			dialog.setTitle(JFDResource.LABELS.getString("title_copy"));

			//	メッセージ
			dialog.addMessage(JFDResource.MESSAGES.getString("message_copy"));

			//	ボタン
			dialog.addButton(OK, JFDResource.LABELS.getString("ok"), 'o', true);
			dialog.addButton(CANCEL, JFDResource.LABELS.getString("cancel"),
					'c', false);

			//	ファイル構造を無視してのコピー
			dialog.addCheckBox(IGNORE_FILE_STRUCTURE, JFDResource.LABELS
					.getString(IGNORE_FILE_STRUCTURE), 'i', !jfd.showsRelativePath(), null, false);

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
			
			dialog.applyConfig();

			boolean ignoreFileStructure = dialog.isChecked(IGNORE_FILE_STRUCTURE);
			dest = VFS.getInstance(jfd).getFile(fileName);
			
			dest.getFileSystem().registerUser(this);
			
			model.getHistory().add(dest);
			model.getNoOverwrapHistory().add(dest);

			showProgress(1000);
			
			jfd.getModel().clearMark();

			if(dest.getFileSystem() instanceof FileListFileSystem) {
				registerFileList(dest, selectedFile, markedFiles);
			} else {
				Manipulation[] copyManipulations = initCopyManipulations(
						selectedFile, markedFiles, dest, ignoreFileStructure);
				setChildManipulations(copyManipulations);
	
				for (int i = 0; i < copyManipulations.length; i++) {
					if(isStopped()) {
						throw new ManipulationStoppedException(this);
					}
	
					copyManipulations[i].execute();
				}
	
				if(isStopped()) {
					throw new ManipulationStoppedException(this);
				}
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
	 * ファイルリストにファイルを登録する。
	 * @param dest
	 * @param selectedFile
	 * @param markedFiles
	 * @throws VFSException
	 */
	private void registerFileList(VFile dest, VFile selectedFile, VFile[] markedFiles) throws VFSException {
		if(markedFiles == null || markedFiles.length == 0) {
			markedFiles = new VFile[1];
			markedFiles[0] = selectedFile;
		}
		
		FileListFileSystem fileSystem = (FileListFileSystem)dest.getFileSystem();
		fileSystem.initFileTree(this);
		
		for(int i=0; i<markedFiles.length; i++) {
			fileSystem.addFile(markedFiles[i]);
		}
		
		fileSystem.save();
	}
	
	/**
	 * 子コピー操作を初期化する。
	 * 
	 * @param selectedFile
	 * @param markedFiles
	 * @param dest
	 * @return @throws
	 *         VFSException
	 */
	private CopyFileManipulation[] initCopyManipulations(VFile selectedFile,
			VFile[] markedFiles, VFile dest, boolean ignoreFileStructure) throws VFSException {
		CopyOverwritePolicy policy = new CopyOverwritePolicy(this);
		CopyFileManipulation[] rtn;
		if (markedFiles.length == 0) {
			VFile target = ignoreFileStructure ? dest : dest.getRelativeFile(getJFD().getModel().getCurrentDirectory().getRelation(selectedFile.getParent()));
			rtn = new CopyFileManipulation[1];
			rtn[0] = FileUtil.prepareCopyTo(selectedFile, target
					.getChild(selectedFile.getName()), policy, this);
		} else {
			rtn = new CopyFileManipulation[markedFiles.length];
			for (int i = 0; i < markedFiles.length; i++) {
				VFile target = ignoreFileStructure ? dest : dest.getRelativeFile(getJFD().getModel().getCurrentDirectory().getRelation(markedFiles[i].getParent()));
				rtn[i] = FileUtil.prepareCopyTo(markedFiles[i], target
						.getChild(markedFiles[i].getName()), policy, this);
			}
		}

		for (int i = 0; i < rtn.length; i++) {
			rtn[i].setParentManipulation(this);
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
