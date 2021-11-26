/*
 * Created on 2004/05/25
 *
 */
package com.nullfish.app.jfd2.ui.labels;

import javax.swing.JLabel;

import com.nullfish.app.jfd2.JFDModel;
import com.nullfish.app.jfd2.JFDModelListener;

/**
 * マークファイル数情報表示ラベル
 * 
 * @author shunji
 */
public class MarkCountLabel extends JLabel {
	private JFDModel model;
	
	public void setModel(JFDModel model) {
		this.model = model;
		model.addJFDModelListener(new JFDModelListenerImpl(this));
	}

	public void showInfo() {
		int sum = 0;
		int fileCount = model.getFilesCount();
		for(int i=0; i<fileCount; i++) {
			if(model.isMarked(i)) {
				sum ++;
			}
		}
		
		this.setText(Integer.toString(sum));
	}
	
	private static class JFDModelListenerImpl implements JFDModelListener {
		MarkCountLabel label;
		
		public JFDModelListenerImpl(MarkCountLabel label) {
			this.label = label;
		}

		/* (non-Javadoc)
		 * @see com.nullfish.app.jfd2.JFDModelListener#dataChanged(com.nullfish.app.jfd2.JFDModel)
		 */
		public void dataChanged(JFDModel model) {
			label.showInfo();
		}

		/* (non-Javadoc)
		 * @see com.nullfish.app.jfd2.JFDModelListener#directoryChanged(com.nullfish.app.jfd2.JFDModel)
		 */
		public void directoryChanged(JFDModel model) {
			label.showInfo();
		}

		/* (non-Javadoc)
		 * @see com.nullfish.app.jfd2.JFDModelListener#cursorMoved(com.nullfish.app.jfd2.JFDModel, int, int)
		 */
		public void cursorMoved(JFDModel model) {
		}

		/* (non-Javadoc)
		 * @see com.nullfish.app.jfd2.JFDModelListener#markChanged(com.nullfish.app.jfd2.JFDModel)
		 */
		public void markChanged(JFDModel model) {
			label.showInfo();
		}
	}
}
