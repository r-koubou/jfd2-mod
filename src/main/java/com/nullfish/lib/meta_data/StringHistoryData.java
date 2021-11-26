/*
 * Created on 2004/05/25
 *
 */
package com.nullfish.lib.meta_data;

import java.util.List;

import org.jdom.Element;

import com.nullfish.app.jfd2.util.StringHistory;


/**
 * @author shunji
 *
 */
public class StringHistoryData implements DataType {
	public static final String NAME = "string_history";
	
	/**
	 * オーバーラップ有無パラメータ
	 */
	public static final String ATTR_NO_OVERWRAPS = "no_overwraps";
	
	/**
	 * 最大サイズパラメータ
	 */
	public static final String ATTR_MAX_SIZE = "max_size";
	
	/* (non-Javadoc)
	 * @see com.nullfish.lib.data_type.DataType#getName()
	 */
	public String getName() {
		return NAME;
	}

	/* (non-Javadoc)
	 * @see com.nullfish.lib.data_type.DataType#string2Object(java.lang.String)
	 */
	public Object node2Object(Element node) {
		boolean noOverwraps = true;
		String noOverwrapsStr = node.getAttributeValue(ATTR_NO_OVERWRAPS);
		if(noOverwrapsStr != null && noOverwrapsStr.length() > 0) {
			noOverwraps = Boolean.valueOf(noOverwrapsStr).booleanValue();
		}

		int size = 50;
		String sizeStr = node.getAttributeValue(ATTR_MAX_SIZE);
		if(sizeStr != null && sizeStr.length() > 0) {
			size = Integer.parseInt(sizeStr);
		}
		
		StringHistory rtn = new StringHistory(size, noOverwraps);
		
		List memberNodes = node.getChildren(TextData.NAME);
		
		for(int i = 0; i<memberNodes.size(); i++) {
			rtn.add(((Element)memberNodes.get(memberNodes.size() - i - 1)).getText());
		}
		
		return rtn;
	}

	/* (non-Javadoc)
	 * @see com.nullfish.lib.data_type.DataType#object2String(java.lang.Object)
	 */
	public Element object2Node(Object o) {
		StringHistory history = (StringHistory)o;
		
		Element rtn = new Element(NAME);
		rtn.setAttribute(ATTR_NO_OVERWRAPS, Boolean.toString(history.isNoOverwraps()));
		rtn.setAttribute(ATTR_MAX_SIZE, Integer.toString(history.getMaxSize()));
		
		int size = history.getSize();
		for(int i=0; i<size; i++) {
			Element element = new Element(TextData.NAME);
			element.setText(history.getAt(i));
			rtn.addContent(element);
		}
		
		return rtn;
	}

	/* (non-Javadoc)
	 * @see com.nullfish.lib.data_type.DataType#getDataClass()
	 */
	public Class getDataClass() {
		return StringHistory.class;
	}

	/**
	 * オブジェクトがこのデータ型であるか判別する。
	 * @param o
	 * @return
	 */
	public boolean isConvertable(Object o) {
		return o instanceof StringHistory;
	}
}
