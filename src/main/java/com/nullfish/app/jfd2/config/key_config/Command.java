package com.nullfish.app.jfd2.config.key_config;

import org.jdom.Element;

public class Command {
	private boolean isDefaultCommand;
	
	private String name;

	public String getName() {
		return name;
	}

	public void initAsDefaultCommand(Element node) {
		isDefaultCommand = true;
		name = node.getAttributeValue("name");
	}

	public void initAsScriptCommand(Element node) {
		isDefaultCommand = false;
		name = node.getAttributeValue("name");
	}
	
	public boolean isDefaultCommand() {
		return isDefaultCommand;
	}
}

