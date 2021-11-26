package com.nullfish.app.jfd2.filelist;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;

import com.nullfish.app.jfd2.resource.JFDResource;
import com.nullfish.lib.vfs.impl.filelist.condition.Condition;

public class ConditionEditorPanel extends JPanel {
	private ConditionEditorsPanel owner;
	
	private JComboBox combo = new JComboBox();
	
	private JButton addButton = new JButton("+");
	private JButton deleteButton = new JButton("-");
	
	private Map nameEditorMap = new HashMap();
	
	private JPanel editorsPanel = new JPanel();
	
	private CardLayout editorLayout = new CardLayout();
	
	public ConditionEditorPanel(ConditionEditorsPanel owner) {
		this.owner = owner;
		initGui();
	}
	
	private void initGui() {
		setLayout(new GridBagLayout());
		Dimension d = combo.getPreferredSize();
		d.width = 100;
		combo.setPreferredSize(d);
		
		editorsPanel.setLayout(editorLayout);

		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				owner.removeChild(ConditionEditorPanel.this);
			}
		});
		
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				owner.addNew();
			}
		});
		
		combo.setRenderer(new DefaultListCellRenderer() {
			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				ConditionEditor editor = (ConditionEditor)value;
				return super.getListCellRendererComponent(list,
						JFDResource.LABELS.getString(editor.getLabel()), index,
						isSelected, cellHasFocus);
			}
		});
		
		combo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ConditionEditor editor = (ConditionEditor)combo.getSelectedItem();
				editorLayout.show(editorsPanel, editor.getName());
			}
		});
		
		ConditionEditor[] editors = {
				new NameConditionEditor(),
				new LengthConditionEditor(),
				new TimestampConditionEditor(),
				new GrepConditionEditor(this),
				new TagConditionEditor()
		};
			
		for(int i=0; i<editors.length; i++) {
			ConditionEditor editor = editors[i];
			nameEditorMap.put(editor.getName(), editor);

			editorsPanel.add(editor.getEditorComponent(), editor.getName());
			combo.addItem(editor);
		}
		
		combo.setSelectedIndex(0);
		
		JPanel buttonsPanel = new JPanel(new GridLayout(1,2));
		buttonsPanel.add(deleteButton);
		buttonsPanel.add(addButton);
		
		add(combo, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
		add(editorsPanel, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(3, 3, 3, 3), 0, 0));
		add(buttonsPanel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
	}
	
	public void init(Condition condition) {
		ConditionEditor editor = (ConditionEditor)nameEditorMap.get(condition.getName());
		editor.init(condition);
		combo.setSelectedItem(editor);
	}
	
	public Condition getCondition() {
		return ((ConditionEditor)combo.getSelectedItem()).getCondition();
	}
	
	public void parentUpdated() {
		deleteButton.setEnabled(owner.getCount() > 1);
	}
	
	public ConditionEditorsPanel getOwner() {
		return owner;
	}
	
	public boolean isEmpty() {
		return ((ConditionEditor)combo.getSelectedItem()).isEmpty();
	}
}
