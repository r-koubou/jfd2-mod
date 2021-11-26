package com.nullfish.app.jfd2.viewer.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.JFDModel;
import com.nullfish.app.jfd2.viewer.FileViewer;
import com.nullfish.lib.vfs.VFile;

public class OpenOrCloseAction extends AbstractAction {
	private FileViewer viewer;
	
	public OpenOrCloseAction(FileViewer viewer) {
		this.viewer = viewer;
	}
	
	public void actionPerformed(ActionEvent e) {
		try {
			JFD jfd = viewer.getJFD();
			JFDModel model = jfd.getModel();
			VFile selectedFile = model.getSelectedFile();
			if(selectedFile != null
					&& selectedFile.isDirectory()) {
				model.setDirectoryAsynchIfNecessary(selectedFile, model.getCurrentDirectory(), jfd);
			} else {
				viewer.close();
			}
		} catch (Exception ex) {
			viewer.close();
		}
	}

}
