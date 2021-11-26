/*
 * Created on 2005/01/08
 *
 */
package com.nullfish.app.jfd2.ext_command_panel;

import org.jdom.Element;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.command.groovy.GroovyFileCommand;
import com.nullfish.lib.vfs.exception.VFSException;

/**
 * シェルコマンド
 * 
 * @author shunji
 */
public class ScriptFileExternalCommand {
	/**
	 * 実行スクリプト
	 */
	private String scriptFile;
	
	/**
	 * ノード名
	 */
	public static final String NODE_NAME = "scriptfile";
	
	/**
	 * コンストラクタ
	 *
	 */
	public ScriptFileExternalCommand() {
	}
	
	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.ext_command_panel.ExternalCommand#execute()
	 */
	public void execute(JFD jfd) throws VFSException {
		GroovyFileCommand command = new GroovyFileCommand(scriptFile);
		command.setJFD(jfd);
		command.startAsync();
	}

	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.ext_command_panel.ExternalCommand#init(org.jdom.Element)
	 */
	public void init(Element node) {
		if(node == null) {
			scriptFile = "";
		}
		
		scriptFile = node.getText();
	}

	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.ext_command_panel.ExternalCommand#toNode()
	 */
	public Element toNode() {
		Element rtn = new Element(NODE_NAME);
		rtn.setText(scriptFile);
		return rtn;
	}

	public String getScriptFile() {
		return scriptFile;
	}

	public void setScriptFile(String scriptFile) {
		this.scriptFile = scriptFile;
	}
	
}
