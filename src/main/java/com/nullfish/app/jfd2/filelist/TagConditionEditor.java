package com.nullfish.app.jfd2.filelist;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.nullfish.lib.vfs.impl.filelist.condition.Condition;
import com.nullfish.lib.vfs.impl.filelist.condition.TagCondition;

public class TagConditionEditor implements ConditionEditor {
	public static final String NAME = "tag";
	public static final String LABEL = "condition_tag";
	public static final String TAG_NAME = "tag";

	private JPanel panel = new JPanel(new BorderLayout());

	private JTextField text = new JTextField();

	public TagConditionEditor() {
		panel.add(text, BorderLayout.CENTER);
	}

	public Condition getCondition() {
		return new TagCondition(text.getText());
	}

	public JComponent getEditorComponent() {
		return panel;
	}

	public String getName() {
		return NAME;
	}

	public String getTagName() {
		return TAG_NAME;
	}

	public void init(Condition condition) {
		TagCondition cond = (TagCondition) condition;
		text.setText(cond.getPattern());
	}

	public String getLabel() {
		return LABEL;
	}
	
	public boolean isEmpty() {
		return text.getText().length() == 0;
	}
}
