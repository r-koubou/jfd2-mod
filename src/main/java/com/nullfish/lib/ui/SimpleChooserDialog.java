/*
 * Created on 2004/06/08
 *
 */
package com.nullfish.lib.ui;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.DefaultListModel;
import javax.swing.InputMap;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;

import com.nullfish.lib.keymap.KeyStrokeMap;

/**
 * シンプルな選択用ダイアログ。
 * Jlistがあるだけ。
 * 
 * @author shunji
 */
public class SimpleChooserDialog extends JDialog implements MouseListener {
	JList list = new MigemoList();
	JScrollPane scroll = new JScrollPane();

	//	リストのデータモデル
	DefaultListModel model = new DefaultListModel();

	//	選択された文字列
	Object choosen;

	public static final String CHOICE_ACTION = "choice";

	public static final String CANCEL_ACTION = "cancel";

	/**
	 * コンストラクタ
	 * 
	 * @param owner 親フレーム
	 */
	public SimpleChooserDialog(Frame owner) {
		super(owner, true);

		init();
		initKey();
	}

	/**
	 * コンストラクタ
	 * 
	 * @param owner 親ダイアログ
	 */
	public SimpleChooserDialog(Dialog owner) {
		super(owner, true);
		setUndecorated(true);

		init();
		initKey();
	}

	private void init() {
		list.setModel(model);
		list.setVisibleRowCount(7);
		scroll.setViewportView(list);
		getContentPane().add(scroll);

		list.addMouseListener(this);
	}

	private void initKey() {
		ActionMap actionMap = list.getActionMap();
		actionMap.put(CHOICE_ACTION, new ChoiceAction());
		actionMap.put(CANCEL_ACTION, new CancelAction());

		InputMap imap = list.getInputMap();
		imap.put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_ENTER, 0), CHOICE_ACTION);
		imap.put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_ESCAPE, 0), CANCEL_ACTION);
	}

	/**
	 * 廃棄処理。
	 * リスナを外す
	 */
	public void dispose() {
		list.removeMouseListener(this);
	}

	/**
	 * 選択された要素を返す。
	 * もしもキャンセルされたならnullを返す。
	 * 
	 * @return Object
	 */
	public Object getChoosen() {
		return choosen;
	}

	public void setListData(Object[] data) {
		model.removeAllElements();
		for (int i = 0; i < data.length; i++) {
			model.addElement(data[i]);
		}
		list.setSelectedIndex(0);
	}

	public void setListData(java.util.List data) {
		model.removeAllElements();
		for (int i = 0; i < data.size(); i++) {
			model.add(0, data.get(i));
		}

		if (model.size() > 0) {
			list.setSelectedIndex(0);
		}
	}
	
	public void addData(Object data) {
		model.add(0, data);
	}
	
	public void setSelectedData(Object value) {
		list.setSelectedValue(value, true);
	}

	public void setVisible(boolean bool) {
		if (bool) {
			choosen = null;
		}

		super.setVisible(bool);
	}

    /**
     * レンダラコンポーネントをセットする
     */
    public void setCellRenderer(ListCellRenderer renderer) {
    	list.setCellRenderer(renderer);
    }

	/*------------------------ MouseListener ----------------------*/
	/**
	 * ダブルクリックの処理
	 */
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
			choosen = (String) list.getSelectedValue();
			setVisible(false);
		}
	}

	/**
	 * Invoked when a mouse button has been pressed on a component.
	 */
	public void mousePressed(MouseEvent e) {
	}

	/**
	 * Invoked when a mouse button has been released on a component.
	 */
	public void mouseReleased(MouseEvent e) {
	}

	/**
	 * Invoked when the mouse enters a component.
	 */
	public void mouseEntered(MouseEvent e) {
	}

	/**
	 * Invoked when the mouse exits a component.
	 */
	public void mouseExited(MouseEvent e) {
	}

	/*------------------------ FocusListener ----------------------*/
	/**
	 * 選択し、ダイアログを閉じるActionクラス
	 */
	class ChoiceAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			choosen = list.getSelectedValue();
			setVisible(false);
		}
	}

	/**
	 * キャンセルし、ダイアログを閉じるActionクラス
	 */
	class CancelAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			choosen = null;
			setVisible(false);
		}
	}
}
