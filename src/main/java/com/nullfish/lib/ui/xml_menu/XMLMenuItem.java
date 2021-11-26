/*
 * Created on 2004/05/12
 *
 */
package com.nullfish.lib.ui.xml_menu;

import javax.swing.JMenuItem;

import org.jdom.Element;

import com.nullfish.lib.command.CommandCallable;
import com.nullfish.lib.resource.ResourceManager;

/**
 * @author shunji
 *
 */
public class XMLMenuItem extends JMenuItem implements XMLMenuElement {
	private ResourceManager resource;
	
	private CommandCallable callable;
	
	/**
	 * ノード名称
	 */
	public static final String NODE_NAME = "menuitem";
	
	/**
	 * コマンド属性
	 */
	public static final String ATTR_COMMAND = "command";
	
	/**
	 * 表示文字列属性
	 */
	public static final String ATTR_TEXT = "text";
	
	/**
	 * 表示文字列の、リソース中でのキー名称属性
	 */
	public static final String ATTR_LABEL = "label";
	
	/**
	 * ニーモニックキー名称属性
	 */
	public static final String ATTR_MNEMONIC = "mnemonic";
	
	/**
	 * デフォルトコンストラクタ
	 *
	 *
	 */
	public XMLMenuItem() {
		super();
	}
	
	/**
	 * XML要素から初期化する。
	 * @param element
	 */
	public void convertFromNode(Element element) {
		if(!NODE_NAME.equals(element.getName())) {
			// TODO:例外処理
		}

		XMLMenuUtility.initMenuItemFromNode(this, element, getResource(), callable);
	}
	
	/**
	 * @return Returns the resource.
	 */
	public ResourceManager getResource() {
		return resource;
	}
	/**
	 * @param resource The resource to set.
	 */
	public void setResource(ResourceManager resource) {
		this.resource = resource;
	}
	/**
	 * @return Returns the callable.
	 */
	public CommandCallable getCallable() {
		return callable;
	}
	/**
	 * @param callable The callable to set.
	 */
	public void setCallable(CommandCallable callable) {
		this.callable = callable;
	}
}
