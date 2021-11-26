/*
 * Created on 2005/01/06
 *
 */
package com.nullfish.app.jfd2.ext_command_panel.translators;

import com.nullfish.app.jfd2.JFD;

/**
 * 追加入力ダイアログ非表示変換クラス。
 * 
 * @author shunji
 */
public class NoDialogTranslator implements CommandTranslator {
	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.ext_command_panel.CommandTranslator#translate(java.lang.String, com.nullfish.app.jfd2.JFDView)
	 */
	public String[] translate(String[] original, JFD jfd) {
		for(int i=0; i<original.length; i++) {
			original[i] = original[i].replaceAll("\\$R", "");
		}
		return original;
	}
}
