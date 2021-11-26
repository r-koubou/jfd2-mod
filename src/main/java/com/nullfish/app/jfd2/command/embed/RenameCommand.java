/*
 * Created on 2004/05/31
 *
 */
package com.nullfish.app.jfd2.command.embed;

import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.JFDComponent;
import com.nullfish.app.jfd2.JFDModel;
import com.nullfish.app.jfd2.command.Command;
import com.nullfish.app.jfd2.command.embed.rename.LumpSumRenameDialog;
import com.nullfish.app.jfd2.command.embed.rename.LumpSumRenamer;
import com.nullfish.app.jfd2.dialog.ConfigurationInfo;
import com.nullfish.app.jfd2.dialog.DialogUtilities;
import com.nullfish.app.jfd2.dialog.JFDDialog;
import com.nullfish.app.jfd2.resource.JFDResource;
import com.nullfish.lib.ui.Choice;
import com.nullfish.lib.ui.UIUtilities;
import com.nullfish.lib.ui.document.RegexRestriction;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.ManipulationStoppedException;
import com.nullfish.lib.vfs.exception.VFSException;
import com.nullfish.lib.vfs.exception.WrongPathException;
import com.nullfish.lib.vfs.impl.DefaultOverwritePolicy;

/**
 * リネームコマンド
 * 
 * @author shunji
 */
public class RenameCommand extends Command {
	public static final String OK = "ok";

	public static final String CANCEL = "cancel";

	public static final String NAME = "name";

	public static final String TO_UPPER = "to_upper";

	public static final String TO_LOWER = "to_lower";

	public static final String RENAME_EACH = "rename_each";

	public static final String CHANGE_EXTENSION = "change_extension";

	public static final String RENAME_ONCE = "rename_once";

	private Pattern numberPattern = Pattern.compile("\\$\\d*N");
	private Pattern numericPattern = Pattern.compile("\\d+");

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nullfish.lib.vfs.manipulation.abst.AbstractManipulation#doExecute()
	 */
	public void doExecute() throws VFSException {
		JFD jfd = getJFD();
		JFDModel model = jfd.getModel();
		VFile selectedFile = model.getSelectedFile();
		VFile[] markedFiles = model.getMarkedFiles();
		VFile currentDir = model.getCurrentDirectory();
		VFile fileToSelect = selectedFile;
		boolean selectedFileMarked = model.isMarked(model.getSelectedIndex());
		
		try {
			model.lockAutoUpdate(this);

			if (markedFiles != null && markedFiles.length > 0) {
				String renameWay = showRenamingWaySelectionDialog();
				if (TO_UPPER.equals(renameWay)) {
					renameToUpper(markedFiles);
					if(selectedFileMarked) {
						fileToSelect = selectedFile.getParent().getChild(selectedFile.getName().toUpperCase());
					}
				} else if (TO_LOWER.equals(renameWay)) {
					renameToLower(markedFiles);
					if(selectedFileMarked) {
						fileToSelect = selectedFile.getParent().getChild(selectedFile.getName().toLowerCase());
					}
				} else if (RENAME_EACH.equals(renameWay)) {
					for(int i=0; i<markedFiles.length; i++) {
						VFile dest = renameFile(markedFiles[i]);
						if(markedFiles[i].equals(selectedFile)) {
							fileToSelect = dest;
						}
					}
				} else if (CHANGE_EXTENSION.equals(renameWay)) {
					String[] message = {
							JFDResource.MESSAGES.getString("message_input_extension")
					};
					String extension = showInputDialog(message, "");
					renameExtension(markedFiles, extension);

					if(selectedFileMarked) {
						fileToSelect = selectedFile.getParent().getChild(selectedFile.getFileName().getExceptExtension() + "." + extension);
					}
				} else if (RENAME_ONCE.equals(renameWay)) {
					fileToSelect = renameOnce(markedFiles, selectedFile);
				}

				// for (int i = 0; i < markedFiles.length; i++) {
				// renameFile(markedFiles[i]);
				// }
			} else {
				if (selectedFile.equals(currentDir)
						|| (currentDir != null && selectedFile
								.equals(currentDir.getParent()))) {
					return;
				}

				fileToSelect = renameFile(selectedFile);
			}

			model.setDirectoryAsynchIfNecessary(currentDir, fileToSelect, jfd);
		} finally {
			model.unlockAutoUpdate(this);
		}
	}

	private VFile renameFile(VFile file) throws VFSException {
		VFile newFile = null;
		while (true) {
			try {
				String[] messages = {
						JFDResource.MESSAGES.getString("message_rename")
				};
				String newName = showInputDialog(messages, file.getName());

				newFile = file.getParent().getChild(newName);
				if (newFile.exists()) {
					if (!file.equals(newFile)) {
						showFileExistsDialog(newFile);
					} else if (!file.getName().equals(newName)) {
						file.moveTo(newFile, this);
						return newFile;
					}
				} else {
					file.moveTo(newFile, DefaultOverwritePolicy.OVERWRITE, this, true);
					return newFile;
				}
			} catch (WrongPathException e) {
				String[] messages = {
						JFDResource.MESSAGES.getString("wrong_path"),
						e.getMessage() };
				DialogUtilities.showMessageDialog(getJFD(), messages,
						JFDResource.LABELS.getString("title_rename"));
			}
		}
	}

	private void renameToUpper(VFile[] files) throws VFSException {
		VFile[] dests = new VFile[files.length];
		
		for(int i=0; i<files.length; i++) {
			dests[i] = files[i].getParent().getChild(files[i].getName().toUpperCase());
			if(!dests[i].equals(files[i]) && dests[i].exists(this)) {
				String[] messages = {
						JFDResource.MESSAGES.getString("rename_file_exists"),
						dests[i].getName()						
				};
				DialogUtilities.showMessageDialog(getJFD(), messages, JFDResource.LABELS.getString("title_rename"));
				return;
			}
		}

		for(int i=0; i<files.length; i++) {
			files[i].moveTo(dests[i], this);
		}
	}
	
	private void renameToLower(VFile[] files) throws VFSException {
		VFile[] dests = new VFile[files.length];
		
		for(int i=0; i<files.length; i++) {
			dests[i] = files[i].getParent().getChild(files[i].getName().toLowerCase());
			if(!dests[i].equals(files[i]) && dests[i].exists(this)) {
				String[] messages = {
						JFDResource.MESSAGES.getString("rename_file_exists"),
						dests[i].getName()						
				};
				DialogUtilities.showMessageDialog(getJFD(), messages, JFDResource.LABELS.getString("title_rename"));
				return;
			}
		}

		for(int i=0; i<files.length; i++) {
			files[i].moveTo(dests[i], this);
		}
	}
	
	private void renameExtension(VFile[] files, String extension) throws VFSException {
		VFile[] dests = new VFile[files.length];
		
		for(int i=0; i<files.length; i++) {
			dests[i] = files[i].getParent().getChild(files[i].getFileName().getExceptExtension() + "." + extension);
			if(!dests[i].equals(files[i]) && dests[i].exists(this)) {
				String[] messages = {
						JFDResource.MESSAGES.getString("rename_file_exists"),
						dests[i].getName()						
				};
				DialogUtilities.showMessageDialog(getJFD(), messages, JFDResource.LABELS.getString("title_rename"));
				return;
			}
		}

		for(int i=0; i<files.length; i++) {
			files[i].moveTo(dests[i], this);
		}
	}
	
	private String showInputDialog(String[] messages, String originalValue)
			throws ManipulationStoppedException {
		JFDDialog dialog = null;
		try {
			dialog = getJFD().createDialog();
			dialog.setTitle(JFDResource.LABELS.getString("title_rename"));

			// メッセージ
			dialog.setMessage(messages);

			// ボタン
			dialog.addButton(OK, JFDResource.LABELS.getString("ok"), 'o', true);
			dialog.addButton(CANCEL, JFDResource.LABELS.getString("cancel"),
					'c', false);

//			if(((Boolean)(getJFD().getCommonConfiguration().getParam("rename_sprit_text", Boolean.TRUE))).booleanValue() ) {
//				dialog.addSpritFileNameField(NAME, originalValue, true, null);
//			} else {
				dialog.addRenameTextField(NAME, originalValue, true, null);
//			}
			
			dialog.pack();
			dialog.setVisible(true);

			String answer = dialog.getButtonAnswer();
			if (CANCEL.equals(answer) || answer == null) {
				throw new ManipulationStoppedException(this);
			}

			return dialog.getTextFieldAnswer(NAME);
		} finally {
			if (dialog != null) {
				dialog.dispose();
			}
		}
	}

	private void showFileExistsDialog(VFile file) {
		// メッセージ
		String[] messages = {
				JFDResource.MESSAGES.getString("rename_file_exists"),
				file.getName() };
		DialogUtilities.showMessageDialog(getJFD(), messages,
				JFDResource.LABELS.getString("title_rename"));
	}

	private VFile renameOnce(VFile[] files, VFile selectedFile) throws VFSException {
		JFDDialog dialog = null;
		try {
			dialog = DialogUtilities.createOkCancelDialog(getJFD(), JFDResource.LABELS.getString("title_rename"));
			String[] message = {
				JFDResource.MESSAGES.getString("message_input_rename_pattern")
			};
			
			dialog.setMessage(message);
			
			dialog.addTextField("pattern", "", false, JFDResource.LABELS.getString("filename"));
			dialog.addTextField("number", "1", false, JFDResource.LABELS.getString("start_number"), RegexRestriction.getInstance("\\d*") );
			
			dialog.pack();
			dialog.setVisible(true);
			
			String answer = dialog.getButtonAnswer();
			if (CANCEL.equals(answer) || answer == null) {
				throw new ManipulationStoppedException(this);
			}

			String pattern = dialog.getTextFieldAnswer("pattern");
			if(pattern == null || pattern.length() == 0) {
				throw new ManipulationStoppedException(this);
			}
			
			String numberStr = dialog.getTextFieldAnswer("number");
			
			int number = 1;
			try {
				number = Integer.parseInt(numberStr);
			} catch (Exception e) {}
			
			List newFilesList = new ArrayList();
			
			for(int i=0; i<files.length; i++) {
				//	新ファイル名
				VFile newFile = createFileName(files[i], pattern, number++);
				newFilesList.add(newFile); 
/*
				//	重複チェック
				if(newFilesList.contains(newFile)) {
					String[] messages = {
							JFDResource.MESSAGES.getString("message_repeated_filename"),
							newFile.getName()						
					};
					DialogUtilities.showMessageDialog(getJFD(), messages, JFDResource.LABELS.getString("title_rename"));
					throw new ManipulationStoppedException(this);
				}
				
				newFilesList.add(newFile); 
				
				//	カーソルチェック
				if(selectedFile.equals(files[i])) {
					selectedFile = newFile;
				}
				
				//	上書きチェック
				if(!files[i].equals(newFile) && newFile.exists(this)) { 
					String[] messages = {
							JFDResource.MESSAGES.getString("rename_file_exists"),
							newFile.getName()						
					};
					DialogUtilities.showMessageDialog(getJFD(), messages, JFDResource.LABELS.getString("title_rename"));
					throw new ManipulationStoppedException(this);
				}
*/
			}
			
			//	リネーム
			VFile[] newFiles = new VFile[files.length];
			newFiles = (VFile[])newFilesList.toArray(newFiles);

			LumpSumRenamer renamer = new LumpSumRenamer(files, newFiles);
			Container owner = UIUtilities.getTopLevelOwner((Container)((JFDComponent)getJFD()).getComponent());
			LumpSumRenameDialog renameDialog;
			if(owner instanceof Frame) {
				renameDialog = new LumpSumRenameDialog((Frame)owner, renamer);
			} else {
				renameDialog = new LumpSumRenameDialog((Dialog)owner, renamer);
			}

			renameDialog.pack();
			renameDialog.setLocationRelativeTo(null);
			renameDialog.setVisible(true);
			
			if(renameDialog.getResult() != LumpSumRenameDialog.OK) {
				throw new ManipulationStoppedException(this);
			}
			
			renamer.renameAll(this);
			
			return selectedFile;
		} finally {
			dialog.dispose();
		}
	}
	
	/**
	 * パターンから新しいファイルを作成する
	 * @param original	元ファイル
	 * @param pattern	パターン
	 * @param number	連番
	 * @return	生成された新しいファイル
	 * @throws VFSException
	 */
	private VFile createFileName(VFile original, String pattern, int number) throws VFSException {
		String name = original.getFileName().getExceptExtension();
		String extension = original.getFileName().getExtension();
		
		String newName = pattern;
		newName = newName.replace("$F", original.getName());
		newName = newName.replace("$f", name);
		newName = newName.replace("$E", "." + extension);
		newName = newName.replace("$X", extension);

		Matcher numberMatcher = numberPattern.matcher(newName);
		
		while(numberMatcher.find()) {
			String found = numberMatcher.group();
			
			//	桁
			int unit = 0;
			Matcher numericMatcher = numericPattern.matcher(found);
			if(numericMatcher.find()) {
				try {
					unit = Integer.parseInt(numericMatcher.group());
				} catch (Exception e) {}
			}
			
			String numericStr;
			if(unit > 0) {
				StringBuffer format = new StringBuffer();
				for(int i=0; i<unit; i++) {
					format.append("0");
				}
				
				numericStr = new DecimalFormat(format.toString()).format(number);
			} else {
				numericStr = Integer.toString(number);
			}
			
			newName = newName.replace(found, numericStr);
		}
		
		return original.getParent().getChild(newName);
	}
	
	private String showRenamingWaySelectionDialog() throws VFSException {
		JFDDialog dialog = null;
		try {
			Choice[] choices = {
					new Choice(TO_UPPER, JFDResource.LABELS
							.getString("rename_to_upper"), 'u'),
					new Choice(TO_LOWER, JFDResource.LABELS
							.getString("rename_to_lower"), 'l'),
					new Choice(RENAME_EACH, JFDResource.LABELS
							.getString("rename_each"), 'r'),
					new Choice(CHANGE_EXTENSION, JFDResource.LABELS
							.getString("rename_extension"), 'e'),
					new Choice(RENAME_ONCE, JFDResource.LABELS
							.getString("rename_all"), 'w')
			};

			dialog = DialogUtilities.createOkCancelDialog(getJFD(),
					JFDResource.LABELS.getString("title_rename"));
			ConfigurationInfo configInfo = new ConfigurationInfo(getJFD()
					.getLocalConfiguration(), "rename_way");
			dialog.addChooser("rename_way", JFDResource.MESSAGES
					.getString("message_rename_files"), choices, 1, TO_UPPER,
					configInfo, true);

			dialog.pack();
			dialog.setVisible(true);

			String answer = dialog.getButtonAnswer();
			if (CANCEL.equals(answer) || answer == null) {
				throw new ManipulationStoppedException(this);
			}

			dialog.applyConfig();
			return dialog.getChooserAnswer("rename_way");
		} finally {
			if (dialog != null) {
				dialog.dispose();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nullfish.app.jfd2.command.Command#closesUnusingFileSystem()
	 */
	public boolean closesUnusingFileSystem() {
		return false;
	}
}
