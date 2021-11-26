package com.nullfish.app.jfd2.dialog;

import java.awt.BorderLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JComponent;
import javax.swing.JPanel;

import com.nullfish.lib.ui.FocusAndSelectAllTextField;

public class ExtensionSpritFileNameEditor extends JPanel implements TextEditor {
	private FocusAndSelectAllTextField nameField = new FocusAndSelectAllTextField();
	private FocusAndSelectAllTextField extensionField = new FocusAndSelectAllTextField();

	public ExtensionSpritFileNameEditor() {
		setLayout(new BorderLayout());
		extensionField.setColumns(5);
		add(nameField, BorderLayout.CENTER);
		add(extensionField, BorderLayout.EAST);
		
		setFocusCycleRoot(true);
		addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				nameField.requestFocusInWindow();
			}

			public void focusLost(FocusEvent e) {
			}
		});
		
		
	}
	
	public String getAnswer() {
		return nameField.getText() + extensionField.getText();
	}

	public JComponent getComponent() {
		return this;
	}

	public void setAnswer(String text) {
		if(text.indexOf('.') != -1) {
			nameField.setText(text.substring(0, text.lastIndexOf('.')));
			extensionField.setText(text.substring(text.lastIndexOf('.')));
		} else {
			nameField.setText(text);
			extensionField.setText("");
		}
	}

	public void setColumns(int columns) {
		nameField.setColumns(columns - 5);
	}
}
