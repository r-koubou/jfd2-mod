/*
 * Created on 2004/05/13
 *
 */
package com.nullfish.lib.command;

/**
 * @author shunji
 *
 */
public interface CommandCallable {
	/**
	 * 指定された名称のコマンドを呼び出す。
	 * @param command	コマンド名称
	 */ 
	public void callCommand(String command);
	 
}
