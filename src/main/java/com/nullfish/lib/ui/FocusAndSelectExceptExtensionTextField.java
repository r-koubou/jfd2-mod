/*
 * Created on 2004/07/09
 *
 */
package com.nullfish.lib.ui;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JTextField;

import com.nullfish.app.jfd2.dialog.TextEditor;
import com.nullfish.app.jfd2.dialog.components.FocusNextAction;
import com.nullfish.lib.keymap.KeyStrokeMap;


/**
 * ダイアログ用テキストフィールド
 * フォーカスを受け取ったら全選択する。
 * 
 * @author shunji
 */
public class FocusAndSelectExceptExtensionTextField extends JTextField implements TextEditor {
	public FocusAndSelectExceptExtensionTextField() {
		super();
		
		addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				int dotIndex = getText().lastIndexOf('.');
				
				setSelectionStart(0);
				setSelectionEnd(dotIndex != -1 ? dotIndex : getText().length());
			}

			public void focusLost(FocusEvent e) {
			}
		});
		
		getInputMap(JComponent.WHEN_FOCUSED).put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_ENTER, 0), FocusNextAction.NAME);
		getActionMap().put(FocusNextAction.NAME, new FocusNextAction(this));
	}

	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.ui.dialog.TextEditor#getAnswer()
	 */
	public String getAnswer() {
		return getText();
	}

	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.ui.dialog.TextEditor#getComponent()
	 */
	public JComponent getComponent() {
		return this;
	}

	public void setAnswer(String text) {
		setText(text);
	}
}
