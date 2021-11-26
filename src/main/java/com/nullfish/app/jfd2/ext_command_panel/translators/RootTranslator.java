/*
 * Created on 2005/01/06
 *
 */
package com.nullfish.app.jfd2.ext_command_panel.translators;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.util.WindowsUtil;
import com.nullfish.lib.vfs.VFile;

/**
 * ルートファイル変換クラス。
 * 
 * @author shunji
 */
public class RootTranslator implements CommandTranslator {
	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.ext_command_panel.CommandTranslator#translate(java.lang.String, com.nullfish.app.jfd2.JFDView)
	 */
	public String[] translate(String[] original, JFD jfd) {
		VFile file = jfd.getModel().getCurrentDirectory();
		
		if(file == null) {
			return original;
		}
		
		while(file.getParent() != null) {
			file = file.getParent();
		}
		
		for(int i=0; i<original.length; i++) {
			String path = file.getAbsolutePath().replaceAll("\\\\", "\\\\\\\\");
			path = path.indexOf(' ') != -1 ? "\"" + path + "\"" : path;
			original[i] = original[i].replaceAll("\\$D", WindowsUtil.escapeFileName(path));
		}
		return original;
	}
}
