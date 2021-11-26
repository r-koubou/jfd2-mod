package com.nullfish.app.jfd2.util;

import java.util.ResourceBundle;

import javax.swing.JComboBox;

public class ResourceBundleComboBox extends JComboBox {
	public ResourceBundleComboBox(ResourceBundle resourceBundle) {
		setRenderer(new ResourceBundleListCellRenderer(resourceBundle));
		setEditable(false);
	}
}
