/*
 * Created on 2004/05/25
 *
 */
package com.nullfish.lib.meta_data;

import org.jdom.Element;

import com.nullfish.app.jfd2.ui.table.RendererMode;


/**
 * @author shunji
 *
 */
public class RendererModeData implements DataType {
	public static final String NAME = "renderer_mode";
	
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
		return RendererMode.fromName(node.getText());
	}

	/* (non-Javadoc)
	 * @see com.nullfish.lib.data_type.DataType#object2String(java.lang.Object)
	 */
	public Element object2Node(Object o) {
		Element rtn = new Element(NAME);
		rtn.setText(((RendererMode)o).getName());
		return rtn;
	}

	/* (non-Javadoc)
	 * @see com.nullfish.lib.data_type.DataType#getDataClass()
	 */
	public Class getDataClass() {
		return RendererMode.class;
	}
	
	/**
	 * オブジェクトがこのデータ型であるか判別する。
	 * @param o
	 * @return
	 */
	public boolean isConvertable(Object o) {
		return o instanceof RendererMode;
	}
}
