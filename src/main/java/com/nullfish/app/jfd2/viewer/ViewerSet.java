package com.nullfish.app.jfd2.viewer;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author shunji
 *
 */
public class ViewerSet {
	/**
	 * 位置とビューアのマップ
	 */
	private Map positionViewerMap = new HashMap();
	
	/**
	 * ビューアを取得する。
	 * 
	 * @param position	位置
	 * @return
	 */
	public FileViewer getViewer(FileViewerPosition position) {
		return (FileViewer)positionViewerMap.get(position);
	}
	
	/**
	 * ビューアをセットする。
	 * 
	 * @param position
	 * @param viewer
	 */
	public void setViewer(FileViewerPosition position, FileViewer viewer) {
		positionViewerMap.put(position, viewer);
	}
	
	public FileViewer getTopViewer() {
		FileViewerPosition[] priority = FileViewerPosition.getPriority();
		for(int i=0; i<priority.length; i++) {  
			FileViewer viewer = getViewer(priority[i]);
			if(viewer != null) {
				return viewer;
			}
		}

		return null;
	}
	
	public boolean contains(FileViewer viewer) {
		return positionViewerMap.containsValue(viewer);
	}
	
	public void removeViewer(FileViewer viewer) {
		FileViewerPosition[] priority = FileViewerPosition.getPriority();
		for(int i=0; i<priority.length; i++) {  
			FileViewer v = getViewer(priority[i]);
			if(v == viewer) {
				positionViewerMap.remove(priority[i]);
			}
		}

	}
}
