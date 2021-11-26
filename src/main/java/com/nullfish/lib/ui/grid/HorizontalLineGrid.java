/*
 * Created on 2004/05/08
 *
 */
package com.nullfish.lib.ui.grid;

import java.awt.Graphics;

/**
 * @author shunji
 *
 */
public class HorizontalLineGrid extends LineGrid {
	public HorizontalLineGrid() {
		
	}
	
	public HorizontalLineGrid(Object groupKey) {
		super(groupKey);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		int center = getHeight() / 2;
		if(doubleLine) {
			g.drawLine(0, center - 2, getWidth(), center - 2);
			g.drawLine(0, center + 2, getWidth(), center + 2);
		} else {
			g.drawLine(0, center, getWidth(), center);
		}
	}
}
