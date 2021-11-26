package com.nullfish.app.jfd2.filelist;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.nullfish.app.jfd2.resource.JFDResource;
import com.nullfish.app.jfd2.util.ResourceBundleComboBox;
import com.nullfish.lib.ui.document.RegexRestriction;
import com.nullfish.lib.ui.document.RestrictedDocument;
import com.nullfish.lib.vfs.impl.filelist.condition.Condition;
import com.nullfish.lib.vfs.impl.filelist.condition.LengthCondition;

public class LengthConditionEditor implements ConditionEditor {
	public static final String NAME = "length";
	public static final String LABEL = "condition_length";
	public static final String TAG_NAME = "length";

	private JPanel panel = new JPanel(new BorderLayout());

	private JComboBox operationComboBox = new ResourceBundleComboBox(JFDResource.LABELS);
	
	private JTextField text = new JTextField();

	public LengthConditionEditor() {
		Dimension d = operationComboBox.getPreferredSize();
		d.width = 80;
		operationComboBox.setPreferredSize(d);

		panel.add(text, BorderLayout.CENTER);
		panel.add(operationComboBox, BorderLayout.WEST);
		
		operationComboBox.addItem("above");
		operationComboBox.addItem("below");
		
		RestrictedDocument doc = new RestrictedDocument();
		doc.addRestriction(RegexRestriction.getInstance("[0-9]*"));
		text.setDocument(doc);
	}

	public Condition getCondition() {
		return new LengthCondition(Long.parseLong(text.getText()), "above"
				.equals(operationComboBox.getSelectedItem()));
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
		LengthCondition cond = (LengthCondition) condition;
		text.setText(Long.toString(cond.getLength()));

		operationComboBox.setSelectedItem(cond.isAbove() ? "above" : "below");
	}

	public String getLabel() {
		return LABEL;
	}
	
	public boolean isEmpty() {
		return text.getText().length() == 0;
	}
}
