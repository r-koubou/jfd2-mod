/*
 * Created on 2004/05/25
 *
 */
package com.nullfish.app.jfd2.command;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingUtilities;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.dialog.DialogUtilities;
import com.nullfish.app.jfd2.dialog.JFDDialog;
import com.nullfish.app.jfd2.resource.JFDResource;
import com.nullfish.lib.vfs.FileSystem;
import com.nullfish.lib.vfs.Manipulation;
import com.nullfish.lib.vfs.VFS;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.ManipulationStoppedException;
import com.nullfish.lib.vfs.exception.VFSException;
import com.nullfish.lib.vfs.manipulation.abst.AbstractManipulation;

/**
 * @author shunji
 * 
 */
public abstract class Command extends AbstractManipulation {
	/**
	 * jFDのモデル
	 */
	private JFD jfd;

	/**
	 * 非同期実行フラグ
	 */
	private boolean asynch = false;

	/**
	 * 実行中画面ロックフラグ
	 */
	private boolean locks = true;

	/**
	 * 名称
	 */
	private String name;

	/**
	 * 主操作として画面にセットされ、経過表示、中止操作受付を行なうかのフラグ
	 */
	private boolean primary = false;

	/**
	 * 中止時にダイアログ表示を行なうかのフラグ
	 */
	private boolean showsStopDialog = false;
	
	/**
	 * パラメータのマップ
	 */
	private Map paramsMap;

	/**
	 * コマンドタグ名
	 */
	public static final String COMMAND_TAG = "command";

	/**
	 * 名称属性
	 */
	public static final String ATTR_NAME = "name";

	/**
	 * キャッシュ使用属性
	 */
	public static final String ATTR_CACHE = "cache";

	/**
	 * 名称属性
	 */
	public static final String ATTR_ASYNCH = "asynch";

	/**
	 * 実行中画面ロック属性
	 */
	public static final String ATTR_LOCKS = "locks";

	/**
	 * 主操作属性
	 */
	public static final String ATTR_PRIMARY = "primary";

	/**
	 * 中止時ダイアログ表示属性
	 */
	public static final String ATTR_SHOWS_STOP_DIALOG = "show_stopped";

	/**
	 * パラメータタグ
	 */
	public static final String PARAM_TAG = "param";

	/**
	 * 全子操作
	 */
	private Manipulation[] manipulations;

	/**
	 * 開始時のカレントディレクトリ
	 */
	protected VFile currentDir;
	
	/**
	 * コンストラクタ
	 * 
	 * @param name
	 */
	public Command() {
		super(null);
	}

	/**
	 * Manipulation#start()のオーバーライド。
	 * 
	 */
	public void start() throws VFSException {
		try {
			currentDir = getJFD().getModel().getCurrentDirectory();
			
			registerUserToFileSystem();
			super.start();
		} catch (ManipulationStoppedException e) {
			if(showsStopDialog) {
				DialogUtilities.showMessageDialog(jfd, e.getErrorMessage(), "jFD2");
			}
			throw e;
		} catch (VFSException e) {
			DialogUtilities.showMessageDialog(jfd, e.getErrorMessage(), "jFD2");
			throw e;
		} catch (Exception e) {
			//	本来発生してはいけない
			JFDDialog dialog = null;
			try {
				dialog = jfd.createDialog();
				dialog.setTitle(JFDResource.LABELS.getString("title_error"));
				dialog.addMessage(JFDResource.MESSAGES.getString("unexpected_exception"));
				StringWriter writer = new StringWriter();
				e.printStackTrace(new PrintWriter(writer));
				writer.flush();
				writer.close();
				dialog.addTextArea("stackTrace", writer.toString(), false);
				dialog.addButton("ok", JFDResource.LABELS.getString("ok"), 'o', true);
				dialog.pack();
				dialog.setVisible(true);
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				dialog.dispose();
			}
		} finally {
			unregisterUserToFileSystem();
			
			if(closesUnusingFileSystem()) {
				VFS.getInstance(jfd).closeUnusedFileSystem();
			}
		}
	}

	/**
	 * ファイルシステムにこの操作を利用者として登録する。
	 *
	 */
	private void registerUserToFileSystem() {
		if(currentDir != null) {
			currentDir.getFileSystem().registerUser(this);
		}
		
		FileSystem[] usingFileSystems = getUsingFileSystem();
		for (int i = 0; usingFileSystems != null && i < usingFileSystems.length; i++) {
			usingFileSystems[i].registerUser(this);
		}
	}

	/**
	 * ファイルシステムからこの操作を利用者として削除する。
	 * 
	 * @throws VFSException
	 *
	 */
	private void unregisterUserToFileSystem() throws VFSException {
		if(currentDir != null) {
			currentDir.getFileSystem().removeUser(this);
		}

		FileSystem[] usingFileSystems = getUsingFileSystem();
		if(usingFileSystems == null || usingFileSystems.length == 0) {
			return;
		}
		
		for (int i = 0; usingFileSystems != null && i < usingFileSystems.length; i++) {
			usingFileSystems[i].removeUser(this);
		}
	}

	/**
	 * 非同期実行を行うならtrueを返す｡
	 * 
	 * @return
	 */
	public boolean isAsynch() {
		return asynch;
	}

	/**
	 * 非同期実行を行うか設定する。
	 * 
	 * @param asynch
	 */
	public void setAsynch(boolean asynch) {
		this.asynch = asynch;
	}

	/**
	 * 実行中、画面をロックするならtrueを返す｡
	 * 
	 * @return
	 */
	public boolean isLocks() {
		return locks;
	}

	/**
	 * 実行中、画面をロックするか設定する。
	 * 
	 * @param locks
	 */
	public void setLocks(boolean locks) {
		this.locks = locks;
	}

	/**
	 * 操作の名称を返す｡
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return Returns the jfdModel.
	 */
	public JFD getJFD() {
		return jfd;
	}

	/**
	 * @param jfdModel
	 *            The jfdModel to set.
	 */
	public void setJFD(JFD jfd) {
		this.jfd = jfd;
	}

	public Object getParameter(String name) {
		if (paramsMap == null) {
			return null;
		}

		return paramsMap.get(name);
	}

	public void setParameter(String name, Object value) {
		if (paramsMap == null) {
			paramsMap = new HashMap();
		}
		paramsMap.put(name, value);
	}

	/**
	 * 
	 * @param paramsMap
	 */
	public void setParamsMap(Map paramsMap) {
		this.paramsMap = paramsMap;
	}

	/**
	 * 作業経過メッセージを取得する。
	 * 
	 * @return
	 */
	public String getProgressMessage() {
		Manipulation currentManipulation = getCurrentManipulation();
		
		if(currentManipulation == null) {
			return "";
		}
		
		if (currentManipulation != this) {
			return currentManipulation.getProgressMessage();
		}

		return "";
	}

	/**
	 * 子操作を設定する。
	 * 
	 * @param children
	 */
	protected void setChildManipulations(Manipulation[] children) {
		manipulations = children;
	}

	public long getProgressMin() {
		if (manipulations == null) {
			return PROGRESS_INDETERMINED;
		}

		long sum = 0;
		for (int i = 0; i < manipulations.length; i++) {
			long p = manipulations[i].getProgressMin();
			if (p == PROGRESS_INDETERMINED) {
				return PROGRESS_INDETERMINED;
			}
			sum += p;
		}

		return sum;
	}

	public long getProgressMax() {
		if (manipulations == null) {
			return PROGRESS_INDETERMINED;
		}

		long sum = 0;
		for (int i = 0; i < manipulations.length; i++) {
			long p = manipulations[i].getProgressMax();
			if (p == PROGRESS_INDETERMINED) {
				return PROGRESS_INDETERMINED;
			}
			sum += p;
		}

		return sum;
	}

	public long getProgress() {
		if (manipulations == null) {
			return PROGRESS_INDETERMINED;
		}

		long sum = 0;
		for (int i = 0; i < manipulations.length; i++) {
			long p = manipulations[i].getProgress();
			if (p == PROGRESS_INDETERMINED) {
				return PROGRESS_INDETERMINED;
			}
			sum += p;
		}

		return sum;
	}

	/**
	 * @return Returns the primary.
	 */
	public boolean isPrimary() {
		return primary;
	}

	/**
	 * @param primary
	 *            The primary to set.
	 */
	public void setPrimary(boolean primary) {
		this.primary = primary;
	}

	/**
	 * 経過表示を行なう。 スレッドセーフ
	 */
	public void showProgress() {
		showProgress(0);
	}

	/**
	 * 経過表示を行なう。 スレッドセーフ
	 */
	public void showProgress(final int delay) {
		final Runnable opener = new Runnable() {
			public void run() {
				if (!Command.this.isFinished()) {
					getJFD().getProgressViewer().addCommand(Command.this);
				}
			}
		};

		Thread waiter = new Thread() {
			public void run() {
				try {
					if (delay > 0) {
						Thread.sleep(delay);
					}
				} catch (InterruptedException e) {
				}
				SwingUtilities.invokeLater(opener);
			}
		};

		waiter.start();
	}

	/**
	 * 使用するファイルシステムを返す。
	 * もしも戻り値がnull以外かつ要素が一つ以上の場合、このコマンドの実行後
	 * ファイルシステムを閉じる。
	 * もしもカレントディレクトリのファイルシステム以外を使う場合、オーバーライドする。
	 * 
	 * @return
	 */
	public FileSystem[] getUsingFileSystem() {
		return null;
	}
	
	/**
	 * 実行後不要なファイルシステムを閉じるか判定する。
	 * 
	 * @return	もしもtrueならstartメソッド実行後、不要なファイルシステムを閉じる。
	 */
	public abstract boolean closesUnusingFileSystem();

	public boolean isShowsStopDialog() {
		return showsStopDialog;
	}
	

	public void setShowsStopDialog(boolean showsStopDialog) {
		this.showsStopDialog = showsStopDialog;
	}
	
}
