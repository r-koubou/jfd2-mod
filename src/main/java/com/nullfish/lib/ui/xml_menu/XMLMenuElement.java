/*
 * Created on 2004/05/13
 *
 */
package com.nullfish.lib.ui.xml_menu;

import org.jdom.Element;

import com.nullfish.lib.command.CommandCallable;
import com.nullfish.lib.resource.ResourceManager;

/**
 * @author shunji
 *
 */
public interface XMLMenuElement {
	public void convertFromNode(Element node);
	
	public void setResource(ResourceManager resource);
	
	public void setCallable(CommandCallable callable);
}
