package com.nullfish.lib.ui.combobox;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;

import com.nullfish.app.jfd2.dialog.TextEditor;
import com.nullfish.lib.keymap.KeyStrokeMap;
import com.nullfish.lib.ui.FocusAndSelectAllTextField;
import com.nullfish.lib.ui.SimpleChooserDialog;
import com.nullfish.lib.ui.UIUtilities;

public class ComboBoxTextField extends FocusAndSelectAllTextField implements TextEditor {
	private SimpleChooserDialog dialog;
	
	private Window owner;
	
	private List list = new ArrayList();
	
	public ComboBoxTextField() {
		init();
	}
	
	private void init() {
		AbstractAction showListAction = new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				showPopup();
			}
		};

		getInputMap(WHEN_FOCUSED).put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_UP, 0), "show_list");
		getInputMap(WHEN_FOCUSED).put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_DOWN, 0), "show_list");
		
		getActionMap().put("show_list", showListAction);
	}
	
	public void showPopup() {
		if (dialog == null) {
			initTopLevelOwner();

			//	ダイアログが未作成の場合のみ新規作成する
			if (owner instanceof Frame) {
				dialog = new SimpleChooserDialog((Frame)owner);
			} else {
				dialog = new SimpleChooserDialog((Dialog)owner);
			}
			dialog.setUndecorated(true);
			dialog.pack();
		}

		Point point = getLocationOnScreen();

		dialog.setBounds((int) point.getX(), (int) point.getY() + getHeight(),
				this.getWidth(), dialog.getHeight());

		Object choosen = null;
		if (list.size() == 1) {
			choosen = list.get(0);
		} else {
			String currentText = getText();
			dialog.setListData(list);
			if(!list.contains(currentText) && currentText.length() > 0 ) {
				dialog.addData(currentText);
			}
			dialog.setSelectedData(currentText);
			dialog.setVisible(true);
			choosen = dialog.getChoosen();
		}
		
		if (choosen != null) {
			setText(choosen.toString());
		}
		
	}
	
	private void initTopLevelOwner() {
		owner = (Window)UIUtilities.getTopLevelOwner(this);
	}

	public void clear() {
		list.clear();
	}
	
	public void addItem(Object item) {
		list.add(0, item);
	}
}
