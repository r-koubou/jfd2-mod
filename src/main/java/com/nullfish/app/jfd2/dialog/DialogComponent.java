/*
 * Created on 2004/08/03
 *
 */
package com.nullfish.app.jfd2.dialog;

/**
 * ダイアログに表示されるコンポーネントのインターフェイス。
 * メソッドの実装は必ずスレッドセーフとして実装される。
 * 
 * @author shunji
 */
public interface DialogComponent {
	/**
	 * 決定時（エンターキー押下時）にダイアログを閉じるかどうか設定する。
	 * @param bool
	 */
	public void setClosesOnDecision(boolean bool);

	/**
	 * 設定情報を設定する。
	 * @param config
	 */
	public void setConfigueationInfo(ConfigurationInfo config);
	
	/**
	 * 決定を設定に反映させる。
	 *
	 */
	public void applyConfiguration();
}
