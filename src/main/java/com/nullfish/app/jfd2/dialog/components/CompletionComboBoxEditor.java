/*
 * Created on 2004/06/08
 *
 */
package com.nullfish.app.jfd2.dialog.components;

import javax.swing.plaf.basic.BasicComboBoxEditor;

import com.nullfish.app.jfd2.JFD;

/**
 * パス補完機能付きコンボボックスエディタ
 * 
 * @author shunji
 */
public class CompletionComboBoxEditor extends BasicComboBoxEditor {
	public CompletionComboBoxEditor(JFD jfd) {
		super();
		editor = new FileCompletionTextField(jfd);
		editor.setBorder(null);
	}
	
	/**
	 * @see javax.swing.ComboBoxEditor#setItem(Object)
	 */
    public void setItem(Object anObject) {
    	//	選択が変更されたら全選択する
        super.setItem(anObject);
        selectAll();
    }
}
