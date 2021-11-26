/*
 * Created on 2004/08/27
 *
 */
package com.nullfish.app.jfd2.dnd;

import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.lib.vfs.VFile;

/**
 * @author shunji
 *
 */
public class JFDDragGestureListener implements DragGestureListener {
	private JFD jfd;
	
	public JFDDragGestureListener(JFD jfd) {
		this.jfd = jfd;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.dnd.DragGestureListener#dragGestureRecognized(java.awt.dnd.DragGestureEvent)
	 */
	public void dragGestureRecognized(DragGestureEvent e) {
        if((e.getDragAction()|DnDConstants.ACTION_COPY_OR_MOVE)!=0) {
        	VFile selectedFile = jfd.getModel().getSelectedFile();
        	VFile[] markedFiles = jfd.getModel().getMarkedFiles();
        	TransferableVFileList list;
        	if(markedFiles.length > 0) {
        		list = new TransferableVFileList(markedFiles, jfd, false);
        	} else {
        		VFile[] files = new VFile[1];
        		files[0] = selectedFile;
        		list = new TransferableVFileList(files, jfd, false);
        	}
			
			if((e.getDragAction()|DnDConstants.ACTION_COPY)!=0) {
				e.startDrag(DragSource.DefaultCopyDrop, list, null);
			} else {
				e.startDrag(DragSource.DefaultMoveDrop, list, null);
			}
        }
	}

}
