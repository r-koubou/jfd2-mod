package com.nullfish.app.jfd2.util;

import java.util.List;

import com.nullfish.lib.vfs.VFile;

public class TagUtil {
	private static StringBuffer tagBuffer = new StringBuffer();
	
	public static String file2TagString(VFile file) {
		List tags = file.getTag();
		if(tags == null || tags.size() == 0) {
			return "";
		}
		tagBuffer.setLength(0);
		tagBuffer.append(" ");
		for(int i=0; i<tags.size(); i++) {
			tagBuffer.append("[");
			tagBuffer.append(tags.get(i));
			tagBuffer.append("]");
		}
		
		return tagBuffer.toString();
		
	}

}
