package com.nullfish.app.jfd2.filelist;

import javax.swing.JComponent;

import com.nullfish.lib.vfs.impl.filelist.condition.Condition;

public interface ConditionEditor {
	public String getName();
	
	public String getLabel();
	
	public void init(Condition condition);
	
	public Condition getCondition();
	
	public JComponent getEditorComponent();
	
	public String getTagName();
	
	public boolean isEmpty();
}
