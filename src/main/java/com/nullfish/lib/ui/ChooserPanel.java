/*
 * Created on 2004/06/07
 *
 */
package com.nullfish.lib.ui;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.KeyStroke;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import com.nullfish.lib.keymap.KeyStrokeMap;

/**
 * いくつかの選択肢から一つを選ぶのに使うコンポーネント。
 * フォーカスの状態によって枠線が変わる。
 * 使用後、dispose()を呼ぶ必要がある。
 * 
 * 著作権:   Copyright (c) Shunji Yamaura
 * 会社名:
 * @author Shunji Yamaura
 * @version 1.0
 */
public class ChooserPanel extends JPanel implements FocusListener {
	//	選択ラジオボタンの配列
	JRadioButton[] buttons;
	
	Choice[] choice;
	
	ButtonGroup group = new ButtonGroup();
	
	TitledBorder border;
	
	static Border focusedBorder = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
//	static Border focusedBorder = BorderFactory.createRaisedBevelBorder();
	
//	static Border noFocusBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
	static Border noFocusBorder = BorderFactory.createEmptyBorder();
//	static Border noFocusBorder = BorderFactory.createLineBorder(Color.WHITE);
	
	GridLayout layout;
	
	private List listeners = new ArrayList();
	
	public static final String SELECT_NEXT = "next";
	
	public static final String SELECT_PREV = "prev";
	
	/**
	 * コンストラクタ
	 * 
	 * @param	title	枠に表示されるタイトルの文字列
	 * @param	choice	選択肢となる文字列
	 * @param	keys	選択ラジオボタンのショートカットキー
	 * @param	rows	ラジオボタンの、横表示数
	 * @param	cols	ラジオボタンの、縦表示数
	 * @param	defaultChoice	最初に選択されている選択の番号
	 */
	public ChooserPanel(
		String title,
		Choice[] choice,
		int cols,
		String defaultChoice) {
		for(int i=0; i<choice.length; i++) {
			if(choice[i].getName().equals(defaultChoice)) {
				init(title, choice, cols, i);
				return;
			}
		}
		init(title, choice, cols, 0);		
	}
	
	/**
	 * コンストラクタ
	 * 
	 * @param	title	枠に表示されるタイトルの文字列
	 * @param	choice	選択肢となる文字列
	 * @param	keys	選択ラジオボタンのショートカットキー
	 * @param	rows	ラジオボタンの、横表示数
	 * @param	cols	ラジオボタンの、縦表示数
	 * @param	defaultChoice	最初に選択されている選択の番号
	 */
	public ChooserPanel(
		String title,
		Choice[] choice,
		int cols,
		int defaultChoice) {
		init(title, choice, cols, defaultChoice);
	}
	
	private void init(
			String title,
			Choice[] choice,
			int cols,
			int defaultChoice) {
		this.choice = choice;
		
		int rows = 1+ ((choice.length - 1) / cols);
		
		//	ボーダー設定
        border = new TitledBorder(noFocusBorder, title);
        this.setBorder(border);
        
        this.setFocusable(true);
        
   		layout = new GridLayout(rows, cols);
		this.setLayout(layout);
		
		buttons = new JRadioButton[choice.length];

		ActionMap actionMap = getActionMap();
				
		InputMap inWindowMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);

		for(int i=0; i<buttons.length; i++) {
			if(choice[i].getMnemonic() == (char)-1) {
				buttons[i] = new JRadioButton(choice[i].getLabel());
			} else {
				buttons[i] = new JRadioButton(choice[i].getLabel() + '(' + Character.toUpperCase(choice[i].getMnemonic()) + ')');
				inWindowMap.put(KeyStroke.getKeyStroke(choice[i].getMnemonic()), buttons[i]);
			}
			buttons[i].setFocusable(false);
			buttons[i].setEnabled(choice[i].isActive());
			buttons[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					requestFocusInWindow();
					noticeActionPerformed();
				}
			});
			
			actionMap.put(buttons[i], new SelectAction(buttons[i]));
			
			group.add(buttons[i]);
			this.add(buttons[i]);
			
			if(i == defaultChoice) {
				buttons[i].setSelected(true);
			}
		}
		
		actionMap.put(SELECT_NEXT, new SelectNextAction());
		actionMap.put(SELECT_PREV, new SelectPrevAction());
	
		InputMap focusedMap = getInputMap(JComponent.WHEN_FOCUSED);
		focusedMap.put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_RIGHT, 0), SELECT_NEXT);
		focusedMap.put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_DOWN, 0), SELECT_NEXT);

		focusedMap.put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_LEFT, 0), SELECT_PREV);
		focusedMap.put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_UP, 0), SELECT_PREV);
		
		addFocusListener(this);
	}

	/**
	 * 廃棄
	 */
	public void dispose() {
		getActionMap().clear();
		removeFocusListener(this);
	}
		
	/**
	 * 選択されたラジオボタンのインデックスを返す
	 */
	public int getSelectedIndex() {
		for(int i=0; i<buttons.length; i++) {
			if(buttons[i].isSelected()) {
				return i;
			}
		}
		
		return -1;
	}
	
	/**
	 * 選択された答えを返す。
	 * 
	 * @return
	 */
	public String getSelectedAnswer() {
		int index = getSelectedIndex();
		if(index < 0) {
			return null;
		}
		
		return choice[index].getName();
	}

	public void setSelectedAnser(String answer) {
		for(int i=0; i<choice.length; i++) {
			if(answer.equals(choice[i].getName())) {
				buttons[i].setSelected(true);
				return;
			}
		}
	}
	
	/**
	 * インデックスのラジオボタンを選択する
	 */
	public void setSelectedIndex(int index) {
		buttons[index].setSelected(true);
	}
	
	/**
	 * 現在の選択の次のラジオボタンを選ぶAction
	 */
	class SelectNextAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			int index = getSelectedIndex();
			while(true) {
				index++;
				index -= (index >= buttons.length ? buttons.length : 0);
				if(buttons[index].isEnabled()) {
					break;
				}
			}
			
			buttons[index].setSelected(true);
		}
	}
	
	/**
	 * 現在の選択の前のラジオボタンを選ぶAction
	 */
	class SelectPrevAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			int index = getSelectedIndex();
			while(true) {
				index--;
				if(index < 0) {
					index += buttons.length;
				}
				if(buttons[index].isEnabled()) {
					break;
				}
			}
			
			buttons[index].setSelected(true);
		}
	}
	
	/**
	 * 特定のラジオボタンを選び、フォーカスを呼ぶAction
	 */
	class SelectAction extends AbstractAction {
		JRadioButton button;
		
		public SelectAction(JRadioButton button) {
			this.button = button;
		}
			
		public void actionPerformed(ActionEvent e) {
			if(button.isEnabled()) {
				button.setSelected(true);
				requestFocusInWindow();
			}
		}
	}
	
	public void focusGained(FocusEvent e) {
		border.setBorder(focusedBorder);
		repaint();
	}

	public void focusLost(FocusEvent e) {
		border.setBorder(noFocusBorder);
		repaint();
	}
	
	/**
	 * リスナを追加する。
	 * リスナは選択が変化した際に呼び出される。
	 * @param listener
	 */
	public void addActionListener(ActionListener listener) {
		listeners.add(listener);
	}
	
	/**
	 * リスナを削除する。
	 * @param listener
	 */
	public void removeActionListener(ActionListener listener) {
		listeners.remove(listener);
	}
	
	/**
	 * 選択が変化したことを通知する。
	 *
	 */
	private void noticeActionPerformed() {
		ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, getSelectedAnswer());
		
		for(int i=0; i<listeners.size(); i++) {
			((ActionListener)listeners.get(i)).actionPerformed(e);
		}
	}
	
	public void setFont(Font font) {
		for(int i=0; buttons != null && i<buttons.length; i++) {
			buttons[i].setFont(font);
		}
		
		if(border != null) {
			border.setTitleFont(font);
		}
		
		super.setFont(font);
	}
}
