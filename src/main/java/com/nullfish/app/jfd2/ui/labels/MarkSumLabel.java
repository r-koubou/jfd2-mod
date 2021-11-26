/*
 * Created on 2004/05/25
 *
 */
package com.nullfish.app.jfd2.ui.labels;

import java.text.DecimalFormat;

import javax.swing.JLabel;

import com.nullfish.app.jfd2.JFDModel;
import com.nullfish.app.jfd2.JFDModelListener;
import com.nullfish.lib.vfs.exception.VFSException;

/**
 * マークファイルのサイズ情報表示ラベル
 * 
 * @author shunji
 */
public class MarkSumLabel extends JLabel {
	private JFDModel model;
	
	DecimalFormat format = new DecimalFormat("##,###,###,##0");

	public void setModel(JFDModel model) {
		this.model = model;
		model.addJFDModelListener(new JFDModelListenerImpl(this));
	}

	public void showInfo() {
		try {
			long sum = 0;
			int fileCount = model.getFilesCount();
			for(int i=0; i<fileCount; i++) {
				if(model.isMarked(i)) {
					sum += model.getFileAt(i).getLength();
				}
			}
			
			this.setText(format.format(sum));
		} catch (VFSException e) {
			setText("N/A");
		}
	}
	
	private static class JFDModelListenerImpl implements JFDModelListener {
		MarkSumLabel label;
		
		public JFDModelListenerImpl(MarkSumLabel label) {
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
