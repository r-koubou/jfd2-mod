/*
 * Created on 2004/05/25
 *
 */
package com.nullfish.app.jfd2.ui.labels;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JLabel;

import com.nullfish.app.jfd2.JFDModel;
import com.nullfish.app.jfd2.JFDModelListener;
import com.nullfish.app.jfd2.util.TagUtil;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;

/**
 * ファイル情報の表示ラベル
 * 
 * @author shunji
 */
public class FileInfoLabel extends JLabel {
	DecimalFormat format = new DecimalFormat("##,###,###,##0");
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	FieldPosition integerPosition =
		new FieldPosition(NumberFormat.INTEGER_FIELD);

	private StringBuffer buffer = new StringBuffer();
	
	public void setModel(JFDModel model) {
		model.addJFDModelListener(new JFDModelListenerImpl(this));
	}

	public void showInfo(VFile file) {
		try {
			buffer.setLength(0);
	
			if(file == null) {
				setText("");
				return;
			} else if (file.isDirectory()) {
				buffer.append("         <DIR>");
			} else {
				format.format(
					file.getLength(),
					buffer,
					integerPosition);
	
				for (int i = 0; i < 14 - integerPosition.getEndIndex(); i++) {
					buffer.insert(0, ' ');
				}
			}
	
			buffer.append(' ');
			Date date = file.getTimestamp();
			if(date != null) {
				buffer.append(dateFormat.format(date));
			} else {
				buffer.append("0000-00-00 00:00:00");
			}
			buffer.append(' ');
	
			if(file.getPermission() != null) {
				buffer.append(file.getPermission().getPermissionString());
			}
			
			buffer.append(" ");
			buffer.append(TagUtil.file2TagString(file));
	
			setText(buffer.toString());
		} catch (VFSException e) {
			setText("N/A");
		}
	}
	
	private static class JFDModelListenerImpl implements JFDModelListener {
		FileInfoLabel label;
		
		public JFDModelListenerImpl(FileInfoLabel label) {
			this.label = label;
		}

		/* (non-Javadoc)
		 * @see com.nullfish.app.jfd2.JFDModelListener#dataChanged(com.nullfish.app.jfd2.JFDModel)
		 */
		public void dataChanged(JFDModel model) {
			label.showInfo(model.getSelectedFile());
		}

		/* (non-Javadoc)
		 * @see com.nullfish.app.jfd2.JFDModelListener#directoryChanged(com.nullfish.app.jfd2.JFDModel)
		 */
		public void directoryChanged(JFDModel model) {
			label.showInfo(model.getSelectedFile());
		}

		/* (non-Javadoc)
		 * @see com.nullfish.app.jfd2.JFDModelListener#cursorMoved(com.nullfish.app.jfd2.JFDModel, int, int)
		 */
		public void cursorMoved(JFDModel model) {
			label.showInfo(model.getSelectedFile());
		}

		/* (non-Javadoc)
		 * @see com.nullfish.app.jfd2.JFDModelListener#markChanged(com.nullfish.app.jfd2.JFDModel)
		 */
		public void markChanged(JFDModel model) {
		}
	}
}
