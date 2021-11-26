package com.nullfish.app.jfd2.ui.shortcut_tree;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;

public class VFileTreeCellRenderer extends DefaultTreeCellRenderer {
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		try {
			if (!(value instanceof VFile)) {
				return super.getTreeCellRendererComponent(tree, value,
						selected, expanded, leaf, row, hasFocus);
			}

			VFile file = (VFile) value;
			if (file.isDirectory()) {
				return super.getTreeCellRendererComponent(tree, file.getName(),
						selected, expanded, leaf, row, hasFocus);
			}

			String fileName = file.getName();
			if (fileName.endsWith(".jfdlnk")) {
				return super.getTreeCellRendererComponent(tree, fileName
						.substring(0, fileName.length() - 7), selected,
						expanded, leaf, row, hasFocus);
			}

			return super.getTreeCellRendererComponent(tree, fileName, selected,
					expanded, leaf, row, hasFocus);
		} catch (VFSException e) {
			return super.getTreeCellRendererComponent(tree, value, selected,
					expanded, leaf, row, hasFocus);
		}
	}
}
