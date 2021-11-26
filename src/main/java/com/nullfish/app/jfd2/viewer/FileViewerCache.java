package com.nullfish.app.jfd2.viewer;

import java.util.HashMap;
import java.util.Map;

import com.nullfish.app.jfd2.JFD;

/**
 * ファイルビューアのキャッシュクラス
 * 
 * @author shunji
 *
 */
public class FileViewerCache {
	/**
	 * ビューアのマップ
	 */
	private Map viewerMap = new HashMap();
	
	/**
	 * ファクトリに関連付けられたビューアのキャッシュを取得する。
	 * キャッシュされてない場合は新規に作成し、キャッシュしてから返す。
	 * 
	 * @param factory
	 * @param jfd
	 * @return
	 */
	public FileViewer getCachedViewer(FileViewerFactory factory, JFD jfd) {
		FileViewer rtn = (FileViewer)viewerMap.get(factory);
		if(rtn == null) {
			rtn = factory.getFileViewer(jfd);
			viewerMap.put(factory, rtn);
		}
		
		return rtn;
	}
	
	public void disposeAll() {
		Object[] keys = viewerMap.keySet().toArray();
		for(int i=0; i<keys.length; i++) {
			FileViewer viewer = (FileViewer)viewerMap.get(keys[i]);
			viewer.dispose();
		}
		
		viewerMap.clear();
	}
}
