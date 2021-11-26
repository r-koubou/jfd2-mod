package com.nullfish.app.jfd2.ui.labels;

import java.text.DecimalFormat;

/**
 * @author shunji
 *
 */
public class FileSystemSizeLabel extends ToolTipLabel {
	private DecimalFormat format = new DecimalFormat("#,###,###,###,##0 ");

	public void setSize(long size) {
		if(size == -1) {
			setText("N/A");
		} else {
			setText(format.format(size));
		}
	}
}
