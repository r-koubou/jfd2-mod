/*
 * Created on 2004/12/30
 *
 */
package com.nullfish.lib.ui.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;

/**
 * 同じサイズで左右に分割するレイアウト。
 * ただし片方しか設定されてない、もしくは片方不可視の場合は一つを全体に表示する。
 * 
 * @author shunji
 */
public class EqualSplitLayout implements LayoutManager2 {
	private Component firstComponent;

	private Component secondComponent;

	public static final String FIRST = "first";

	public static final String SECOND = "second";

	private boolean horizontal = true;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.LayoutManager2#addLayoutComponent(java.awt.Component,
	 *      java.lang.Object)
	 */
	public void addLayoutComponent(Component comp, Object constraints) {
		if (FIRST.equals(constraints)) {
			firstComponent = comp;
		} else {
			secondComponent = comp;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.LayoutManager2#maximumLayoutSize(java.awt.Container)
	 */
	public Dimension maximumLayoutSize(Container target) {
		return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.LayoutManager2#getLayoutAlignmentX(java.awt.Container)
	 */
	public float getLayoutAlignmentX(Container target) {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.LayoutManager2#getLayoutAlignmentY(java.awt.Container)
	 */
	public float getLayoutAlignmentY(Container target) {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.LayoutManager2#invalidateLayout(java.awt.Container)
	 */
	public void invalidateLayout(Container target) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.LayoutManager#addLayoutComponent(java.lang.String,
	 *      java.awt.Component)
	 */
	public void addLayoutComponent(String name, Component comp) {
		addLayoutComponent(comp, name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.LayoutManager#removeLayoutComponent(java.awt.Component)
	 */
	public void removeLayoutComponent(Component comp) {
		if (comp == firstComponent) {
			firstComponent = null;
		}

		if (comp == secondComponent) {
			secondComponent = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.LayoutManager#preferredLayoutSize(java.awt.Container)
	 */
	public Dimension preferredLayoutSize(Container parent) {
		Dimension first = firstComponent != null ? firstComponent
				.getPreferredSize() : new Dimension(0, 0);
		Dimension second = secondComponent != null ? secondComponent
				.getPreferredSize() : new Dimension(0, 0);

		if(horizontal) {
			return new Dimension(
					first.width + second.width, 
					first.height > second.height ? first.height : second.height);
		} else {
			return new Dimension(
					first.width > second.width ? first.width : second.width, 
					first.height + second.height);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.LayoutManager#minimumLayoutSize(java.awt.Container)
	 */
	public Dimension minimumLayoutSize(Container parent) {
		Dimension first = firstComponent != null ? firstComponent
				.getPreferredSize() : new Dimension(0, 0);
		Dimension second = secondComponent != null ? secondComponent
				.getPreferredSize() : new Dimension(0, 0);

		return new Dimension(
				first.width > second.width ? first.width : second.width, 
				first.height > second.height ? first.height : second.height);
	}

	/**
	 * 分割方向を指定する。
	 * 
	 * @param horizontal	trueなら水平分割
	 */
	public void setHorizontal(boolean horizontal) {
		this.horizontal = horizontal;
	}
	
	/**
	 * 水平分割ならtrueを返す。
	 * @return
	 */
	public boolean isHorizontal() {
		return horizontal;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.LayoutManager#layoutContainer(java.awt.Container)
	 */
	public void layoutContainer(Container parent) {
		Dimension size = parent.getSize();

		if(firstComponent != null && firstComponent.isVisible()
			&& secondComponent != null && secondComponent.isVisible()) {
			if(horizontal) {
				firstComponent.setBounds(0, 0, size.width / 2, size.height);
				secondComponent.setBounds(size.width / 2, 0, size.width / 2, size.height);
			} else {
				firstComponent.setBounds(0, 0, size.width, size.height / 2);
				secondComponent.setBounds(0, size.height / 2, size.width, size.height / 2);
			}
		} else if(firstComponent != null && firstComponent.isVisible()
				&& (secondComponent == null || !secondComponent.isVisible())) {
			firstComponent.setBounds(0, 0, size.width , size.height);
		} else if((firstComponent == null || !firstComponent.isVisible())
				&& secondComponent != null && secondComponent.isVisible()) {
			secondComponent.setBounds(0, 0, size.width, size.height);
		}
	}
}
