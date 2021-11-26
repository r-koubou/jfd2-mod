/*
 * Created on 2004/06/11
 *
 */
package com.nullfish.app.jfd2.dialog;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;

import com.nullfish.app.jfd2.dialog.focus.FocusOrderManagingPanel;
import com.nullfish.lib.ui.ReturnableRunnable;
import com.nullfish.lib.ui.ThreadSafeUtilities;

/**
 * @author shunji
 * 
 * Preferences - Java - Code Style - Code Templates
 */
public class MainContainerPanel extends FocusOrderManagingPanel {
	Map nameEditorMap = new HashMap();
	
	private int yCount = 0;
	
	private Component firstComponent;
	
	public MainContainerPanel() {
		setLayout(new GridBagLayout());
	}

//	public void add(String name, TextEditor editor) {
//		add(name, editor, null);
//	}
	
	public void add(String name, TextEditor editor, String title) {
		if(firstComponent == null) {
			firstComponent = editor.getComponent();
		}
		
		super.add(editor.getComponent(), new GridBagConstraints(1, yCount, 1, 1, 1,
				1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(0, 0, 0, 0), 0, 0));
		super.addMember(editor.getComponent());
		nameEditorMap.put(name, editor);
		
		if(title != null && title.length() > 0) {
			super.add(new JLabel(title), new GridBagConstraints(0, yCount, 1, 1, 1,
					1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
					new Insets(0, 0, 0, 10), 0, 0));
		}
		
		yCount++;
	}
	
	public void addLabel(String text) {
		JLabel label = new JLabel(text);
		label.setFont(getFont());
		super.add(label, new GridBagConstraints(0, getComponentCount(), 1, 1, 1,
				1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(0, 0, 0, 0), 0, 0));
	}
	
	public String getAnswer(final String name) {
		ReturnableRunnable runnable = new ReturnableRunnable() {
			private String rtn;
			
			public Object getReturnValue() {
				return rtn;
			}

			public void run() {
				rtn = (((TextEditor)nameEditorMap.get(name)).getAnswer());
			}
		};
		
		return (String)ThreadSafeUtilities.executeReturnableRunnable(runnable);
	}
	
	public boolean focusFirstComponent() {
		if(firstComponent != null) {
			firstComponent.requestFocusInWindow();
			return true;
		}
		
		return false;
	}
}
