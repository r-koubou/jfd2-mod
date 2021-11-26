package com.nullfish.app.jfd2.command.embed.rename;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import com.nullfish.lib.vfs.VFile;

public class FileRenameTableCellRenderer extends DefaultTableCellRenderer {
	private static final Color COLOR_OK = Color.BLACK;

	private static final Color COLOR_EXISTS = Color.RED;

	private static final Color COLOR_SAME_DEST = Color.RED;

	private static final Color COLOR_UNKNOWN = Color.RED;

	private LumpSumRenamer renamer;

	public FileRenameTableCellRenderer(LumpSumRenamer renamer) {
		this.renamer = renamer;
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		if (value == null) {
			return super.getTableCellRendererComponent(table, value,
					isSelected, hasFocus, row, column);
		}

		VFile file = (VFile) value;
		Component rtn = super.getTableCellRendererComponent(table, file
				.getName(), isSelected, hasFocus, row, column);

		switch (renamer.getStatus(row)) {
		case LumpSumRenamer.STATUS_OK:
			setForeground(COLOR_OK);
			break;
		case LumpSumRenamer.STATUS_DEST_FILE_EXISTS:
			setForeground(COLOR_EXISTS);
			break;
		case LumpSumRenamer.STATUS_SAME_DEST_EXISTS:
			setForeground(COLOR_SAME_DEST);
			break;
		case LumpSumRenamer.STATUS_UNKNOWN:
		default:
			setForeground(COLOR_UNKNOWN);
			break;
		}

		return rtn;
	}
}
