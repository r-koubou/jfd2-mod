/*
 * Created on 2004/08/12
 *
 */
package com.nullfish.app.jfd2.dialog.components;

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;

/**
 * @author shunji
 *
 */
public class FocusNextAction extends AbstractAction {
	public JComponent component;
	
	public static final String NAME = "focus_next";
	
	/**
	 * コンストラクタ
	 * @param component
	 */
	public FocusNextAction(JComponent component) {
		this.component = component;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		Container container = getFocusCycleRoot(component);
		FocusTraversalPolicy policy = container.getFocusTraversalPolicy();
		Component nextComponent = policy.getComponentAfter(container, component);
		if(nextComponent != null) {
			nextComponent.requestFocusInWindow();
		} else {
			System.out.println(component);
		}
	}
	
	private Container getFocusCycleRoot(Component compo) {
		Container rtn = compo.getParent();
		while(!rtn.isFocusCycleRoot()) {
			rtn = rtn.getParent();
		}
		
		return rtn;
	}
}
