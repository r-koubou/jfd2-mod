package com.nullfish.lib.ui.tristate_checkbox;

//package eu.javaspecialists.tjsn.gui;

import java.awt.AWTEvent;
import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ActionMapUIResource;

public class TristateCheckBox extends JCheckBox {
	private static final Composite HALF_SELECT_COMPOSITE = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f);
	
	private boolean allowsIndetermined = true;
	
	public static final String FOCUS_NEXT = "next";
	public static final String FOCUS_PREV = "prev";

	public TristateCheckBox(String text) {
		this(text, null, TristateState.DESELECTED);
		
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

	public TristateCheckBox(String text, Icon icon, TristateState initial) {
		super(text, icon);

		// Set default single model
		setModel(new TristateButtonModel(initial));

		// override action behaviour
		super.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				TristateCheckBox.this.iterateState();
			}
		});
		ActionMap actions = new ActionMapUIResource();
		actions.put("pressed", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				TristateCheckBox.this.iterateState();
			}
		});
		actions.put("released", null);
		SwingUtilities.replaceUIActionMap(this, actions);
		
		init();
	}

	// Next two methods implement new API by delegation to model
	public void setIndeterminate() {
		getTristateModel().setIndeterminate();
	}

	public boolean isIndeterminate() {
		return getTristateModel().isIndeterminate();
	}

	public TristateState getState() {
		return getTristateModel().getState();
	}

	// Overrides superclass method
	public void setModel(ButtonModel newModel) {
		super.setModel(newModel);
	}

	// Empty override of superclass method
	public void addMouseListener(MouseListener l) {
	}

	// Mostly delegates to model
	private void iterateState() {
		// Maybe do nothing at all?
		if (!getModel().isEnabled())
			return;

		grabFocus();

		// Iterate state
		getTristateModel().iterateState();
		
		if(!allowsIndetermined && getTristateModel().getState() == TristateState.INDETERMINATE) {
			getTristateModel().iterateState();
		}

		// Fire ActionEvent
		int modifiers = 0;
		AWTEvent currentEvent = EventQueue.getCurrentEvent();
		if (currentEvent instanceof InputEvent) {
			modifiers = ((InputEvent) currentEvent).getModifiers();
		} else if (currentEvent instanceof ActionEvent) {
			modifiers = ((ActionEvent) currentEvent).getModifiers();
		}
		fireActionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
				getText(), System.currentTimeMillis(), modifiers));
	}

	// Convenience cast
	public TristateButtonModel getTristateModel() {
		return (TristateButtonModel) super.getModel();
	}
	
	public void paintComponent(Graphics g) {
		if(((TristateButtonModel)getModel()).isIndeterminate()) {
			((Graphics2D)g).setComposite(HALF_SELECT_COMPOSITE);
		}
		super.paintComponent(g);
	}
	
	public boolean isAllowsIndetermined() {
		return allowsIndetermined;
	}

	public void setAllowsIndetermined(boolean allowsIndetermined) {
		this.allowsIndetermined = allowsIndetermined;
	}

	
	class FocusNextAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			Container container = getFocusCycleRoot(TristateCheckBox.this);
			container.getFocusTraversalPolicy().getComponentAfter(container, TristateCheckBox.this).requestFocusInWindow();
		}
	}
	
	class FocusPrevAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			Container container = getFocusCycleRoot(TristateCheckBox.this);
			container.getFocusTraversalPolicy().getComponentBefore(container, TristateCheckBox.this).requestFocusInWindow();
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
