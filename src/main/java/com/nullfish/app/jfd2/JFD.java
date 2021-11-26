/*
 * Created on 2004/05/31
 *
 */
package com.nullfish.app.jfd2;

import com.nullfish.app.jfd2.aliase.AliaseManager;
import com.nullfish.app.jfd2.command.Command;
import com.nullfish.app.jfd2.command.CommandManager;
import com.nullfish.app.jfd2.command.progress.ProgressViewer;
import com.nullfish.app.jfd2.config.Configuration;
import com.nullfish.app.jfd2.dialog.JFDDialog;
import com.nullfish.app.jfd2.ui.table.RendererMode;
import com.nullfish.app.jfd2.util.IncrementalSearcher;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;

/**
 * @author shunji
 *
 */
public interface JFD extends JFDComponent {
	/**
	 * 共通設定ファイル名
	 */
	public static final String COMMON_PARAM_FILE = "common_config.xml";
	/**
	 * 個別設定ファイル名
	 */
	public static final String LOCAL_PARAM_FILE = "local_config.xml";

	/**
	 * モデルを取得する。
	 * @return
	 */
	public JFDModel getModel();

	/**
	 * 表示カラム数を設定する。
	 * @param columnCount
	 */
	public void setColumnCount(int columnCount);
	
	/**
	 * 表示カラム数を取得する。
	 * @return
	 */
	public int getColumnCount();
	
	/**
	 * 表示行数を取得する。
	 * @return
	 */
	public int getRowCount();
	
	/**
	 * ページ番号を取得する。
	 * @return
	 */
	public int getPage();
	
	/**
	 * 操作をロック、解除する。
	 * @param locked	trueならロック
	 */
	public void setLocked(boolean locked);
	
	/**
	 * インクリメンタルサーチモードを設定する。
	 * @param bool	trueならインクリメンタルサーチ
	 */
	public void setIncrementalSearchMode(boolean bool);
	
	/**
	 * コマンドマネージャを取得する。
	 *
	 */
	public CommandManager getCommandManager();
	
	/**
	 * 共通設定を取得する。
	 * @return
	 */
	public Configuration getCommonConfiguration();

	/**
	 * 個別設定を取得する。
	 * @return
	 */
	public Configuration getLocalConfiguration();

	/**
	 * 非保存設定を取得する。
	 * @return
	 */
	public Configuration getTemporaryConfiguration();

	/**
	 * 現在のファイル表示モードを設定する。
	 * 
	 */
	public void setRendererMode(RendererMode mode);

	/**
	 * 空のダイアログを生成する。
	 * @return
	 */
	public JFDDialog createDialog();
	
	/**
	 * 下に表示するメッセージをセットする。
	 * @param message
	 */
	public void setMessage(String message);
	
	/**
	 * 下に表示するメッセージを制限時間付きでセットする。
	 * @param message
	 * @param time
	 */
	public void setMessage(String message, long time);
	
	/**
	 * 主操作を設定する。
	 * @param command
	 */
	public void setPrimaryCommand(Command command);
	
	/**
	 * 主操作を取得する。
	 */
	public Command getPrimaryCommand();
	
	/**
	 * 経過表示ビューアを取得する。
	 * @return
	 */
	public ProgressViewer getProgressViewer();
	
	/**
	 * 初期化処理
	 * @param baseDir
	 * @throws VFSException
	 */
	public void init(VFile baseDir) throws VFSException;

	/**
	 * 設定保存
	 * @param baseDir
	 * @throws VFSException
	 */
	public void save(VFile baseDir) throws VFSException;
	
	public void dispose();
	
	public AliaseManager getAliaseManager();
	
	public void setShowsRelativePath(boolean bool);
	
	public boolean showsRelativePath();
	
	public IncrementalSearcher getIncrementalSearcher();
	
}
