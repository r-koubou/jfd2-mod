/*
 * Created on 2004/09/03
 *
 */
package com.nullfish.app.jfd2.dialog.components;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ComboBoxEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;
import javax.swing.text.JTextComponent;

import com.nullfish.app.jfd2.dialog.TextEditor;
import com.nullfish.lib.keymap.KeyStrokeMap;

/**
 * @author shunji
 * 
 * Preferences - Java - Code Style - Code Templates
 */
public class DialogComboBox extends JComboBox implements TextEditor {
	private ComboBoxEditor editor;

	private FocusNextAction focusNextAction;

	private CloseAction closeAction;

	private boolean closesOnDecision = false;
	
	public DialogComboBox(JDialog dialog) {
		final ComboBoxEditor editor = getEditor();

		editor.getEditorComponent().addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) {
				((JTextComponent) editor.getEditorComponent()).selectAll();
			}

			public void focusLost(FocusEvent arg0) {
			}
		});
		initUI(editor, dialog);
	}

	public DialogComboBox(ComboBoxEditor editor, JDialog dialog) {
		initUI(editor, dialog);
	}

	private void initUI(ComboBoxEditor editor, JDialog dialog) {
		this.editor = editor;

		setEditor(editor);

		//	次コンポーネントフォーカスアクションを無効化
		((JComponent) editor.getEditorComponent()).getInputMap(
				JComponent.WHEN_FOCUSED).put(
				KeyStrokeMap.getKeyStroke(KeyEvent.VK_ENTER, 0), null);

		focusNextAction = new FocusNextAction(this);
		closeAction = new CloseAction(dialog);
		editor.addActionListener(closeAction);

		addKeyListener(new KeyAdapter() {
		    public void keyPressed(KeyEvent e) {
		    	if(KeyStrokeMap.getKeyStrokeForEvent(e).equals(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0))) {
		    		if(closesOnDecision) {
		    			closeAction.actionPerformed(null);
		    		} else {
		    			focusNextAction.actionPerformed(null);
		    		}
		    	}
		    }
		});
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nullfish.app.jfd2.dialog.TextEditor#getAnswer()
	 */
	public String getAnswer() {
		return (String) getSelectedItem();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nullfish.app.jfd2.dialog.TextEditor#getComponent()
	 */
	public JComponent getComponent() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nullfish.app.jfd2.dialog.DialogComponent#setClosesOnDecision(boolean)
	 */
	public void setClosesOnDecision(boolean close) {
		closesOnDecision = close;
		if (close) {
			editor.removeActionListener(focusNextAction);
			editor.addActionListener(closeAction);
		} else {
			editor.removeActionListener(closeAction);
			editor.addActionListener(focusNextAction);
		}
	}

	public void setAnswer(String text) {
		setSelectedItem(text);
	}
}
