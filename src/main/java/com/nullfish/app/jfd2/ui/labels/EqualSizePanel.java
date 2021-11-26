package com.nullfish.app.jfd2.ui.labels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import javax.swing.JComponent;
import javax.swing.JPanel;

public class EqualSizePanel extends JPanel {
	private Object key;
	
	private static Map keySetMap = new WeakHashMap();
	
	public EqualSizePanel(Object key, JComponent comp) {
		setLayout(new BorderLayout());
		add(comp, BorderLayout.WEST);
		//add(comp);
		setOpaque(false);
		
		this.key = key;
		Set set = (Set)keySetMap.get(key);
		if(set == null) {
			set = new HashSet();
			keySetMap.put(key, set);
		}
		
		set.add(this);
	}
	
	public Dimension getPreferredSize() {
		if(keySetMap.get(key) == null) {
			return new Dimension(0, 0);
		}
		Iterator iterator = ((Set)keySetMap.get(key)).iterator();
		
		Dimension maximumSize = new Dimension();
		
		while(iterator.hasNext()) {
			EqualSizePanel panel = (EqualSizePanel)iterator.next();
			Dimension d = panel.getSuperPreferredSize();
			
			maximumSize.width = maximumSize.width > d.width ? maximumSize.width : d.width;
			maximumSize.height = maximumSize.height > d.height ? maximumSize.width : d.height;
		}
		
		return maximumSize;
	}
	
	private Dimension getSuperPreferredSize() {
		return super.getPreferredSize();
	}
	
	public Dimension getMinimumSize() {
		if(keySetMap.get(key) == null) {
			return new Dimension(0,0);
		}
		Iterator iterator = ((Set)keySetMap.get(key)).iterator();
		
		Dimension maximumSize = new Dimension();
		
		while(iterator.hasNext()) {
			EqualSizePanel panel = (EqualSizePanel)iterator.next();
			Dimension d = panel.getSuperMinimumSize();
			
			maximumSize.width = maximumSize.width > d.width ? maximumSize.width : d.width;
			maximumSize.height = maximumSize.height > d.height ? maximumSize.width : d.height;
		}
		return maximumSize;
	}
	
	private Dimension getSuperMinimumSize() {
		return super.getMinimumSize();
	}
	
	public Dimension getSize() {
		Iterator iterator = ((Set)keySetMap.get(key)).iterator();
		
		Dimension maximumSize = new Dimension();
		
		while(iterator.hasNext()) {
			EqualSizePanel panel = (EqualSizePanel)iterator.next();
			Dimension d = panel.getSuperSize();
			
			maximumSize.width = maximumSize.width > d.width ? maximumSize.width : d.width;
			maximumSize.height = maximumSize.height > d.height ? maximumSize.width : d.height;
		}
		
		return maximumSize;
	}
	
	private Dimension getSuperSize() {
		return super.getMinimumSize();
	}
	
	public void dispose() {
		keySetMap.clear();
	}
}
