/*
 * Created on 2004/08/08
 *
 */
package com.nullfish.app.jfd2.dialog.components;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import com.nullfish.app.jfd2.dialog.ConfigurationInfo;
import com.nullfish.app.jfd2.dialog.DialogComponent;
import com.nullfish.lib.keymap.KeyStrokeMap;
import com.nullfish.lib.ui.ThreadSafeUtilities;


public class ConfigCheckBox extends JCheckBox implements DialogComponent {
	private ConfigurationInfo config;
	
	public static final String CLICK_ACTION = "click_action";

	private boolean closeOnDecision = false;
	
	public ConfigCheckBox(String label) {
		this(label, false);
	}
	
	public ConfigCheckBox(String label, boolean selected) {
		super(label, selected);

		getInputMap(JComponent.WHEN_FOCUSED).put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_ENTER, 0, true), FocusNextAction.NAME);
		getActionMap().put(FocusNextAction.NAME, new FocusNextAction(this));
		
		getActionMap().put(CLICK_ACTION, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				doClick();
				requestFocusInWindow();
			}
		});
	}
	
	public void setConfigueationInfo(final ConfigurationInfo config) {
		if(config == null) {
			return;
		}
		
		Runnable runnable = new Runnable() {
			public void run() {
				ConfigCheckBox.this.config = config;
				Boolean value = (Boolean)config.getParam();
				if(value != null) {
					setSelected(value.booleanValue());
				} else {
					setSelected(false);
				}
			}
		};
		
		ThreadSafeUtilities.executeRunnable(runnable);
	}

	public void applyConfiguration() {
		Runnable runnable = new Runnable() {
			public void run() {
				if(config != null) {
					config.setParam(new Boolean(isSelected()));
				}
			}
		};
		
		ThreadSafeUtilities.executeRunnable(runnable);
	}

	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.dialog.DialogComponent#setClosesOnDecision(boolean)
	 */
	public void setClosesOnDecision(final boolean bool) {
		Runnable runnable = new Runnable() {
			public void run() {
				ConfigCheckBox.this.closeOnDecision = bool;
			}
		};
		
		ThreadSafeUtilities.executeRunnable(runnable);
	}

	public void setMnemonic(int c) {
		super.setMnemonic(c);

		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke((char)c), CLICK_ACTION);
	};
	
	public void setMnemonic(char c) {
		super.setMnemonic(c);

		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(c), CLICK_ACTION);
	}
	
	public String getText() {
		if(getMnemonic() >= 0) {
			return super.getText() + '(' + Character.toUpperCase((char)getMnemonic()) + ')';
		} else {
			return super.getText();
		}
	}
}
