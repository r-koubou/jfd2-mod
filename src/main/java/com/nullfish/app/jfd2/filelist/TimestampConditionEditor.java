package com.nullfish.app.jfd2.filelist;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.nullfish.app.jfd2.resource.JFDResource;
import com.nullfish.app.jfd2.util.ResourceBundleComboBox;
import com.nullfish.lib.ui.document.RestrictedDocument;
import com.nullfish.lib.ui.document.YyyymmddDateRestriction;
import com.nullfish.lib.vfs.impl.filelist.condition.Condition;
import com.nullfish.lib.vfs.impl.filelist.condition.TimestampCondition;

public class TimestampConditionEditor implements ConditionEditor {
	public static final String NAME = "timestamp";
	public static final String LABEL = "condition_timestamp";
	public static final String TAG_NAME = "timestamp";

	private JPanel panel = new JPanel(new BorderLayout(3, 0));

	private JComboBox operationComboBox = new ResourceBundleComboBox(JFDResource.LABELS);
	
	private RecentPanel recentPanel = new RecentPanel();

	private CardLayout cardLayout = new CardLayout();
	private JPanel cardPanel = new JPanel(cardLayout);
	
	private JTextField text = new JTextField();

	public TimestampConditionEditor() {
		Dimension d = operationComboBox.getPreferredSize();
		d.width = 80;
		operationComboBox.setPreferredSize(d);

		cardPanel.add(text, "absolute");
		cardPanel.add(recentPanel, "recent");
		
		panel.add(cardPanel, BorderLayout.CENTER);
		panel.add(operationComboBox, BorderLayout.WEST);
		
		operationComboBox.addItem("before");
		operationComboBox.addItem("after");
		operationComboBox.addItem("recent");
		
		RestrictedDocument doc = new RestrictedDocument();
		doc.addRestriction(new YyyymmddDateRestriction());
		
		text.setDocument(doc);
		
		text.setText(new SimpleDateFormat("yyyyMMdd").format(new Date(System.currentTimeMillis())));
		
		operationComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cardLayout.show(cardPanel, "recent".equals(operationComboBox.getSelectedItem()) ? "recent" : "absolute");
			}
		});
	}

	public Condition getCondition() {
		String operation = (String)operationComboBox.getSelectedItem();
		
		if("recent".equals(operation)) {
			return new TimestampCondition(operation, recentPanel.getValue());
		} else {
			SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMddHHmm");
			Date date;
			try {
				date = format1.parse(text.getText());
			} catch (ParseException e) {
				date = new Date();
			}
			return new TimestampCondition(operation, format2.format(date));
		}
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
		TimestampCondition cond = (TimestampCondition) condition;
		operationComboBox.setSelectedItem(cond.getOperation());
		if("recent".equals(cond.getOperation())) {
			recentPanel.setValue(cond.getTime());
		} else {
			String value = cond.getTime();
			if(value.length() > 8) {
				value = value.substring(0, 8);
			}
			text.setText(value);
		}
	}

	public String getLabel() {
		return LABEL;
	}
	
	public boolean isEmpty() {
		return text.getText().length() == 0;
	}
}
