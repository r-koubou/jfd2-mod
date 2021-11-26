package com.nullfish.app.jfd2.util;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;

public class PropertiesCache {
	private Map filePropCache = new HashMap();
	
	private Map fileTimestampMap = new HashMap();
	
	private static PropertiesCache instance = new PropertiesCache();
	
	public static PropertiesCache getInstance() {
		return instance;
	}
	
	public Properties getDocument(VFile file) throws VFSException, IOException {
		Date timestamp = (Date)fileTimestampMap.get(file);
		if(timestamp != null && !timestamp.equals(file.getTimestamp())) {
			filePropCache.remove(file);
		}
		
		Properties rtn = (Properties) filePropCache.get(file);
		if(rtn == null) {
			rtn = new Properties();
			rtn.load(file.getInputStream());
			filePropCache.put(file, rtn);
			fileTimestampMap.put(file, file.getTimestamp());
		}
		
		return rtn;
	}
}
