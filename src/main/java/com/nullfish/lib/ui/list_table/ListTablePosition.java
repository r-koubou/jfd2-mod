/*
 * Created on 2004/05/07
 *
 */
package com.nullfish.lib.ui.list_table;

/**
 * リストテーブル中の位置を求める。
 * @author shunji
 */
public class ListTablePosition {
	/*
	 * ページ番号
	 */
	private int page;
	
	/*
	 * ロウ番号
	 */
	private int row;
	
	/*
	 * カラム番号
	 */
	private int column;
	
	/**
	 * @return Returns the column.
	 */
	public int getColumn() {
		return column;
	}
	
	/**
	 * @param column The column to set.
	 */
	public void setColumn(int column) {
		this.column = column;
	}
	
	/**
	 * @return Returns the page.
	 */
	public int getPage() {
		return page;
	}
	
	/**
	 * @param page The page to set.
	 */
	public void setPage(int page) {
		this.page = page;
	}
	
	/**
	 * @return Returns the row.
	 */
	public int getRow() {
		return row;
	}
	
	/**
	 * @param row The row to set.
	 */
	public void setRow(int row) {
		this.row = row;
	}
}
