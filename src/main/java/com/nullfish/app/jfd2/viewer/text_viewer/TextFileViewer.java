package com.nullfish.app.jfd2.viewer.text_viewer;

import java.awt.Component;
import java.util.ResourceBundle;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.JFDComponent;
import com.nullfish.app.jfd2.ui.container2.JFDOwner;
import com.nullfish.app.jfd2.viewer.AbstractFileViewer;
import com.nullfish.app.jfd2.viewer.FileViewerContainerPanel;
import com.nullfish.lib.vfs.VFile;

public class TextFileViewer extends AbstractFileViewer {
	private TextViewerPanel panel = new TextViewerPanel(this);
	
	public static final ResourceBundle RESOURCE = ResourceBundle.getBundle("text_viewer");
	
	public void doOpen(VFile file, JFD jfd) {
		close();
		panel.open(jfd);
		panel.setFile(file, true);
		
		JFDOwner owner = jfd.getJFDOwner();
		JFDComponent activeComponent = "opponent".equals((String)getParam(FileViewerContainerPanel.PARAM_CONSTRAINTS) ) ? (JFDComponent)jfd : panel;
		if(owner != null) {
			owner.setActiveComponent(activeComponent);
		}
		((Component)activeComponent).requestFocusInWindow();
	}

	public void doClose() {
		panel.close();

	}

	public void dispose() {
		
	}

	public void init(VFile baseDir) {
		panel.init(baseDir);
//		panel.setEncoding((List)getParam("encoding"));
	}
}
