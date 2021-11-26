/*
 * Created on 2004/05/25
 *
 */
package com.nullfish.lib.meta_data;

import org.jdom.Element;

/**
 * 文字列と相互変換可能なデータ型のメタ情報を表すインターフェイス。
 * 
 * @author shunji
 */
public interface DataType {
	/**
	 * 型の名称を求める。
	 * @return
	 */
	public String getName();
	
	/**
	 * ノード→オブジェクトの変換を行う。
	 * @param str
	 * @return
	 */
	public Object node2Object(Element node);
	
	/**
	 * オブジェクト→ノードの変換を行う。
	 * @param o
	 * @return
	 */
	public Element object2Node(Object o);
	
	/**
	 * Javaオブジェクトの型を求める。
	 * @return
	 */
	public Class getDataClass();
	
	/**
	 * オブジェクトがこのデータ型であるか判別する。
	 * @param o
	 * @return
	 */
	public boolean isConvertable(Object o);
}
