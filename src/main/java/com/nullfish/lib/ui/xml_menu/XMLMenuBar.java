/*
 * Created on 2004/05/13
 *
 */
package com.nullfish.lib.ui.xml_menu;

import java.util.List;

import javax.swing.JMenuBar;

import org.jdom.Element;

import com.nullfish.lib.command.CommandCallable;
import com.nullfish.lib.resource.ResourceManager;

/**
 * @author shunji
 *
 */
public class XMLMenuBar extends JMenuBar implements XMLMenuElement {
	private ResourceManager resource;
	
	private CommandCallable callable;
	
	public XMLMenuBar() {
		
	}
	
	public XMLMenuBar(ResourceManager resource, CommandCallable callable) {
		this.resource = resource;
		this.callable = callable;
	}
	
	/* (non-Javadoc)
	 * @see org.sexyprogrammer.lib.ui.xml_menu.XMLMenuElement#convertFromNode(org.jdom.Element)
	 */
	public void convertFromNode(Element node) {
		List children = node.getChildren();
		for(int i=0; i<children.size(); i++) {
			Element child = (Element)children.get(i);
			if(child.getName().equals(XMLMenu.NODE_NAME)) {
				XMLMenu menu = new XMLMenu();
				menu.setCallable(callable);
				menu.setResource(resource);
				menu.convertFromNode(child);
				add(menu);
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
