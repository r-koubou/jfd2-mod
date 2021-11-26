/*
 * Created on 2004/05/13
 *
 */
package com.nullfish.lib.ui;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import org.jdom.Element;

import com.nullfish.app.jfd2.config.DefaultConfig;

/**
 * @author shunji
 *
 */
public class KeyStrokeUtility {
	/**
	 * キータグ名
	 */
	public static final String KEY_NODE = "key";
	
	/**
	 * キー名称属性
	 */
	public static final String ATTR_NAME = "name";
	
	/**
	 * シフトキー属性
	 */
	public static final String ATTR_SHIFT = "shift";
	
	/**
	 * Ctrlキー属性
	 */
	public static final String ATTR_CTRL = "ctrl";
	
	/**
	 * メタキー属性
	 */
	public static final String ATTR_META = "meta";
	
	/**
	 * Altキー属性
	 */
	public static final String ATTR_ALT = "alt";
	
	/**
	 * キーを離す動作属性
	 */
	public static final String ATTR_ON_RELEASE = "onrelease";
	
	/**
	 * XMLノードからKeyStrokeを生成する。
	 * @param node
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static KeyStroke node2KeyStroke(Element node) throws IllegalArgumentException {
		try {
			String keyName = node.getAttributeValue(ATTR_NAME);
			if(keyName == null || keyName.length() == 0) {
				throw new IllegalArgumentException("key name is : " + keyName);
			}
			
			Class keyEventClass = Class.forName("java.awt.event.KeyEvent");
			int keyCode = keyEventClass.getDeclaredField(keyName).getInt(null);
			
			//	モディファイア
			int modifier = 0;
			if( Boolean.valueOf(node.getAttributeValue(ATTR_SHIFT)).booleanValue() ) {
				modifier += InputEvent.SHIFT_MASK;
			}
			
			if( Boolean.valueOf(node.getAttributeValue(ATTR_CTRL)).booleanValue() ) {
				if(DefaultConfig.getDefaultConfig().isSwapCtrlMeta()) {
					modifier += KeyEvent.META_MASK;
				} else {
					modifier += KeyEvent.CTRL_MASK;
				}
			}
			
			if( Boolean.valueOf(node.getAttributeValue(ATTR_META)).booleanValue() ) {
				if(DefaultConfig.getDefaultConfig().isSwapCtrlMeta()) {
					modifier += KeyEvent.CTRL_MASK;
				} else {
					modifier += KeyEvent.META_MASK;
				}
			}
			
			if( Boolean.valueOf(node.getAttributeValue(ATTR_ALT)).booleanValue() ) {
				modifier += KeyEvent.ALT_MASK;
			}
			
			//	キーリリース時発生か
			String onReleaseStr = node.getAttributeValue(ATTR_ON_RELEASE);
			boolean onRelease = false;
			if(onReleaseStr != null && onReleaseStr.length() > 0) {
				onRelease = Boolean.valueOf(onReleaseStr).booleanValue();
			}

			return KeyStroke.getKeyStroke(keyCode, modifier, onRelease);
		} catch (ClassNotFoundException e) {
			//	発生し得ない。java.awt.event.KeyEventは必ず存在するため。
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		} catch (SecurityException e) {
			throw new IllegalArgumentException(e);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(e);
		} catch (NoSuchFieldException e) {
			throw new IllegalArgumentException(e);
		}
	}
}
