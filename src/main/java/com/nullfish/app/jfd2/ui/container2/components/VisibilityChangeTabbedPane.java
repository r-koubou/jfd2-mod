/*
 * Created on 2004/11/18
 *
 */
package com.nullfish.app.jfd2.ui.container2.components;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JTabbedPane;

/**
 * タブ数が0の時は不可視になるタブペイン
 * 
 * @author shunji
 */
public class VisibilityChangeTabbedPane extends JTabbedPane {
	public VisibilityChangeTabbedPane() {
		//	クリックされたら中身をフォーカス
		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				getSelectedComponent().requestFocusInWindow();
			}
		});
		
		setVisible(false);
	}
	
	public TabContainer getNewContainer() {
		return new TabContainer(this);
	}
	
	public boolean contains(Component c) {
		for(int i=0; i<getComponentCount(); i++) {
			TabContainer container = (TabContainer)getComponentAt(i);
			if(container.getComponent(0) == c) {
				return true;
			}
		}
		
		return false;
	}
	
    public void insertTab(String title, Icon icon, Component component, String tip, int index) {
    	super.insertTab(title, icon, component, tip, index);
		setVisible(getTabCount() > 0);
    }
    
    public void removeTabAt(int index) {
    	super.removeTabAt(index);
		setVisible(getTabCount() > 0);
		if(getTabCount() > 0) {
			getSelectedComponent().requestFocusInWindow();
		}
    }
}
