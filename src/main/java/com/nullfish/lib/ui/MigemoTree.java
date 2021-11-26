package com.nullfish.lib.ui;

import java.util.regex.Pattern;

import javax.swing.JTree;
import javax.swing.text.Position;
import javax.swing.tree.TreePath;

import org.monazilla.migemo.Migemo;

import com.nullfish.app.jfd2.util.MigemoInfo;

public class MigemoTree extends JTree {
	public TreePath getNextMatch(String prefix, int startingRow,
			Position.Bias bias) {
		if(!MigemoInfo.usesMigemo()) {
			return super.getNextMatch(prefix, startingRow, bias);
		}
		
		int max = getRowCount();
		if (prefix == null) {
			throw new IllegalArgumentException();
		}
		if (startingRow < 0 || startingRow >= max) {
			throw new IllegalArgumentException();
		}

		// start search from the next/previous element froom the
		// selected element
		int increment = (bias == Position.Bias.Forward) ? 1 : -1;
		int row = startingRow;

		Pattern pattern = Pattern.compile(Migemo.lookup(prefix));
		
		do {
			TreePath path = getPathForRow(row);
			String text = convertValueToText(path.getLastPathComponent(),
					isRowSelected(row), isExpanded(row), true, row, false);

			if (pattern.matcher(text.toLowerCase()).find()) {
				return path;
			}
			row = (row + increment + max) % max;
		} while (row != startingRow);
		return null;
	}

}
