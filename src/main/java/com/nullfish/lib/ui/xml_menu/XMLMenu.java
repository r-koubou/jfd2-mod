/*
 * Created on 2004/05/13
 *
 */
package com.nullfish.lib.ui.xml_menu;

import java.util.List;

import javax.swing.JMenu;

import org.jdom.Element;

import com.nullfish.lib.command.CommandCallable;
import com.nullfish.lib.resource.ResourceManager;

/**
 * @author shunji
 *
 */
public class XMLMenu extends JMenu implements XMLMenuElement {
	public static final String NODE_NAME = "menu";
	
	public static final String NODE_SEPARATOR = "separator";
	
	private ResourceManager resource;
	
	private CommandCallable callable;
	
	/* (non-Javadoc)
	 * @see org.sexyprogrammer.lib.ui.xml_menu.XMLMenuElement#convertFromNode(org.jdom.Element)
	 */
	public void convertFromNode(Element element) {
		if(!NODE_NAME.equals(element.getName())) {
			// TODO:例外処理
		}

		XMLMenuUtility.initMenuItemFromNode(this, element, resource, callable);
		
		List children = element.getChildren();
		for(int i=0; i<children.size(); i++) {
			Element child = (Element)children.get(i);
			String childName = child.getName();
			
			if(childName.equals(NODE_SEPARATOR)) {
				addSeparator();
			} else if(childName.equals(XMLMenuItem.NODE_NAME)) {
				XMLMenuItem item = new XMLMenuItem();
				item.setResource(resource);
				item.setCallable(callable);
				item.convertFromNode(child);
				add(item);
			} else if(childName.equals(XMLMenu.NODE_NAME)) {
				XMLMenu item = new XMLMenu();
				item.setResource(resource);
				item.setCallable(callable);
				item.convertFromNode(child);
				add(item);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.sexyprogrammer.lib.ui.xml_menu.XMLMenuElement#setResource(org.sexyprogrammer.lib.resource.ResourceManager)
	 */
	public void setResource(ResourceManager resource) {
		this.resource = resource;
	}

	/* (non-Javadoc)
	 * @see org.sexyprogrammer.lib.ui.xml_menu.XMLMenuElement#setCallable(org.sexyprogrammer.lib.command.CommandCallable)
	 */
	public void setCallable(CommandCallable callable) {
		this.callable = callable;
	}
}
