/*
 * Created on 2004/05/25
 *
 */
package com.nullfish.lib.meta_data;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;


/**
 * @author shunji
 *
 */
public class ListData implements DataType {
	public static final String NAME = "list";
	
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
		List rtn = new ArrayList();
		
		List children = node.getChildren();
		MetaDataManager dataManager = MetaDataManager.getInstance();
		for(int i=0; i<children.size(); i++) {
			rtn.add(dataManager.node2Object((Element)children.get(i)));
		}
		
		return rtn;
	}

	/* (non-Javadoc)
	 * @see com.nullfish.lib.data_type.DataType#object2String(java.lang.Object)
	 */
	public Element object2Node(Object o) {
		MetaDataManager dataManager = MetaDataManager.getInstance();

		Element rtn = new Element(NAME);
		
		List list = (List)o;
		for(int i=0; i<list.size(); i++) {
			rtn.addContent(dataManager.object2Node(list.get(i)));
		}
		
		return rtn;
	}

	/* (non-Javadoc)
	 * @see com.nullfish.lib.data_type.DataType#getDataClass()
	 */
	public Class getDataClass() {
		return List.class;
	}
	
	/**
	 * オブジェクトがこのデータ型であるか判別する。
	 * @param o
	 * @return
	 */
	public boolean isConvertable(Object o) {
		return o instanceof List;
	}
}
