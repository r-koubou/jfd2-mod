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
 * マークファイルファイル変換クラス。
 * 
 * @author shunji
 */
public class MarkedFilesExecAllTranslator implements CommandTranslator {
	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.ext_command_panel.CommandTranslator#translate(java.lang.String, com.nullfish.app.jfd2.JFDView)
	 */
	public String[] translate(String[] original, JFD jfd) {
		VFile[] markedFiles = jfd.getModel().getMarkedFiles();
		if(markedFiles.length == 0) {
			for(int i=0; i<original.length; i++) {
				original[i] = original[i].replaceAll("\\$M", "");
			}
			
			return original;
		}
		
		List list = new ArrayList();
		
		for(int i=0; i<original.length; i++) {
			if(original[i].indexOf("$M") == -1) {
				list.add(original[i]);
			} else {
				for(int j=0; j<markedFiles.length; j++) {
					String name = markedFiles[j].getName();
					name = name.indexOf(' ') != -1 ? "\"" + name + "\"" : name;
					list.add(original[i].replaceAll("\\$M", WindowsUtil.escapeFileName(name)));
				}
			}
		}
		
		return (String[])list.toArray(new String[0]);
	}
}
