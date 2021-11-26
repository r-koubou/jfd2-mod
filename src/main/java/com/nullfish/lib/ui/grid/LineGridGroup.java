/*
 * Created on 2004/05/08
 *
 */
package com.nullfish.lib.ui.grid;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

/**
 * @author shunji
 *
 */
public class LineGridGroup {
	private List gridList = new ArrayList(); 
	
	public void add(LineGrid grid) {
		gridList.add(this);
	}
	
	public void setDoubleLine(boolean bool) {
		for(int i=0; i<gridList.size(); i++) {
			LineGrid lineGrid = (LineGrid)gridList.get(i);
			lineGrid.setDoubleLine(bool);
		}
	}

	public void setFont(Font font) {
		for(int i=0; i<gridList.size(); i++) {
			LineGrid lineGrid = (LineGrid)gridList.get(i);
			lineGrid.setFont(font);
		}
	}
}
