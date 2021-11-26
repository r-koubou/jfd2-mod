/*
 * Created on 2004/05/18
 *
 */
package com.nullfish.app.jfd2.ui.table;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.JFDModel;
import com.nullfish.app.jfd2.config.Configuration;
import com.nullfish.app.jfd2.ui.labels.JFDLabelUI;
import com.nullfish.app.jfd2.util.ShortCutFile;
import com.nullfish.app.jfd2.util.TagUtil;
import com.nullfish.app.jfd2.util.thumbnail.ThumbnailCache;
import com.nullfish.lib.ui.list_table.ListTableModel;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;
import com.nullfish.lib.vfs.impl.filelist.FileListFileFactory;
import com.nullfish.lib.vfs.impl.local.LocalFile;
import com.nullfish.lib.vfs.permission.ClassFileAccess;
import com.nullfish.lib.vfs.permission.PermissionType;

/**
 * ファイルを表示するセルレンダラ。
 * 
 * @author shunji
 */
public class JFDCellRenderer extends JPanel implements TableCellRenderer {

	private JFD jfd;
	private JFDModel jfdModel;
	private ListTableModel listTableModel;

	private RendererMode mode;
	
	private Map extensionColorMap = new HashMap();
	
	/**
	 * 非フォーカス時にカーソルを隠すかどうかのフラグ
	 */
	private boolean hidesCursor = true;
	
	/**
	 * 非フォーカス時に文字色を変えるか同化のフラグ
	 */
	private boolean changesColor = true;
	
	private ImagePanel thumbnailPanel = new ImagePanel();
	JPanel paddingPanel = new JPanel(new GridBagLayout());
	
	private boolean nativeIconAvailable = true;
	
	/**
	 * サムネイルキャッシュ
	 */
	private ThumbnailCache thumbnailCache;
	
	private static Set imageExtensions = new HashSet();
	{
		imageExtensions.add("jpg");
		imageExtensions.add("jpeg");
		imageExtensions.add("gif");
		imageExtensions.add("png");
	}
	
	JLabel selectionLabel = new JLabel();
	JLabel nameLabel = new JLabel();
	JLabel sizeLabel = new JLabel();
	JLabel dateLabel = new JLabel();
	JLabel tagLabel = new JLabel();

	GridBagLayout layout = new GridBagLayout();

	DateFormat dateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);

	Color bgColor = Color.BLACK;
	Color defaultLabelColor = Color.WHITE;
//    Color markedColor = Color.white;
    Color currentDirectoryColor = Color.white;
    Color subDirectoryColor = Color.BLUE;
    Color parentDirectoryColor = Color.WHITE;
    Color readOnlyColor = Color.YELLOW;
    Color linkColor = Color.BLUE;
    Color listColor = Color.MAGENTA;
    Color unfocusedColor = Color.GRAY;
    Color tagColor = Color.YELLOW;

	private DecimalFormat sizeFormat = new DecimalFormat("###,###,###,##0 ");
	
    /**
	 * ディレクトリ表示文字列
	 */
	public static final String DIR_STR = "<dir> ";

    /**
	 * ショートカット表示文字列
	 */
	public static final String LINK_STR = "<lnk> ";

    /**
	 * ショートカット表示文字列
	 */
	public static final String LIST_STR = "<lst> ";

	/**
	 * デフォルトの日付フォーマット
	 */
	public static final String DEFAULT_DATE_FORMAT = "yy-MM-dd HH:mm ";

	/**
	 * 値が不明な場合に表示される文字列
	 */
	public static final String NOT_AVAILABLE = "N/A ";

	/**
	 * コンストラクタ
	 * 
	 * @param jfdModel
	 * @param selectionModel
	 */
	public JFDCellRenderer(JFD jfd, ListTableModel listTableModel, ThumbnailCache thumbnailCache) {
		this.jfd = jfd;
		this.jfdModel = jfd.getModel();
		this.listTableModel = listTableModel;
		this.thumbnailCache = thumbnailCache;
		
		initUi();
		setOpaque(false);
		this.setBackground(bgColor);
		this.setForeground(defaultLabelColor);
		setMode(RendererMode.TYPE_1);
		
		setThumbnailVisible(false);
	}

	public void initUi() {
		setLayout(new BorderLayout());
		paddingPanel.setOpaque(false);
		paddingPanel.add(thumbnailPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0,
						0, 0, 0), 0, 0));
		thumbnailPanel.setPreferredSize(new Dimension(100, 100));
		add(paddingPanel, BorderLayout.CENTER);
		thumbnailPanel.setOpaque(false);
		
		JPanel lowerPanel = new JPanel(new GridBagLayout());
		lowerPanel.setOpaque(false);
		add(lowerPanel, BorderLayout.SOUTH);
		
		lowerPanel.add(selectionLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,
						0, 0, 0), 0, 0));
		JPanel nameTagPanel = new JPanel(new NameTagLayout());
		nameTagPanel.setOpaque(false);
		nameTagPanel.add(nameLabel);
		nameTagPanel.add(tagLabel);
		lowerPanel.add(nameTagPanel, new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,
						0, 0, 0), 0, 0));
		lowerPanel.add(sizeLabel, new GridBagConstraints(3, 1, 1, 1, 0.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,
						0, 0, 0), 0, 0));
		lowerPanel.add(dateLabel, new GridBagConstraints(4, 1, 1, 1, 0.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,
						0, 0, 0), 0, 0));
		
		selectionLabel.setOpaque(false);
		nameLabel.setOpaque(false);
		sizeLabel.setOpaque(false);
		dateLabel.setOpaque(false);

		sizeLabel.setAlignmentX(JLabel.RIGHT_ALIGNMENT);
		
		tagLabel.setForeground(tagColor);
		
		nameLabel.setUI(new JFDLabelUI());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable,
	 *      java.lang.Object, boolean, boolean, int, int)
	 */
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		if (value == null) {
			selectionLabel.setText(" ");
			nameLabel.setText("");
			tagLabel.setText("");
			sizeLabel.setText("");
			dateLabel.setText("");
			initLabelColor(null, null, isSelected);
			thumbnailPanel.setImage(null);
			return this;
		}

		VFile file = (VFile) value;

		VFile current = jfdModel.getCurrentDirectory();
		
		boolean isMountPoint = file.equals(current.getFileSystem().getMountPoint());
		boolean isCurrent = file.equals(current);
		boolean isParent = file.equals(current.getParent());
		
		tagLabel.setText(TagUtil.file2TagString(file));
		if (isMountPoint) {
			isMountPoint = true;
			nameLabel.setText("...");
		} else {
			if(jfd.showsRelativePath()) {
				nameLabel.setText(current.getRelation(file));
			} else {
				if(isCurrent || isParent) {
					nameLabel.setText(current.getRelation(file));
				} else if(file.isRoot()) {
					nameLabel.setText(file.getAbsolutePath());
				} else {
					nameLabel.setText(file.getName());
				}
			}
		}

		//	マーク
		int rowCount = jfd.getRowCount();
		int columnCount = jfd.getColumnCount();
		int page = jfd.getPage();
		int index = (rowCount * columnCount * page) + (rowCount * column) + row;
		setMarked(jfdModel.isMarked(index));
		
		initSizeLabel(file);
		initDateLabel(file);
		initLabelColor(file, current, isSelected);
		initThumbnail(file);

		return this;
	}
	
	public void setThumbnailVisible(boolean visible) {
		thumbnailPanel.setVisible(visible);
		paddingPanel.setVisible(visible);
		
		if(!visible) {
			setMode(getMode());
			
			thumbnailCache.clearTask();
			thumbnailCache.clear();
		} else {
			dateLabel.setVisible(false);
			sizeLabel.setVisible(false);
		}
		
		revalidate();
	}
	
	public boolean isThumbnailVisible() {
		return thumbnailPanel.isVisible();
	}
	
	public void setThumbnailSize(int width, int height) {
		thumbnailPanel.setPreferredSize(new Dimension(width, height));
		thumbnailCache.clear();
	}

	private void setMarked(boolean selected) {
		selectionLabel.setText(selected ? "*" : " ");
	}

	/**
	 * サイズ表示初期化
	 */
	private void initSizeLabel(VFile file) {
		if (!sizeLabel.isVisible()) {
			return;
		}

		try {
			String extension = file.getFileName().getExtension();
			if (file.isDirectory()) {
				sizeLabel.setText(DIR_STR);
			} else if(FileListFileFactory.EXTENSION_FILE_LIST.equals(extension)
					|| FileListFileFactory.EXTENSION_SMART_FILE_LIST.equals(extension)) {
				sizeLabel.setText(LIST_STR);
			} else if(ShortCutFile.EXTENSION.equals(file.getFileName().getExtension())) {
				sizeLabel.setText(LINK_STR);
			} else {
//				sizeLabel.setText(Long.toString(file.getLength()) + " ");
				sizeLabel.setText(sizeFormat.format(file.getLength()));
			}
		} catch (VFSException e) {
			sizeLabel.setText(NOT_AVAILABLE);
		}
	}

	/**
	 * 日付表示初期化
	 * 
	 * @param file
	 */
	private void initDateLabel(VFile file) {
		if (!dateLabel.isVisible()) {
			return;
		}

		try {
			Date date = file.getTimestamp();
			if(date != null && dateFormat != null) {
				dateLabel.setText(dateFormat.format(date));
			} else {
				dateLabel.setText(NOT_AVAILABLE);
			}
		} catch (VFSException e) {
			dateLabel.setText(NOT_AVAILABLE);
		}
	}

	private void initLabelColor(VFile file, VFile current, boolean selected) {
		if (file == null || current == null) {
			setOpaque(false);
			this.setBackground(bgColor);
			return;
		}
		
		Color foreColor;
		Color backColor;
		boolean writable = true;
		boolean isDirectory = false;		
		try {
			isDirectory = file.isDirectory();
			writable = file.getPermission().hasPermission(PermissionType.WRITABLE, ClassFileAccess.ALL);
		} catch (Exception e) {
			writable = false;
		}
		
		String extension = file.getFileName().getExtension();
		boolean noFocus =
			jfd != null
			&& jfd.getJFDOwner() != null
			&& ((jfd.getJFDOwner().getActiveComponent() != null && !jfd.getComponent().equals(jfd.getJFDOwner().getActiveComponent())) || jfd.getJFDOwner().getActiveComponent() == null) ;
		
		Color mappedColor = (Color) extensionColorMap.get(file.getFileName().getLowerExtension());
		if (changesColor && noFocus) {
			foreColor = unfocusedColor;
	    } else if(file.equals(current)) {
			foreColor = currentDirectoryColor;
	    } else if(mappedColor != null) {
	    	foreColor = mappedColor;
		} else if(file.equals(current.getParent())) {
			foreColor = parentDirectoryColor;
		} else if(FileListFileFactory.EXTENSION_FILE_LIST.equals(extension)
				|| FileListFileFactory.EXTENSION_SMART_FILE_LIST.equals(extension)) {
			foreColor = listColor;
		} else if(ShortCutFile.EXTENSION.equals(extension)) {
			foreColor = linkColor;
		} else if(!writable) {
			foreColor = readOnlyColor;
		} else if(isDirectory) {
			foreColor = subDirectoryColor;
		} else {
			foreColor = defaultLabelColor;
		}

		if (selected && (!hidesCursor || !noFocus)) {
			setOpaque(true);
			backColor = foreColor;
			foreColor = bgColor;
		} else {
			setOpaque(false);
			backColor = bgColor;
		}
		
		this.setBackground(backColor);
		selectionLabel.setForeground(foreColor);
		nameLabel.setForeground(foreColor);
		sizeLabel.setForeground(foreColor);
		dateLabel.setForeground(foreColor);
		
		tagLabel.setForeground(selected ? bgColor : tagColor);
	}
	
	/**
	 * サイズ表示初期化
	 */
	private void initThumbnail(VFile file) {
		if (!thumbnailPanel.isVisible()) {
			return;
		}

		try {
			if (file.isDirectory()) {
				setNativeIconIfPossible(file);
/*
				thumbnailPanel.setImage(null);

				if(file instanceof LocalFile) {
					File lfile = ((LocalFile)file).getFile();
					try {
						thumbnailPanel.setImage(sun.awt.shell.ShellFolder.getShellFolder(lfile).getIcon(true));
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
*/
			} else if(isImageFile(file)) {
				thumbnailPanel.setImage(thumbnailCache.getThumbnail(file, listTableModel));
			} else {
				setNativeIconIfPossible(file);

//				thumbnailPanel.setImage(null);
			}
		} catch (VFSException e) {
			e.printStackTrace();
			thumbnailPanel.setImage(null);
		}
	}
	
	private void setNativeIconIfPossible(VFile file) {
		if(nativeIconAvailable && file instanceof LocalFile) {
			File lfile = ((LocalFile)file).getFile();
			try {
				thumbnailPanel.setImage(sun.awt.shell.ShellFolder.getShellFolder(lfile).getIcon(true));
			} catch (FileNotFoundException e) {
				thumbnailPanel.setImage(null);
			} catch (Throwable e) {
				thumbnailPanel.setImage(null);
				nativeIconAvailable = false;
			}
		} else {
			thumbnailPanel.setImage(null);
		}
	}
	
	/**
	 * ファイルがサムネイル表示可能なイメージファイルか判定する。
	 * @param file
	 * @return
	 */
	public boolean isImageFile(VFile file) {
		return imageExtensions.contains(file.getFileName().getLowerExtension());
	}

	public int getPreferredRowHeight() {
		return getFontMetrics(nameLabel.getFont()).getHeight() + 
		(thumbnailPanel.isVisible() ? thumbnailPanel.getPreferredSize().height + 6 : 0);
	}
	
	public void setLabelFont(Font font) {
		selectionLabel.setFont(font);
		nameLabel.setFont(font);
		tagLabel.setFont(font);
		sizeLabel.setFont(font);
		dateLabel.setFont(font);
	}
	
	/**
	 * 表示モードをセットする。
	 * @param mode
	 */
	public void setMode(RendererMode mode) {
		this.mode = mode;
		sizeLabel.setVisible(mode.isSizeVisible());
		dateLabel.setVisible(mode.isDateVisible());
		dateFormat = mode.getDateFormat();
	}
	
	/**
	 * 表示モードを取得する。
	 * @return
	 */
	public RendererMode getMode() {
		return mode;
	}
	
	/**
	 * これでパフォーマンスが上がる、みたいなことがどこかに書いてあったんだが、
	 * 嘘か本当か怪しい。
	 */
    public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {}
    public void firePropertyChange(String propertyName, byte oldValue, byte newValue) {}
    public void firePropertyChange(String propertyName, char oldValue, char newValue) {}
    public void firePropertyChange(String propertyName, double oldValue, double newValue) {}
    public void firePropertyChange(String propertyName, float oldValue, float newValue) {}
    public void firePropertyChange(String propertyName, int oldValue, int newValue) {}
    public void firePropertyChange(String propertyName, long oldValue, long newValue) {}
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {}
    public void firePropertyChange(String propertyName, short oldValue, short newValue) {}
    public void repaint(long tm, int x, int y, int width, int height) {}
    public void repaint(Rectangle r) {}

	/**
	 * 色を初期化する。
	 * @see com.nullfish.app.jfd2.Initable#init(com.nullfish.lib.vfs.VFile)
	 */
	public void initColor() throws VFSException {
		Configuration config = jfd.getCommonConfiguration();

		bgColor = (Color)config.getParam("background_color", Color.BLACK);
		
		defaultLabelColor = (Color)config.getParam("default_label_color", Color.WHITE);

		currentDirectoryColor = (Color)config.getParam("current_directory_color", Color.WHITE);
		
		subDirectoryColor = (Color)config.getParam("sub_directory_color", Color.CYAN);
		
		parentDirectoryColor = (Color)config.getParam("parent_directory_color", Color.WHITE);
		
		readOnlyColor = (Color)config.getParam("read_only_color", Color.YELLOW);
		
		linkColor = (Color)config.getParam("link_color", Color.GREEN);
		
		listColor = (Color)config.getParam("list_color", Color.MAGENTA);
		
		unfocusedColor = (Color)config.getParam("grid_color_no_focus", Color.GRAY);
		
		hidesCursor = ((Boolean)config.getParam("hides_cursor", Boolean.TRUE)).booleanValue();
		
		changesColor = ((Boolean)config.getParam("change_color_with_focus", Boolean.TRUE)).booleanValue();

		tagColor = (Color)config.getParam("tag_color", Color.YELLOW);
	}
	
	public void setExtensionColorMap(Map extensionColorMap) {
		this.extensionColorMap = extensionColorMap;
	}

	private static class NameTagLayout implements LayoutManager2 {
		private List components = new ArrayList();
		
		public void addLayoutComponent(Component comp, Object constraints) {
			components.add(comp);
		}

		public float getLayoutAlignmentX(Container target) {
			return 0;
		}

		public float getLayoutAlignmentY(Container target) {
			return 0;
		}

		public void invalidateLayout(Container target) {
		}

		public Dimension maximumLayoutSize(Container target) {
			return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
		}

		public void addLayoutComponent(String name, Component comp) {
			addLayoutComponent(comp, name);
		}

		public void layoutContainer(Container parent) {
			Dimension size = parent.getSize();
			int x=0;
			for(int i=0; i<components.size(); i++) {
				Component c = (Component) components.get(i);
				Dimension preferredSize = c.getPreferredSize();
				int realWidth = size.width - x < preferredSize.width ? size.width - x : preferredSize.width;
				c.setBounds(x, 0, realWidth, size.height);
				x += realWidth;
			}
		}

		public Dimension minimumLayoutSize(Container parent) {
			return new Dimension(0, 0);
		}

		public Dimension preferredLayoutSize(Container parent) {
			int w = 0;
			int h = 0;
			for(int i=0; i<components.size(); i++) {
				Component c = (Component) components.get(i);
				Dimension size = c.getPreferredSize();
				w += size.width;
				h = h > size.height ? h : size.height;
			}
			
			return new Dimension(w, h);
		}

		public void removeLayoutComponent(Component comp) {
			components.remove(comp);
		}
		
	}
}
