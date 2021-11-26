/*
 * Created on 2004/05/20
 *
 */
package com.nullfish.app.jfd2.ui.table;

import javax.swing.AbstractListModel;

import com.nullfish.app.jfd2.JFDModel;
import com.nullfish.app.jfd2.JFDModelListener;
import com.nullfish.lib.ui.ThreadSafeUtilities;

/**
 * @author shunji
 * 
 */
public class JFDListModel extends AbstractListModel implements JFDModelListener {
	/**
	 * JFDのモデル
	 */
	private JFDModel jfdModel;

	public void setJFDModel(JFDModel jfdModel) {
		if(this.jfdModel != null) {
			jfdModel.removeJFDModelListener(this);
		}
		
		this.jfdModel = jfdModel;
		jfdModel.addJFDModelListener(this);
	}

	public JFDModel getJFDModel() {
		return jfdModel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nullfish.app.jfd2.JFDModelListener#dataChanged(com.nullfish.app.jfd2.JFDModel)
	 */
	public void dataChanged(JFDModel model) {
		Runnable caller = new Runnable() {
			public void run() {
				fireContentsChanged(this, 0, getSize() - 1);
			}
		};
		
		ThreadSafeUtilities.executeRunnable(caller, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nullfish.app.jfd2.JFDModelListener#directoryChanged(com.nullfish.app.jfd2.JFDModel)
	 */
	public void directoryChanged(JFDModel model) {
		Runnable caller = new Runnable() {
			public void run() {
				fireContentsChanged(this, 0, getSize() - 1);
			}
		};
		
		ThreadSafeUtilities.executeRunnable(caller, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nullfish.app.jfd2.JFDModelListener#selectionChanged(int)
	 */
	public void cursorMoved(JFDModel model) {
		//fireContentsChanged(this, oldIndex, oldIndex);
		//fireContentsChanged(this, newIndex, newIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.ListModel#getSize()
	 */
	public int getSize() {
		return jfdModel.getFilesCount();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.ListModel#getElementAt(int)
	 */
	public Object getElementAt(int index) {
		return jfdModel.getFileAt(index);
	}
	
	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.JFDModelListener#markChanged(com.nullfish.app.jfd2.JFDModel)
	 */
	public void markChanged(JFDModel model) {
		// TODO Auto-generated method stub
		
	}
}
