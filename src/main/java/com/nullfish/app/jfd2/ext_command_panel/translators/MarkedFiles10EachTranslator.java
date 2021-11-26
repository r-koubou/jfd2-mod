/*
 * Created on 2005/01/06
 *
 */
package com.nullfish.app.jfd2.ext_command_panel.translators;

import java.util.ArrayList;
import java.util.List;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.util.WindowsUtil;
import com.nullfish.lib.vfs.VFile;

/**
 * マークファイルファイルを１０ずつ実行する変換クラス。
 * 
 * @author shunji
 */
public class MarkedFiles10EachTranslator implements CommandTranslator {
	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.ext_command_panel.CommandTranslator#translate(java.lang.String, com.nullfish.app.jfd2.JFDView)
	 */
	public String[] translate(String[] original, JFD jfd) {
		VFile[] markedFiles = jfd.getModel().getMarkedFiles();
		
		List buffers = new ArrayList();
		
		for(int i=0; i*10<markedFiles.length; i++) {
			StringBuffer buffer = new StringBuffer();
			buffers.add(buffer);
			for(int j=0; i*10 + j<markedFiles.length; j++) {
				String name = markedFiles[i*10 + j].getName();
				name = name.indexOf(' ') != -1 ? "\"" + name + "\"" : name;
				name = WindowsUtil.escapeFileName(name);
				buffer.append(name);

				buffer.append(" ");
			}
		}
		
		List rtn = new ArrayList();
		for(int i=0; i<original.length; i++) {
			if(original[i].indexOf("$TA") == -1) {
				rtn.add(original[i]);
			} else {
				for(int j = 0; j<buffers.size(); j++) {
					rtn.add(original[i].replaceAll("\\$TA", ((StringBuffer)buffers.get(j)).toString()));
				}
			}
		}
		
		return (String[])rtn.toArray(new String[0]);
	}
}
