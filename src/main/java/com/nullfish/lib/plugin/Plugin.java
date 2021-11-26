/*
 * Created on 2005/01/25
 *
 */
package com.nullfish.lib.plugin;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.ui.container2.JFDOwner;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;

/**
 * @author shunji
 *
 */
public interface Plugin {
	/**
	 * システム開始時に呼び出される。
	 * 
	 * @throws VFSException
	 */
	public void systemStarted();
	
	/**
	 * システム終了時に呼び出される。
	 * 
	 * @throws VFSException
	 */
	public void systemExited();
	
	/**
	 * 設定変更時に呼び出される。
	 * 
	 * @throws VFSException
	 */
	public void configurationChanged();
	
	/**
	 * JFD2インスタンスが生成された際に呼び出される。
	 * 
	 * @param jfd
	 */
	public void jfdCreated(JFD jfd);
	
	/**
	 * JFD2インスタンスが初期化された際に呼び出される。
	 * 
	 * @param jfd
	 */
	public void jfdInited(JFD jfd, VFile baseDir);
	
	/**
	 * JFD2インスタンスが廃棄された際に呼び出される。
	 * 
	 * @param jfd
	 */
	public void jfdDisposed(JFD jfd);
	
	/**
	 * JFDオーナーインスタンスが生成された際に呼び出される。
	 * 
	 * @param jfd
	 */
	public void jfdOwnerCreated(JFDOwner owner);
	
	/**
	 * JFD2オーナーインスタンスが廃棄された際に呼び出される。
	 * 
	 * @param jfd
	 */
	public void jfdOwnerDisposed(JFDOwner owner);
	
	/**
	 * プラグイン名称を取得する。
	 * @return
	 */
	public String getName();
	
	/**
	 * プラグインバージョンを取得する。
	 * @return
	 */
	public double getVersion();
	
	/**
	 * 説明文を取得する。
	 * @return
	 */
	public String getDescription();
	
	/**
	 * パラメータを取得する。
	 * @param key
	 * @return
	 */
	public Object getParam(String key);
	
	/**
	 * プラグイン内のリソースを取得する。
	 * @param path
	 * @return
	 */
	public VFile getResource(String path);
	
	/**
	 * プラグインのディレクトリを取得する。
	 * @return
	 */
	public VFile getBaseDir();
	
	/**
	 * プラグインを停止する。
	 *
	 */
	public void close();
}
