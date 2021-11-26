/*
 * Created on 2004/09/28
 *
 */
package com.nullfish.app.jfd2.dialog;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JPasswordField;

import com.nullfish.app.jfd2.dialog.components.FocusNextAction;
import com.nullfish.lib.keymap.KeyStrokeMap;

/**
 * @author shunji
 * 
 * Preferences - Java - Code Style - Code Templates
 */
public class JFDPasswordField extends JPasswordField implements TextEditor {
	public JFDPasswordField() {
		super();
		
		addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				setSelectionStart(0);
				setSelectionEnd(getPassword().length);
			}

			public void focusLost(FocusEvent e) {
			}
		});
		
		getInputMap(JComponent.WHEN_FOCUSED).put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_ENTER, 0), FocusNextAction.NAME);
		getActionMap().put(FocusNextAction.NAME, new FocusNextAction(this));
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nullfish.app.jfd2.dialog.TextEditor#getAnswer()
	 */
	public String getAnswer() {
		return new String(getPassword());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nullfish.app.jfd2.dialog.TextEditor#getComponent()
	 */
	public JComponent getComponent() {
		return this;
	}

	public void setAnswer(String text) {
		setText(text);
	}

}
