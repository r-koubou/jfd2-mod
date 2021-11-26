/*
 * Created on 2005/01/08
 *
 */
package com.nullfish.app.jfd2.ext_command_panel;

import java.io.File;
import java.io.IOException;

import org.jdom.Element;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.command.JFDException;
import com.nullfish.app.jfd2.dialog.DialogUtilities;
import com.nullfish.app.jfd2.dialog.JFDDialog;
import com.nullfish.app.jfd2.ext_command.CommandExecuter;
import com.nullfish.app.jfd2.resource.JFDResource;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.impl.local.LocalFile;

/**
 * シェルコマンド
 * 
 * @author shunji
 */
public class ShellExternalCommand {
	/**
	 * 実行コマンド
	 */
	private String shellCommand;
	
	/**
	 * 作業ディレクトリ
	 */
	private String workDir;
	
	/**
	 * シェル使用フラグ
	 */
	private boolean useShell;

	/**
	 * ノード名
	 */
	public static final String NODE_NAME = "shellcommand";
	
	/**
	 * シェル使用属性
	 */
	public static final String ATTR_USE_SHELL = "useshell";
	
	/**
	 * 作業ディレクトリ属性
	 */
	public static final String ATTR_WORKDIR = "workdir";
	
	/**
	 * コンストラクタ
	 *
	 */
	public ShellExternalCommand() {
	}
	
	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.ext_command_panel.ExternalCommand#getLabel()
	 */
	public String getLabel() {
		return shellCommand;
	}

	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.ext_command_panel.ExternalCommand#getWorkDir()
	 */
	public String getWorkDir() {
		return workDir;
	}

	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.ext_command_panel.ExternalCommand#execute()
	 */
	public void execute(JFD jfd) throws JFDException {
		MacroTranslator translator = new MacroTranslator();
		String[] translatedCommands = translator.translate(shellCommand, jfd);
		
		VFile currentDir = jfd.getModel().getCurrentDirectory();
		File workDirFile = null;
		if(workDir != null && workDir.length() > 0) {
			try {
				workDirFile = new File(workDir);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if(currentDir instanceof LocalFile){
			workDirFile = ((LocalFile) currentDir).getFile();
		}
		
		for(int i=0; i<translatedCommands.length; i++) {
			try {
				String command;
				if(translator.isNoDialog()) {
					command = translatedCommands[i];
				} else {
					command = showDialog(jfd, translatedCommands[i]);
					if(command == null) {
						return;
					}
				}
				CommandExecuter.getInstance().exec(command, useShell, workDirFile);
			} catch (IOException e) {
				e.printStackTrace();
				throw new JFDException(e, JFDResource.MESSAGES.getString("failed_to_execute"), new Object[0]);
			}
		}
	}

	public String showDialog(JFD jfd, String command) {
		JFDDialog dialog = null;
		try {
			dialog = DialogUtilities.createOkCancelDialog(jfd, "jFD2 - ExternalCommand");
			dialog.addMessage(JFDResource.MESSAGES.getString("input_option"));
			dialog.addTextField("option", command, true);
			
			dialog.pack();
			dialog.setVisible(true);
			String buttonAnswer = dialog.getButtonAnswer();
			if(buttonAnswer == null || JFDDialog.CANCEL.equals(buttonAnswer)) {
				return null;
			}
			
			return dialog.getTextFieldAnswer("option");
		} finally {
			dialog.dispose();
		}
	}
	
	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.ext_command_panel.ExternalCommand#isUseShell()
	 */
	public boolean isUseShell() {
		return useShell;
	}

	public void setShellCommand(String shellCommand) {
		this.shellCommand = shellCommand;
	}
	
	public String getShellCommand() {
		return shellCommand;
	}
	
	public void setUseShell(boolean useShell) {
		this.useShell = useShell;
	}
	
	public void setWorkDir(String workDir) {
		this.workDir = workDir;
	}

	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.ext_command_panel.ExternalCommand#init(org.jdom.Element)
	 */
	public void init(Element node) {
		if(node == null) {
			shellCommand = "";
			useShell = false;
			workDir = "";
		}
		
		shellCommand = node.getText();
		useShell = node.getAttributeValue(ATTR_USE_SHELL) == null || "true".equals(node.getAttributeValue(ATTR_USE_SHELL));
		workDir = node.getAttributeValue(ATTR_WORKDIR);
	}

	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.ext_command_panel.ExternalCommand#toNode()
	 */
	public Element toNode() {
		Element rtn = new Element(NODE_NAME);
		rtn.setText(shellCommand);
		rtn.setAttribute(ATTR_USE_SHELL, Boolean.toString(useShell));
		rtn.setAttribute(ATTR_WORKDIR, workDir);
		
		return rtn;
	}
}
