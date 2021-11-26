package com.nullfish.app.jfd2.viewer;

import com.nullfish.app.jfd2.ui.container2.JFDContainer;
import com.nullfish.app.jfd2.ui.container2.TitleUpdater;
import com.nullfish.lib.vfs.VFile;

public class FileNameTitleUpdater implements TitleUpdater {
	private JFDContainer container;
	
	public void setContainer(JFDContainer container) {
		this.container = container;
	}

	public void setFile(VFile file) {
		container.setTitle(file.getSecurePath());
	}
}
