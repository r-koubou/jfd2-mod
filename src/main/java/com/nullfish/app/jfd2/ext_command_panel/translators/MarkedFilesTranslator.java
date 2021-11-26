/*
 * Created on 2005/01/06
 *
 */
package com.nullfish.app.jfd2.ext_command_panel.translators;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.util.WindowsUtil;
import com.nullfish.lib.vfs.VFile;

/**
 * マークファイルファイル変換クラス。
 * 
 * @author shunji
 */
public class MarkedFilesTranslator implements CommandTranslator {
	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.ext_command_panel.CommandTranslator#translate(java.lang.String, com.nullfish.app.jfd2.JFDView)
	 */
	public String[] translate(String[] original, JFD jfd) {
		VFile[] markedFiles = jfd.getModel().getMarkedFiles();
		
		StringBuffer buffer = new StringBuffer();
		
		for(int i=0; i < 10 && i<markedFiles.length; i++) {
			String name = markedFiles[i].getName();
			name = name.indexOf(' ') != -1 ? "\"" + name + "\"" : name;
			name = WindowsUtil.escapeFileName(name);
			buffer.append(name);

			buffer.append(" ");
		}
		
		for(int i=0; i<original.length; i++) {
			original[i] = original[i].replaceAll("\\$T", buffer.toString());
		}
		
		return original;
	}
}
