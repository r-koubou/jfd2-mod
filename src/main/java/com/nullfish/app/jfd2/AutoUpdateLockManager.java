/*
 * Created on 2004/08/22
 *
 */
package com.nullfish.app.jfd2;

import java.util.HashSet;
import java.util.Set;

/**
 * 自動更新のロックを管理するクラス。
 * 
 * @author shunji
 */
public class AutoUpdateLockManager {
	private Set locks = new HashSet();
	
	/**
	 * ロックする。
	 * @param o	ロックオブジェクト
	 */
	public synchronized void lock(Object o) {
		locks.add(o);
	}
	
	/**
	 * ロックを解除する。
	 * @param o	ロックオブジェクト
	 */
	public synchronized void unlock(Object o) {
		locks.remove(o);
	}
	
	/**
	 * ロックされているならtrueを返す。
	 * 
	 * @return
	 */
	public synchronized boolean isLocked() {
		return locks.size() != 0;
	}
}
