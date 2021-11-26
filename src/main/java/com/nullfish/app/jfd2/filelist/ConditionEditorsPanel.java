package com.nullfish.app.jfd2.filelist;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.resource.JFDResource;
import com.nullfish.lib.vfs.VFS;
import com.nullfish.lib.vfs.exception.VFSException;
import com.nullfish.lib.vfs.impl.filelist.condition.Condition;

public class ConditionEditorsPanel extends JPanel {
	private List children = new ArrayList();
	
	private JFD jfd;
	
	public ConditionEditorsPanel(JFD jfd) {
		this.jfd = jfd;
		setBorder(BorderFactory.createTitledBorder(JFDResource.LABELS.getString("search_condition")));
	}
	
	public int getCount() {
		return children.size();
	}
	
	public void updated() {
		for(int i=0; i<children.size(); i++) {
			((ConditionEditorPanel)children.get(i)).parentUpdated();
		}
	}
	
	public void addNew() {
		ConditionEditorPanel newPanel = new ConditionEditorPanel(this);
		children.add(newPanel);
		
		relayout();
	}
	
	public void removeChild(ConditionEditorPanel child) {
		children.remove(child);
		
		relayout();
	}
	
	private void relayout() {
		invalidate();
		updated();
		removeAll();
		setLayout(new GridLayout(children.size(), 1, 3, 3));
		
		for(int i=0; i<children.size(); i++) {
			add(((ConditionEditorPanel)children.get(i)));
		}
		
		SwingUtilities.getWindowAncestor(this).pack();
		validate();
	}
	
	public List toConditionList(VFS vfs) throws VFSException {
		List rtn = new ArrayList();
		for(int i=0; i<children.size(); i++) {
			ConditionEditorPanel child = (ConditionEditorPanel)children.get(i);
			if(!child.isEmpty()) {
				rtn.add(child.getCondition());
			}
		}
		
		return rtn;
	}
	
	public void clear() {
		children.clear();
		relayout();
	}
	
	public void setConditions(List list) {
		invalidate();
		clear();
		
		for(int i=0; i<list.size(); i++) {
			ConditionEditorPanel child = new ConditionEditorPanel(this);
			child.init((Condition)list.get(i));
			
			children.add(child);
		}
		
		if(children.size() == 0) {
			ConditionEditorPanel child = new ConditionEditorPanel(this);
			children.add(child);
		}
		
		relayout();
		SwingUtilities.getWindowAncestor(this).pack();
		validate();
	}

	public JFD getJfd() {
		return jfd;
	}
}
