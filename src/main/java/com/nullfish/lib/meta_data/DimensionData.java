/*
 * Created on 2004/05/25
 *
 */
package com.nullfish.lib.meta_data;

import java.awt.Dimension;

import org.jdom.Element;


/**
 * @author shunji
 *
 */
public class DimensionData implements DataType {
	public static final String NAME = "dimension";
	
	public static final String WIDTH = "width";
	public static final String HEIGHT = "height";
	
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
		String widthStr = node.getAttributeValue(WIDTH);
		int width = widthStr != null && widthStr.length() > 0 ? Integer.parseInt(widthStr) : 0;
		
		String heightStr = node.getAttributeValue(WIDTH);
		int height = heightStr != null && heightStr.length() > 0 ? Integer.parseInt(heightStr) : 0;
		
		return new Dimension(width, height);
	}

	/* (non-Javadoc)
	 * @see com.nullfish.lib.data_type.DataType#object2String(java.lang.Object)
	 */
	public Element object2Node(Object o) {
		Element rtn = new Element(NAME);
		Dimension d = (Dimension)o;
		
		rtn.setAttribute(WIDTH, Integer.toString(d.width));
		rtn.setAttribute(HEIGHT, Integer.toString(d.height));
		
		return rtn;
	}

	/* (non-Javadoc)
	 * @see com.nullfish.lib.data_type.DataType#getDataClass()
	 */
	public Class getDataClass() {
		return Dimension.class;
	}
	
	/**
	 * オブジェクトがこのデータ型であるか判別する。
	 * @param o
	 * @return
	 */
	public boolean isConvertable(Object o) {
		return o instanceof Dimension;
	}
}
