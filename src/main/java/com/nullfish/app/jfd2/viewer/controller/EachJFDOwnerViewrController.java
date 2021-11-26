package com.nullfish.app.jfd2.viewer.controller;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.ui.container2.JFDOwner;
import com.nullfish.app.jfd2.viewer.FileViewer;
import com.nullfish.app.jfd2.viewer.FileViewerFactory;
import com.nullfish.app.jfd2.viewer.FileViewerManager;
import com.nullfish.app.jfd2.viewer.ViewerController;
import com.nullfish.app.jfd2.viewer.ViewerSet;
import com.nullfish.lib.vfs.VFile;

public class EachJFDOwnerViewrController implements ViewerController {
	public FileViewer getViewer(JFD jfd, VFile file, FileViewerFactory factory) {
		JFDOwner owner = jfd.getJFDOwner();
		if (owner == null) {
			return null;
		}
		
		return FileViewerManager.getInstance().getFileViewerCache(
				owner).getCachedViewer(factory, jfd);
	}
	
	public ViewerSet getViewerSet(JFD jfd) {
		JFDOwner owner = jfd.getJFDOwner();
		if (owner == null) {
			return null;
		}
		
		return FileViewerManager.getInstance().getViewerSet(owner);
	}
	
	public void close(FileViewer viewer) {
	}
	
}
