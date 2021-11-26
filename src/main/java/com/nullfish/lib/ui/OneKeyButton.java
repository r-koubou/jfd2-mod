/*
 * Created on 2004/06/07
 *
 */
package com.nullfish.lib.ui;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import com.nullfish.lib.keymap.KeyStrokeMap;

/**
 * Altとか無しで１キーで押せるボタン
 * 
 * @author shunji
 */
public class OneKeyButton extends JButton {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4870489433997724398L;
	public static final String CLICK_ACTION = "click_action";

	public static final String FOCUS_NEXT = "next";
	public static final String FOCUS_PREV = "prev";

	public OneKeyButton(KeyStroke key) {
		super();
		registerKey(key);
		
		init();
	}

	public OneKeyButton(Action a, KeyStroke key) {
		super(a);
		registerKey(key);
		
		init();
	}

	public OneKeyButton(Icon icon, KeyStroke key) {
		super(icon);
		registerKey(key);
		
		init();
	}

	public OneKeyButton(String text, KeyStroke key) {
		super(text + "(" + KeyEvent.getKeyText(key.getKeyCode()) + ")");
		registerKey(key);
		
		init();
	}

	public OneKeyButton(String text, Icon icon, KeyStroke key) {
		super(text + "(" + KeyEvent.getKeyText(key.getKeyCode()) + ")");
		registerKey(key);
		
		init();
	}
	
	private void init() {
		getActionMap().put(FOCUS_NEXT, new FocusNextAction());
		getActionMap().put(FOCUS_PREV, new FocusPrevAction());
		
		InputMap inputMap = getInputMap(JComponent.WHEN_FOCUSED);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), FOCUS_PREV);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), FOCUS_PREV);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), FOCUS_NEXT);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), FOCUS_NEXT);
		
	}

	public void registerKey(KeyStroke key) {
		this.setMnemonic(key.getKeyChar());
		ActionMap actionMap = getActionMap();

		actionMap.put(CLICK_ACTION, new ButtonClickAction(this));

		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(key, CLICK_ACTION);
		getInputMap(JComponent.WHEN_FOCUSED).put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_ENTER, 0), CLICK_ACTION);
	}

	public class ButtonClickAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 7696541629407658217L;
		AbstractButton button;

		public ButtonClickAction(AbstractButton button) {
			this.button = button;
		}

		public void actionPerformed(ActionEvent e) {
			button.doClick();
		}
	}
	
	class FocusNextAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			Container container = getFocusCycleRoot(OneKeyButton.this);
			container.getFocusTraversalPolicy().getComponentAfter(container, OneKeyButton.this).requestFocusInWindow();
		}
	}
	
	class FocusPrevAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			Container container = getFocusCycleRoot(OneKeyButton.this);
			container.getFocusTraversalPolicy().getComponentBefore(container, OneKeyButton.this).requestFocusInWindow();
		}
	}
	
	private Container getFocusCycleRoot(java.awt.Component compo) {
		Container rtn = compo.getParent();
		while(!rtn.isFocusCycleRoot()) {
			rtn = rtn.getParent();
		}

		return rtn;

	}
}
