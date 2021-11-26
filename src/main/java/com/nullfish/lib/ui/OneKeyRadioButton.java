/*
 * Created on 2004/06/08
 *
 */
package com.nullfish.lib.ui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.KeyStroke;

/**
 * @author shunji
 *
 */
public class OneKeyRadioButton extends JRadioButton {
	public static final String SELECT_ACTION = "select";
	
	public OneKeyRadioButton() {
		getActionMap().put(SELECT_ACTION, new SelectAction(this));
	}

	public void setMnemonic(char mnemonic) {
		super.setMnemonic(mnemonic);
		InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		inputMap.put(KeyStroke.getKeyStroke(mnemonic), SELECT_ACTION);
	}
	
	/**
	 * 特定のラジオボタンを選び、フォーカスを呼ぶAction
	 */
	class SelectAction extends AbstractAction {
		JRadioButton button;
		
		public SelectAction(JRadioButton button) {
			this.button = button;
		}
			
		public void actionPerformed(ActionEvent e) {
			button.setSelected(true);
			requestFocus();
		}
	}
}
