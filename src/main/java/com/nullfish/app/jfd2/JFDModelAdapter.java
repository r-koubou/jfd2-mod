/*
 * Created on 2004/05/25
 *
 */
package com.nullfish.app.jfd2;

/**
 * @author shunji
 *
 */
public abstract class JFDModelAdapter implements JFDModelListener {

	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.JFDModelListener#dataChanged(com.nullfish.app.jfd2.JFDModel)
	 */
	public void dataChanged(JFDModel model) {
	}

	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.JFDModelListener#directoryChanged(com.nullfish.app.jfd2.JFDModel)
	 */
	public void directoryChanged(JFDModel model) {
	}

	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.JFDModelListener#cursorMoved(com.nullfish.app.jfd2.JFDModel, int, int)
	 */
	public void cursorMoved(JFDModel model, int oldIndex, int newIndex) {
	}

	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.JFDModelListener#cursorMoved(com.nullfish.app.jfd2.JFDModel)
	 */
	public void cursorMoved(JFDModel model) {
	}

	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.JFDModelListener#markChanged(com.nullfish.app.jfd2.JFDModel)
	 */
	public void markChanged(JFDModel model) {
	}
}
