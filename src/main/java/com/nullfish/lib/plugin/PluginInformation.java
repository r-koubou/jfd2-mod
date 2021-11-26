/*
 * Created on 2005/01/27
 *
 */
package com.nullfish.lib.plugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import com.nullfish.app.jfd2.util.DomCache;
import com.nullfish.lib.meta_data.MetaDataManager;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;
import com.nullfish.lib.vfs.exception.VFSIOException;

/**
 * @author shunji
 *
 */
public class PluginInformation {
	private String name;
	
	private double version = 0;
	
	private String className;
	
	private String description;
	
	private Map paramMap = new HashMap();
	
	private List classPathList = new ArrayList();
	
	/**
	 * 定義ファイル名
	 */
	public static final String DEFINITION = "plugin.xml";
	
	/**
	 * ルートノード名
	 */
	public static final String NODE_NAME = "plugin";
	
	/**
	 * 名称属性
	 */
	public static final String ATTR_NAME = "name";
	
	/**
	 * バージョン属性
	 */
	public static final String ATTR_VERSION = "version";
	
	/**
	 * クラス属性
	 */
	public static final String ATTR_CLASS = "class";
	
	/**
	 * 説明ノード
	 */
	public static final String NODE_DESCRIPTION = "description";
	
	/**
	 * クラスパス属性
	 */
	public static final String ATTR_CLASSPATH = "classpath";
	
	public PluginInformation(VFile file) throws VFSException {
		
		Document doc = null;
		try {
			doc = DomCache.getInstance().getDocument(file);
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			throw new VFSIOException(e);
		}
		
		Element root = doc.getRootElement();
		name = root.getAttributeValue(ATTR_NAME);
		String versionStr = root.getAttributeValue(ATTR_VERSION);
		try {
			version = Double.parseDouble(versionStr);
		} catch (Exception e) {}
		
		className = root.getAttributeValue(ATTR_CLASS);
		
		Element descNode = root.getChild(NODE_DESCRIPTION);
		if(descNode != null) {
			description = descNode.getText();
		}
		
		List paramsNodeList = root.getChildren(MetaDataManager.TAG_NAME);
		for(int i=0; i<paramsNodeList.size(); i++) {
			Element paramNode = (Element)paramsNodeList.get(i);
			String name = paramNode.getAttributeValue(ATTR_NAME);
			Object value = MetaDataManager.getInstance().paramNode2Object(paramNode);
			paramMap.put(name, value);
		}
		
		String classPathAttr = root.getAttributeValue(ATTR_CLASSPATH);
		if(classPathAttr != null && classPathAttr.length() > 0) {
			StringTokenizer tokenizer = new StringTokenizer(classPathAttr, ",");
			while(tokenizer.hasMoreTokens()) {
				classPathList.add( tokenizer.nextToken() );
			}
		}
	}
	
	public String getClassName() {
		return className;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getName() {
		return name;
	}
	
	public Object getParameter(Object key) {
		return paramMap.get(key);
	}

	public List getClassPathList() {
		return classPathList;
	}
	
	public double getVersion() {
		return version;
	}
}
