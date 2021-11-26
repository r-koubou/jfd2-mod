/*
 * Created on 2004/05/31
 *
 */
package com.nullfish.app.jfd2.command.embed;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.io.IOException;

import javax.swing.SwingUtilities;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.JFDModel;
import com.nullfish.app.jfd2.command.Command;
import com.nullfish.app.jfd2.config.DefaultConfig;
import com.nullfish.app.jfd2.dialog.DialogUtilities;
import com.nullfish.app.jfd2.dialog.JFDDialog;
import com.nullfish.app.jfd2.ext_command.CommandExecuter;
import com.nullfish.app.jfd2.filelist.SmartFileListEditorDialog;
import com.nullfish.app.jfd2.resource.JFDResource;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;
import com.nullfish.lib.vfs.exception.VFSIOException;

/**
 * エディタオープンコマンド
 * 
 * @author shunji
 */
public class EditCommand extends Command {
	public static final String PARAM_EDITOR = "editor";

	public static final String PARAM_TEMP_DIR = "temp_dir";

	public static final String PARAM_USE_SHELL = "editor_use_shell";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nullfish.lib.vfs.manipulation.abst.AbstractManipulation#doExecute()
	 */
	public void doExecute() throws VFSException {
		try {
			JFD jfd = getJFD();
			JFDModel model = jfd.getModel();
			VFile selectedFile = model.getSelectedFile();
			if (selectedFile == null) {
				return;
			}

			if("slist".equals(selectedFile.getFileName().getExtension().toLowerCase())
					&& editAsSmartFileList(selectedFile)) {
				return;
			}
			
			String editor = (String) jfd.getCommonConfiguration().getParam(
					PARAM_EDITOR, null);
			Boolean useShell = (Boolean) jfd.getCommonConfiguration().getParam(
					PARAM_USE_SHELL, Boolean.valueOf(DefaultConfig.getDefaultConfig().isEditorUseShell()));

			if (editor == null) {
				DialogUtilities.showMessageDialog(jfd, JFDResource.MESSAGES
						.getString("editor_not_specified"), JFDResource.LABELS.getString("title_edit"));
				return;
			}

			if(selectedFile.isDirectory(this)) {
				CommandExecuter.getInstance().exec(editor, true);
				return;
			}
			
			if (model.getCurrentDirectory().getFileSystem().isShellCompatible()) {
				String[] params = {selectedFile.getAbsolutePath()};
				
				CommandExecuter.getInstance().exec(
						editor, params, useShell.booleanValue(), null);
			} else {
				showProgress();
				TemporaryFilePutBackCommand putBackCommand = new TemporaryFilePutBackCommand(
						selectedFile);
				putBackCommand.setParentManipulation(this);
				putBackCommand.setJFD(jfd);

				putBackCommand.prepare();

				VFile tempFile = putBackCommand.getTemporaryFile();
				
				String[] params = {tempFile.getAbsolutePath()};

				Process process = CommandExecuter.getInstance().exec(
						editor, params, 
						useShell.booleanValue(), null);
				putBackCommand.setProcess(process);

				putBackCommand.execute();
			}
		} catch (IOException e) {
			throw new VFSIOException(e);
		} catch (VFSException e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	private boolean editAsSmartFileList(VFile file) {
		JFDDialog dialog = DialogUtilities.createYesNoDialog(getJFD(), JFDDialog.YES);
		dialog.addMessage(JFDResource.MESSAGES.getString("opens_as_smart_file_list"));
		
		dialog.pack();
		dialog.setVisible(true);
		
		String answer = dialog.getButtonAnswer();
		dialog.dispose();
		
		if(!JFDDialog.YES.equals(answer)) {
			return false;
		}
		
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

	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.command.Command#closesUnusingFileSystem()
	 */
	public boolean closesUnusingFileSystem() {
		return false;
	}
}
