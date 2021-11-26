package com.nullfish.lib.ui;

import java.util.regex.Pattern;

import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.text.Position;

import org.monazilla.migemo.Migemo;

import com.nullfish.app.jfd2.util.MigemoInfo;

public class MigemoList extends JList {
    public int getNextMatch(String prefix, int startIndex, Position.Bias bias) {
    	if(!MigemoInfo.usesMigemo()) {
    		return super.getNextMatch(prefix, startIndex, bias);
    	}
    	
    	ListModel model = getModel();
    	int max = model.getSize();
    	if (prefix == null) {
    	    throw new IllegalArgumentException();
    	}
    	if (startIndex < 0 || startIndex >= max) {
    	    throw new IllegalArgumentException();
    	}
		Pattern pattern = Pattern.compile(Migemo.lookup(prefix));
		
    	// start search from the next element after the selected element
    	int increment = (bias == Position.Bias.Forward) ? 1 : -1;
    	int index = startIndex;
    	do {
    	    Object o = model.getElementAt(index);
    	    
    	    if (o != null) {
    		String string;
    		
    		if (o instanceof String) {
    		    string = ((String)o).toLowerCase();
    		}
    		else {
    		    string = o.toString();
    		    if (string != null) {
    			string = string.toLowerCase();
    		    }
    		}
    		
    		if (string != null && pattern.matcher(string.toLowerCase()).find()) {
    		    return index;
    		}
    	    }
    	    index = (index + increment + max) % max;
    	} while (index != startIndex);
    	return -1;
        }

}
