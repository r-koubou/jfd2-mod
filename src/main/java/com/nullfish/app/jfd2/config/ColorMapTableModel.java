package com.nullfish.app.jfd2.config;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.table.AbstractTableModel;

public class ColorMapTableModel extends AbstractTableModel {
	private List extensionColorList = new ArrayList();
	
	public int getColumnCount() {
		return 2;
	}

	public int getRowCount() {
		return extensionColorList.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		switch(columnIndex) {
		case 0: return ((ExtensionColor) extensionColorList.get(rowIndex)).getExtension();
		case 1: return ((ExtensionColor) extensionColorList.get(rowIndex)).getColor();
		}
		return null;
	}
	
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex == 1;
	}
	
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		((ExtensionColor) extensionColorList.get(rowIndex)).setColor((Color)aValue);
	}
	
	public void add(String extension, Color color) {
		extensionColorList.add(new ExtensionColor(extension, color));
		sort();
	}
	
	public void remove(int rowIndex) {
		extensionColorList.remove(rowIndex);
		fireTableRowsDeleted(rowIndex, rowIndex);
	}
	
	private void sort() {
		Collections.sort(extensionColorList, new Comparator() {
			
			public int compare(Object arg0, Object arg1) {
				return ((ExtensionColor)arg0).getExtension().compareTo(((ExtensionColor)arg1).getExtension());
			}
		});
		fireTableDataChanged();
		
	}
	
	public void setColorMap(Map map) {
		extensionColorList.clear();
		Iterator entries = map.entrySet().iterator();
		while(entries.hasNext()) {
			Entry entry = (Entry)entries.next();
			extensionColorList.add(new ExtensionColor((String)entry.getKey(), (Color)entry.getValue()));
		}
		sort();
	}
	
	public Map getColorMap() {
		Map rtn = new HashMap();
		for(int i=0; i<extensionColorList.size(); i++) {
			ExtensionColor ec = (ExtensionColor)extensionColorList.get(i);
			rtn.put(ec.getExtension(), ec.getColor());
		}
		
		return rtn;
	}

	private class ExtensionColor {
		String extension;
		Color color;
		
		public ExtensionColor() {
		}
		
		public ExtensionColor(String extension, Color color) {
			this.extension = extension;
			this.color = color;
		}
		
		public String getExtension() {
			return extension;
		}
		public void setExtension(String extension) {
			this.extension = extension;
		}
		public Color getColor() {
			return color;
		}
		public void setColor(Color color) {
			this.color = color;
		}
	}
}
