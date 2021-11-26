package com.nullfish.lib.ui.image_panel;

/**
 * ImagePanelの表示内容が更新された際に、通知を受け取る
 * リスナインターフェイス。
 * 
 * @author shunji
 */
public interface ImagePanelListener {
	/**
	 * ImagePanelが更新された際に呼び出される。
	 * 
	 * @param c
	 */
	public void imageComponentUpdated(ImagePanel c);
}
