package com.nullfish.app.jfd2.viewer.graphicviewer;

/**
 * ImageComponentの表示内容が更新された際に、通知を受け取る
 * リスナインターフェイス。
 * 
 * @author shunji
 */
public interface ImageComponentListener {
	/**
	 * ImageComponentが更新された際に呼び出される。
	 * 
	 * @param c
	 */
	public void imageComponentUpdated(ImageComponent c);
}
