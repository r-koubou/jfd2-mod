/*
 * Created on 2004/05/21
 *
 */
package com.nullfish.app.jfd2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.nullfish.app.jfd2.aliase.AliaseManager;
import com.nullfish.app.jfd2.command.Command;
import com.nullfish.app.jfd2.command.CommandManager;
import com.nullfish.app.jfd2.command.JFDException;
import com.nullfish.app.jfd2.command.progress.ProgressViewer;
import com.nullfish.app.jfd2.command.progress.ProgressViewerFactory;
import com.nullfish.app.jfd2.command.progress.WindowProgressViewerFactory;
import com.nullfish.app.jfd2.comparator.FileComparator;
import com.nullfish.app.jfd2.comparator.JFDComparator;
import com.nullfish.app.jfd2.config.Configuration;
import com.nullfish.app.jfd2.dialog.JFDDialog;
import com.nullfish.app.jfd2.dnd.JFDDragGestureListener;
import com.nullfish.app.jfd2.dnd.JFDDropTargetListener;
import com.nullfish.app.jfd2.ext_command.CommandExecuter;
import com.nullfish.app.jfd2.ext_command_panel.ExternalCommandManager;
import com.nullfish.app.jfd2.ext_command_panel.ExternalCommandPanel;
import com.nullfish.app.jfd2.resource.JFDResource;
import com.nullfish.app.jfd2.ui.container2.JFDOwner;
import com.nullfish.app.jfd2.ui.function.FunctionPanel;
import com.nullfish.app.jfd2.ui.labels.EqualSizePanel;
import com.nullfish.app.jfd2.ui.labels.FileInfoLabel;
import com.nullfish.app.jfd2.ui.labels.FileSumLabel;
import com.nullfish.app.jfd2.ui.labels.FileSystemSizeLabel;
import com.nullfish.app.jfd2.ui.labels.MarkCountLabel;
import com.nullfish.app.jfd2.ui.labels.MarkSumLabel;
import com.nullfish.app.jfd2.ui.labels.PageLabel;
import com.nullfish.app.jfd2.ui.labels.ToolTipLabel;
import com.nullfish.app.jfd2.ui.table.JFDCellRenderer;
import com.nullfish.app.jfd2.ui.table.JFDListCursorModel;
import com.nullfish.app.jfd2.ui.table.JFDListModel;
import com.nullfish.app.jfd2.ui.table.RendererMode;
import com.nullfish.app.jfd2.util.DomCache;
import com.nullfish.app.jfd2.util.IncrementalSearcher;
import com.nullfish.app.jfd2.util.SortUtility;
import com.nullfish.app.jfd2.util.thumbnail.ThumbnailCache;
import com.nullfish.lib.command.CommandCallable;
import com.nullfish.lib.keymap.KeyStrokeMap;
import com.nullfish.lib.resource.ResourceManager;
import com.nullfish.lib.tablelayout.HtmlTablePanel;
import com.nullfish.lib.ui.ClockLabel;
import com.nullfish.lib.ui.ThreadSafeUtilities;
import com.nullfish.lib.ui.UIUtilities;
import com.nullfish.lib.ui.grid.CrossLineGrid;
import com.nullfish.lib.ui.grid.HorizontalLineGrid;
import com.nullfish.lib.ui.grid.LineGrid;
import com.nullfish.lib.ui.grid.VerticalLineGrid;
import com.nullfish.lib.ui.list_table.ListTable;
import com.nullfish.lib.ui.xml_menu.XMLPopupMenu;
import com.nullfish.lib.vfs.VFS;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;
import com.nullfish.lib.vfs.exception.VFSIOException;
import com.nullfish.lib.vfs.impl.antzip.ZIPFileSystem;
import com.nullfish.lib.vfs.impl.commons_http.CommonsHTTPFileSystem;
import com.nullfish.lib.vfs.impl.ftp.FTPFileSystem;
import com.nullfish.lib.vfs.tag_db.TagDataBase;

/**
 * @author shunji
 * 
 */
public class JFD2 extends JPanel implements JFD, Initable {
	/**
	 * IDの種
	 */
	private static int idSeed = 0;

	/**
	 * ID値
	 */
	private int id = idSeed++;

	/**
	 * モデル
	 */
	private JFDModel model;

	/**
	 * 画面のロックフラグ
	 */
	private boolean locked = false;

	private JFDListModel listModel = new JFDListModel();
	
	private boolean incrementalSearchMode = false;
	
	private IncrementalSearcher searcher = new IncrementalSearcher(this);

	private JFDListCursorModel cursorModel;

	/**
	 * コマンド管理クラス
	 */
	private CommandManager commandManager = new CommandManager(this);

	/**
	 * 設定ファイルディレクトリと共通設定のマップ
	 */
	// private static Map commonConfigMap = new HashMap();
	/**
	 * 共通設定
	 */
	private Configuration commonConfig;

	/**
	 * 個別設定
	 */
	private Configuration localConfig = new Configuration();

	/**
	 * 一時設定
	 */
	private Configuration tempConfig = new Configuration();

	/**
	 * このインスタンスのオーナー
	 */
	private JFDOwner jfdOwner;

	/**
	 * 相対パス表示フラグ
	 */
	private boolean showsRepativePath = true;
	
	/**
	 * ファンクションキーのパネル
	 */
	private FunctionPanel functionPanel = new FunctionPanel(this);

	/**
	 * 操作メッセージ更新クラス
	 */
	private ManipulationMessageUpdater messageUpdater = new ManipulationMessageUpdater(
			this);

	/**
	 * エイリアス管理クラス
	 */
	private AliaseManager aliaseManager = new AliaseManager(this);

	/**
	 * 
	 * ラベル
	 */
	private JLabel labelTitle = new JLabel(JFDResource.LABELS
			.getString("title"));

	private JLabel labelTitleLeftPad = new JLabel(" ");

	private JLabel labelTitleRightPad = new JLabel(" ");

	private ClockLabel clockLabel = new ClockLabel();

	private JLabel clockLabelLeftPad = new JLabel(" ");

	private JLabel clockLabelRightPad = new JLabel(" ");

	private static Element layoutNode;
	static {
		try {
			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			org.w3c.dom.Document doc = docBuilder.parse(HtmlTablePanel.class
					.getResource("/layout.xml").openStream());
			layoutNode = doc.getDocumentElement();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private HtmlTablePanel panel;

	private ListTable table = new ListTable();

	private JLabel labelPath = new JLabel("Path=");

	private JLabel labelPathInfo = new ToolTipLabel() {
		public Dimension getMinimumSize() {
			Dimension rtn = super.getMinimumSize();
			rtn.width = 1;
			return rtn;
		}
	};

	private JLabel labelPage = new JLabel(" Page:");

	private PageLabel labelPageInfo = new PageLabel();

	private JLabel labelName = new JLabel("Name=");

	private JLabel labelNameInfo = new ToolTipLabel() {
		public Dimension getMinimumSize() {
			Dimension rtn = super.getMinimumSize();
			rtn.width = 1;
			return rtn;
		}
	};

	private JLabel labelMark = new JLabel("Marked");

	private MarkCountLabel labelMarkInfo = new MarkCountLabel();

	private MarkSumLabel labelMarkSizeInfo = new MarkSumLabel();

	private JLabel labelInfo = new JLabel("Info:");

	private FileInfoLabel labelInfoInfo = new FileInfoLabel();

	private JLabel labelFiles = new JLabel("Files");

	private JLabel labelFilesInfo = new JLabel("0");

	private FileSumLabel labelFilesSizeInfo = new FileSumLabel();

	private JLabel labelMessagePad = new JLabel(" ");

	private JLabel labelMessage = new JLabel() {
		public Dimension getMinimumSize() {
			Dimension rtn = super.getMinimumSize();
			rtn.width = 1;
			return rtn;
		}
	};

	private JLabel labelTopLeftPad = new JLabel(" ");

	private JLabel labelTopRightPad = new JLabel(" ");

	private JLabel labelTotal = new JLabel(" Total:");

	private FileSystemSizeLabel labelTotalInfo = new FileSystemSizeLabel();
//	private FileSizeLabel labelTotalInfo = new FileSizeLabel();
	private EqualSizePanel labelTotalPanel = new EqualSizePanel(this, labelTotalInfo);

	private JLabel labelUsed = new JLabel(" Used:");

	private FileSystemSizeLabel labelUsedInfo = new FileSystemSizeLabel();
	private EqualSizePanel labelUsedPanel = new EqualSizePanel(this, labelUsedInfo);

	private JLabel labelFree = new JLabel(" Free:");

	private FileSystemSizeLabel labelFreeInfo = new FileSystemSizeLabel();
	private EqualSizePanel labelFreePanel = new EqualSizePanel(this, labelFreeInfo);

	private JLabel labelIncrementalSearch = new JLabel("@Incremental Search");

	private JLabel labelIncrementalSearchPad = new JLabel(" ");

	// ↑の全部のラベル
	private JLabel[] labels = { labelTitle, labelTitleLeftPad,
			labelTitleRightPad, clockLabel, clockLabelLeftPad,
			clockLabelRightPad, labelPath, labelPathInfo, labelPage,
			labelPageInfo, labelName, labelNameInfo, labelMark, labelMarkInfo,
			labelMarkSizeInfo, labelInfo, labelInfoInfo, labelFiles,
			labelFilesInfo, labelFilesSizeInfo, labelMessagePad, labelMessage,
			labelTopLeftPad, labelTopRightPad, labelTotal, labelTotalInfo, 
			labelUsed, labelUsedInfo, labelFree, labelFreeInfo, 
			labelIncrementalSearch, labelIncrementalSearchPad };

	// 枠線
//	private CrossLineGrid crossTopLeft = new CrossLineGrid(this);

//	private HorizontalLineGrid horizonTopLeft = new HorizontalLineGrid(this);

//	private HorizontalLineGrid horizonTopRight = new HorizontalLineGrid(this);

//	private CrossLineGrid crossTopRight = new CrossLineGrid(this);

	private VerticalLineGrid verticalTopLeft = new VerticalLineGrid(this);

	private VerticalLineGrid verticalTopRight = new VerticalLineGrid(this);

	private CrossLineGrid crossMiddleLeft = new CrossLineGrid(this);

	private HorizontalLineGrid horizonMiddleLeft = new HorizontalLineGrid(this);

	private HorizontalLineGrid horizonMiddleRight = new HorizontalLineGrid(this);

	private CrossLineGrid crossMiddleRight = new CrossLineGrid(this);

	private VerticalLineGrid verticalBottomLeft = new VerticalLineGrid(this);

	private VerticalLineGrid verticalBottomRight = new VerticalLineGrid(this);

	private CrossLineGrid crossBottomLeft = new CrossLineGrid(this);

	private HorizontalLineGrid horizonBottomLeft = new HorizontalLineGrid(this);

	private HorizontalLineGrid horizonBottomRight = new HorizontalLineGrid(this);

	private CrossLineGrid crossBottomRight = new CrossLineGrid(this);

	private Color focusedLineColor = Color.WHITE;

	private Color unfocusedLineColor = Color.DARK_GRAY;


	private CrossLineGrid crossPageLeftTop = new CrossLineGrid(this);
	private CrossLineGrid crossPageTotalTop = new CrossLineGrid(this);
	private CrossLineGrid crossTotalUsedTop = new CrossLineGrid(this);
	private CrossLineGrid crossusedFreeTop = new CrossLineGrid(this);
	private CrossLineGrid crossFreeRightTop = new CrossLineGrid(this);
	
	private HorizontalLineGrid horizonPageTop = new HorizontalLineGrid(this);
	private HorizontalLineGrid horizonTotalTop = new HorizontalLineGrid(this);
	private HorizontalLineGrid horizonUsedTop = new HorizontalLineGrid(this);
	private HorizontalLineGrid horizonFreeTop = new HorizontalLineGrid(this);

	private VerticalLineGrid verticalPageLeft = new VerticalLineGrid(this);
	private VerticalLineGrid verticalPageTotal = new VerticalLineGrid(this);
	private VerticalLineGrid verticalTotalUsed = new VerticalLineGrid(this);
	private VerticalLineGrid verticalUsedFree = new VerticalLineGrid(this);
	private VerticalLineGrid verticalFreeRight = new VerticalLineGrid(this);

	private CrossLineGrid crossPageLeft = new CrossLineGrid(this);
	private CrossLineGrid crossPageTotal = new CrossLineGrid(this);
	private CrossLineGrid crossTotalUsed = new CrossLineGrid(this);
	private CrossLineGrid crossusedFree = new CrossLineGrid(this);
	private CrossLineGrid crossFreeRight = new CrossLineGrid(this);
	
	private HorizontalLineGrid horizonPage = new HorizontalLineGrid(this);
	private HorizontalLineGrid horizonTotal = new HorizontalLineGrid(this);
	private HorizontalLineGrid horizonUsed = new HorizontalLineGrid(this);
	private HorizontalLineGrid horizonFree = new HorizontalLineGrid(this);

	/**
	 * 外部コマンドパネル
	 */
	private ExternalCommandPanel externalCommandPanel = new ExternalCommandPanel(
			this);

	/**
	 * セルレンダラ
	 */
	private JFDCellRenderer renderer;

	/**
	 * サムネイルキャッシュ
	 */
	private ThumbnailCache thumbnailCache = new ThumbnailCache();

	private ProgressViewerFactory progressViewerFactory = new WindowProgressViewerFactory();

	// private static com.nullfish.lib.memory.MemoryReport report =
	// new com.nullfish.lib.memory.MemoryReport(1000);

	private Command primaryCommand;

	/**
	 * ポップアップメニュー
	 */
	private XMLPopupMenu popup;

	/**
	 * カラム数の設定名
	 */
	public static final String CONFIG_COLUMNS = "columns";

	/**
	 * カラム数の設定名
	 */
	public static final String CONFIG_RENDERER_MODE = "renderer_mode";

	/**
	 * 相対パス表示の設定名
	 */
	public static final String CONFIG_SHOWS_RELATIVE_PATH = "shows_relative";

	/**
	 * 最後に開かれたディレクトリの設定名
	 */
	public static final String CONFIG_LAST_OPENED = "last_opened";

	/**
	 * サイズの設定名
	 */
	public static final String CONFIG_SIZE = "size";

	/**
	 * コンストラクタ
	 * 
	 */
	public JFD2() {
		model = new JFDModel();
		cursorModel = new JFDListCursorModel(model);
		try {
			initGUI();
			layoutPanel();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// VFS.getInstance(this).setUserInfoManager(new
		// JFD2UserInfoManager(this));
	}

	/**
	 * GUIの初期化
	 * 
	 */
	private void initGUI() {
		setBackground(Color.BLACK);
		setOpaque(true);

		// setBackground(Color.BLACK);
		setFocusable(true);

		listModel.setJFDModel(model);
		table.setListModel(listModel);
		table.setListCursorModel(cursorModel);
		table.setIntercellSpacing(new Dimension(0, 0));

		renderer = new JFDCellRenderer(this, table.getListTableModel(), thumbnailCache);
		thumbnailCache.start();
		
		table.setDefaultRenderer(Object.class, renderer);
		table.setPreferredSize(new Dimension(600, 400));
		table.setColumnCount(1);
		table.setOpaque(false);
		table.setRowMargin(0);
		table.setShowGrid(false);
		table.setShowHorizontalLines(false);
		table.setShowVerticalLines(false);
		table.setFocusable(false);
		table.setIntercellSpacing(new Dimension(0, 0));
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					try {
						VFile file = getModel().getSelectedFile();
						if (file == null) {
							return;
						}
						if (file.isFile()) {
							getCommandManager().execute("extension_map");
						} else {
							getCommandManager().execute("open");
						}
					} catch (VFSException ex) {
						ex.printStackTrace();
					}
				}
			}
		});

		labelTitle.setOpaque(true);
		labelTitleLeftPad.setOpaque(true);
		labelTitleRightPad.setOpaque(true);
		labelTitle.setBackground(Color.BLACK);
		labelTitleLeftPad.setBackground(Color.BLACK);
		labelTitleRightPad.setBackground(Color.BLACK);
		
		clockLabel.setOpaque(true);
		clockLabelLeftPad.setOpaque(true);
		clockLabelRightPad.setOpaque(true);
		clockLabel.setBackground(Color.BLACK);
		clockLabelLeftPad.setBackground(Color.BLACK);
		clockLabelRightPad.setBackground(Color.BLACK);
		
		labelFilesSizeInfo.setJFDModel(model);
		labelPageInfo.setListTableModel(table.getListTableModel());
		labelInfoInfo.setModel(model);
		labelMarkSizeInfo.setModel(model);
		labelMarkInfo.setModel(model);

		// モデル変更時のラベルの反映処理。
		model.addJFDModelListener(new JFDModelListener() {
			public void directoryChanged(JFDModel model) {
				thumbnailCache.clearTask();
				if(renderer.isThumbnailVisible()) {
					VFile[] files = model.getFiles();
					
					for(int i=0; i < thumbnailCache.getCacheSize() &&  i < files.length; i++) {
						if(renderer.isImageFile(files[i])) {
							thumbnailCache.loadLater(files[i], table.getListTableModel());
						}
					}
				}
				updateLabels(model);
				updateSpaceLabels(model);
			}

			public void dataChanged(JFDModel model) {
				updateSpaceLabels(model);
			}

			public void cursorMoved(JFDModel model) {
				updateLabels(model);
			}

			private void updateLabels(JFDModel model) {
				VFile selectedFile = model.getSelectedFile();
				labelFilesInfo.setText(Integer.toString(model.getFilesCount()));

				if (selectedFile.equals(model.getCurrentDirectory())) {
					labelPathInfo.setText(model.getCurrentDirectory()
							.getSecurePath());
					labelNameInfo.setText(".");
					return;
				}
				VFile parent = model.getCurrentDirectory().getParent();
				if (parent != null && parent.equals(selectedFile)) {
					labelPathInfo.setText(model.getCurrentDirectory()
							.getSecurePath());
					labelNameInfo.setText("..");
					return;
				}
				if (selectedFile.isRoot()) {
					labelPathInfo.setText(selectedFile.getSecurePath());
					labelNameInfo.setText("");
					return;
				}

				labelPathInfo.setText(selectedFile != null
						&& selectedFile.getParent() != null ? selectedFile
						.getParent().getSecurePath() : "");
				labelNameInfo.setText(model.getSelectedFile().getName());
			}

			private void updateSpaceLabels(JFDModel model) {
				try {
					long total = model.getCurrentDirectory().getAttribute().getTotalSpace();
					long free = model.getCurrentDirectory().getAttribute().getFreeSpace();
					long used = (total == -1 || free == -1) ? -1 : total - free;
					
					labelTotalInfo.setSize(total);
					labelFreeInfo.setSize(free);
					labelUsedInfo.setSize(used);
				} catch (VFSException e) {
					labelTotalInfo.setSize(-1);
					labelFreeInfo.setSize(-1);
					labelUsedInfo.setSize(-1);
				}
			}
			public void markChanged(JFDModel model) {
			}
		});

		// 枠線の交差ポイント
		crossMiddleLeft.setDirections(true, true, true, false);
		crossMiddleRight.setDirections(true, false, true, true);
		crossBottomLeft.setDirections(true, true, false, false);
		crossBottomRight.setDirections(true, false, false, true);
		
		crossPageLeftTop.setDirections(false, true, true, false);		
		crossPageTotalTop.setDirections(false, true, true, true);
		crossTotalUsedTop.setDirections(false, true, true, true);
		crossusedFreeTop.setDirections(false, true, true, true);
		crossFreeRightTop.setDirections(false, false, true, true);
		
		crossPageLeft.setDirections(true, true, true, false);		
		crossPageTotal.setDirections(true, true, false, true);
		crossTotalUsed.setDirections(true, true, false, true);
		crossusedFree.setDirections(true, true, false, true);
		crossFreeRight.setDirections(true, false, true, true);

		functionPanel.setColor(Color.BLACK, Color.WHITE);

		setColumnCount(2);
		// setLabelFont(new Font("Monospaced", 0, 12));
		for (int i = 0; i < labels.length; i++) {
			labels[i].setForeground(Color.WHITE);
		}
		labelTitle.setForeground(Color.YELLOW);

		labelIncrementalSearch.setOpaque(true);
		labelIncrementalSearch.setVisible(false);
		labelIncrementalSearch.setForeground(Color.BLACK);
		labelIncrementalSearch.setBackground(Color.YELLOW);
		
		horizonMiddleLeft.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				getCommandManager().execute("cursor-prev-page");
			}
		});

		horizonMiddleRight.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				getCommandManager().execute("cursor-next-page");
			}
		});

		addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				JFDOwner owner = getJFDOwner();
				if(owner != null) {
					owner.componentActivated(JFD2.this);
				}
				functionPanel.setMode(0);
			}

			public void focusLost(FocusEvent e) {
				functionPanel.setMode(0);
			}
		});

		// ドラッグアンドドロップがらみ
		JFDDropTargetListener dropListener = new JFDDropTargetListener(this);
		new DropTarget(this, dropListener);
		new DropTarget(table, dropListener);

		JFDDragGestureListener gestureListener = new JFDDragGestureListener(
				this);

		DragSource source = DragSource.getDefaultDragSource();
		source.createDefaultDragGestureRecognizer(this,
				DnDConstants.ACTION_COPY_OR_MOVE, gestureListener);
		source.createDefaultDragGestureRecognizer(table,
				DnDConstants.ACTION_COPY_OR_MOVE, gestureListener);

		// マウスホイールがらみ
		MouseWheelListener mouseWheelListener = new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {
				int count = e.getWheelRotation();
				if (count < 0) {
					getCommandManager().execute("cursor-prev-page");
				} else if (count > 0) {
					getCommandManager().execute("cursor-next-page");
				}
			}
		};

		this.addMouseWheelListener(mouseWheelListener);
		table.addMouseWheelListener(mouseWheelListener);

		MouseListener mouseListener = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				requestFocusInWindow();
			}
		};
		addMouseListener(mouseListener);
		table.addMouseListener(mouseListener);
		functionPanel.addMouseListener(mouseListener);
		for (int i = 0; i < labels.length; i++) {
			labels[i].addMouseListener(mouseListener);
		}

		// 枠線色
		LineGrid.setGroupColor(Color.GRAY, JFD2.this);
		addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				changeColor();
			}

			public void focusLost(FocusEvent e) {
				changeColor();
			}
		});

		externalCommandPanel.setVisible(false);
		addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				externalCommandPanel.setVisible(false);
			}
		});
	}

	private void changeColor() {
		JFDOwner owner = getJFDOwner();
		if(owner != null) {
			JFDComponent comp = owner.getActiveComponent();
			LineGrid.setGroupColor(comp != null
					&& JFD2.this.equals(comp) ? focusedLineColor
					: unfocusedLineColor, JFD2.this);
		}
		repaint();
	}
	
	/**
	 * レイアウトを行う。
	 * 
	 * @throws FileNotFoundException
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws FactoryConfigurationError
	 */
	private void layoutPanel() throws FileNotFoundException, SAXException,
			IOException, ParserConfigurationException,
			FactoryConfigurationError {
		//panel = new HtmlTablePanel(null, "/layout.xml");
		panel = new HtmlTablePanel(layoutNode);
		
		this.setLayout(new BorderLayout());
		this.add(panel, BorderLayout.CENTER);
		panel.addComponent(table, "table");

		HtmlTablePanel titlePanel = new HtmlTablePanel("/layout_title.xml");
		panel.add(titlePanel, new GridBagConstraints(1, 1, 7, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0,0));
		titlePanel.addComponent(labelTitle, "title");
		titlePanel.addComponent(labelTitleLeftPad, "title_left_pad");
		titlePanel.addComponent(labelTitleRightPad, "title_right_pad");

		HtmlTablePanel clockPanel = new HtmlTablePanel("/layout_clock.xml");
		panel.add(clockPanel, new GridBagConstraints(1, 1, 7, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0,0));
		clockPanel.addComponent(clockLabel, "clock");
		clockPanel.addComponent(clockLabelLeftPad, "clock_left_pad");
		clockPanel.addComponent(clockLabelRightPad, "clock_right_pad");

		panel.addComponent(labelPath, "label_path");
		panel.addComponent(labelPathInfo, "info_path");

		panel.addComponent(labelPage, "label_page");
		panel.addComponent(labelPageInfo, "info_page");

		panel.addComponent(labelName, "label_name");
		panel.addComponent(labelNameInfo, "info_name");

		panel.addComponent(labelMark, "label_mark");
		panel.addComponent(labelMarkInfo, "info_mark");
		panel.addComponent(labelMarkSizeInfo, "info_marksize");

		panel.addComponent(labelInfo, "label_info");
		panel.addComponent(labelInfoInfo, "info_info");

		panel.addComponent(labelFiles, "label_filecount");
		panel.addComponent(labelFilesInfo, "info_filecount");
		panel.addComponent(labelFilesSizeInfo, "info_filesize");

		panel.addComponent(labelMessagePad, "message_pad");
		panel.addComponent(labelMessage, "message");
		panel.addComponent(labelIncrementalSearch, "incremental_search_label");
		panel.addComponent(labelIncrementalSearchPad, "incremental_search_pad");

		panel.addComponent(labelTopLeftPad, "pad_top_left");
		panel.addComponent(labelTopRightPad, "pad_top_right");

		panel.addComponent(labelTotal, "label_total");
		panel.addComponent(labelTotalPanel, "info_total");
		
		panel.addComponent(labelUsed, "label_used");
		panel.addComponent(labelUsedPanel, "info_used");
		
		panel.addComponent(labelFree, "label_free");
		panel.addComponent(labelFreePanel, "info_free");
		
		panel.addComponent(verticalTopLeft, "ver_top_left");
		panel.addComponent(verticalTopRight, "ver_top_right");
		panel.addComponent(crossMiddleLeft, "cross_middle_left");
		panel.addComponent(horizonMiddleLeft, "hor_middle_left");
		panel.addComponent(horizonMiddleRight, "hor_middle_right");
		panel.addComponent(crossMiddleRight, "cross_middle_right");
		panel.addComponent(verticalBottomLeft, "ver_bottom_left");
		panel.addComponent(verticalBottomRight, "ver_bottom_right");
		panel.addComponent(crossBottomLeft, "cross_bottom_left");
		panel.addComponent(horizonBottomLeft, "hor_bottom_left");
		panel.addComponent(horizonBottomRight, "hor_bottom_right");
		panel.addComponent(crossBottomRight, "cross_bottom_right");

		panel.addComponent(crossPageLeftTop, "cross_page_left_top");
		panel.addComponent(crossPageTotalTop, "cross_page_total_top");
		panel.addComponent(crossTotalUsedTop, "cross_total_used_top");
		panel.addComponent(crossusedFreeTop, "cross_used_free_top");
		panel.addComponent(crossFreeRightTop, "cross_free_right_top");
		
		panel.addComponent(horizonPageTop, "hor_page_top");
		panel.addComponent(horizonTotalTop, "hor_total_top");
		panel.addComponent(horizonUsedTop, "hor_used_top");
		panel.addComponent(horizonFreeTop, "hor_free_top");

		panel.addComponent(verticalPageLeft, "ver_page_left");
		panel.addComponent(verticalPageTotal, "ver_page_total");
		panel.addComponent(verticalTotalUsed, "ver_total_used");
		panel.addComponent(verticalUsedFree, "ver_used_free");
		panel.addComponent(verticalFreeRight, "ver_free_right");

		panel.addComponent(crossPageLeft, "cross_page_left");
		panel.addComponent(crossPageTotal, "cross_page_total");
		panel.addComponent(crossTotalUsed, "cross_total_used");
		panel.addComponent(crossusedFree, "cross_used_free");
		panel.addComponent(crossFreeRight, "cross_free_right");
		
		panel.addComponent(horizonPage, "hor_page");
		panel.addComponent(horizonTotal, "hor_total");
		panel.addComponent(horizonUsed, "hor_used");
		panel.addComponent(horizonFree, "hor_free");

		panel.addComponent(functionPanel, "functions");

		table.setLayout(new GridBagLayout());
		table.add(externalCommandPanel, new GridBagConstraints(0, 0, 1, 1, 1.0,
				0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(0, 50, 0, 50), 0, 0));

		panel.setComponentZOrder(clockPanel, 0);
		panel.setComponentZOrder(titlePanel, 1);
	}

	/**
	 * @return Returns the model.
	 */
	public JFDModel getModel() {
		return model;
	}

	/**
	 * テーブルの表示列数を指定する。
	 * 
	 * @param columnCount
	 */
	public void setColumnCount(int columnCount) {
		if(isAdjustsColumnCountAuto()) {
			setAdjustsColumnCountAuto(false, 1);
		}
		table.setColumnCount(columnCount);

		getLocalConfiguration().setParam(CONFIG_COLUMNS,
				new Integer(columnCount));
	}

	/**
	 * テーブルの表示列数を取得する。
	 * 
	 * @return
	 */
	public int getColumnCount() {
		return table.getColumnCount();
	}

	/**
	 * テーブルのロウ数を取得する。
	 * 
	 * @return
	 */
	public int getRowCount() {
		return table.getRowCount();
	}

	/**
	 * ページ番号を取得する。
	 * 
	 * @return
	 */
	public int getPage() {
		return table.getListTableModel().getPage();
	}

	/**
	 * フォントを設定する。
	 */
	public void setLabelFont(Font font) {
		for (int i = 0; i < labels.length; i++) {
			labels[i].setFont(font);
		}
		renderer.setLabelFont(font);
		functionPanel.setLabelFont(font);
		updateRowHeight();

		LineGrid.setGroupFont(font, this);
		
	}

	/**
	 * テーブルの1行の高さを調整する。
	 *
	 */
	private void updateRowHeight() {
		table.setRowHeight(renderer.getPreferredRowHeight()
				+ ((Integer) getCommonConfiguration().getParam("row-inset",
						new Integer(0))).intValue());
	}
	
	/**
	 * JComponent#processKeyEventの拡張。 キー入力を元に、登録されたコマンドを実行する。
	 */
	public void processKeyEvent(KeyEvent e) {
		KeyStroke keyStroke = KeyStrokeMap.getKeyStrokeForEvent(e);
		
		if (!isLocked()) {
			if(incrementalSearchMode) {
				searcher.processKeyEvent(e);
			}
			functionPanel.processKey(e);
			if(!e.isConsumed()) {
				if (commandManager.execute(keyStroke)) {
					e.consume();
				} else {
					processKeyEventForExtCommand(e, keyStroke);
				}
			}
		} else {
			// ロックされている場合、エスケープキーのみ有効
			if (keyStroke.getKeyCode() == KeyEvent.VK_ESCAPE
					&& commandManager.execute(keyStroke)) {
				e.consume();
			}
		}

		/*
		 * JFDOwner owner = getJFDOwner(); if(!e.isConsumed() && owner != null) {
		 * owner.getCommandManager().execute(keyStroke); }
		 */
		super.processKeyEvent(e);
	}

	private void processKeyEventForExtCommand(KeyEvent e, KeyStroke keyStroke) {
		int keyCode = keyStroke.getKeyCode();
		if (keyStroke.getKeyEventType() == KeyEvent.KEY_PRESSED
				&& keyCode == KeyEvent.VK_ALT) {
			externalCommandPanel.setVisible(true);
			e.consume();
			return;
		}

		if (keyStroke.getKeyEventType() == KeyEvent.KEY_RELEASED
				&& keyCode == KeyEvent.VK_ALT) {
			externalCommandPanel.setVisible(false);
			externalCommandPanel.initSet();
			e.consume();
			return;
		}

		if (keyStroke.getKeyEventType() == KeyEvent.KEY_PRESSED
				&& keyCode == KeyEvent.VK_SHIFT
				&& (keyStroke.getModifiers() & KeyEvent.ALT_MASK) != 0
				&& (keyStroke.getModifiers() & KeyEvent.CTRL_MASK) == 0) {
			externalCommandPanel.increaseSet();
			return;
		}

		if (keyStroke.getKeyEventType() == KeyEvent.KEY_RELEASED
				&& keyCode == KeyEvent.VK_SHIFT
				&& (keyStroke.getModifiers() & KeyEvent.ALT_MASK) != 0
				&& (keyStroke.getModifiers() & KeyEvent.CTRL_MASK) == 0) {
			externalCommandPanel.initSet();
			return;
		}

		if (keyStroke.getKeyEventType() == KeyEvent.KEY_PRESSED
				&& (keyStroke.getModifiers() & KeyEvent.ALT_MASK) != 0
				&& (keyStroke.getModifiers() & KeyEvent.CTRL_MASK) == 0
//				&& (keyStroke.getModifiers() & KeyEvent.SHIFT_MASK) == 0
				&& KeyEvent.VK_A <= keyCode && keyCode <= KeyEvent.VK_Z) {
			int set = externalCommandPanel.getSet();
			int number = keyCode - KeyEvent.VK_A;
			ExternalCommandManager.getInstance().execute(set, number, this);
		}
	}

	/**
	 * 操作をロック、解除する。
	 * 
	 * @param locked
	 *            trueならロック
	 */
	public synchronized void setLocked(boolean locked) {
		this.locked = locked;
	}

	private synchronized boolean isLocked() {
		return locked;
	}

	/**
	 * コマンドマネージャを取得する。
	 * 
	 */
	public CommandManager getCommandManager() {
		return commandManager;
	}

	/**
	 * 共通設定を取得する。
	 * 
	 * @return
	 */
	public Configuration getCommonConfiguration() {
		return commonConfig;
	}

	/**
	 * 個別設定を取得する。
	 * 
	 * @return
	 */
	public Configuration getLocalConfiguration() {
		return localConfig;
	}

	/**
	 * 一時設定を取得する
	 */
	public Configuration getTemporaryConfiguration() {
		return tempConfig;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nullfish.app.jfd2.Initable#init(com.nullfish.lib.vfs.VFile)
	 */
	public void init(VFile baseDir) throws VFSException {
		commandManager.init(baseDir);
		functionPanel.init(baseDir);
		searcher.init(baseDir);

		try {
			localConfig.load(baseDir.getChild(JFD.LOCAL_PARAM_FILE));
		} catch (VFSException e) {
			throw e;
		} catch (Exception e) {
			throw new JFDException(JFDResource.MESSAGES
					.getString("read_config_failed"), null);
		}

		try {
			commonConfig = Configuration.getInstance(baseDir
					.getChild(JFD.COMMON_PARAM_FILE));
		} catch (VFSException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new JFDException(JFDResource.MESSAGES
					.getString("read_config_failed"), null);
		}

		getModel().setFiltersFile(((Boolean)localConfig.getParam("filters_dot_file", Boolean.FALSE)).booleanValue());
		
		setBackground((Color) commonConfig.getParam("background_color",
				Color.BLACK));
		labelTitle.setBackground((Color) commonConfig.getParam("background_color",
				Color.BLACK));
		labelTitleLeftPad.setBackground((Color) commonConfig.getParam("background_color",
				Color.BLACK));
		labelTitleRightPad.setBackground((Color) commonConfig.getParam("background_color",
				Color.BLACK));
		clockLabel.setBackground((Color) commonConfig.getParam("background_color",
				Color.BLACK));
		clockLabelLeftPad.setBackground((Color) commonConfig.getParam("background_color",
				Color.BLACK));
		clockLabelRightPad.setBackground((Color) commonConfig.getParam("background_color",
				Color.BLACK));
//		labelTitle.setBackground((Color) commonConfig.getParam("background_color",
//				Color.BLACK));
		labelTitleLeftPad.setBackground((Color) commonConfig.getParam("background_color",
				Color.BLACK));
		labelTitleRightPad.setBackground((Color) commonConfig.getParam("background_color",
				Color.BLACK));
		clockLabel.setBackground((Color) commonConfig.getParam("background_color",
				Color.BLACK));
		clockLabelLeftPad.setBackground((Color) commonConfig.getParam("background_color",
				Color.BLACK));
		clockLabelRightPad.setBackground((Color) commonConfig.getParam("background_color",
				Color.BLACK));

		RendererMode rendererMode = (RendererMode) localConfig.getParam(
				CONFIG_RENDERER_MODE, RendererMode.TYPE_1);
		setRendererMode(rendererMode);
		
		showsRepativePath = ((Boolean)localConfig.getParam(CONFIG_SHOWS_RELATIVE_PATH, Boolean.TRUE)).booleanValue();

		setColumnCount(((Integer) localConfig.getParam(CONFIG_COLUMNS,
				new Integer(2))).intValue());

		FileComparator[] comparators = SortUtility.createComparators(this);
		model.setComparator(new JFDComparator(comparators));
		model.setFilter(((String)commonConfig.getParam("filter_regex", "^\\..*")).replaceAll("\\n", "|"));

		initColor();

		initPopup(baseDir);

		initFileSystem();
		
		// 枠線数
		Boolean doubleLine = (Boolean) commonConfig.getParam("double_line",
				Boolean.TRUE);
		LineGrid.setGroupDoubleLine(doubleLine == null ? true : doubleLine
				.booleanValue(), this);

		CommandExecuter.getInstance().init(this);

		aliaseManager.init(baseDir);

		externalCommandPanel.init(baseDir);

		setLabelFont((Font) commonConfig.getParam("label_font", new Font(
				"Monospaced", Font.PLAIN, 12)));
		
		renderer.setExtensionColorMap((Map) commonConfig.getParam("color_map", new HashMap()));
	}

	// 色関連初期化
	private void initColor() throws VFSException {
		renderer.initColor();

		Configuration commonConfig = getCommonConfiguration();
		Color messageColor = (Color) commonConfig.getParam("message_color",
				Color.YELLOW);

		labelMessage.setForeground(messageColor);

		labelIncrementalSearch.setForeground((Color) commonConfig.getParam("background_color",
				Color.BLACK));
		labelIncrementalSearch.setBackground(messageColor);
		
		// 枠線色
		focusedLineColor = (Color) commonConfig.getParam("grid_color",
				Color.WHITE);

		unfocusedLineColor = (Color) commonConfig.getParam(
				"grid_color_no_focus", Color.DARK_GRAY);

		LineGrid.setGroupColor(unfocusedLineColor, this);
	}

	private void initPopup(VFile configDir) {
		if (popup == null) {
			ResourceManager resource = new ResourceManager() {
				public String getResource(String name) {
					return JFDResource.LABELS.getString(name);
				}
			};

			CommandCallable callable = new CommandCallable() {
				public void callCommand(String command) {
					getCommandManager().execute(command);
				}
			};

			popup = new XMLPopupMenu(resource, callable);
			popup.addFocusListener(new FocusAdapter() {
				public void focusLost(FocusEvent e) {
					popup.setVisible(false);
				}
			});
		}

		try {
			Document doc = DomCache.getInstance().getDocument(configDir.getChild("popup.xml"));
			popup.convertFromNode(doc.getRootElement());
		} catch (Exception e) {
			e.printStackTrace();
		}

		MouseListener mouseListener = new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				showPopup(e);
			}

			public void mouseReleased(MouseEvent e) {
				showPopup(e);
			}

			private void showPopup(MouseEvent e) {
				if (e.isPopupTrigger()) {
					popup.show(JFD2.this, e.getX(), e.getY());
				}
			}
		};

		addMouseListener(mouseListener);
		table.addMouseListener(mouseListener);

	}

	public void showPopup() {
		Rectangle cellRect = table.getCellRect(table.getSelectedRow(), table.getSelectedColumn(), true);
		popup.show(table, cellRect.x + 5, cellRect.y + cellRect.height + 5);
	}
	
	// 色関連初期化
	private void initFileSystem() throws VFSException {
		Configuration commonConfig = getCommonConfiguration();
		VFS vfs = VFS.getInstance(this);
		
		com.nullfish.lib.vfs.Configuration config = vfs.getConfiguration();
		//	FTP絡み
		config.setDefaultConfig(
						FTPFileSystem.TRANSFER_MODE,
						commonConfig.getParam("ftp_transfer_mode", FTPFileSystem.TRANSFER_MODE_EXTENSION));
		config.setDefaultConfig(
				FTPFileSystem.CONFIG_ASCII_MODE_EXTENSION,
				commonConfig.getParam("auto_ascii_mode_extension",
						new ArrayList()));
		config.setDefaultConfig(
				FTPFileSystem.CONFIG_PASSIVE,
				commonConfig.getParam("passive_mode",
						Boolean.FALSE));
		config.setDefaultConfig(
				FTPFileSystem.CONFIG_USE_PROXY,
				commonConfig.getParam("ftp_use_proxy",
						Boolean.FALSE));
		config.setDefaultConfig(
				FTPFileSystem.CONFIG_PROXY_HOST,
				commonConfig.getParam("ftp_proxy_host",
						null));
		config.setDefaultConfig(
				FTPFileSystem.CONFIG_PROXY_HOST,
				commonConfig.getParam("ftp_proxy_port",
						null));
		config.setDefaultConfig(
				FTPFileSystem.CONFIG_ENCODE,
				commonConfig.getParam("ftp_encoding",
						"EUC-JP"));
		String fileSystemEncode = (String) commonConfig.getParam("ftp_filesystem_encoding", "");
		String[] lines = fileSystemEncode.split("\n");
		for(int i=0; i<lines.length; i++) {
			String line = lines[i];
			if(line.length() > 0) {
				String[] strs = line.split("\\s");
				if(strs.length == 2) {
					config.setConfig(FTPFileSystem.CONFIG_ENCODE, strs[0], strs[1]);
				}
			}
		}
		
		//	HTTPがらみ
		Boolean useHttpProxy = (Boolean)commonConfig.getParam("http_use_proxy",
				Boolean.FALSE);
		if (useHttpProxy.booleanValue()) {
			config.setDefaultConfig(CommonsHTTPFileSystem.CONFIG_PROXY_SERVER,
					commonConfig.getParam("http_proxy_host", null));
			config.setDefaultConfig(CommonsHTTPFileSystem.CONFIG_PROXY_PORT,
					commonConfig.getParam("http_proxy_port", new Integer(8080)));
		} else {
			config.setDefaultConfig(CommonsHTTPFileSystem.CONFIG_PROXY_SERVER,
					null);
			config.setDefaultConfig(CommonsHTTPFileSystem.CONFIG_PROXY_PORT,
					null);
		}

		//	ZIPがらみ
		config.setDefaultConfig(
				ZIPFileSystem.CONFIG_FILE_ENCODING,
				commonConfig.getParam("zip_encoding",
						"JISAutoDetect"));
		
		//	タグがらみ
		config.setDefaultConfig(
				TagDataBase.CONFIG_USE_TAG,
				commonConfig.getParam("use_tag",
						"true"));
		config.setDefaultConfig(
				TagDataBase.CONFIG_FILEFISH_DB_DIR,
				commonConfig.getParam("tag_db_dir",
						null));
		
		vfs.configChanged();
	}

	public void dispose() {
		JFDOwner owner = getJFDOwner();
		if (owner != null) {
			VFile configDir = owner.getConfigDirectory();
			try {
				save(configDir);
			} catch (VFSException e) {
				e.printStackTrace();
			}
		}

		VFS.close(this);
		model.dispose();
		model = null;
		listModel = null;
		cursorModel = null;
		commandManager = null;
		jfdOwner = null;
		renderer = null;

		clockLabel.stop();
		messageUpdater.setManipulation(null);
		messageUpdater = null;

		thumbnailCache.clearTask();
		thumbnailCache.stop();

		LineGrid.removeGroup(this);
		
		labelTotalPanel.dispose();
		labelUsedPanel.dispose();
		labelFreePanel.dispose();
		
		removeAll();
	}

	public AliaseManager getAliaseManager() {
		return aliaseManager;
	}

	public void save(VFile baseDir) throws VFSException {
		getLocalConfiguration().setParam(CONFIG_LAST_OPENED,
				getModel().getCurrentDirectory().getAbsolutePath());

		try {
			VFile commonTemp = baseDir.getChild(JFD.COMMON_PARAM_FILE + ".tmp");
			if (!commonTemp.exists()) {
				commonTemp.createFile();
			}

			if(getCommonConfiguration().isUpdatedAfterSave()) {
				getCommonConfiguration().save(commonTemp.getOutputStream());
				VFile commonFile = baseDir.getChild(JFD.COMMON_PARAM_FILE);
				commonTemp.moveTo(commonFile);
			}
			
			VFile localTemp = baseDir.getChild(JFD.LOCAL_PARAM_FILE + ".tmp");
			if (!localTemp.exists()) {
				localTemp.createFile();
			}
			getLocalConfiguration().save(localTemp.getOutputStream());
			VFile localFile = baseDir.getChild(JFD.LOCAL_PARAM_FILE);
			localTemp.moveTo(localFile);
		} catch (IOException e) {
			throw new VFSIOException(e);
		}
	}

	/**
	 * 現在のファイル表示モードを設定する。
	 * 
	 */
	public void setRendererMode(RendererMode mode) {
		renderer.setMode(mode);
		localConfig.setParam(
				CONFIG_RENDERER_MODE, mode);

		table.repaint();
	}

	/**
	 * サムネイル表示非表示を切り替える。
	 * 
	 */
	public void setThumbnailVisible(boolean visible) {
		renderer.setThumbnailVisible(visible);
		setAdjustsColumnCountAuto(visible, 150);
	}

	/**
	 * サムネイルが表示可能か取得する。
	 * @return
	 */
	public boolean isThumbnailVisible() {
		return renderer.isThumbnailVisible();
	}
	
	private boolean adjustsColumnCountAuto = false;
	
	/**
	 * 最小カラム幅に合わせて自動で列数調整を行うか設定する
	 * @param bool	trueなら自動で列数調整する
	 * @param minWidth	最小のカラム幅
	 */
	public void setAdjustsColumnCountAuto(boolean adjustsColumnWidthAuto, int minWidth) {
		if(isThumbnailVisible() && !adjustsColumnWidthAuto) {
			setThumbnailVisible(false);
			return;
		}
		this.adjustsColumnCountAuto = adjustsColumnWidthAuto;
		table.setPreferredColumnWidth(minWidth);
		updateRowHeight();
		table.setAutoAdjustColumnCount(adjustsColumnWidthAuto);
		table.repaint();
	}
	
	public boolean isAdjustsColumnCountAuto() {
		return adjustsColumnCountAuto;
	}

	/**
	 * 空のダイアログを生成する。
	 * 
	 * @return
	 */
	public JFDDialog createDialog() {
		Container owner = UIUtilities.getTopLevelOwner(this);
		JFDDialog rtn;
		if (owner instanceof Frame) {
			rtn = new JFDDialog((Frame) owner, true, this);
		} else if (owner instanceof Dialog) {
			rtn = new JFDDialog((Dialog) owner, true, this);
		} else {
			rtn = new JFDDialog((Frame) null, true, this);

		}

		return rtn;
	}

	/**
	 * 下に表示するメッセージをセットする。
	 * 
	 * @param message
	 */
	public void setMessage(final String message) {
		Runnable runnable = new Runnable() {
			public void run() {
				labelMessage.setText(message);
			}
		};

		ThreadSafeUtilities.executeRunnable(runnable);
	}

	/**
	 * 下に表示するメッセージを制限時間付きでセットする。
	 * 
	 * @param message
	 * @param time
	 */
	public void setMessage(final String message, final long time) {
		Thread thread = new Thread() {
			public void run() {
				setMessage(message);
				try {
					sleep(time);
				} catch (InterruptedException e) {
				}

				if(getMessageThread() == this) {
					setMessage("");
				}
			}
		};

		setMessageThread(thread);
		thread.start();
	}
	
	/**
	 * メッセージクリアスレッド
	 */
	private Thread messageThread;
	private synchronized void setMessageThread(Thread messageThread) {
		this.messageThread = messageThread;
	}
	
	private synchronized Thread getMessageThread() {
		return messageThread;
	}

	/**
	 * 主操作を設定する。
	 * 
	 * @param command
	 */
	public void setPrimaryCommand(Command command) {
		primaryCommand = command;
		messageUpdater.setManipulation(command);
	}

	/**
	 * 主操作を取得する。
	 */
	public Command getPrimaryCommand() {
		return primaryCommand;
	}

	public ProgressViewer getProgressViewer() {
		return progressViewerFactory.getProgressViewer();
	}

	public void setProgressViewerFactory(ProgressViewerFactory factory) {
		this.progressViewerFactory = factory;
	}

	/**
	 * ハッシュ値を求める
	 */
	public int hashCode() {
		return id;
	}

	/**
	 * Object#equalsのオーバーライド
	 */
	public boolean equals(Object o) {
		return this == o;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nullfish.app.jfd2.JFDComponent#setOwner(com.nullfish.app.jfd2.ui.container2.JFDOwner)
	 */
	public void setOwner(com.nullfish.app.jfd2.ui.container2.JFDOwner owner) {
		this.jfdOwner = owner;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nullfish.app.jfd2.JFDComponent#getJFDOwner()
	 */
	public com.nullfish.app.jfd2.ui.container2.JFDOwner getJFDOwner() {
		return jfdOwner;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nullfish.app.jfd2.JFDComponent#getComponent()
	 */
	public Component getComponent() {
		return this;
	}

	/**
	 * ポップアップメニューを取得する。
	 * 
	 * @return
	 */
	public XMLPopupMenu getPopup() {
		return popup;
	}

	protected void setBgImage(Image image, String alignName) {
		panel.setBgImage(image, alignName);
	}

	public void setShowsRelativePath(boolean bool) {
		this.showsRepativePath = bool;
		localConfig.setParam(CONFIG_SHOWS_RELATIVE_PATH, Boolean.valueOf(bool));

		table.repaint();
	}

	public boolean showsRelativePath() {
		return showsRepativePath;
	}

	public synchronized void setIncrementalSearchMode(boolean bool) {
		this.incrementalSearchMode = bool;
		labelIncrementalSearch.setVisible(incrementalSearchMode);
	}
	
	public synchronized boolean isIncrementalSearchMode() {
		return incrementalSearchMode;
	}

	public static void initKeyMap(VFile configDir) {
		try {
			Configuration commonConfig = Configuration.getInstance(configDir
					.getChild(JFD.COMMON_PARAM_FILE));
			String keyMapPath = (String) commonConfig.getParam("key_map", null);
			if(keyMapPath != null && keyMapPath.length() > 0) {
				try {
					VFile keyMapFile = VFS.getInstance().getFile(keyMapPath);
					if(keyMapFile.exists()) {
						KeyStrokeMap.getInstance().init(keyMapFile);
					}
				} catch (VFSException ex) {
					ex.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (VFSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public IncrementalSearcher getIncrementalSearcher() {
		return searcher;
	}
//	
//	public void finalize() throws Throwable {
//		System.out.println("finalize JFD2");
//		super.finalize();
//	}
}
