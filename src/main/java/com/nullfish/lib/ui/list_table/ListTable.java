/*
 * Created on 2004/05/07
 *
 */
package com.nullfish.lib.ui.list_table;

import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.ListModel;

/**
 * @author shunji
 * 
 */
public class ListTable extends JTable {
	private ListTableModel listTableModel;

	/**
	 * ロウ選択モデル
	 */
	private ListTableRowSelectionModel rowSelectionModel = new ListTableRowSelectionModel(
			this);

	/**
	 * カラム選択モデル
	 */
	private ListTableColumnSelectionModel columnSelectionModel = new ListTableColumnSelectionModel(
			this);

	private int columnCount;
	
	private boolean autoAdjustColumnCount = false;
	
	private int preferredColumnWidth;
	
	/**
	 * コンストラクタ
	 *  
	 */
	public ListTable() {
		super();
		listTableModel = new ListTableModel();
		setModel(listTableModel);
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				revalidateRowCount();
				revalidateColumnCount();
			}
		});

		setEnabled(false);
		setSelectionModel(rowSelectionModel);
		getColumnModel().setSelectionModel(columnSelectionModel);

		setColumnSelectionAllowed(true);
		setRowSelectionAllowed(true);

		addMouseListener(new SelectionChanger(this));
	}

	/**
	 * リストモデルを取得する。
	 * 
	 * @return
	 */
	public ListModel getListModel() {
		return listTableModel.getListModel();
	}

	/**
	 * リストモデルをセットする。
	 * 
	 * @param listModel
	 */
	public void setListModel(ListModel listModel) {
		listTableModel.setListModel(listModel);
	}

	public void setListCursorModel(ListCursorModel cursorModel) {
		listTableModel.setListCursorModel(cursorModel);
		cursorModel.addListCursorModelListener(rowSelectionModel);
		cursorModel.addListCursorModelListener(columnSelectionModel);
	}

	/**
	 * カラム数をセットする。
	 * 
	 * @param columnCount
	 */
	public void setColumnCount(int columnCount) {
		this.columnCount = columnCount;
		
		if(autoAdjustColumnCount) {
			return;
		}
		
		listTableModel.setColumnCount(columnCount);
		rowSelectionModel.cursorChanged(listTableModel.getListCursorModel());
		columnSelectionModel.cursorChanged(listTableModel.getListCursorModel());
	}
	
	/**
	 * 通常モード（自動カラム数変更しない）でのカラム数を取得する。
	 */
	public int getPreferredColumnCount() {
		return columnCount;
	}

	public ListTableModel getListTableModel() {
		return listTableModel;
	}

	/**
	 * 現在のコンポーネントのサイズからロウ数を設定しなおす。
	 *  
	 */
	private void revalidateRowCount() {
		if (listTableModel != null) {
			listTableModel.setRowCount(getHeight() / (getRowHeight()));

			ListCursorModel cursorModel = listTableModel.getListCursorModel();
			rowSelectionModel.cursorChanged(cursorModel);
			columnSelectionModel.cursorChanged(cursorModel);
		}
	}

	private void revalidateColumnCount() {
		if(!autoAdjustColumnCount) {
			setColumnCount(columnCount);
			return;
		}
		
		if(preferredColumnWidth <= 0) {
			return;
		}
		
		int tempColumnCount = getWidth() / preferredColumnWidth;
		listTableModel.setColumnCount(tempColumnCount);
		rowSelectionModel.cursorChanged(listTableModel.getListCursorModel());
		columnSelectionModel.cursorChanged(listTableModel.getListCursorModel());
	}
	
	/**
	 * ロウの高さをセットする。
	 */
	public void setRowHeight(int rowHeight) {
		super.setRowHeight(rowHeight);
		revalidateRowCount();
	}

	/**
	 * リスト中の指定インデックス番目のセルを選択する。
	 * 
	 * @param index
	 */
	public void setSelectedIndex(int index) {
		listTableModel.getListCursorModel().setSelectedIndex(index);
	}

	/**
	 * リスト中の選択インデックスを取得する。
	 * 
	 * @return
	 */
	public int getSelectedIndex() {
		return listTableModel.getListCursorModel().getSelectedIndex();
	}

	/**
	 * マウスでクリックされた位置を元に、カーソルのインデックスを変更する。
	 * 
	 * @param point
	 */
	private void selectIndexFromPoint(Point point) {
		int column = columnAtPoint(point);
		int row = rowAtPoint(point);
		int page = listTableModel.getPage();
		int rowCount = getRowCount();
		int columnCount = getColumnCount();

		int index = (page * rowCount * columnCount) + (column * rowCount) + row;

		listTableModel.getListCursorModel().setSelectedIndex(index);
	}

	public void clearSelection() {
		//	何もしない。
	}

	private static class SelectionChanger extends MouseAdapter {
		ListTable table;

		public SelectionChanger(ListTable table) {
			this.table = table;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
		 */
		public void mousePressed(MouseEvent e) {
			table.selectIndexFromPoint(e.getPoint());
		}
	}

	public void setAutoAdjustColumnCount(boolean autoAdjustColumnCount) {
		this.autoAdjustColumnCount = autoAdjustColumnCount;
		revalidateColumnCount();
	}

	public void setPreferredColumnWidth(int preferredColumnWidth) {
		this.preferredColumnWidth = preferredColumnWidth;
		revalidateColumnCount();
	}
}
