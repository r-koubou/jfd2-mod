/*
 * Created on 2004/06/08
 *
 */
package com.nullfish.app.jfd2.dialog.components;

import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.monazilla.migemo.Migemo;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.config.DefaultConfig;
import com.nullfish.app.jfd2.ui.container2.NumberedJFD2;
import com.nullfish.app.jfd2.util.MigemoInfo;
import com.nullfish.lib.keymap.KeyStrokeMap;
import com.nullfish.lib.ui.FocusAndSelectAllTextField;
import com.nullfish.lib.ui.SimpleChooserDialog;
import com.nullfish.lib.ui.UIUtilities;
import com.nullfish.lib.vfs.VFS;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;

/**
 * ファイル名補完機能を持ったテキストフィールド。
 * 
 * @author shunji
 */
public class FileCompletionTextField extends FocusAndSelectAllTextField {
	private VFS vfs;

	private JFD jfd;
	
	private SimpleChooserDialog dialog;

	private Frame ownerFrame;

	private Dialog ownerDialog;

	private static final String POPUP_ACTION = "popup";

	/**
	 * 補完モード
	 */
	private int mode = MODE_DIRECTORY;

	/**
	 * ファイルのみ
	 */
	public static final int MODE_FILE = 0;

	/**
	 * ディレクトリのみ
	 */
	public static final int MODE_DIRECTORY = 1;

	/**
	 * 両方
	 */
	public static final int MODE_BOTH = 2;

	public FileCompletionTextField(JFD jfd) {
		this(jfd, MODE_DIRECTORY);
		initFocusListener();
	}

	public FileCompletionTextField(JFD jfd, int mode) {
		super();
		this.jfd = jfd;
		this.vfs = VFS.getInstance(jfd);
		initKey();
		initFocusListener();
	}

	private void initFocusListener() {
		//	保管候補リストからフォーカスが移ってきた場合は全選択をしない。
		addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				if(SwingUtilities.getRoot(e.getOppositeComponent()) == dialog) {
					setSelectionStart(getText().length());
					setSelectionEnd(getText().length());
				}
			}

			public void focusLost(FocusEvent e) {
			}
		});
	}
	
	private void initKey() {
		getActionMap().put(POPUP_ACTION, new ShowPopupAction());
		getInputMap().put(
				KeyStrokeMap.getKeyStroke(KeyEvent.VK_SPACE, KeyEvent.SHIFT_MASK),
				POPUP_ACTION);
		getInputMap().put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_TAB, 0),
				POPUP_ACTION);

		Set forwardFocusSet = new HashSet();
		forwardFocusSet.add(KeyStroke.getKeyStroke(KeyEvent.VK_TAB,
				KeyEvent.CTRL_MASK)
				);
		setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
				forwardFocusSet);

		Set backwardFocusSet = new HashSet();
		backwardFocusSet.add(KeyStroke.getKeyStroke(KeyEvent.VK_TAB,
				KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK));
		setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
				backwardFocusSet);
		
		for(int i=1; i<10; i++) {
			String key = "SHORTCUT_" + i;
			PathShortCutAction shortCutAction = new PathShortCutAction(i);
			getActionMap().put(key, shortCutAction);
			getInputMap().put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_0 + i, DefaultConfig.getDefaultConfig().isSwapCtrlMeta() ? KeyEvent.META_MASK : KeyEvent.CTRL_MASK), key);
		}
	}

	public void showCandidatesPopup() {
		if (dialog == null) {
			initTopLevelOwner();

			//	ダイアログが未作成の場合のみ新規作成する
			if (ownerDialog != null) {
				dialog = new SimpleChooserDialog(ownerDialog);
			} else {
				dialog = new SimpleChooserDialog(ownerFrame);
			}
			dialog.setCellRenderer(new FileNameListCellRenderer());
			dialog.setUndecorated(true);
			dialog.pack();
		}

		Point point = getLocationOnScreen();

		dialog.setBounds((int) point.getX(), (int) point.getY() + getHeight(),
				dialog.getWidth(), dialog.getHeight());

		VFile[] candidates = getCandidates(getText());

		if (candidates == null || candidates.length == 0) {
			return;
		}

		VFile choosen = null;
		
		if (candidates.length == 1) {
			choosen = candidates[0];
		} else {
			dialog.setListData(candidates);
			dialog.setVisible(true);
			choosen = (VFile) dialog.getChoosen();
		}
		
		if (choosen != null) {
			try {
				if (choosen.isFile()) {
					setText(choosen.getAbsolutePath());
				} else {
					setText(choosen.getAbsolutePath()
							+ choosen.getFileName().getSeparator());
				}
			} catch (VFSException e) {
				setText(choosen.getAbsolutePath());
			}
		}
	}

	private void initTopLevelOwner() {
		Container c = UIUtilities.getTopLevelOwner(this);

		if (c instanceof Dialog) {
			ownerDialog = (Dialog) c;
		} else {
			ownerFrame = (Frame) c;
		}
	}

	public VFile[] getCandidates(String path) {
		try {
			VFile file = vfs.getFile(path);
			if (!file.getFileSystem().isLocal()) {
				return new VFile[0];
			}

			VFile directory;
			Pattern pattern = null;

			if ("\\/".indexOf(path.charAt(path.length() - 1)) != -1) {
				directory = file;
				pattern = Pattern.compile(".*");
			} else if(MigemoInfo.usesMigemo()) {
				directory = file.getParent();
				pattern = Pattern.compile(Migemo.lookup(file.getName()));
			} else {
				directory = file.getParent();
				pattern = Pattern.compile("^\\Q" + file.getName().toLowerCase() + "\\E");
			}

			if (directory == null) {
				return new VFile[0];
			}

			VFile[] children = directory.getChildren();

			List filesList = new ArrayList();
			for (int i = 0; i < children.length; i++) {
				if( pattern.matcher(children[i].getName().toLowerCase()).find() ) {
					if (mode == MODE_BOTH
							|| (mode == MODE_DIRECTORY && children[i]
									.isDirectory())
							|| (mode == MODE_FILE && children[i].isFile())) {
						filesList.add(children[i]);
					}
				}
			}

			VFile[] rtn = new VFile[filesList.size()];
			rtn = (VFile[]) filesList.toArray(rtn);
			Arrays.sort(rtn, new FileComparator());
			return rtn;
		} catch (VFSException e) {
			return new VFile[0];
		}
	}

	public static class FileComparator implements Comparator {
		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(Object o1, Object o2) {
			VFile file1 = (VFile) o1;
			VFile file2 = (VFile) o2;

			return file1.getFileName().getLowerName().compareTo(
					file2.getFileName().getLowerName());
		}
	}

	/**
	 * finalizeのオーバーライド。
	 * ダイアログを廃棄する。
	 */
	public void finalize() throws Throwable {
		if (dialog != null) {
			dialog.dispose();
		}
		super.finalize();
	}

	/**
	 * 補完候補のポップアップを表示するアクションクラス
	 */
	class ShowPopupAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			showCandidatesPopup();
		}
	}
	
	/**
	 * 指定番号のjFD2のカレントディレクトリを表示するアクションクラス
	 * @author shunji
	 */
	class PathShortCutAction extends AbstractAction {
		private int number;

		public PathShortCutAction(int number) {
			this.number = number;
		}
		
		public void actionPerformed(ActionEvent e) {
			if(!(jfd instanceof NumberedJFD2)) {
				return;
			}
			
			NumberedJFD2 theJfd = NumberedJFD2.getJfdAt(number - 1);
			if(theJfd == null) {
				return;
			}
			
			VFile current = theJfd.getModel().getCurrentDirectory();
			if(current == null) {
				return;
			}
			
			setText(current.getSecurePath());
		}
	}
}
