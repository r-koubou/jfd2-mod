package com.nullfish.lib.ui.document;

import java.util.ArrayList;
import java.util.List;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * 入力制限つきドキュメント
 * @author shunji
 *
 */
public class RestrictedDocument extends PlainDocument {
	/**
	 * 制約リスト
	 */
	private List restrictions = new ArrayList();
	
	public void insertString(int offs, String str, AttributeSet a) throws BadLocationException { 
		StringBuffer text = new StringBuffer( getText(0, getLength()) );
		text.insert(offs, str);
		String newText = text.toString();
		
		for(int i=0; i<restrictions.size(); i++) {
			if(!((Restriction)restrictions.get(i)).isAllowed(newText)) {
				return;
			}
		}
		
		super.insertString(offs, str, a);
	}
	
	/**
	 * 制約を追加する。
	 * @param rest
	 */
	public void addRestriction(Restriction rest) {
		restrictions.add(rest);
	}
}
