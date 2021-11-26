package com.nullfish.app.jfd2.viewer;

import java.util.HashMap;
import java.util.Map;

/**
 * 基準となるオブジェクト（ファイラー、オーナー、システム等）に対しての
 * ファイルビューアのポジション（前、後ろ等）をあらわすクラス。
 * 
 * @author shunji
 *
 */
public class FileViewerPosition {
	private static final Map instanceMap = new HashMap();
	
	/**
	 * 前
	 */
	public static final FileViewerPosition FORWARD = new FileViewerPosition("forward");
	
	/**
	 * 後
	 */
	public static final FileViewerPosition BACKGROUND = new FileViewerPosition("bg");
	
	private static final FileViewerPosition[] priority = {
		FORWARD,
		BACKGROUND
	};
		
	private FileViewerPosition(String position) {
		instanceMap.put(position, this);
	}
	

	/**
	 * 優先順位の配列を取得する。
	 * @return
	 */
	public static FileViewerPosition[] getPriority() {
		return (FileViewerPosition[])priority.clone();
	}
	
	public static FileViewerPosition getInstance(String name) {
		return (FileViewerPosition)instanceMap.get(name);
	}
}
