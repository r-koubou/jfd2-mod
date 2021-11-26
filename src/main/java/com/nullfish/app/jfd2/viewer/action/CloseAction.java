package com.nullfish.app.jfd2.viewer.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.nullfish.app.jfd2.viewer.FileViewer;

public class CloseAction extends AbstractAction {
	private FileViewer viewer;
	
	public CloseAction(FileViewer viewer) {
		this.viewer = viewer;
	}
	
	public void actionPerformed(ActionEvent e) {
		viewer.close();
	}
}
