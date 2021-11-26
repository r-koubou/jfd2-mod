/*
 * Created on 2004/05/25
 *
 */
package com.nullfish.lib.meta_data;

import java.awt.Color;

import org.jdom.Element;

import com.nullfish.lib.ui.ColorUtility;

/**
 * Colorのメタデータクラス。
 * 
 * @author shunji
 */
public class ColorData implements DataType {
	/**
	 * 型名称
	 */
	public static final String NAME = "color";
	
	public String getName() {
		return NAME;
	}

	/* (non-Javadoc)
	 * @see com.nullfish.lib.data_type.DataType#string2Object(java.lang.String)
	 */
	public Object node2Object(Element node) {
		return ColorUtility.stringToColor(node.getText());
	}

	/* (non-Javadoc)
	 * @see com.nullfish.lib.data_type.DataType#object2String(java.lang.Object)
	 */
	public Element object2Node(Object o) {
		Element rtn = new Element(NAME);
		rtn.setText(ColorUtility.color2String((Color)o));
		return rtn;
	}

	/* (non-Javadoc)
	 * @see com.nullfish.lib.data_type.DataType#getDataClass()
	 */
	public Class getDataClass() {
		return Color.class;
	}
	
	/**
	 * オブジェクトがこのデータ型であるか判別する。
	 * @param o
	 * @return
	 */
	public boolean isConvertable(Object o) {
		return o instanceof Color;
	}
}
