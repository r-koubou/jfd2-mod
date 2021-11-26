/*
 * Created on 2005/01/23
 *
 */
package com.nullfish.app.jfd2.viewer;

import java.util.HashMap;
import java.util.Map;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.JFDModel;
import com.nullfish.app.jfd2.JFDModelAdapter;
import com.nullfish.lib.vfs.VFile;

/**
 * @author shunji
 *
 */
public abstract class AbstractFileViewer implements FileViewer {
	/**
	 * パラメータのマップ
	 */
	private Map paramMap = new HashMap();
	
	private FileViewerConstraints constraints;
	
	private FileViewerPosition position;
	
	private JFD jfd;
	
	private boolean followsOwner = false;
	
	private OwnerFollowew follower = new OwnerFollowew();
	
	private ViewerController controller;
	
	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.viewer.FileViewer#init(com.nullfish.lib.vfs.VFile)
	 */
	public void init(VFile baseDir) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.viewer.FileViewer#setParam(java.lang.String, java.lang.Object)
	 */
	public void setParam(String name, Object value) {
		paramMap.put(name, value);
	}

	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.viewer.FileViewer#getParam(java.lang.String)
	 */
	public Object getParam(String name) {
		return paramMap.get(name);
	}
	
	public void setConstraints(FileViewerConstraints constraints) {
		this.constraints = constraints;
	}
	
	public FileViewerConstraints getConstraints() {
		return constraints;
	}
	
	public FileViewerPosition getPosition() {
		return position;
	}

	public void setPosition(FileViewerPosition position) {
		this.position = position;
	}

	public final void open(VFile file, JFD jfd) {
		this.jfd = jfd;
		if(followsOwner) {
			jfd.getModel().removeJFDModelListener(follower);
			jfd.getModel().addJFDModelListener(follower);
		}
		
		doOpen(file, jfd);
	}
	
	public abstract void doOpen(VFile file, JFD jfd);
	
	public final void close() {
		if(followsOwner) {
			jfd.getModel().removeJFDModelListener(follower);
		}

		FileViewerManager.getInstance().fileViewerClosed(this);
		doClose();
		getController().close(this);
	}
	
	public abstract void doClose();
	
	
	public void setFollowsCursor(boolean followsOwner) {
		this.followsOwner = followsOwner;
	}
	
	public JFD getJFD() {
		return jfd;
	}
	
	public void setController(ViewerController controller) {
		this.controller = controller;
	}

	public ViewerController getController() {
		return controller;
	}

	/**
	 * jFD本体のカーソル移動を追うクラス
	 * @author shunji
	 *
	 */
	class OwnerFollowew extends JFDModelAdapter {
		public void directoryChanged(JFDModel model) {
			reopen();
		}

		public void cursorMoved(JFDModel model) {
			reopen();
		}
		
		private void reopen() {
			//close();
			open(jfd.getModel().getSelectedFile(), jfd);
		}
	}
}
