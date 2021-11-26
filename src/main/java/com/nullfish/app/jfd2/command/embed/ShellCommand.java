/*
 * Created on 2004/05/31
 *
 */
package com.nullfish.app.jfd2.command.embed;

import java.io.File;
import java.io.IOException;
import java.text.Format;
import java.text.MessageFormat;
import java.util.List;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.JFDModel;
import com.nullfish.app.jfd2.command.Command;
import com.nullfish.app.jfd2.config.DefaultConfig;
import com.nullfish.app.jfd2.dialog.ConfigurationInfo;
import com.nullfish.app.jfd2.dialog.JFDDialog;
import com.nullfish.app.jfd2.ext_command.CommandExecuter;
import com.nullfish.app.jfd2.resource.JFDResource;
import com.nullfish.app.jfd2.util.StringHistory;
import com.nullfish.app.jfd2.util.WindowsUtil;
import com.nullfish.lib.vfs.exception.VFSException;
import com.nullfish.lib.vfs.exception.VFSIOException;
import com.nullfish.lib.vfs.impl.local.LocalFile;

/**
 * ソートコマンド
 * 
 * @author shunji
 */
public class ShellCommand extends Command {
	public static final String OK = "ok";

	public static final String CANCEL = "cancel";

	public static final String COMMAND = "command";

	public static final String USE_SHELL = "use shell";

	public static final String MODE = "mode";
	
	public static final String MODE_SHELL = "shell";
	
	public static final String MODE_EXECUTE = "execute";
	
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
			dialog.setTitle(JFDResource.LABELS.getString("title_shell"));

			//	メッセージ
			dialog.addMessage(JFDResource.MESSAGES.getString("message_shell"));

			//	ボタン
			dialog.addButton(OK, JFDResource.LABELS.getString("ok"), 'o', true);
			dialog.addButton(CANCEL, JFDResource.LABELS.getString("cancel"),
					'c', false);

			StringHistory history = (StringHistory) jfd.getLocalConfiguration()
					.getParam("shell_history", null);
			if (history == null) {
				history = new StringHistory(50, true);
				jfd.getLocalConfiguration().setParam("shell_history", history);
			}
			
			List historyList = history.toList();
			if(MODE_EXECUTE.equals(getParameter(MODE))) {
				String fileName = model.getSelectedFile().getAbsolutePath();
				if(fileName.indexOf(' ') != -1) {
					fileName = "\"" + fileName + "\"";
				}
				fileName = WindowsUtil.escapeFileName(fileName);
				if(!model.getSelectedFile().isFile(this)) {
					Format format = new MessageFormat((String)jfd.getCommonConfiguration().getParam(ExtensionMapCommand.PARAM_DIR_OPEN, DefaultConfig.getDefaultConfig().getOpenDirCommand()));
					Object[] params = {
							fileName
					};
					fileName = format.format(params);
				}
				historyList.add(0, fileName);
			}

			dialog.addComboBox(COMMAND, historyList, null, true, true, null);

			dialog.addCheckBox(USE_SHELL, JFDResource.LABELS
					.getString("use_shell"), 's', true, new ConfigurationInfo(
					jfd.getLocalConfiguration(), USE_SHELL), false);

			dialog.pack();
			dialog.setVisible(true);

			String answer = dialog.getButtonAnswer();
			String command = dialog.getTextFieldAnswer(COMMAND);
			
			if (answer == null || CANCEL.equals(answer)) {
				return;
			}
			
			File directory = null;
			if (currentDir instanceof LocalFile) {
				directory = ((LocalFile) currentDir).getFile();
			}

			if(command == null || command.length() == 0) {
				command = (String)jfd.getCommonConfiguration().getParam("open_console_command", null);
				if(command == null | command.length() == 0) {
					return;
				}
				
				CommandExecuter.getInstance().exec(command,
						dialog.isChecked(USE_SHELL), directory);
				dialog.applyConfig();
				return;
			}
			
			history.add(command);

			dialog.applyConfig();
			CommandExecuter.getInstance().exec(command,
					dialog.isChecked(USE_SHELL), directory);
		} catch (IOException e) {
			throw new VFSIOException(e);
		} finally {
			model.unlockAutoUpdate(this);

			if (dialog != null) {
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
