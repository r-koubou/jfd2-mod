/*
 * Created on 2004/06/08
 *
 */
package com.nullfish.lib.ui;

import javax.swing.KeyStroke;

/**
 * ChooserPanel等での選択肢を表すクラス
 * 
 * @author shunji
 */
public class Choice {
	/**
	 * 名称
	 */
	String name;
	
	/**
	 * 表示ラベル
	 */
	String label;
	
	/**
	 * ニーモニックキー
	 */
	char mnemonic;
	
	/**
	 * 選択可能フラグ
	 */
	boolean active = true;
	
	public Choice(String name, String label) {
		this(name, label, (char)-1, true);
	}
	
	public Choice(String name, String label, char mnemonic) {
		this(name, label, mnemonic, true);
	}
	
	public Choice(String name, String label, char mnemonic, boolean active) {
		this.name = name;
		this.label = label;
		this.mnemonic = mnemonic;
		this.active = active;
	}
	
	/**
	 * @return Returns the label.
	 */
	public String getLabel() {
		return label;
	}
	
	/**
	 * @return Returns the mnemonic.
	 */
	public char getMnemonic() {
		return mnemonic;
	}
	
	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}
	
	public KeyStroke getKeyStroke() {
		if(mnemonic == -1) {
			return null;
		}
			
		return KeyStroke.getKeyStroke(mnemonic);
	}
	
	/**
	 * @return Returns the active.
	 */
	public boolean isActive() {
		return active;
	}
}
