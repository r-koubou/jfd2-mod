/*
 * Created on 2004/12/28
 *
 */
package com.nullfish.app.jfd2.viewer;

import java.util.HashMap;
import java.util.Map;

import com.nullfish.app.jfd2.JFDComponent;
import com.nullfish.app.jfd2.ui.container2.ContainerPosition;
import com.nullfish.app.jfd2.viewer.constraints.MainConstraints;
import com.nullfish.app.jfd2.viewer.constraints.NoneConstraints;
import com.nullfish.app.jfd2.viewer.constraints.OpponentConstraints;
import com.nullfish.app.jfd2.viewer.constraints.SameConstraints;
import com.nullfish.app.jfd2.viewer.constraints.SubConstraints;

/**
 * 
 * @author shunji
 *
 */
public abstract class FileViewerConstraints {
	/**
	 * 名称とインスタンスのマップ
	 */
	private static Map nameInstanceMap = new HashMap();
	
	public static FileViewerConstraints MAIN = new MainConstraints();
	public static FileViewerConstraints SUB = new SubConstraints();
	public static FileViewerConstraints OPPONENT = new OpponentConstraints();
	public static FileViewerConstraints SAME = new SameConstraints();
	public static FileViewerConstraints NONE = new NoneConstraints();

	/**
	 * コンストラクタ
	 * @param name
	 */
	public FileViewerConstraints(String name) {
		nameInstanceMap.put(name, this);
	}
	
	/**
	 * 名称からインスタンスを取得する。
	 * @param name
	 * @return
	 */
	public static FileViewerConstraints getInstance(String name) {
		return (FileViewerConstraints)nameInstanceMap.get(name);
	}
	
	/**
	 * 
	 * @param jfd
	 * @return
	 */
	public abstract ContainerPosition getPosition(JFDComponent component);
}
