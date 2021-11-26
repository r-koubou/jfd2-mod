package com.nullfish.app.jfd2.config;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import com.nullfish.app.jfd2.resource.JFDResource;

public class BgImageAlignComboBoxRenderer extends BasicComboBoxRenderer {
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		JLabel rtn = (JLabel) super.getListCellRendererComponent(list, value,
				index, isSelected, cellHasFocus);
		if(value != null) {
			rtn.setText(JFDResource.LABELS.getString("bg_image_align_" + value));
		} else {
			rtn.setText("");
		}
		
		return rtn;
	}
}
