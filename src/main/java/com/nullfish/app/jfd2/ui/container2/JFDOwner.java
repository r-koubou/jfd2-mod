/*
 * Created on 2004/09/16
 *
 */
package com.nullfish.app.jfd2.ui.container2;


import com.nullfish.app.jfd2.JFDComponent;
import com.nullfish.app.jfd2.command.owner.OwnerCommandManager;
import com.nullfish.lib.vfs.VFile;

/**
 * @author shunji
 *
 */
public interface JFDOwner {
	/**
	 * 現在アクティブなjFDを取得する。
	 * @return
	 */
	public JFDComponent getActiveComponent();
	
	/**
	 * 現在アクティブなコンポーネントを設定する。
	 */
	public void setActiveComponent(JFDComponent component);

	/**
	 * アクティブなコンポーネントが変更された際に呼び出される。
	 */
	public void componentActivated(JFDComponent component);

	/**
	 * 保持しているコンポーネントの数を返す。
	 * @return
	 */
	public int getCount();
	
	/**
	 * 新しいコンポーネントを追加する。
	 * @param constants
	 */
	public void addComponent(JFDComponent component, ContainerPosition constants, TitleUpdater titleUpdater);
	
	/**
	 * コンポーネントを削除する。
	 * @param component
	 */
	public void removeComponent(JFDComponent component);
	
	/**
	 * 設定ファイルのディレクトリを取得する
	 * @return
	 */
	public VFile getConfigDirectory();
	
	/**
	 * コンポーネントの位置を求める。
	 * 
	 * @param component
	 * @return
	 */
	public ContainerPosition getComponentPosition(JFDComponent component);
	
	/**
	 * 廃棄処理
	 *
	 */
	public void dispose();
	
	/**
	 * コマンドマネージャを取得する。
	 * 
	 * @return
	 */
	public OwnerCommandManager getCommandManager();
	
	/**
	 * 指定位置のコンポーネントを取得する。
	 * 複数ある場合は最前面のコンポーネントを取得する。
	 * 
	 * @param comp
	 * @return
	 */
	public JFDComponent getComponent(ContainerPosition position);
	
	/**
	 * 最前面に移動する。
	 *
	 */
	public void toFront();
}
