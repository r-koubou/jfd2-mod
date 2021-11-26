/*
 * Created on 2004/08/31
 *
 */
package com.nullfish.app.jfd2.ui.labels;

import javax.swing.JLabel;

/**
 * @author shunji
 *
 */
public class ToolTipLabel extends JLabel {
	public void setText(String text) {
		setToolTipText(text);
		super.setText(text);
	}
/*	
	public Dimension getMinimumSize() {
		Dimension rtn = super.getMinimumSize();
		rtn.width = 1;
		return rtn;
	}
*/
}
