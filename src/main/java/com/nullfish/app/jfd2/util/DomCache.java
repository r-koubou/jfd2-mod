package com.nullfish.app.jfd2.util;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;

public class DomCache {
	private Map fileDomCache = new HashMap();
	
	private Map fileTimestampMap = new HashMap();
	
	private static DomCache instance = new DomCache();
	
	public static DomCache getInstance() {
		return instance;
	}
	
	public Document getDocument(VFile file) throws JDOMException, IOException, VFSException {
		Date timestamp = (Date)fileTimestampMap.get(file);
		if(timestamp != null && !timestamp.equals(file.getTimestamp())) {
			fileDomCache.remove(file);
		}
		
		Document rtn = (Document) fileDomCache.get(file);
		if(rtn == null) {
			rtn = new SAXBuilder().build(file.getInputStream());
			fileDomCache.put(file, rtn);
			fileTimestampMap.put(file, file.getTimestamp());
		}
		
		return rtn;
	}
}
