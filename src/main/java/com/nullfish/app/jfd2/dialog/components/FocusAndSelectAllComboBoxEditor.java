/*
 * Created on 2004/09/07
 *
 */
package com.nullfish.app.jfd2.dialog.components;

import javax.swing.plaf.basic.BasicComboBoxEditor;


/**
 * フォーカスされたら選択状態になるコンボボックスエディタ
 */
public class FocusAndSelectAllComboBoxEditor extends BasicComboBoxEditor {
    public FocusAndSelectAllComboBoxEditor() {
        super();
    }

    public void setItem(Object anObject) {
        super.setItem(anObject);
        selectAll();
    }
}
