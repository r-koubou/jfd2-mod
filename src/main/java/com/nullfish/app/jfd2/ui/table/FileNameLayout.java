package com.nullfish.app.jfd2.ui.table;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;

public class FileNameLayout implements LayoutManager2 {
	public static final String FILENAME = "filename";

	public static final String EXTENSION = "extension";

	public static final String DOT = "dot";

	private Component fileNameComp;
	
	private Component extensionComp;

	private Component dotComp;
	
	public void addLayoutComponent(Component comp, Object constraints) {
		if(FILENAME.equals(constraints)) {
			fileNameComp = comp;
		} else if(EXTENSION.equals(constraints)){
			extensionComp = comp;
		} else {
			dotComp = comp;
		}
	}

	public Dimension maximumLayoutSize(Container target) {
		return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	public float getLayoutAlignmentX(Container target) {
		return 0;
	}

	public float getLayoutAlignmentY(Container target) {
		return 0;
	}

	public void invalidateLayout(Container target) {
	}

	public void addLayoutComponent(String name, Component comp) {
		addLayoutComponent(comp, name);
	}

	public void removeLayoutComponent(Component comp) {
		if (comp == fileNameComp) {
			fileNameComp = null;
			return;
		}

		if (comp == extensionComp) {
			extensionComp = null;
			return;
		}

		if (comp == dotComp) {
			dotComp = null;
		}
	}

	public Dimension preferredLayoutSize(Container parent) {
		Dimension name = fileNameComp.isVisible() ? fileNameComp
				.getPreferredSize() : new Dimension(0, 0);
		Dimension ext = extensionComp.isVisible() ? extensionComp
				.getPreferredSize() : new Dimension(0, 0);
		Dimension dot = dotComp.isVisible() ? dotComp
				.getPreferredSize() : new Dimension(0, 0);

		return new Dimension(
				name.width + ext.width + dot.width, 
				name.height > ext.height ? name.height : ext.height);
	}

	public Dimension minimumLayoutSize(Container parent) {
		if(extensionComp == null) {
			return new Dimension(0, 0);
		}
		
		Dimension ext = extensionComp.isVisible() ? extensionComp
				.getPreferredSize() : new Dimension(0, 0);
		Dimension dot = dotComp.isVisible() ? dotComp
				.getPreferredSize() : new Dimension(0, 0);

		return new Dimension(
				ext.width + dot.width, 
				dot.height);
	}

	public void layoutContainer(Container parent) {
		Dimension size = parent.getSize();
		
		Dimension extSize = extensionComp.isVisible() ?  extensionComp.getPreferredSize() : new Dimension();		
		Dimension dotSize = dotComp.isVisible() ? dotComp.getPreferredSize(): new Dimension();
		Dimension nameSize = fileNameComp.isVisible() ? fileNameComp.getPreferredSize() : new Dimension();

		int maxHeight = extSize.height > dotSize.height ? extSize.height : dotSize.height;
		maxHeight = maxHeight > nameSize.height ? maxHeight : nameSize.height;
		int y = (size.height - maxHeight) / 2; 
		
		int possibleNameLabelWidth = size.width - extSize.width - dotSize.width;
		int realNameLabelWidth = nameSize.width < possibleNameLabelWidth ? nameSize.width : possibleNameLabelWidth; 
		fileNameComp.setBounds(0, y, realNameLabelWidth, nameSize.height);
		dotComp.setBounds(realNameLabelWidth, y, dotSize.width, dotSize.height);
		extensionComp.setBounds(realNameLabelWidth + dotSize.width, y, extSize.width, extSize.height);
	}
}
