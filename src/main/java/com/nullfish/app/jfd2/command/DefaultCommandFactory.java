/*
 * Created on 2004/05/28
 *
 */
package com.nullfish.app.jfd2.command;

import org.jdom.Element;

/**
 * @author shunji
 *
 */
public class DefaultCommandFactory extends CommandFactory {
	private String className;
	
	private ClassLoader loader = this.getClass().getClassLoader();
	
	public static final String ATTR_CLASS_NAME = "class";
	
	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.command.CommandFactory#getCommand()
	 */
	public Command doGetCommand() {
		try {
			return (Command)loader.loadClass(className).newInstance();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	public void convertFromNode(Element node) {
		super.convertFromNode(node);

		className = node.getAttributeValue(ATTR_CLASS_NAME);
	}
	
	public void setClassName(String className) {
		this.className = className;
	}
	
	public void setLoader(ClassLoader loader) {
		this.loader = loader;
	}
}
