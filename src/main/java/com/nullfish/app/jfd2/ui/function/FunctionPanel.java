/*
 * Created on 2004/06/15
 *
 */
package com.nullfish.app.jfd2.ui.function;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import com.nullfish.app.jfd2.Initable;
import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.command.JFDException;
import com.nullfish.app.jfd2.util.PropertiesCache;
import com.nullfish.lib.keymap.KeyStrokeMap;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;

/**
 * @author shunji
 * 
 * Preferences - Java - Code Style - Code Templates
 */
public class FunctionPanel extends JPanel implements Initable {
	private JFD jfd;

	private Map keyMap = new HashMap();
	
	private int mode = 0;

	private CommandHolder[][] commands = new CommandHolder[2][12];

	private CommandLabel[] functionLabels = new CommandLabel[12];

	private JLabel pad1 = new JLabel(" ");

	private JLabel pad2 = new JLabel(" ");

	private JLabel pad3 = new JLabel(" ");

	private JLabel pad4 = new JLabel(" ");

	private JLabel pad5 = new JLabel("  ");

	private JLabel pad6 = new JLabel(" ");

	private JLabel pad7 = new JLabel(" ");

	private JLabel pad8 = new JLabel(" ");

	private JLabel pad9 = new JLabel("  ");

	private JLabel pad10 = new JLabel(" ");

	private JLabel pad11 = new JLabel(" ");

	private JLabel pad12 = new JLabel(" ");

	private JLabel pad13 = new JLabel(" ");

	private JLabel[] pads = { pad1, pad2, pad3, pad4, pad5, pad6, pad7, pad8,
			pad9, pad10, pad11, pad12, pad13 };

	public static final String MODE_0 = "mode0";

	public static final String MODE_1 = "mode1";

	public static final String FUNCTION_NO_SHIFT = "no_shift";

	public static final String FUNCTION_WITH_SHIFT = "with_shift";

	/**
	 * 設定ファイル名
	 */
	public static final String PREF_FILE = "function.ini";

	/**
	 * コンストラクタ
	 *  
	 */
	public FunctionPanel(JFD jfd) {
		this.jfd = jfd;

		for (int i = 0; i < functionLabels.length; i++) {
			functionLabels[i] = new CommandLabel(jfd);
		}

		initGUI();
		initActionMap();
		initKey();
	}

	private void initGUI() {
		setOpaque(false);
		for (int i = 0; i < functionLabels.length; i++) {
			functionLabels[i].setOpaque(true);
		}

		setLayout(new GridBagLayout());
		add(pad1, new GridBagConstraints(0, 0, 1, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,
						0, 0, 0), 0, 0));
		add(functionLabels[0], new GridBagConstraints(1, 0, 1, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,
						0, 0, 0), 0, 0));
		add(pad2, new GridBagConstraints(2, 0, 1, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,
						0, 0, 0), 0, 0));
		add(functionLabels[1], new GridBagConstraints(3, 0, 1, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,
						0, 0, 0), 0, 0));
		add(pad3, new GridBagConstraints(4, 0, 1, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,
						0, 0, 0), 0, 0));
		add(functionLabels[2], new GridBagConstraints(5, 0, 1, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,
						0, 0, 0), 0, 0));
		add(pad4, new GridBagConstraints(6, 0, 1, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,
						0, 0, 0), 0, 0));
		add(functionLabels[3], new GridBagConstraints(7, 0, 1, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,
						0, 0, 0), 0, 0));
		add(pad5, new GridBagConstraints(8, 0, 1, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,
						0, 0, 0), 0, 0));
		add(functionLabels[4], new GridBagConstraints(9, 0, 1, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,
						0, 0, 0), 0, 0));
		add(pad6, new GridBagConstraints(10, 0, 1, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,
						0, 0, 0), 0, 0));
		add(functionLabels[5], new GridBagConstraints(11, 0, 1, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,
						0, 0, 0), 0, 0));
		add(pad7, new GridBagConstraints(12, 0, 1, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,
						0, 0, 0), 0, 0));
		add(functionLabels[6], new GridBagConstraints(13, 0, 1, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,
						0, 0, 0), 0, 0));
		add(pad8, new GridBagConstraints(14, 0, 1, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,
						0, 0, 0), 0, 0));
		add(functionLabels[7], new GridBagConstraints(15, 0, 1, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,
						0, 0, 0), 0, 0));
		add(pad9, new GridBagConstraints(16, 0, 1, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,
						0, 0, 0), 0, 0));
		add(functionLabels[8], new GridBagConstraints(17, 0, 1, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,
						0, 0, 0), 0, 0));
		add(pad10, new GridBagConstraints(18, 0, 1, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,
						0, 0, 0), 0, 0));
		add(functionLabels[9], new GridBagConstraints(19, 0, 1, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,
						0, 0, 0), 0, 0));
		add(pad11, new GridBagConstraints(20, 0, 1, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,
						0, 0, 0), 0, 0));
		add(functionLabels[10], new GridBagConstraints(21, 0, 1, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,
						0, 0, 0), 0, 0));
		add(pad12, new GridBagConstraints(22, 0, 1, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,
						0, 0, 0), 0, 0));
		add(functionLabels[11], new GridBagConstraints(23, 0, 1, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,
						0, 0, 0), 0, 0));
		add(pad13, new GridBagConstraints(24, 0, 1, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,
						0, 0, 0), 0, 0));
	}

	private void initActionMap() {
		ActionMap actionMap = getActionMap();
		actionMap.put(MODE_0, new ModeChanger(0));
		actionMap.put(MODE_1, new ModeChanger(1));

		for (int i = 0; i < 12; i++) {
			actionMap.put(FUNCTION_NO_SHIFT + i, new CommandCaller(0, i));
			actionMap.put(FUNCTION_WITH_SHIFT + i, new CommandCaller(1, i));
		}
	}
	
	/**
	 * キー入力を処理する。
	 * 
	 * @param e
	 */
	public void processKey(KeyEvent e) {
		Object actionKey = keyMap.get(KeyStroke.getKeyStrokeForEvent(e));
		if(actionKey == null) {
			return;
		}
		
		Action action = (Action)getActionMap().get(actionKey);
		if(action == null) {
			return;
		}
		
		e.consume();
		action.actionPerformed(new  ActionEvent(this, ActionEvent.ACTION_PERFORMED, "process key"));
	}

	private void initKey() {
		//	InputMapだと複数存在する際に正しいパネルにKeyEventが渡らないので独自のMapを用意した。
/*
		InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		inputMap.put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_SHIFT,
				KeyEvent.SHIFT_MASK, false), MODE_1);
		inputMap
				.put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_SHIFT, 0, true), MODE_0);

		for (int i = 0; i < 12; i++) {
			inputMap.put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_F1 + i, 0),
					FUNCTION_NO_SHIFT + i);
			inputMap.put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_F1 + i,
					KeyEvent.SHIFT_MASK), FUNCTION_WITH_SHIFT + i);
		}
*/
		
		keyMap.put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_SHIFT,
				KeyEvent.SHIFT_MASK, false), MODE_1);
		keyMap.put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_SHIFT, 0, true), MODE_0);

		for (int i = 0; i < 12; i++) {
			keyMap.put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_F1 + i, 0),
					FUNCTION_NO_SHIFT + i);
			keyMap.put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_F1 + i,
					KeyEvent.SHIFT_MASK), FUNCTION_WITH_SHIFT + i);
		}
	}

	/**
	 * ラベルのフォントをセットする。
	 * 
	 * @param font
	 */
	public void setLabelFont(Font font) {
		for (int i = 0; i < functionLabels.length; i++) {
			functionLabels[i].setFont(font);
		}

		for (int i = 0; i < pads.length; i++) {
			pads[i].setFont(font);
		}
	}

	public void setColor(Color foreColor, Color bgColor) {
		for (int i = 0; i < functionLabels.length; i++) {
			functionLabels[i].setForeground(foreColor);
			functionLabels[i].setBackground(bgColor);
		}
	}

	public int getMode() {
		return mode;
	}

	public void initFromStream(VFile file) throws IOException, VFSException {
		invalidate();
		try {
			Properties prop = PropertiesCache.getInstance().getDocument(file);

			for (int set = 0; set < 2; set++) {
				for (int i = 0; i < 12; i++) {
					String label = prop.getProperty("f" + (i + 1) + "." + set
							+ ".label");
					String command = prop.getProperty("f" + (i + 1) + "." + set
							+ ".command");

					commands[set][i] = new CommandHolder(command, label);
				}
			}
		} finally {
			validate();
		}
	}

	public void setMode(int mode) {
		invalidate();

		for (int i = 0; i < 12; i++) {
			functionLabels[i].setCommandHolder(commands[mode][i]);
		}

		validate();
	}

	class ModeChanger extends AbstractAction {
		int mode;

		public ModeChanger(int mode) {
			this.mode = mode;
		}

		public void actionPerformed(ActionEvent e) {
			setMode(mode);
		}
	}

	/**
	 * ファンクションキーに対応したコマンドを呼び出すアクション
	 */
	class CommandCaller extends AbstractAction {
		int mode;

		int key;

		public CommandCaller(int mode, int key) {
			this.mode = mode;
			this.key = key;
		}

		public void actionPerformed(ActionEvent arg0) {
			if (jfd == null || commands.length <= mode
					|| commands[mode].length <= key
					|| commands[mode][key] == null) {
				return;
			}

			jfd.getCommandManager().execute(commands[mode][key].getCommand());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nullfish.app.jfd2.Initable#init(com.nullfish.lib.vfs.VFile)
	 */
	public void init(VFile baseDir) throws VFSException {
		try {
			initFromStream(baseDir.getChild(PREF_FILE));
		} catch (IOException e) {
			throw new JFDException(e, "", null);
		} catch (VFSException e) {
			throw e;
		}
		
		setMode(0);
	}
}
