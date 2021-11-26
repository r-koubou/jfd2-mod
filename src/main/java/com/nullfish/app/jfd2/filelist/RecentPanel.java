package com.nullfish.app.jfd2.filelist;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.nullfish.app.jfd2.resource.JFDResource;
import com.nullfish.lib.ui.document.RegexRestriction;
import com.nullfish.lib.ui.document.RestrictedDocument;

public class RecentPanel extends JPanel {
	private JTextField numberText = new JTextField();
	private JComboBox unitCombo = new JComboBox();

	Pattern numberPattern = Pattern.compile("\\d*");
	Pattern alphabetPattern = Pattern.compile("[a-zA-Z]+");
	
	private static final Unit[] units = { new Unit("hour", "h"),
			new Unit("day", "d"), new Unit("week", "w"),
			new Unit("month", "m"), new Unit("year", "y") };

	public RecentPanel() {
		setLayout(new BorderLayout(3, 0));
		add(numberText, BorderLayout.CENTER);
		add(unitCombo, BorderLayout.EAST);

		Dimension d = unitCombo.getPreferredSize();
		d.width = 60;
		unitCombo.setPreferredSize(d);

		RestrictedDocument doc = new RestrictedDocument();
		doc.addRestriction(RegexRestriction
				.getInstance("[\\d]*[a-z]?"));
		numberText.setDocument(doc);

		unitCombo.setRenderer(new DefaultListCellRenderer() {
			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				return super.getListCellRendererComponent(list, ((Unit) value)
						.getLabel(), index, isSelected, cellHasFocus);
			}
		});

		for (int i = 0; i < units.length; i++) {
			unitCombo.addItem(units[i]);
		}
		
		numberText.setText("1");
	}
	
	public void setValue(String value) {
		Matcher textMatcher = numberPattern.matcher(value);
		numberText.setText(textMatcher.find() ? textMatcher.group() : "");

		Matcher alphabetMatcher = alphabetPattern.matcher(value);
		String unit = alphabetMatcher.find() ? alphabetMatcher.group() : "";
		unitCombo.setSelectedIndex(0);
		for(int i=0; i<units.length; i++) {
			if(units[i].unit.equals(unit)) {
				unitCombo.setSelectedItem(units[i]);
			}
		}
	}
	
	public String getValue() {
		return numberText.getText() + ((Unit)unitCombo.getSelectedItem()).unit;
	}

	private static class Unit {
		String label;
		String unit;

		Unit(String label, String unit) {
			this.label = label;
			this.unit = unit;
		}

		String getLabel() {
			return JFDResource.LABELS.getString(label);
		}

		public int hashCode() {
			return unit.hashCode();
		}

		public boolean equals(Object o) {
			if (o.getClass() != Unit.class) {
				return false;
			}

			return unit.equals(((Unit) o).unit);
		}
	}
}
