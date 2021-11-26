/*
 * Created on 2004/07/06
 *
 */
package com.nullfish.app.jfd2.command.groovy;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.codehaus.groovy.control.CompilationFailedException;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.command.JFDException;
import com.nullfish.app.jfd2.dialog.DialogUtilities;
import com.nullfish.app.jfd2.dialog.JFDDialog;
import com.nullfish.app.jfd2.ext_command.window.ConsoleFrame;
import com.nullfish.app.jfd2.resource.JFDResource;
import com.nullfish.lib.vfs.VFS;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;
import com.nullfish.lib.vfs.exception.VFSIOException;

/**
 * @author shunji
 *
 */
public class GroovyEditCommand extends AbstractGroovyCommand {
	/**
	 * スクリプトセーブボタン
	 */
	public static final String SAVE_SCRIPT = "save_script";
	
	/**
	 * スクリプトセーブファイル名
	 */
	public static final String SCRIPT_FILE = "script_file";
	
	/**
	 * スクリプト
	 */
	public static final String INPUT_SCRIPT = "input_script";
	
	/* (non-Javadoc)
	 * @see com.nullfish.lib.vfs.manipulation.abst.AbstractManipulation#doExecute()
	 */
	public void doExecute() throws VFSException {
		JFDDialog dialog = null;
		try {
			VFS vfs = VFS.getInstance(getJFD());
			JFD jfd = getJFD();
//			String scriptDirPath = (String)jfd.getCommonConfiguration().getParam(SCRIPT_DIR_PATH, DEFAULT_SCRIPT_DIR);
//			VFile scriptDir = vfs.getFile(scriptDirPath);
			
			dialog = DialogUtilities.createOkCancelDialog(jfd);
			String script = (String)jfd.getCommonConfiguration().getParam(INPUT_SCRIPT, "");
			dialog.addTextArea(INPUT_SCRIPT, script, true);
			dialog.addButton(SAVE_SCRIPT, JFDResource.LABELS.getString("save_script"), 's', false);
			
			//dialog.addMessage(JFDResource.MESSAGES.getString("message_select_script"));
			
			dialog.pack();
			dialog.setVisible(true);
			
			String answer = dialog.getButtonAnswer();

			script = dialog.getTextFieldAnswer(INPUT_SCRIPT);
			if (answer == null || JFDDialog.CANCEL.equals(answer) || script == null
					|| script.length() == 0) {
				return;
			}
			
			jfd.getCommonConfiguration().setParam(INPUT_SCRIPT, script);
			
			if(answer.equals(SAVE_SCRIPT)) {
				saveScript(script);
				return;
			}
			
			Binding binding = new Binding();
			binding.setVariable("jfd", getJFD());
			binding.setVariable("command", this);
			binding.setVariable("vfs", vfs);
			binding.setVariable("console", ConsoleFrame.getInstance());
			GroovyShell shell = new GroovyShell(binding);

			shell.evaluate(script);
		} catch (CompilationFailedException e) {
			showErrorMessage(e);
//			throw new JFDException(JFDResource.MESSAGES.getString("can_not_execute_script"), null);
		} catch (RuntimeException e) {
			showErrorMessage(e);
//			throw new JFDException(e, e.getMessage(), new Object[0]);
		} finally {
			if(dialog != null) {
				dialog.dispose();
			}
		}
	}
	
	private void saveScript(String script) throws VFSException {
		JFDDialog dialog = null;
		Writer writer = null;
		try {
			VFile scriptDir = getScriptDirectory();
			
			dialog = DialogUtilities.createOkCancelDialog(getJFD());
			dialog.addMessage(JFDResource.MESSAGES.getString("input_save_filename"));
			dialog.addTextField(SCRIPT_FILE, "", true);
			
			dialog.pack();
			dialog.setVisible(true);
			
			String buttonAnswer = dialog.getButtonAnswer();
			String fileName = dialog.getTextFieldAnswer(SCRIPT_FILE);
			if(buttonAnswer == null || buttonAnswer.equals(JFDDialog.CANCEL)
					|| fileName.length() == 0) {
				return;
			}
			
			VFile saveFile = scriptDir.getChild(fileName);
			if(saveFile.exists(this)) {
				JFDDialog overwriteDialog = null;
				try {
					overwriteDialog = DialogUtilities.createYesNoDialog(getJFD(), JFDDialog.NO);
					overwriteDialog.addMessage(JFDResource.MESSAGES.getString("overwrite_script"));
					overwriteDialog.pack();
					overwriteDialog.setVisible(true);
					
					String overwriteAnswer = overwriteDialog.getButtonAnswer();
					if(overwriteAnswer == null || overwriteAnswer.length() == 0
							|| overwriteAnswer.equals(JFDDialog.NO)) {
						return;
					}
				} finally {
					if(overwriteDialog != null) {
						overwriteDialog.dispose();
					}
				}
			}
			
			writer = new OutputStreamWriter(saveFile.getOutputStream(this));
			writer.write(script);
		} catch (IOException e) {
			throw new VFSIOException(e);
		} finally {
			if(dialog != null) {
				dialog.dispose();
			}
			
			if(writer != null) {
				try {
					writer.flush();
					writer.close();
				} catch (Exception e) {}
			}
		}
	}
}
