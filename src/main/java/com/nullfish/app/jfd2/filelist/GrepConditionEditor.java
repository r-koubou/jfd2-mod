package com.nullfish.app.jfd2.filelist;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.nullfish.app.jfd2.resource.JFDResource;
import com.nullfish.lib.vfs.impl.filelist.condition.Condition;
import com.nullfish.lib.vfs.impl.filelist.condition.GrepCondition;

public class GrepConditionEditor implements ConditionEditor {
	public static final String NAME = "grep";
	public static final String LABEL = "condition_grep";
	public static final String TAG_NAME = "grep";

	private JPanel panel = new JPanel(new GridBagLayout());

	private JTextField text = new JTextField();
	private JCheckBox caseSensitiveCheck = new JCheckBox(JFDResource.LABELS
			.getString("case_sensitive"));
	private JComboBox encodeCombo = new JComboBox();

	public GrepConditionEditor(ConditionEditorPanel owner) {
		List encodeList = (List)owner.getOwner().getJfd().getCommonConfiguration().getParam("grep_encode_all", null);
		for(int i=0; i<encodeList.size(); i++) {
			encodeCombo.addItem(encodeList.get(i));
		}

		panel.add(text, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 3, 0, 3), 0, 0));
		panel.add(caseSensitiveCheck, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 3, 0, 3), 0, 0));
		panel.add(encodeCombo, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 3, 0, 3), 0, 0));
	}

	public Condition getCondition() {
		return new GrepCondition(text.getText(), (String) encodeCombo
				.getSelectedItem(), caseSensitiveCheck.isSelected());
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
		GrepCondition cond = (GrepCondition) condition;
		text.setText(cond.getPattern());

		caseSensitiveCheck.setSelected(cond.isCaseSensitive());
		
		encodeCombo.setSelectedItem(cond.getEncode());
	}

	public String getLabel() {
		return LABEL;
	}
	
	public boolean isEmpty() {
		return text.getText().length() == 0;
	}
}
