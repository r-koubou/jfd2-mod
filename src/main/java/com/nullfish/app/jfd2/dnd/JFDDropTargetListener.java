/*
 * Created on 2004/08/26
 *
 */
package com.nullfish.app.jfd2.dnd;

import java.awt.dnd.DropTargetDropEvent;

import com.nullfish.app.jfd2.JFD;

/**
 * @author shunji
 *
 */
public class JFDDropTargetListener extends AbstractJFDDropTargetListener {
	private JFD jfd;
	
	public JFDDropTargetListener(JFD jfd) {
		this.jfd = jfd;
	}

	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.dnd.AbstractJFDDropTargetListener#getJFD()
	 */
	public JFD getJFD(DropTargetDropEvent dtde) {
		return jfd;
	}

	
}
