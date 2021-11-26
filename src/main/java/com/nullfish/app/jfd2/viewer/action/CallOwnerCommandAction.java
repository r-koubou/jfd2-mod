package com.nullfish.app.jfd2.viewer.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.viewer.FileViewer;

public class CallOwnerCommandAction extends AbstractAction {
	private String commandName;
	
	private FileViewer viewer;
	
	/**
	 * コンストラクタ
	 * @param commandName
	 */
	public CallOwnerCommandAction(FileViewer viewer, String commandName) {
		this.viewer = viewer;
		this.commandName = commandName;
	}
	
	public void actionPerformed(ActionEvent e) {
		JFD jfd = viewer.getJFD();
		
		if(jfd == null) {
			return;
		}
		
		jfd.getCommandManager().execute(commandName);
	}
}
