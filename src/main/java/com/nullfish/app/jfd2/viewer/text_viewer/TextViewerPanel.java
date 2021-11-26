package com.nullfish.app.jfd2.viewer.text_viewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.Element;

import org.dyndns.longinus.utils.HTMLUtil;
import org.jdom.Document;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.JFDComponent;
import com.nullfish.app.jfd2.command.embed.CursorMoveCommand;
import com.nullfish.app.jfd2.config.Configuration;
import com.nullfish.app.jfd2.config.DefaultConfig;
import com.nullfish.app.jfd2.resource.JFDResource;
import com.nullfish.app.jfd2.ui.container2.ContainerPosition;
import com.nullfish.app.jfd2.ui.container2.JFDOwner;
import com.nullfish.app.jfd2.util.DomCache;
import com.nullfish.app.jfd2.viewer.FileNameTitleUpdater;
import com.nullfish.app.jfd2.viewer.FileViewerContainerPanel;
import com.nullfish.lib.EncodeDetector;
import com.nullfish.lib.command.CommandCallable;
import com.nullfish.lib.keymap.KeyStrokeMap;
import com.nullfish.lib.resource.ResourceManager;
import com.nullfish.lib.ui.LineNumberView;
import com.nullfish.lib.ui.ThreadSafeUtilities;
import com.nullfish.lib.ui.xml_menu.XMLPopupMenu;
import com.nullfish.lib.vfs.VFS;
import com.nullfish.lib.vfs.VFile;

public class TextViewerPanel extends FileViewerContainerPanel {
	/**
	 * タイトル更新クラス
	 */
	private FileNameTitleUpdater updater = new FileNameTitleUpdater();

	private JScrollPane scroll = new JScrollPane();
	
	private LineNumberView.RowHeightAccessibleTextArea textArea = new LineNumberView.RowHeightAccessibleTextArea();

	private LineNumberView lineNumberView = new LineNumberView(textArea);
	
	JPanel topPanel = new JPanel(new GridBagLayout());

	private JLabel fileNameLabel = new JLabel();

	private JLabel formatLabel = new JLabel();

	private JLabel lineNumberTitleLabel = new JLabel("Line No: ");

	private JLabel lineNumberLabel = new JLabel();

	private ReaderThread thread;

	private int maxSize = 1000000;

	private FindDialog dialog;

	private JFD currentJFD;

	private VFile file;

	private List encodings;

	private String encoding;

	private boolean htmlMode = false;
	
	private XMLPopupMenu popup;
	
	private Color focusedColor = Color.WHITE;
	private Color unfocusedColor = Color.GRAY;
	
	/**
	 * 検索ダイアログを表示する
	 */
	public static final String SHOW_DIALOG = "show_dialog";

	/**
	 * 次を検索する。
	 */
	public static final String SEARCH_NEXT = "search_next";

	/**
	 * 一つ前を検索する。
	 */
	public static final String SEARCH_PREV = "search_prev";

	/**
	 * 次のエンコードを適用する。
	 */
	public static final String NEXT_ENCODE = "next_encode";

	/**
	 * htmlモードを適用、解除する。
	 */
	public static final String HTML_MODE = "html_mode";

	/**
	 * 行数表示モードを適用、解除する。
	 */
	public static final String LINE_NUMBER_MODE = "line_number_mode";

	/**
	 * 編集する。
	 */
	public static final String EDIT = "edit";

	/**
	 * ポップアップ定義ファイル
	 */
	public static final String POPUP_FILE = "classpath:///resources/text_viewer_menu.xml";
	
	public TextViewerPanel(TextFileViewer viewer) {
		super(viewer);
		setTitleUpdater(updater);
		initGui();
		initPopup();
		initCommand();
		initKey();

		this.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				JFDOwner owner = getJFDOwner();
				if(owner != null) {
					owner.componentActivated(TextViewerPanel.this);
				}
				textArea.requestFocusInWindow();
			}

			public void focusLost(FocusEvent e) {
				//System.out.println(e.getOppositeComponent());
			}
		});
		
		textArea.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				JFDOwner owner = getJFDOwner();
				if(owner != null) {
					owner.componentActivated(TextViewerPanel.this);
				}
				changeColor();
			}

			public void focusLost(FocusEvent e) {
				changeColor();
			}
			
			private void changeColor() {
				boolean changesColor = ((Boolean)getViewer().getJFD().getCommonConfiguration().getParam("change_color_with_focus", Boolean.TRUE)).booleanValue();
				
				if(changesColor) {
					textArea.setForeground(textArea.isFocusOwner() || (dialog != null && dialog.isVisible()) ? focusedColor : unfocusedColor);
				} else {
					textArea.setForeground(focusedColor);
				}
			}
		});
	}

	private void initGui() {
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textArea.setForeground(Color.WHITE);
		textArea.setBackground(Color.BLACK);
		textArea.setSelectedTextColor(Color.BLACK);
		textArea.setSelectionColor(Color.WHITE);
		textArea.setTabSize(2);

		scroll
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroll
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setViewportView(textArea);
//		scroll.setRowHeaderView(lineNumberView);

		scroll.getViewport().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				scrolled();
			}
		});

		fileNameLabel.setForeground(Color.WHITE);
		formatLabel.setForeground(Color.WHITE);
		lineNumberTitleLabel.setForeground(Color.YELLOW);
		lineNumberLabel.setForeground(Color.WHITE);
		topPanel.setBackground(Color.BLACK);
		topPanel.setOpaque(true);
		topPanel.add(new JLabel(" "), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		topPanel.add(fileNameLabel, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		topPanel.add(lineNumberTitleLabel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		topPanel.add(lineNumberLabel, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		topPanel.add(formatLabel, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

		this.setLayout(new BorderLayout());
		this.add(scroll, BorderLayout.CENTER);
		this.add(topPanel, BorderLayout.NORTH);		

	}

	private void initPopup() {
		try {
			ResourceManager resource = new ResourceManager() {
				public String getResource(String name) {
					return JFDResource.LABELS.getString(name);
				}
			};
			CommandCallable callable = new CommandCallable() {
				public void callCommand(String command) {
					Action action = textArea.getActionMap().get(command);
					if (action != null) {
						action.actionPerformed(new ActionEvent(
								textArea,
								ActionEvent.ACTION_PERFORMED, null));
						return;
					}

					action = getActionMap().get(command);
					if (action != null) {
						action.actionPerformed(new ActionEvent(
								TextViewerPanel.this,
								ActionEvent.ACTION_PERFORMED, null));
						return;
					}

					currentJFD.getCommandManager().execute(command);
				}
			};

			popup = new XMLPopupMenu(resource, callable);
			popup.addFocusListener(new FocusAdapter() {
				public void focusLost(FocusEvent e) {
					popup.setVisible(false);
				}
			});

			Document doc = DomCache.getInstance().getDocument(VFS.getInstance().getFile(POPUP_FILE));
			popup.convertFromNode(doc.getRootElement());
			
			MouseListener mouseListener =  new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					showPopup(e);
				}
				
				public void mouseReleased(MouseEvent e) {
					showPopup(e);
				}
				
				private void showPopup(MouseEvent e) {
					if(e.isPopupTrigger()) {
						popup.show(textArea, e.getX(), e.getY());
					}
				}
			};
			
			textArea.addMouseListener(mouseListener);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void initCommand() {
		getActionMap().put(SHOW_DIALOG, new ShowFindDialogAction());
		getActionMap().put(NEXT_ENCODE, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				increaseEncode();
			}
		});
		getActionMap().put(HTML_MODE, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				changeHtmlMode();
			}
		});
		getActionMap().put(EDIT, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				currentJFD.getCommandManager().execute(EDIT);
			}
		});
/*
		getActionMap().put(LINE_NUMBER_MODE, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				lineNumberView.setShows(!lineNumberView.isShows());
			}
		});
*/
	}

	private void initKey() {
		InputMap thisMap = getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		thisMap.put(
				KeyStrokeMap.getKeyStroke(KeyEvent.VK_F, 0), SHOW_DIALOG);
		thisMap.put(
				KeyStrokeMap.getKeyStroke(KeyEvent.VK_SLASH, 0), SHOW_DIALOG);
		thisMap.put(
				KeyStrokeMap.getKeyStroke(KeyEvent.VK_BACK_SLASH, 0), SHOW_DIALOG);
		thisMap.put(
				KeyStrokeMap.getKeyStroke(KeyEvent.VK_L, 0), NEXT_ENCODE);
		thisMap.put(
				KeyStrokeMap.getKeyStroke(KeyEvent.VK_H, 0), HTML_MODE);
		thisMap.put(
				KeyStrokeMap.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.SHIFT_MASK), EDIT);
		thisMap.put(
				KeyStrokeMap.getKeyStroke(KeyEvent.VK_N, 0), LINE_NUMBER_MODE);

		textArea.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).setParent(null);
		InputMap textMap = textArea.getInputMap(JComponent.WHEN_FOCUSED);
		textMap.setParent(null);

		textMap.put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_C, 
				DefaultConfig.getDefaultConfig().isSwapCtrlMeta() ?
						KeyEvent.META_MASK : KeyEvent.CTRL_MASK),
				"copy");
		textMap.put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_HOME, 0), "caret-begin");
		textMap.put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_END, 0), "caret-end");
		textMap.put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_A, 
				DefaultConfig.getDefaultConfig().isSwapCtrlMeta() ?
				KeyEvent.META_MASK : KeyEvent.CTRL_MASK), "select-all");

		scroll.getActionMap().put("scroll-up-half", new ScrollOrPaneChangeAction("main", -2));
		scroll.getActionMap().put("scroll-down-half", new ScrollOrPaneChangeAction("sub", 2));
		InputMap scrollMap = scroll
				.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		scrollMap.getParent().remove(KeyStrokeMap.getKeyStroke(KeyEvent.VK_RIGHT, 0));
		scrollMap.getParent().remove(KeyStrokeMap.getKeyStroke(KeyEvent.VK_LEFT, 0));
		scrollMap.put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_RIGHT, 0),
				"scroll-down-half");
		scrollMap.put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_LEFT, 0),
				"scroll-up-half");
		scrollMap.put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_SPACE, 0),
				"scrollDown");
		scrollMap.put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_SPACE,
				KeyEvent.SHIFT_MASK), "scrollUp");
		scrollMap.put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0),
				"scrollUp");
	}

	public void open(JFD jfd) {
		//encoding = (String) encodings.get(0);
		formatLabel.setText(encoding);
		lineNumberLabel.setText("             ");

		currentJFD = jfd;
		super.open(jfd);
	}

	public synchronized void setFile(VFile file) {
		setFile(file, false);
	}
	
	public synchronized void setFile(VFile file, boolean encodingAutoDetect) {
		this.file = file;
		fileNameLabel.setText(file.getSecurePath());
		updater.setFile(file);
		if (thread != null) {
			thread.stopReading();
		}

		thread = new ReaderThread(file, encoding, htmlMode, encodingAutoDetect);
		thread.start();
	}

	private void increaseEncode() {
		int index = encodings.indexOf(encoding) + 1;
		while (index >= encodings.size()) {
			index -= encodings.size();
		}

		encoding = (String) encodings.get(index);

		formatLabel.setText(encoding);
		setFile(file);
	}

	private void changeHtmlMode() {
		htmlMode = !htmlMode;
		formatLabel.setText(htmlMode ? "HTML" : "");
		setFile(file, true);
	}
	
	private synchronized void removeThread() {
		thread = null;
	}

	public synchronized void close() {
		if (thread != null) {
			thread.stopReading();
			thread = null;
		}
		textArea.setText("");
		if(dialog != null && dialog.isVisible()) {
			dialog.setVisible(false);
		}
		
		super.close();
	}

	private void scrolled() {
		showLineNumber();
		
		int caretPos = textArea.getCaretPosition();
		Rectangle rect = scroll.getViewport().getViewRect();

		Point leftUp = rect.getLocation();
		int start = textArea.viewToModel(leftUp);
		if (caretPos < start) {
//			textArea.setCaretPosition(start <= textArea.getDocument()
//					.getLength() ? start : textArea.getDocument().getLength());
			return;
		}

		Point rightDown = new Point((int) (rect.getX() + rect.getWidth()),
				(int) (rect.getY() + rect.getHeight() - textArea.getFontMetrics(textArea.getFont()).getHeight()));
		int end = textArea.viewToModel(rightDown);
		if (caretPos > end && caretPos > 0) {
//			textArea.setCaretPosition(end);
		}
		
	}

	public void setEncoding(List encodings) {
		this.encodings = encodings;
		encoding = (String) encodings.get(0);
	}

	private class ReaderThread extends Thread {
		private VFile file;

		private String enc;

		private boolean reading;

		private boolean html;
		
		private boolean encAutoDetect;
		
		public ReaderThread(VFile file, String enc, boolean html, boolean encAutoDetect) {
			this.file = file;
			this.enc = enc;
			this.html = html;
			this.encAutoDetect = encAutoDetect;
		}

		public void run() {
			textArea.setText("");
			reading = true;

			BufferedInputStream is = null;
			BufferedReader reader = null;

			try {
				InputStream ins =file.getInputStream();
				is = new BufferedInputStream(ins);
				if(encAutoDetect) {
					enc = EncodeDetector.detectEncoding(is);
					enc = enc != null ? enc : (String)encodings.get(0);
					final String theEnc = enc;
					ThreadSafeUtilities.executeRunnable(new Runnable() {
						public void run() {
							formatLabel.setText(theEnc);
						}
					});
				}
				Charset charset = Charset.forName(enc);

				reader = new BufferedReader(new InputStreamReader(is, charset));
				String line;
				if(html) {
					textArea.setText("Reading");
					
					StringBuffer buffer = new StringBuffer();
					int count = 0;
					while (true) {
						line = reader.readLine();
						if (line == null) {
							break;
						}
		
						if (!reading) {
							break;
						}
		
						buffer.append(line);
						buffer.append("\n");
		
						if (buffer.length() > maxSize) {
							break;
						}
						count++;
						if(count % 10 == 0) {
							textArea.append(".");
						}
					}
					
					//	↓ここちょっと適当
					String text = buffer.toString();
					text = text.replaceAll("<(br|BR)>", "\n");
					
					textArea.setText(HTMLUtil.getStripHTMLTag(text));
				} else {
					char[] buffer = new char[4096];
					
					while (true) {
						int l = reader.read(buffer);
						if (l <= 0) {
							break;
						}
		
						if (!reading) {
							break;
						}
		
						//String str = new String(buffer, 0, l);
						//charset.
						textArea.append(new String(buffer, 0, l));
		
						if (textArea.getDocument().getLength() > maxSize) {
							break;
						}
					}
				}
				Runnable runnable = new Runnable() {
					public void run() {
						showLineNumber();
					}
				};
				
				ThreadSafeUtilities.executeRunnable(runnable);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				removeThread();
				try {
					reader.close();
				} catch (Exception e) {
				}
			}
		}

		public void stopReading() {
			reading = false;
		}
		
	}

	/**
	 * 検索ダイアログ表示アクション
	 * 
	 * @author shunji
	 */
	private class ShowFindDialogAction extends AbstractAction {
		public synchronized void actionPerformed(ActionEvent e) {
			if (dialog == null) {
				Container c = TextViewerPanel.this;
				while (true) {
					if (c.getParent() == null) {
						dialog = new FindDialog(textArea, scroll);
						break;
					}

					c = c.getParent();
					if (c instanceof Dialog) {
						dialog = new FindDialog(textArea, scroll, (Dialog) c);
						break;
					}

					if (c instanceof Frame) {
						dialog = new FindDialog(textArea, scroll, (Frame) c);
						break;
					}
				}
			}

			dialog.setJfd(currentJFD);
			dialog.initFromJFD();
			dialog.setVisible(true);
		}
	}

	private class ScrollAction extends AbstractAction {
		int devideUnit;

		public ScrollAction(int devideUnit) {
			this.devideUnit = devideUnit;
		}

		public void actionPerformed(ActionEvent e) {
			JScrollPane scroll = (JScrollPane) e.getSource();
			JViewport viewport = scroll.getViewport();
			Rectangle rect = viewport.getViewRect();
			rect.y += (rect.height / devideUnit);
			rect.y = rect.y < 0 ? 0 : rect.y;
			Component view = viewport.getView();
			rect.y = rect.y + rect.height > view.getHeight() ? view.getHeight()
					- rect.height : rect.y;

			viewport.setViewPosition(rect.getLocation());
		}
	}
	
	private class ScrollOrPaneChangeAction extends ScrollAction {
		private String pane;
		public ScrollOrPaneChangeAction(String pane, int devideUnit) {
			super(devideUnit);
			this.pane = pane;
		}
		
		public void actionPerformed(ActionEvent e) {
			JFDOwner owner = ((JFDComponent)TextViewerPanel.this).getJFDOwner();
			ContainerPosition thisPosition = owner.getComponentPosition(TextViewerPanel.this);
			ContainerPosition position = null;
			if("main".equals(pane)) {
				position = ContainerPosition.MAIN_PANEL;
			} else if("sub".equals(pane)) {
				position = ContainerPosition.SUB_PANEL;
			}

			JFDComponent opponent = currentJFD.getJFDOwner().getComponent(position);
			boolean multiWindowMode = 
				((Boolean)currentJFD.getCommonConfiguration().getParam(
						CursorMoveCommand.PANE_CHANGE_CURSOR, Boolean.FALSE)).booleanValue()
				&& !thisPosition.equals(position)
				&& opponent != null;

			if(multiWindowMode) {
				owner.setActiveComponent(opponent);
			} else {
				super.actionPerformed(e);
			}
		}

		
	}
	
	public void init(VFile baseDir) {
		try {
			Configuration config = Configuration.getInstance(baseDir
					.getChild(JFD.COMMON_PARAM_FILE));
			focusedColor = (Color) (config.getParam(
					"text_viewer_color", Color.WHITE));
			unfocusedColor = (Color) (config.getParam(
					"grid_color_no_focus", Color.DARK_GRAY));

			textArea.setFont((Font) config.getParam("label_font", null));
			textArea.setBackground((Color) config.getParam("background_color", Color.BLACK));
			textArea.setForeground(focusedColor);

			setEncoding((List)config.getParam("grep_encode_all", null));
			encoding = (String) encodings.get(0);
			lineNumberView.init();

			fileNameLabel.setFont((Font) config.getParam("label_font", null));
			lineNumberTitleLabel.setFont((Font) config.getParam("label_font", null));
			lineNumberLabel.setFont((Font) config.getParam("label_font", null));
			formatLabel.setFont((Font) config.getParam("label_font", null));
			textArea.setEditable(((Boolean)config.getParam("text_viewer_editable", Boolean.FALSE)).booleanValue());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private StringBuffer buffer = new StringBuffer(); 
	public void showLineNumber() {
		String line = Integer.toString(getSelectedLine() + 1);
		
		Element root = textArea.getDocument().getDefaultRootElement();
		String lastLine = Integer.toString(root.getElementCount());
		
		buffer.setLength(0);
		for(int i=line.length(); i<6; i++) {
			buffer.append(" ");
		}
		buffer.append(line);
		buffer.append("/");
		for(int i=lastLine.length(); i<6; i++) {
			buffer.append(" ");
		}
		buffer.append(lastLine);
		buffer.append(": ");
		lineNumberLabel.setText(buffer.toString());
	}

	public int getSelectedLine() {
		Element root = textArea.getDocument().getDefaultRootElement();
		int pos = textArea.viewToModel(new Point(0, scroll.getViewport().getViewPosition().y + 5));
		return root.getElementIndex(pos);
	}
}
