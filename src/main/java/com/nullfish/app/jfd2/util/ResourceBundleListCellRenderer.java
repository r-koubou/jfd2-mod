package com.nullfish.app.jfd2.util;

import java.awt.Component;
import java.util.ResourceBundle;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

public class ResourceBundleListCellRenderer extends DefaultListCellRenderer {
	private ResourceBundle resourceBundle;

	public ResourceBundleListCellRenderer(ResourceBundle resourceBundle) {
		this.resourceBundle = resourceBundle;
	}

	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		return super.getListCellRendererComponent(list, resourceBundle
				.getString((String) value), index, isSelected, cellHasFocus);
	}
}
