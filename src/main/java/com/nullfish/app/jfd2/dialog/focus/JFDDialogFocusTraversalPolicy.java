/*
 * Created on 2004/08/16
 *
 */
package com.nullfish.app.jfd2.dialog.focus;

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;

/**
 * @author shunji
 *
 */
public class JFDDialogFocusTraversalPolicy extends FocusTraversalPolicy {
	/**
	 * 登録されているパネルのリスト
	 */
	private List panels = new ArrayList();

	/**
	 * 先頭パネル
	 */
	private FocusOrderManagingPanel startPanel;
	
	/**
	 * コンストラクタ
	 * @param startPanel
	 */
	public JFDDialogFocusTraversalPolicy(FocusOrderManagingPanel startPanel) {
		this.startPanel = startPanel;
	}
	
	public void addPanel(FocusOrderManagingPanel panel,
			FocusOrderManagingPanel prev, FocusOrderManagingPanel next) {
		panel.setNextPanel(next);
		panel.setPrevPanel(prev);
		panels.add(panel);
	}
	
    public Component getInitialComponent(Window window) {
    	return getDefaultComponent(window);
    }

    /* (non-Javadoc)
	 * @see java.awt.FocusTraversalPolicy#getDefaultComponent(java.awt.Container)
	 */
	public Component getDefaultComponent(Container focusCycleRoot) {
		return startPanel.getComponentToFocus(0);
	}

	/* (non-Javadoc)
	 * @see java.awt.FocusTraversalPolicy#getFirstComponent(java.awt.Container)
	 */
	public Component getFirstComponent(Container focusCycleRoot) {
		return startPanel.getComponentToFocus(0);
	}

	/* (non-Javadoc)
	 * @see java.awt.FocusTraversalPolicy#getLastComponent(java.awt.Container)
	 */
	public Component getLastComponent(Container focusCycleRoot) {
		return startPanel.getComponentToFocus(-1);
	}

	/* (non-Javadoc)
	 * @see java.awt.FocusTraversalPolicy#getComponentAfter(java.awt.Container, java.awt.Component)
	 */
	public Component getComponentAfter(Container focusCycleRoot,
			Component aComponent) {
		if(aComponent == null) {
			return getDefaultComponent(focusCycleRoot);
		}

		for(int i=0; i<panels.size(); i++) {
			FocusOrderManagingPanel panel = ((FocusOrderManagingPanel)panels.get(i));
			int index = panel.indexOf(aComponent);
			if(index != -1) {
				return panel.getComponentToFocus(index + 1);
			}
		}
		
		return getComponentAfter(focusCycleRoot, aComponent.getParent());
	}

	/* (non-Javadoc)
	 * @see java.awt.FocusTraversalPolicy#getComponentBefore(java.awt.Container, java.awt.Component)
	 */
	public Component getComponentBefore(Container focusCycleRoot,
			Component aComponent) {
		if(aComponent == null) {
			return getDefaultComponent(focusCycleRoot);
		}

		for(int i=0; i<panels.size(); i++) {
			FocusOrderManagingPanel panel = ((FocusOrderManagingPanel)panels.get(i));
			int index = panel.indexOf(aComponent);
			if(index != -1) {
				return panel.getComponentToFocus(index - 1);
			}
		}
		
		return getComponentBefore(focusCycleRoot, aComponent.getParent());
	}
}
