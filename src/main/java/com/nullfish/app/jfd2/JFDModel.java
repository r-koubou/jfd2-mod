/*
 * Created on 2004/05/18
 *
 */
package com.nullfish.app.jfd2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import com.nullfish.app.jfd2.command.Command;
import com.nullfish.app.jfd2.command.JFDManipulationListener;
import com.nullfish.app.jfd2.comparator.JFDComparator;
import com.nullfish.app.jfd2.resource.JFDResource;
import com.nullfish.app.jfd2.util.FileHistory;
import com.nullfish.lib.vfs.FileListener;
import com.nullfish.lib.vfs.FileSystem;
import com.nullfish.lib.vfs.UpdateManager;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.ManipulationStoppedException;
import com.nullfish.lib.vfs.exception.VFSException;

/**
 * @author shunji
 * 
 */
public class JFDModel implements FileListener {

	/**
	 * 表示ファイルの配列。 カレントディレクトリ、親ディレクトリ、マウントポイントは 含まない。
	 */
	private FileMarker[] markedFiles;

	private boolean autoNotify = true;

	private boolean hasChanged = false;

	/**
	 * 選択ファイルのインデックス
	 */
	private int selectedIndex = 0;

	/**
	 * カレントディレクトリ
	 */
	private VFile currentDirectory;

	/**
	 * 親ディレクトリ
	 */
	private VFile parentDirectory;

	/**
	 * ファイルシステムのマウントポイント
	 */
	private VFile mountPoint;

	/**
	 * ファイル比較クラス
	 */
	private JFDComparator comparator;

	/**
	 * カレントディレクトリ、親ディレクトリ、マウントポイント等の特殊ファイル
	 */
	private List specialFiles = new ArrayList();

	/**
	 * ディレクトリヒストリ
	 */
	private FileHistory history = new FileHistory(50, false);

	/**
	 * 移動ディレクトリ履歴以外も含む、重複無しのファイルヒストリ
	 */
	private FileHistory noOverwrapHistory = new FileHistory(50, true);

	/**
	 * モデルのリスナ
	 */
	private List listeners = new ArrayList();

	/**
	 * ファイル更新によって自動的に行われるモデルの更新のロック管理クラス
	 */
	private AutoUpdateLockManager lock = new AutoUpdateLockManager();

	/**
	 * ディレクトリが更新され、表示をリフレッシュする必要があるかどうかのフラグ
	 */
	private boolean needsRefresh = false;

	/**
	 * 表示更新タイマー
	 */
	private Timer updateTimer = new Timer();

	/**
	 * 表示更新間隔
	 */
	private static final long UPDATE_PERIOD = 1000;

	/**
	 * ディレクトリオープンコマンド。 非同期で開く際に使用される。
	 */
	private SetDirectoryCommand setDirectoryCommand = null;

	/**
	 * "."で始まるファイルをフィルタするかどうかのフラグ
	 */
	private boolean filtersDotFile = true;
	
	/**
	 * フィルター正規表現
	 */
	private String filter = "^\\..*";
	
	/**
	 * デフォルトコンストラクタ
	 * 
	 */
	public JFDModel() {
		updateTimer.schedule(new UpdateTimerTask(), 0, UPDATE_PERIOD);
	}

	/**
	 * カレントディレクトリを取得する。
	 * 
	 * @return カレントディレクトリ
	 */
	public VFile getCurrentDirectory() {
		return currentDirectory;
	}

	/**
	 * 表示中のファイルを取得する。 ただし、特殊ファイルは含まない。
	 * 
	 * @return
	 */
	public VFile[] getFiles() {
		VFile[] rtn = new VFile[markedFiles.length];
		for (int i = 0; i < markedFiles.length; i++) {
			rtn[i] = markedFiles[i].getFile();
		}

		return rtn;
	}

	/**
	 * カレントディレクトリをセットする。
	 * 
	 * @param currentDirectory
	 */
	public synchronized void setDirectory(VFile currentDirectory,
			VFile selectedFile) throws VFSException {
		stopSetDirectoryCommand();

		VFile[] files = currentDirectory.getChildren();
		doSetFiles(currentDirectory, files, selectedFile);
	}

	/**
	 * カレントディレクトリをセットする。
	 * 
	 * @param currentDirectory
	 */
	public synchronized void setDirectory(VFile currentDirectory,
			int selectedIndex) throws VFSException {
		stopSetDirectoryCommand();

		VFile[] files = currentDirectory.getChildren();
		doSetFiles(currentDirectory, files, selectedIndex);
	}

	/**
	 * カレントディレクトリを必要なら非同期でセットする。
	 * 
	 * @param currentDirectory
	 */
	public synchronized void setDirectoryAsynchIfNecessary(
			VFile currentDirectory, VFile selectedFile, JFD jfd)
			throws VFSException {
		stopSetDirectoryCommand();

		if (currentDirectory.getFileSystem().isLocal()) {
			setDirectory(currentDirectory, selectedFile);
		} else {
			setDirectoryAsynch(currentDirectory, selectedFile, jfd);
		}
	}

	/**
	 * カレントディレクトリを必要なら非同期でセットする。
	 * 
	 * @param currentDirectory
	 */
	public synchronized void setDirectoryAsynchIfNecessary(
			VFile currentDirectory, int selectedIndex, JFD jfd)
			throws VFSException {
		stopSetDirectoryCommand();

		if (currentDirectory.getFileSystem().isLocal()) {
			setDirectory(currentDirectory, selectedIndex);
		} else {
			setDirectoryAsynch(currentDirectory, selectedIndex, jfd);
		}
	}

	/**
	 * カレントディレクトリを非同期でセットする。
	 * 
	 * @param currentDirectory
	 */
	public synchronized void setDirectoryAsynch(
			VFile currentDirectory, VFile selectedFile, JFD jfd)
			throws VFSException {
		stopSetDirectoryCommand();

		setDirectoryCommand = new SetDirectoryCommand(currentDirectory,
				selectedFile);
		setDirectoryCommand.setJFD(jfd);
		setDirectoryCommand.addManipulationListener(new JFDManipulationListener(setDirectoryCommand));
		setDirectoryCommand.startAsync();
	}

	/**
	 * カレントディレクトリを非同期でセットする。
	 * 
	 * @param currentDirectory
	 */
	public synchronized void setDirectoryAsynch(
			VFile currentDirectory, int selectedIndex, JFD jfd)
			throws VFSException {
		stopSetDirectoryCommand();

		setDirectoryCommand = new SetDirectoryCommand(currentDirectory,
				selectedIndex);
		setDirectoryCommand.addManipulationListener(new JFDManipulationListener(setDirectoryCommand));
		setDirectoryCommand.setJFD(jfd);
		jfd.setPrimaryCommand(setDirectoryCommand);
		setDirectoryCommand.startAsync();
	}

	/**
	 * カレントディレクトリと表示ファイルをセットする。
	 * 
	 * @param currentDirectory
	 * @param files
	 * @throws VFSException
	 */
	public synchronized void setFiles(VFile currentDirectory, VFile[] files,
			int selectedIndex) throws VFSException {
		stopSetDirectoryCommand();

		doSetFiles(currentDirectory, files, selectedIndex);
	}

	/**
	 * カレントディレクトリと表示ファイルをセットする。
	 * 
	 * @param currentDirectory
	 * @param files
	 * @throws VFSException
	 */
	private void doSetFiles(VFile currentDirectory, VFile[] files,
			int selectedIndex) throws VFSException {
		sortVFileArray(files);
		initFiles(currentDirectory, files);

		doSetSelectedIndex(selectedIndex);

		fireDirectoryChanged();
	}

	/**
	 * カレントディレクトリと表示ファイルをセットする。
	 * 
	 * @param currentDirectory
	 * @param files
	 * @throws VFSException
	 */
	public synchronized void setFiles(VFile currentDirectory, VFile[] files,
			VFile selectedFile) throws VFSException {
		stopSetDirectoryCommand();

		doSetFiles(currentDirectory, files, selectedFile);
	}

	/**
	 * カレントディレクトリと表示ファイルをセットする。
	 * 
	 * @param currentDirectory
	 * @param files
	 * @throws VFSException
	 */
	private void doSetFiles(VFile currentDirectory, VFile[] files,
			VFile selectedFile) throws VFSException {
		sortVFileArray(files);
		initFiles(currentDirectory, files);

		doSetSelectedIndex(getIndexOfFile(selectedFile));

		fireDirectoryChanged();
	}

	/**
	 * アクティブなファイルシステムの管理を行なう。
	 * @param newDirectory
	 * @throws VFSException
	 */
	private void registerActiveFileSystem(VFile newDirectory) throws VFSException {
		if (currentDirectory != null) {
			if (currentDirectory.getFileSystem() == newDirectory
					.getFileSystem()) {
				return;
			}

			currentDirectory.getFileSystem().removeUser(this);
		}
		
		newDirectory.getFileSystem().registerUser(this);
		newDirectory.getFileSystem().getVFS().closeUnusedFileSystem();
	}

	/**
	 * もしも実行中のディレクトリオープン操作がある場合、中止する。
	 * 
	 */
	private void stopSetDirectoryCommand() {
		if (setDirectoryCommand != null) {
			setDirectoryCommand.stop();
			setDirectoryCommand = null;
		}
	}

	/**
	 * カレントディレクトリと表示ファイルを初期化する。
	 * 
	 * @param currentDirectory
	 * @param files
	 * @throws VFSException
	 */
	private void initFiles(VFile currentDirectory, VFile[] files) throws VFSException {
		registerActiveFileSystem(currentDirectory);
		if(filtersDotFile) {
			List filesList = new ArrayList();
			Pattern pattern = Pattern.compile(filter);
			for(int i=0; i<files.length; i++) {
				if(!pattern.matcher(files[i].getName()).matches()) {
					filesList.add(files[i]);
				}
			}
			files = new VFile[filesList.size()];
			files = (VFile[])filesList.toArray(files);
		}
		
		if (currentDirectory != this.currentDirectory) {
			if (this.currentDirectory != null) {
				this.currentDirectory.removeFileListener(this);
				this.currentDirectory.removeChildFileListener(this);
			}
			currentDirectory.addFileListener(this);
			currentDirectory.addChildFileListener(this);
		}

		// ヒストリに追加
		history.add(currentDirectory);
		noOverwrapHistory.add(currentDirectory);

		this.currentDirectory = currentDirectory;
		if (currentDirectory == null) {
			parentDirectory = null;
			mountPoint = null;
		} else {
			parentDirectory = currentDirectory.getParent();
			if (currentDirectory.isRoot()) {
				mountPoint = currentDirectory.getFileSystem().getMountPoint();
			} else {
				mountPoint = null;
			}
		}

		specialFiles.clear();
		try {
			if (currentDirectory != null && currentDirectory.exists()) {
				specialFiles.add(currentDirectory);
			}
		} catch (VFSException e) {
			// カレントディレクトリにアクセス不能なので追加せず。
		}

		if (parentDirectory != null) {
			specialFiles.add(parentDirectory);
		}

		if (mountPoint != null) {
			specialFiles.add(mountPoint);
		}

		markedFiles = new FileMarker[files.length];
		for (int i = 0; i < files.length; i++) {
			markedFiles[i] = new FileMarker(files[i]);
		}
	}

	/**
	 * 表示ファイル数を取得する。
	 * 
	 * @return
	 */
	public int getFilesCount() {
		return specialFiles.size()
				+ (markedFiles != null ? markedFiles.length : 0);
	}

	/**
	 * 指定インデックス番目のファイルを取得する。
	 * 
	 * @param index
	 * @return
	 */
	public VFile getFileAt(int index) {
		int specialFilesCount = specialFiles.size();
		if (index < specialFilesCount) {
			return (VFile) specialFiles.get(index);
		}

		return markedFiles[index - specialFilesCount].getFile();
	}

	/**
	 * ファイルがマークされているか判定する。
	 * 
	 * @param index
	 * @return
	 */
	public boolean isMarked(int index) {
		int specialsLength = specialFiles.size();
		if (index < specialsLength) {
			return false;
		}

		if (index - specialsLength < markedFiles.length) {
			return markedFiles[index - specialsLength].isMarked();
		}

		return false;
	}

	/**
	 * マークファイル数を取得する。
	 * 
	 * @return
	 */
	public int getMarkedCount() {
		int rtn = 0;
		for (int i = 0; i < markedFiles.length; i++) {
			if (markedFiles[i].isMarked()) {
				rtn++;
			}
		}

		return rtn;
	}

	/**
	 * マークされたファイルの配列を取得する。
	 * 
	 * @return
	 */
	public VFile[] getMarkedFiles() {
		List list = new ArrayList();

		for (int i = 0; i < markedFiles.length; i++) {
			if (markedFiles[i].isMarked()) {
				list.add(markedFiles[i].getFile());
			}
		}

		VFile[] rtn = new VFile[list.size()];
		rtn = (VFile[]) list.toArray(rtn);
		return rtn;
	}

	/**
	 * マークされたファイルがあればそれを、そうでない場合は選択されたファイルを
	 * 配列で返す。
	 * 
	 * @return
	 */
	public VFile[] getMarkedOrSelectedFiles() {
		if(getMarkedCount() == 0) {
			VFile[] rtn = {
					getSelectedFile()
			};
			
			return rtn;
		} else {
			return getMarkedFiles();
		}
	}
	
	public void setMarked(int index, boolean bool) {
		int specialsLength = specialFiles.size();
		if (index < specialsLength) {
			return;
		}

		if (index - specialsLength < markedFiles.length) {
			markedFiles[index - specialsLength].setMarked(bool);
		}

		fireFilesChanged();
	}

	public void clearMark() {
		for(int i=0; i<markedFiles.length; i++) {
			markedFiles[i].setMarked(false);
		}

		fireFilesChanged();
	}

	/**
	 * 選択されたインデックスを返す。
	 * 
	 * @return
	 */
	public int getSelectedIndex() {
		return selectedIndex;
	}

	/**
	 * 選択されたファイルを返す。
	 * 
	 * @return
	 */
	public VFile getSelectedFile() {
		if(selectedIndex < 0) {
			return null;
		}
		return getFileAt(selectedIndex);
	}

	/**
	 * 選択インデックスを設定する。
	 * 
	 * @param index
	 */
	public void setSelectedIndex(int index) {
		int oldIndex = selectedIndex;
		doSetSelectedIndex(index);
		fireCursorMoved(oldIndex, selectedIndex);
	}

	private void doSetSelectedIndex(int index) {
		if (markedFiles == null || index < 0) {
			index = 0;
		}

		if (specialFiles.size() + (markedFiles != null ? markedFiles.length : 0) <= index ) {
			index = specialFiles.size() + (markedFiles != null ? markedFiles.length : 0) - 1;
		}

		selectedIndex = index;
	}

	/**
	 * 選択ファイルを指定する。
	 * 
	 * @param file
	 */
	public void setSelectedFile(VFile file) {
		setSelectedIndex(getIndexOfFile(file));
	}

	/**
	 * 指定されたファイルの順番を求める。 もしも存在しない場合は-1を返す。
	 * 
	 * @param file
	 * @return
	 */
	private int getIndexOfFile(VFile file) {
		for (int i = 0; i < specialFiles.size(); i++) {
			VFile f = (VFile) specialFiles.get(i);
			if (f.equals(file)) {
				return i;
			}
		}

		for (int i = 0; markedFiles != null && i < markedFiles.length; i++) {
			if (markedFiles[i].getFile().equals(file)) {
				return i + specialFiles.size();
			}
		}

		return -1;
	}

	/**
	 * モデルリスナを追加する。
	 * 
	 * @param listener
	 *            リスナ
	 */
	public void addJFDModelListener(JFDModelListener listener) {
		listeners.add(listener);
	}

	/**
	 * モデルリスナを削除する。
	 * 
	 * @param listener
	 *            リスナ
	 */
	public void removeJFDModelListener(JFDModelListener listener) {
		listeners.remove(listener);
	}

	/**
	 * ファイルが変更された際に呼び出され、 リスナに通知する。
	 * 
	 */
	public void fireFilesChanged() {
		if (!autoNotify) {
			hasChanged = true;
			return;
		}

		for (int i = 0; i < listeners.size(); i++) {
			JFDModelListener listener = (JFDModelListener) listeners.get(i);
			listener.dataChanged(this);
		}
	}

	/**
	 * ディレクトリが移動した際に呼び出され、リスナに通知する。
	 * 
	 */
	public void fireDirectoryChanged() {
		for (int i = 0; i < listeners.size(); i++) {
			JFDModelListener listener = (JFDModelListener) listeners.get(i);
			listener.directoryChanged(this);
		}
	}

	/**
	 * カーソルが移動した際に呼び出され、リスナに通知する。
	 * 
	 * @param oldIndex
	 * @param newIndex
	 */
	public void fireCursorMoved(int oldIndex, int newIndex) {
		for (int i = 0; i < listeners.size(); i++) {
			JFDModelListener listener = (JFDModelListener) listeners.get(i);
			listener.cursorMoved(this);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nullfish.lib.vfs.FileListener#fileChanged(com.nullfish.lib.vfs.VFile)
	 */
	public void fileChanged(VFile file) {
		if (!lock.isLocked()) {
			needsRefresh = true;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nullfish.lib.vfs.FileListener#fileDeleted(com.nullfish.lib.vfs.VFile)
	 */
	public void fileDeleted(VFile file) {
		if (!lock.isLocked()) {
			needsRefresh = true;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nullfish.lib.vfs.FileListener#fileCreated(com.nullfish.lib.vfs.VFile)
	 */
	public void fileCreated(VFile file) {
		if (!lock.isLocked()) {
			needsRefresh = true;
		}
	}

	/**
	 * @return Returns the comparator.
	 */
	public JFDComparator getComparator() {
		return comparator;
	}

	/**
	 * @param comparator
	 *            The comparator to set.
	 */
	public void setComparator(JFDComparator comparator) {
		this.comparator = comparator;
	}

	/**
	 * ファイル変更時、falseをセットしておけば、trueにセットするまで通知を行わない。
	 * 
	 * @param autoNotify
	 */
	public void setAutoNotify(boolean autoNotify) {
		this.autoNotify = autoNotify;
		if (autoNotify && hasChanged) {
			fireFilesChanged();
			hasChanged = false;
		}
	}

	private void sortVFileArray(VFile[] files) {
		if (comparator == null || !comparator.compares()) {
			return;
		}

		Arrays.sort(files, comparator);
	}

	public FileHistory getHistory() {
		return history;
	}

	public FileHistory getNoOverwrapHistory() {
		return noOverwrapHistory;
	}

	/**
	 * 自動更新を無効にする。
	 * 
	 * @param o
	 *            ロックオブジェクト
	 */
	public void lockAutoUpdate(Object o) {
		lock.lock(o);
	}

	/**
	 * 自動更新を有効にする。
	 * 
	 * @param o
	 *            ロックオブジェクト
	 */
	public void unlockAutoUpdate(Object o) {
		lock.unlock(o);
	}

	/**
	 * モデルを更新する。
	 * 
	 */
	public void refresh() {
		try {
			VFile[] files = currentDirectory.getChildren();
			VFile selectedFile = getSelectedFile();
			if (selectedFile.exists()) {
				setFiles(currentDirectory, files, getSelectedFile());
			} else {
				setFiles(currentDirectory, files, getSelectedIndex());
			}
		} catch (VFSException e) {
			e.printStackTrace();
		} finally {
			needsRefresh = false;
		}
	}

	public void dispose() {
		listeners = null;
		UpdateManager.getInstance().removeListener(this);
		updateTimer.cancel();
	}

	/**
	 * ディレクトリの更新を監視して、必要なら表示を更新するタイマータスク
	 */
	private class UpdateTimerTask extends TimerTask {
		public void run() {
			if (!needsRefresh) {
				return;
			}
			//needsRefresh = false;

			if (!lock.isLocked()) {
				refresh();
			}

			fireFilesChanged();
		}
	}

	private static String[] openingMessages = {
			JFDResource.LABELS.getString("opening_dir"),
			JFDResource.LABELS.getString("opening_dir") + ".",
			JFDResource.LABELS.getString("opening_dir") + "..",
			JFDResource.LABELS.getString("opening_dir") + "...", };

	private class SetDirectoryCommand extends Command {
		/**
		 * 開くディレクトリ
		 */
		private VFile dir;

		/**
		 * 選択ファイル
		 */
		private VFile selectedFile;

		/**
		 * 選択インデックス
		 */
		private int selectedIndex = -1;

		public SetDirectoryCommand(VFile dir, VFile selectedFile) {
			this.dir = dir;
			this.selectedFile = selectedFile;
		}

		public SetDirectoryCommand(VFile dir, int selectedIndex) {
			this.dir = dir;
			this.selectedIndex = selectedIndex;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.nullfish.lib.vfs.manipulation.abst.AbstractManipulation#doExecute()
		 */
		public void doExecute() throws VFSException {
			getJFD().setPrimaryCommand(this);

			VFile[] children = dir.getChildren(this);
			//	↓事前に親の属性を取得して、イベントディスパッチスレッドで
			//	実行されないようにしている。
			dir.exists(this);
			dir.getPermission(this);
			VFile parent = dir.getParent();
			if(parent != null) {
				parent.exists(this);
				parent.getPermission(this);
			}
			
			if (isStopped()) {
				throw new ManipulationStoppedException(this);
			}

			if (selectedFile != null) {
				doSetFiles(dir, children, selectedFile);
			} else {
				doSetFiles(dir, children, selectedIndex);
			}
		}

		/**
		 * 作業経過メッセージを取得する。
		 * 
		 * @return
		 */
		public String getProgressMessage() {
			return openingMessages[(int) (System.currentTimeMillis() / 600) % 4];
		}

		/* (non-Javadoc)
		 * @see com.nullfish.app.jfd2.command.Command#closesUnusingFileSystem()
		 */
		public boolean closesUnusingFileSystem() {
			return true;
		}

		/**
		 * 
		 * @return
		 */
		public FileSystem[] getUsingFileSystem() {
			FileSystem[] rtn = {
				dir.getFileSystem()
			};
			
			return rtn;
		}
		
		public boolean isPrimary() {
			return true;
		}
	}

	/**
	 * "."で始まるファイルを非表示にするかどうかを設定する。
	 * @param filtersDotFile	trueなら非表示
	 */
	public void setFiltersFile(boolean filtersDotFile) {
		this.filtersDotFile = filtersDotFile;
	}
	
	/**
	 * フィルター文字列を設定する。
	 * @param filter
	 */
	public void setFilter(String filter) {
		this.filter = filter;
	}
}
