/*
 * Created on 2004/07/07
 *
 */
package com.nullfish.app.jfd2.config;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.nullfish.app.jfd2.util.DomCache;
import com.nullfish.lib.meta_data.MetaDataManager;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;

/**
 * @author shunji
 * 
 * Preferences - Java - Code Style - Code Templates
 */
public class Configuration {
	/**
	 * 設定
	 */
	private Map params = new LinkedHashMap();

	/**
	 * 設定のバージョン
	 */
	private double configVersion = 0;
	
	private MetaDataManager dataManager = MetaDataManager.getInstance();

	private boolean updatedAfterSave = false;
	
	/**
	 * 設定ファイルとインスタンスのマップ
	 */
	private static Map fileInstanceMap = new HashMap();
	
	/**
	 * 設定ファイルのルートノード
	 */
	public static final String NODE_ROOT = "config";

	/**
	 * 設定ファイルのバージョン属性
	 */
	public static final String ATTR_VERSION = "version";

	/**
	 * コンストラクタ
	 *  
	 */
	public Configuration() {
	}

	/**
	 * 設定を読み込む。
	 * @param file
	 * @throws JDOMException
	 * @throws IOException
	 * @throws VFSException 
	 */
	public void load(VFile file) throws JDOMException, IOException, VFSException {
		Document doc = DomCache.getInstance().getDocument(file);

		Element root = doc.getRootElement();
		
		String versionStr = root.getAttributeValue(ATTR_VERSION);
		if (versionStr != null && versionStr.length() > 0) {
			try {
				configVersion = Double.parseDouble(versionStr);
			} catch (NumberFormatException e) {}
		} else {
			configVersion = 0;
		}
		
		List nodes = root.getChildren(MetaDataManager.TAG_NAME);
		for (int i = 0; i < nodes.size(); i++) {
			Element element = (Element) nodes.get(i);
			String paramName = element
					.getAttributeValue(MetaDataManager.ATTR_NAME);
			Object paramValue = dataManager.paramNode2Object(element);
			params.put(paramName, paramValue);
		}
	}

	/**
	 * 設定を保存する。
	 * ストリームは保存後閉じられる。
	 * @param os
	 * @throws IOException
	 */
	public void save(OutputStream os) throws IOException {
		try {
			Element root = new Element(NODE_ROOT);
			root.setAttribute(ATTR_VERSION, Double.toString(configVersion));

			Iterator ite = params.keySet().iterator();
			while (ite.hasNext()) {
				String key = (String) ite.next();
				Element param = dataManager.object2ParamNode(params.get(key), key);
				if(param != null)
				root.addContent(param);
			}
			
			Document doc = new Document();
			doc.setRootElement(root);

			XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
			outputter.output(doc, os);

			updatedAfterSave = false;
		} finally {
			try {
				os.flush();
			} catch (Exception e) {}
			try {
				os.close();
			} catch (Exception e) {}
		}
	}

	/**
	 * 共通設定を取得する。
	 * @param key
	 * @return
	 */
	public Object getParam(String key, Object defaultValue) {
		Object rtn = params.get(key);
		if(rtn == null) {
			params.put(key, defaultValue);
			return defaultValue;
		}
		
		return rtn;
	}
	
	/**
	 * 共通設定を設定する。
	 * @param key
	 * @param value
	 */
	public void setParam(String key, Object value) {
		params.put(key, value);
		updatedAfterSave = true;
	}
	
	/**
	 * 設定ファイルに対応したインスタンスを取得する。
	 * @param file
	 * @return
	 * @throws JDOMException
	 * @throws IOException
	 * @throws VFSException
	 */
	public static Configuration getInstance(VFile file) throws JDOMException, IOException, VFSException {
		Configuration rtn = (Configuration) fileInstanceMap.get(file);
		if (rtn == null) {
			rtn = new Configuration();
			rtn.load(file);
			fileInstanceMap.put(file, rtn);
		}
		
		return rtn;
	}

	public double getConfigVersion() {
		return configVersion;
	}

	public void setConfigVersion(double configVersion) {
		this.configVersion = configVersion;
	}
	
	public boolean isUpdatedAfterSave() {
		return updatedAfterSave;
	}
}
