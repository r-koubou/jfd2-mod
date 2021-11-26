package com.nullfish.app.jfd2.command.embed.rename;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

import com.nullfish.lib.keymap.KeyStrokeMap;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;

public class VFileRenameCellEditor extends AbstractCellEditor implements
		TableCellEditor {
	private JTextField textField = new JTextField();
	private VFile directory;

	public VFileRenameCellEditor() {
		textField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireEditingStopped();
			}
		});
		
		textField.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancel");
		textField.getActionMap().put("cancel", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				fireEditingCanceled();
			}
		});
	}

	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		VFile file = (VFile)value;
		directory = file.getParent();
		
		textField.setText(file.getName());
		return textField;
	}

	public Object getCellEditorValue() {
		try {
			VFile rtn = directory.getChild(textField.getText());
			return rtn;
		} catch (VFSException e) {
			return directory;
		}
	}
}
