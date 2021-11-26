/*
 * Created on 2004/08/16
 *
 */
package com.nullfish.app.jfd2.dialog.focus;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

/**
 * @author shunji
 * 
 * Preferences - Java - Code Style - Code Templates
 */
public class FocusOrderManagingPanel extends JPanel {
	/**
	 * 内部のコンポーネントのリスト
	 */
	private List members = new ArrayList();

	/**
	 * 一つ前のパネル
	 */
	private FocusOrderManagingPanel prevPanel;

	/**
	 * 次のパネル
	 */
	private FocusOrderManagingPanel nextPanel;

	public FocusOrderManagingPanel() {
	}

	/**
	 * 要素を追加する。
	 * @param comp
	 */
	public void addMember(Component comp) {
		members.add(comp);
	}
	
	/**
	 * @param nextPanel
	 *            The nextPanel to set.
	 */
	public void setNextPanel(FocusOrderManagingPanel nextPanel) {
		this.nextPanel = nextPanel;
	}

	/**
	 * @param prevPanel
	 *            The prevPanel to set.
	 */
	public void setPrevPanel(FocusOrderManagingPanel prevPanel) {
		this.prevPanel = prevPanel;
	}

	/**
	 * 指定されたコンポーネントのインデックスを求める。
	 * もしもこのパネルに存在しない場合は-1を返す。
	 * @param comp
	 * @return
	 */
	public int indexOf(Component comp) {
		for (int i = 0; i < members.size(); i++) {
			if(members.get(i) == comp) {
				return i;
			}
		}
		
		return -1;
	}
	
	/**
	 * 指定インデックスのコンポーネントを取得する。
	 */
	public Component getComponentToFocus(int index) {
		if(index < 0) {
			return prevPanel.getComponentToFocus(index + prevPanel.getCount());
		}
		
		if(index < members.size()) {
			return (Component)members.get(index);
		}
		
		return nextPanel.getComponentToFocus(index - members.size());
	}
	
	/**
	 * コンポーネントの数を求める。
	 * @return
	 */
	public int getCount() {
		return members.size();
	}
}
