/*
 * Created on 2004/06/07
 *
 */
package com.nullfish.app.jfd2.dialog;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import com.nullfish.app.jfd2.dialog.focus.FocusOrderManagingPanel;
import com.nullfish.lib.keymap.KeyStrokeMap;
import com.nullfish.lib.ui.OneKeyButton;

/**
 * ボタンのパネル。
 * 
 * @author shunji
 * 
 */
public class ButtonsPanel extends FocusOrderManagingPanel {
	OneKeyButton defaultSelectedButton;
	
	String answer;

	List listeners = new ArrayList();

	public static final String NEXT = "next_button";
	public static final String PREV = "prev_button";
	
	private static boolean isMac = System.getProperty("os.name").indexOf("Mac OS") >= 0;
	
	public ButtonsPanel() {
		setLayout(new FlowLayout(isMac ? FlowLayout.RIGHT : FlowLayout.CENTER));
		ActionMap actionMap = getActionMap();
		actionMap.put(JFDDialog.CANCEL, new AnswerAction(null));
		actionMap.put(NEXT, new NextButtonAction(1));
		actionMap.put(PREV, new NextButtonAction(-1));
		
		this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStrokeMap.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JFDDialog.CANCEL);
		
		InputMap ancestorMap = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		ancestorMap.put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_RIGHT, 0), NEXT);
		ancestorMap.put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_LEFT, 0), PREV);
	}

	public void addButton(String name, String label, char mnemonic,
			boolean defaultButton) {
		AnswerAction action = new AnswerAction(name);
		action.putValue(Action.NAME, label + "(" + Character.toUpperCase(mnemonic) + ")");
		OneKeyButton button = new OneKeyButton(action, KeyStroke.getKeyStroke(mnemonic));
		button.setFont(getFont());
		if(isMac) {
			this.add(button, 0);
		} else {
			this.add(button);
		}
		super.addMember(button);
		if (defaultButton) {
			answer = name;
		}
		
		if(defaultSelectedButton == null || defaultButton) {
			defaultSelectedButton = button;
		}
	}

	public String getAnswer() {
		return answer;
	}

	public void canceled() {
		answer = null;
	}

	public void addActionListener(ActionListener listener) {
		listeners.add(listener);
	}

	private void fireActionPerformed() {
		for (int i = 0; i < listeners.size(); i++) {
			ActionListener listener = (ActionListener) listeners.get(i);
			listener.actionPerformed(new ActionEvent(this,
					ActionEvent.ACTION_PERFORMED, answer));
		}
	}

	private class AnswerAction extends AbstractAction {
		String name;

		public AnswerAction(String name) {
			this.name = name;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			answer = name;
			fireActionPerformed();
		}
	}
	
	/**
	 * ボタンのフォーカスを遷移させるアクション
	 */
	private class NextButtonAction extends AbstractAction {
		private int delta;

		public NextButtonAction(int delta) {
			this.delta = delta;
		}
		
		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			int size = getCount();
			for(int i=0; i<size; i++) {
				Component button = getComponent(i);
				if(button.hasFocus()) {
					i += delta;
					while(i < 0) {
						i += size;
					}
					
					while(i >= size) {
						i -= size;
					}
					
					getComponent(i).requestFocusInWindow();
					return;
				}
			}
		}
	}
	
	public boolean focusFirstComponent() {
		if(defaultSelectedButton == null) {
			return false;
		}
		
		defaultSelectedButton.requestFocusInWindow();
		
		return true;
	}
}
