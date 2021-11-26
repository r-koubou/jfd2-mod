/*
 * Created on 2005/01/24
 *
 */
package com.nullfish.app.jfd2.viewer;

import org.jdom.Element;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.lib.vfs.VFile;

/**
 * ファイルビューアの生成を管理するクラス。
 * 
 * @author shunji
 */
public interface FileViewerFactory {
	/**
	 * JFD2インスタンスに対応したファイルビューアを取得する。
	 * @param view
	 * @return
	 */
	public FileViewer getFileViewer(JFD view);

	/**
	 * パラメータをセットする。
	 * 
	 * @param name
	 * @param value
	 */
	public void setParam(String name, Object value);

	/**
	 * オープン、クローズ時の振る舞いクラスを返す。
	 * @return
	 */
	public ViewerController getController();
	
	/**
	 * XMLノードから初期化する。
	 * @param node
	 */
	public void init(VFile baseDir, Element node);
	
	/**
	 * サポートされている拡張子を取得する。
	 * @return
	 */
	public String[] getSupportedExtensions();
	
	/**
	 * クラスローダーを設定する
	 * @param loader
	 */
	public void setLoader(ClassLoader loader);
}
