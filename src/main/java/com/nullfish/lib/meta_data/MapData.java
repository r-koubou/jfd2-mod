/*
 * Created on 2004/05/25
 *
 */
package com.nullfish.lib.meta_data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jdom.Element;

/**
 * Mapのメタデータクラス。
 * 
 * @author shunji
 */
public class MapData implements DataType {
	/**
	 * 型名称
	 */
	public static final String NAME = "map";
	
	public String getName() {
		return NAME;
	}

	/* (non-Javadoc)
	 * @see com.nullfish.lib.data_type.DataType#string2Object(java.lang.String)
	 */
	public Object node2Object(Element node) {
		Map rtn = new HashMap();
		
		List entries = node.getChildren("entry");
		
		for(int i=0; i<entries.size(); i++) {
			Element entry = (Element)entries.get(i);
			rtn.put(entry.getAttributeValue("name"), MetaDataManager.getInstance().node2Object((Element)entry.getChildren().get(0)));
		}
		
		return rtn;
	}

	/* (non-Javadoc)
	 * @see com.nullfish.lib.data_type.DataType#object2String(java.lang.Object)
	 */
	public Element object2Node(Object o) {
		Element rtn = new Element(NAME);
		
		Iterator entries = ((Map)o).entrySet().iterator();
		while(entries.hasNext()) {
			Entry entry = (Entry)entries.next();
			Element entryNode = new Element("entry");
			entryNode.setAttribute("name", (String)entry.getKey());
			entryNode.addContent(MetaDataManager.getInstance().object2Node(entry.getValue()));
			
			rtn.addContent(entryNode);
		}
		
		return rtn;
	}

	/* (non-Javadoc)
	 * @see com.nullfish.lib.data_type.DataType#getDataClass()
	 */
	public Class getDataClass() {
		return Map.class;
	}
	
	/**
	 * オブジェクトがこのデータ型であるか判別する。
	 * @param o
	 * @return
	 */
	public boolean isConvertable(Object o) {
		return o instanceof Map;
	}
}
