/*
 * Created on 2004/05/27
 *
 */
package com.nullfish.lib.meta_data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Element;

/**
 * @author shunji
 * 
 */
public class MetaDataManager {
	/**
	 * シングルトンインスタンス
	 */
	private static MetaDataManager instance = new MetaDataManager(); 
	
	/**
	 * 登録済みデータ型
	 */
	private static DataType[] dataTypes;
	
	Map nameDataTypeMap = new HashMap();

	/**
	 * タグ名称
	 */
	public static final String TAG_NAME = "param";
	
	/**
	 * 名称属性
	 */
	public static final String ATTR_NAME = "name";

	/**
	 * コンストラクタ
	 *
	 */
	private MetaDataManager() {
		DataType[] d = {
				new ListData(),
				new IntegerData(),
				new ColorData(),
				new FontData(),
				new BooleanData(),
				new RendererModeData(),
				new DimensionData(),
				new StringHistoryData(),
				new TextData(),
				new MapData(),
		};

		dataTypes = d;
		
		for(int i=0; i<dataTypes.length; i++) {
			nameDataTypeMap.put(dataTypes[i].getName(), dataTypes[i]);
		}
	}
	
	/**
	 * シングルトンインスタンスを取得する。
	 * @return
	 */
	public static MetaDataManager getInstance() {
		return instance;
	}
	
	/**
	 * paramタグノードからオブジェクトに変換する。
	 * @param node
	 * @return
	 */
	public Object paramNode2Object(Element node) {
		List children = node.getChildren();
		if(children == null || children.size() == 0) {
			return null;
		}
		
		Element child = (Element)children.get(0);
		
		return node2Object(child);
	}
	
	/**
	 * ノードからオブジェクトに変換する。
	 * @param node
	 * @return
	 */
	public Object node2Object(Element node) {
		DataType dataType = (DataType)nameDataTypeMap.get(node.getName());
		if(dataType == null) {
			return null;
		}
				
		return dataType.node2Object(node);
	}
	
	/**
	 * オブジェクトをXMLノード（paramタグ）に変換する。
	 * @param o
	 * @param name
	 * @return
	 */
	public Element object2ParamNode(Object o, String name) {
		if(o == null || name == null) {
			return null;
		}
		Element e = new Element(TAG_NAME);
		e.setAttribute(ATTR_NAME, name);
		e.addContent(object2Node(o));
		
		return e;
	}
	
	/**
	 * オブジェクトをXMLノード（paramタグ）に変換する。
	 * @param o
	 * @param name
	 * @return
	 */
	public Element object2Node(Object o) {
		for(int i=0; i<dataTypes.length; i++) {
			if(dataTypes[i].isConvertable(o)) {
				return dataTypes[i].object2Node(o);
			}
		}
		
		return null;
	}
}
