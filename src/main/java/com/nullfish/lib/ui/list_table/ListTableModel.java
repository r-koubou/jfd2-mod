/*
 * Created on 2004/05/07
 *
 */
package com.nullfish.lib.ui.list_table;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.table.AbstractTableModel;

/**
 * ListModelをデータとして持つテーブルモデルクラス。
 * 
 * @author shunji
 */
public class ListTableModel extends AbstractTableModel {
	/**
	 * カーソル位置保持クラス
	 */
	private ListCursorModel selectionHolder;

	/**
	 * ページ番号
	 *  
	 */
	private int page;

	/**
	 * カラム数
	 */
	private int columnCount = 1;

	/**
	 * ロウ数
	 */
	private int rowCount;

	/**
	 * リストモデル
	 */
	private ListModel listModel;

	/**
	 * ListTableModelListenerのリスト
	 */
	private List listeners = new ArrayList();

	/**
	 * デフォルトコンストラクタ
	 *  
	 */
	public ListTableModel() {
	}

	/**
	 * コンストラクタ
	 * 
	 * @param listModel
	 *            リストモデル
	 */
	public ListTableModel(ListModel listModel, ListCursorModel selectionHolder) {
		setListModel(listModel);
		setListCursorModel(selectionHolder);
	}

	/**
	 * カラム数をセットする。
	 * 
	 * @param columnCount
	 */
	public void setColumnCount(int columnCount) {
		this.columnCount = columnCount;
		recaliculatePage();
		fireTableStructureChanged();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return columnCount;
	}

	/**
	 * ロウ数をセットする。
	 * 
	 * @param rowCount
	 */
	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
		recaliculatePage();
		fireTableStructureChanged();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return rowCount;
	}

	public void setTableSize(int rowCount, int columnCount) {
		this.rowCount = rowCount;
		this.columnCount = columnCount;
		fireTableStructureChanged();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (listModel == null) {
			return null;
		}

		int listIndex = getListPosition(page, getRowCount(), getColumnCount(),
				rowIndex, columnIndex);

		if (listIndex < listModel.getSize()) {
			return listModel.getElementAt(listIndex);
		} else {
			return null;
		}
	}

	public void addListTableModelListener(ListTableModelListener listener) {
		listeners.add(listener);
	}

	public void removeListTableModelListener(ListTableModelListener listener) {
		listeners.remove(listener);
	}

	/**
	 * @return Returns the listModel.
	 */
	public ListModel getListModel() {
		return listModel;
	}

	/**
	 * @param listModel
	 *            The listModel to set.
	 */
	public void setListModel(ListModel listModel) {
		this.listModel = listModel;
		listModel.addListDataListener(new ListTableListDataListener(this));
		fireTableDataChanged();
	}

	/**
	 * カーソルモデルをセットする。
	 * 
	 * @param selectionHolder
	 */
	public void setListCursorModel(ListCursorModel selectionHolder) {
		this.selectionHolder = selectionHolder;
		selectionHolder
				.addListCursorModelListener(new ListTableListCursorModelListener(
						this));
	}

	/**
	 * カーソルモデルを取得する。
	 * 
	 * @return
	 */
	public ListCursorModel getListCursorModel() {
		return selectionHolder;
	}

	/**
	 * @return Returns the page.
	 */
	public int getPage() {
		return page;
	}

	/**
	 * ページ数を取得する。
	 * 
	 * @return
	 */
	public int getPageCount() {
		if (listModel == null || rowCount == 0 || columnCount == 0) {
			return 0;
		}

		return (listModel.getSize() - 1) / (getRowCount() * getColumnCount())
				+ 1;
	}

	/**
	 * リスト中、指定されたインデックスの要素が更新された際に呼び出す。
	 * @param index
	 */
	public void itemUpdated(Object item) {
		int i=0;
		boolean found = false;
		for(; i<listModel.getSize(); i++) {
			if(item.equals(listModel.getElementAt(i))) {
				found = true;
				break;
			}
		}
		
		if(!found) {
			return;
		}
		
		itemUpdated(i);
	}
	
	/**
	 * リスト中、指定されたインデックスの要素が更新された際に呼び出す。
	 * @param index
	 */
	public void itemUpdated(int index) {
		if(!isIndexVisible(index)) {
			return;
		}

		ListTablePosition position = listIndex2ListTablePosition(index, rowCount, columnCount);
		fireTableCellUpdated(position.getRow(), position.getColumn());
	}
	
	/**
	 * カーソル位置からページ番号を再計算し、変更があった場合は リスナに通知する。
	 */
	private void recaliculatePage() {
		int newPage;
		if (getRowCount() == 0 || getColumnCount() == 0) {
			newPage = 0;
		} else {
			newPage = selectionHolder.getSelectedIndex()
					/ (getRowCount() * getColumnCount());
		}

		if (page != newPage) {
			page = newPage;
			fireTableDataChanged();
			firePageChanged();
		}
	}

	private void firePageChanged() {
		for (int i = 0; i < listeners.size(); i++) {
			((ListTableModelListener) listeners.get(i)).pageChanged(this);
		}
	}

	/**
	 * 指定のセルのリスト中の順番を求める。
	 * 
	 * @param page
	 *            ページ番号
	 * @param rowCount
	 *            ロウ数
	 * @param columnCount
	 *            カラム数
	 * @param row
	 *            ロウ番号
	 * @param column
	 *            カラム番号
	 * @return リスト中の順番
	 */
	private static int getListPosition(int page, int rowCount, int columnCount,
			int row, int column) {
		return ((columnCount * rowCount) * page) + (column * rowCount) + row;
	}

	/**
	 * リスト中の番号からテーブルでの位置を求める。
	 * 
	 * @param listIndex
	 *            リスト中の番号
	 * @param rowCount
	 *            ロウ数
	 * @param columnCount
	 *            カラム数
	 * @return 位置
	 */
	private static ListTablePosition listIndex2ListTablePosition(int listIndex,
			int rowCount, int columnCount) {
		ListTablePosition position = new ListTablePosition();

		position.setPage(listIndex / (columnCount * rowCount));
		position.setRow(listIndex % rowCount);
		position.setColumn((listIndex / rowCount) % columnCount);

		return position;
	}

	/**
	 * 指定されたインデックスが現在表示されているか判定する。
	 * @param index
	 * @return
	 */
	private boolean isIndexVisible(int index) {
		int start = getListPosition(page, rowCount, columnCount, 0, 0); 
		int end = getListPosition(page, rowCount, columnCount, rowCount - 1, columnCount - 1);
		
		return start <= index && index <= end;
	}
	
	private static class ListTableListDataListener implements ListDataListener {
		ListTableModel tableModel;

		public ListTableListDataListener(ListTableModel tableModel) {
			this.tableModel = tableModel;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.event.ListDataListener#contentsChanged(javax.swing.event.ListDataEvent)
		 */
		public void contentsChanged(ListDataEvent e) {
			tableModel.fireTableDataChanged();
			tableModel.firePageChanged();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.event.ListDataListener#intervalAdded(javax.swing.event.ListDataEvent)
		 */
		public void intervalAdded(ListDataEvent e) {
			tableModel.fireTableDataChanged();
			tableModel.firePageChanged();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.event.ListDataListener#intervalRemoved(javax.swing.event.ListDataEvent)
		 */
		public void intervalRemoved(ListDataEvent e) {
			tableModel.fireTableDataChanged();
			tableModel.firePageChanged();
		}
	}

	private static class ListTableListCursorModelListener implements
			ListCursorModelListener {
		ListTableModel tableModel;

		public ListTableListCursorModelListener(ListTableModel tableModel) {
			this.tableModel = tableModel;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.nullfish.lib.ui.list_table.ListCursorModelListener#CursorChanged(com.nullfish.lib.ui.list_table.ListCursorModel,
		 *      int, int)
		 */
		public void cursorChanged(ListCursorModel model) {
			tableModel.recaliculatePage();
		}
	}
}
