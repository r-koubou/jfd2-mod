/*
 * Created on 2004/06/15
 *
 */
package com.nullfish.app.jfd2.ui.function;

/**
 * @author shunji
 *
 */
public class CommandHolder {
	private String command;
	
	private String labelText;
	
	public CommandHolder(String command, String labelText) {
		this.command = command;
		this.labelText = labelText;
	}
	
	/**
	 * @return Returns the command.
	 */
	public String getCommand() {
		return command;
	}
	/**
	 * @return Returns the labelText.
	 */
	public String getLabelText() {
		return labelText;
	}
}
