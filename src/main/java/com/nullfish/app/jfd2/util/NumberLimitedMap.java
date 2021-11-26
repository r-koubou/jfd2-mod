package com.nullfish.app.jfd2.util;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

/**
 * 最大要素数が決められるMap。
 * 最後にアクセスされた要素から削除される。
 * 
 * @author shunji
 *
 */
public class NumberLimitedMap extends WeakHashMap {
	/**
	 * 最大要素数
	 */
	private int maxItemNumber = 100;

	/**
	 * キー値の順番リスト
	 */
	private List keyOrderList = new ArrayList();
	
	/**
	 * Map#put(Object, Object)のオーバーライド
	 */
	public Object put(Object key, Object value) {
		keyOrderList.remove(key);
		keyOrderList.add(key);
		
		checkItemNumber();
		
		return super.put(key, value);
	}
	
	/**
	 * アイテム数を最大値に収まるよう調整する。
	 *
	 */
	private void checkItemNumber() {
		while(keyOrderList.size() > maxItemNumber) {
			super.remove(keyOrderList.get(0));
			keyOrderList.remove(0);
		}
	}
	
	public Object get(Object key) {
		Object rtn = super.get(key);
		if(rtn != null) {
			keyOrderList.remove(key);
			keyOrderList.add(key);
		}
		
		return rtn;
	}
	
	public Object remove(Object key) {
		keyOrderList.remove(key);
		return super.remove(key);
	}
	
	public void clear() {
		super.clear();
		keyOrderList.clear();
	}
	
	public int getMaxItemNumber() {
		return maxItemNumber;
	}

	public void setMaxItemNumber(int maxItemNumber) {
		this.maxItemNumber = maxItemNumber;
	}
	
	
}
