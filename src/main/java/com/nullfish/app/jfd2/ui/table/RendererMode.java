/*
 * Created on 2004/07/02
 *
 */
package com.nullfish.app.jfd2.ui.table;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * ファイル表示部の要素の可視、非可視状態や日付のフォーマットを定義するクラス。 TypeSafeEnumrationになっている。
 * 
 * @author shunji
 */
public class RendererMode {
	/**
	 * モード名称
	 */
	private String name;
	
	/**
	 * ファイルサイズ可視性
	 */
	private boolean sizeVisible;

	/**
	 * パーミッション可視性
	 */
	private boolean permissionVisible;

	/**
	 * 日付可視性
	 */
	private boolean dateVisible;

	/**
	 * 日付フォーマット
	 */
	private DateFormat dateFormat;

	public static final RendererMode TYPE_1 = new RendererMode("1", true, true,
			true, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss "));

	public static final RendererMode TYPE_2 = new RendererMode("2", true, false,
			true, new SimpleDateFormat("yy-MM-dd HH:mm "));

	public static final RendererMode TYPE_3 = new RendererMode("3", true, false,
			false, null);

	public static final RendererMode TYPE_4 = new RendererMode("4", false, false,
			false, null);

	public static final RendererMode TYPE_1_ABS = new RendererMode("1A", true, true,
			true, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss "));

	public static final RendererMode TYPE_2_ABS = new RendererMode("2A", true, false,
			true, new SimpleDateFormat("yy-MM-dd HH:mm "));

	public static final RendererMode TYPE_3_ABS = new RendererMode("3A", true, false,
			false, null);

	public static final RendererMode TYPE_4_ABS = new RendererMode("4A", false, false,
			false, null);

	private static final RendererMode[] MODES = {
		TYPE_1,
		TYPE_2,
		TYPE_3,
		TYPE_4,
		TYPE_1_ABS,
		TYPE_2_ABS,
		TYPE_3_ABS,
		TYPE_4_ABS,
	};
		
	private RendererMode(String name, boolean sizeVisible, boolean permissionVisible,
			boolean dateVisible, DateFormat dateFormat) {
		this.name = name;
		this.sizeVisible = sizeVisible;
		this.permissionVisible = permissionVisible;
		this.dateVisible = dateVisible;
		this.dateFormat = dateFormat;
	}

	public static RendererMode fromName(String name) {
		for(int i=0; i<MODES.length; i++) {
			if(MODES[i].getName().equals(name)) {
				return MODES[i];
			}
		}
		
		return MODES[0];
	}
	
	public String getName() {
		return name;
	}
	
	/**
	 * @return Returns the dateFormat.
	 */
	public DateFormat getDateFormat() {
		return dateFormat;
	}

	/**
	 * @return Returns the dateVisible.
	 */
	public boolean isDateVisible() {
		return dateVisible;
	}

	/**
	 * @return Returns the sizeVisible.
	 */
	public boolean isSizeVisible() {
		return sizeVisible;
	}
	
	/**
	 * @return Returns the permissionVisible.
	 */
	public boolean isPermissionVisible() {
		return permissionVisible;
	}
	
	/**
	 * 指定のモードの次のモードを求める。
	 * 
	 * @param mode
	 * @return
	 */
	public static RendererMode getNextMode(RendererMode mode) {
		for(int i=0; i<MODES.length; i++) {
			if(mode == MODES[i]) {
				return MODES[(i + 1) % MODES.length];
			}
		}
		
		//	起こりえない
		return TYPE_1;
	}
}
