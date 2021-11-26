package com.nullfish.app.jfd2.filelist;

import java.awt.BorderLayout;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.nullfish.app.jfd2.resource.JFDResource;
import com.nullfish.lib.vfs.impl.filelist.condition.Condition;
import com.nullfish.lib.vfs.impl.filelist.condition.NameCondition;

public class NameConditionEditor implements ConditionEditor {
	public static final String NAME = "name";
	public static final String LABEL = "condition_name";
	public static final String TAG_NAME = "name";

	private JPanel panel = new JPanel(new BorderLayout());

	private JTextField text = new JTextField();
	private JCheckBox caseSensitiveCheck = new JCheckBox(JFDResource.LABELS
			.getString("case_sensitive"));

	public NameConditionEditor() {
		panel.add(text, BorderLayout.CENTER);
		panel.add(caseSensitiveCheck, BorderLayout.EAST);
	}

	public Condition getCondition() {
		return new NameCondition(text.getText(), caseSensitiveCheck
				.isSelected());
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
		NameCondition cond = (NameCondition) condition;
		text.setText(cond.getPattern());

		caseSensitiveCheck.setSelected(cond.isCaseSensitive());
	}

	public String getLabel() {
		return LABEL;
	}
	
	public boolean isEmpty() {
		return text.getText().length() == 0;
	}
}
