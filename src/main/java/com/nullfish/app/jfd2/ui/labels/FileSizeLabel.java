package com.nullfish.app.jfd2.ui.labels;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.text.DecimalFormat;

import javax.swing.JLabel;

public class FileSizeLabel extends JLabel {
	private long size;
	
	private static DecimalFormat format = new DecimalFormat("#,###,###,###,##0 ");
	
	public FileSizeLabel() {
		super();
		addComponentListener(new ComponentListener() {
			public void componentHidden(ComponentEvent e) {
			}

			public void componentMoved(ComponentEvent e) {
				updateText();
			}

			public void componentResized(ComponentEvent e) {
				updateText();
			}

			public void componentShown(ComponentEvent e) {
				updateText();
			}
		});
	}
	
	public void setSize(long size) {
		this.size = size;
		updateText();
	}
	
	public void updateText() {
		if(size == -1) {
			setText("N/A");
			return;
		}
		
		String normalStr = format.format(size);
		int normalWidth = getFontMetrics(getFont()).stringWidth(normalStr);
		setText(getParent().getBounds().width < normalWidth ? createShortString(size) : normalStr);
	}

	public String createShortString(long size) {
		long s = size;
		
		int count = 0;
		while(s >= 1000) {
			count ++;
			s /= 10;
		}
		
		String unit;
		switch(count) {
		case 0 :
			unit = "";
			break;
		case 1 :
		case 2 :
		case 3 :
			unit = "K";
			break;
		case 4 :
		case 5 :
		case 6 :
			unit = "M";
			break;
		case 7 :
		case 8 :
		case 9 :
			unit = "G";
			break;
		case 10 :
		case 11 :
		case 12 :
			unit = "T";
			break;
		default :
			unit = "P";
		}
		
		int dotPos = count % 3;
		
		StringBuffer sb= new StringBuffer();
		sb.append(s);
		sb.append(unit);
		if(dotPos != 0) {
			sb.insert(dotPos, ".");
		}
		
		return sb.toString();
	}
}
