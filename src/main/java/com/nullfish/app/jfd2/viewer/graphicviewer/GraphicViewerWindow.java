package com.nullfish.app.jfd2.viewer.graphicviewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.RepaintManager;

import org.jdom.Document;

import com.nullfish.app.jfd2.resource.JFDResource;
import com.nullfish.app.jfd2.util.DomCache;
import com.nullfish.app.jfd2.viewer.OwnerCommandCallerPanel;
import com.nullfish.app.jfd2.viewer.action.CloseAction;
import com.nullfish.app.jfd2.viewer.action.OpenOrCloseAction;
import com.nullfish.lib.command.CommandCallable;
import com.nullfish.lib.keymap.KeyStrokeMap;
import com.nullfish.lib.resource.ResourceManager;
import com.nullfish.lib.ui.xml_menu.XMLPopupMenu;
import com.nullfish.lib.vfs.VFS;
import com.nullfish.lib.vfs.VFile;

public class GraphicViewerWindow extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7103926932589535750L;

	private JScrollPane scroll = new JScrollPane();

	private ImageComponent imageComponent = new ImageComponent();

	private OwnerCommandCallerPanel rootPanel = new OwnerCommandCallerPanel();

	private JLabel nameLabel = new JLabel();

	private boolean fullScreen = false;

	private GraphicViewer viewer;

	private XMLPopupMenu popup;

	public static final String CLOSE = "close";

	public static final String FULL_SCREEN = "full_screen";

	public static final String SLIDESHOW = "slideshow";

	public static final String POPUP_FILE = "classpath:///resources/graphic_viewer_menu.xml";

	public GraphicViewerWindow(GraphicViewer viewer) {
		this.viewer = viewer;
		initGui();
		initPopup();
		initKey();
	}

	private void initGui() {
		scroll.getViewport().setBackground(Color.BLACK);

		imageComponent.setFocusable(true);
		imageComponent.addImageComponentListener(new ImageComponentListener() {
			public void imageComponentUpdated(ImageComponent c) {
				scroll.getVerticalScrollBar().setValue(0);
				scroll.getHorizontalScrollBar().setValue(0);

				pack();
			}
		});
		imageComponent.setBackground(Color.BLACK);

		imageComponent.setLayout(new GridBagLayout());
		imageComponent.add(nameLabel, new GridBagConstraints(0, 0, 1, 1, 0.0,
				0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 0, 0));
		nameLabel.setAlignmentY(JComponent.TOP_ALIGNMENT);
		nameLabel.setVisible(false);
		nameLabel.setForeground(Color.YELLOW);

		JPanel pad = new JPanel();
		pad.setOpaque(false);
		imageComponent.add(pad, new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 0, 0));

		rootPanel.setLayout(new BorderLayout());
		rootPanel.setViewer(viewer);
		try {
			rootPanel.init(VFS.getInstance().getFile(
					"classpath:///graphic_viewer_keys.xml"));
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		setContentPane(rootPanel);
		scroll.setViewportView(imageComponent);
		scroll
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroll
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		scroll.getVerticalScrollBar().setBlockIncrement(100);
		scroll.getHorizontalScrollBar().setBlockIncrement(100);

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		rootPanel.add(scroll);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				viewer.close();
			}
			
			public void windowActivated(WindowEvent e) {
				requestFocus();
				imageComponent.requestFocusInWindow();
			}
			
		});
	}

	private void initPopup() {
		try {
			ResourceManager resource = new ResourceManager() {
				public String getResource(String name) {
					return JFDResource.LABELS.getString(name);
				}
			};
			final Map commandMap = new HashMap();
			CommandCallable callable = new CommandCallable() {
				public void callCommand(String command) {
					Action action = (Action) commandMap.get(command);
					if (action == null) {
						action = imageComponent.getAction(command);
					}
					if (action != null) {
						action.actionPerformed(new ActionEvent(
								GraphicViewerWindow.this,
								ActionEvent.ACTION_PERFORMED, null));
						return;
					}

					viewer.getJFD().getCommandManager().execute(command);
				}
			};

			commandMap.put(CLOSE, new AbstractAction() {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					viewer.close();
				}
			});

			commandMap.put(FULL_SCREEN, new ChangeFullScreenAction());
			commandMap.put(SLIDESHOW, new SlideshowAction());

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
//					showPopup(e);
				}
				
				public void mouseReleased(MouseEvent e) {
					boolean mouseButtonOperation = ((Boolean)viewer.getJFD().getCommonConfiguration().getParam("graphic_viewer_mouse_button_operate", Boolean.TRUE)).booleanValue();
					if(!mouseButtonOperation) {
						showPopup(e);
					} else {
						if(e.getButton() == MouseEvent.BUTTON1) {
							viewer.getJFD().getCommandManager().execute("cursor-up");
						} else {
							viewer.getJFD().getCommandManager().execute("cursor-down");
						}
					}
				}
				
				private void showPopup(MouseEvent e) {
					if(e.isPopupTrigger()) {
						popup.show(imageComponent, e.getX(), e.getY());
					}
				}
			};
			
			imageComponent.addMouseListener(mouseListener);
		} catch (Exception e) {
			//	ここには来ない。はず。
			e.printStackTrace();
		}
	}

	public void pack() {
		invalidate();
		super.pack();
		Dimension thisSize = getSize();
		Dimension screenSize = java.awt.Toolkit.getDefaultToolkit()
				.getScreenSize();

		thisSize.height = thisSize.height > screenSize.height ? screenSize.height
				: thisSize.height;
		thisSize.width = thisSize.width > screenSize.width ? screenSize.width
				: thisSize.width;

		setSize(thisSize);
		validate();
	}

	private void initKey() {
		/*
		 * Object[] keys = scroll.getActionMap().allKeys(); for (int i = 0; i <
		 * keys.length; i++) { System.out.println(keys[i]); }
		 */

		InputMap im = scroll
				.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		im.setParent(null);
		im.put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_UP, KeyEvent.SHIFT_MASK),
				"unitScrollUp");
		im.put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_DOWN, KeyEvent.SHIFT_MASK),
				"unitScrollDown");
		im.put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.SHIFT_MASK),
				"unitScrollLeft");
		im.put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.SHIFT_MASK),
				"unitScrollRight");
		scroll.getInputMap(JComponent.WHEN_FOCUSED).setParent(null);
		scroll.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).setParent(null);
		
		rootPanel.getActionMap().put("viewer_close", new CloseAction(viewer));
		rootPanel.getActionMap().put(SLIDESHOW, new SlideshowAction());
		rootPanel.getActionMap().put("viewer_open_close",
				new OpenOrCloseAction(viewer));
		rootPanel.getActionMap().put("full_screen",
				new ChangeFullScreenAction());
		rootPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
				.put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_A, 0), "full_screen");
		rootPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
				.put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_S, 0), SLIDESHOW);
			
		imageComponent.addMouseWheelListener(new MouseWheelListener() {
		    public void mouseWheelMoved(MouseWheelEvent e) {
		    	if(fullScreen) {
		    		if(e.getWheelRotation() > 0) {
		    			viewer.getJFD().getCommandManager().execute("cursor-down");
		    		} else {
		    			viewer.getJFD().getCommandManager().execute("cursor-up");
		    		}
		    		
		    		e.consume();
		    	}
		    }
		});
	}

	public void open(VFile file) {
		setTitle(file != null ? file.getSecurePath() : "");
		nameLabel.setText(file != null ? file.getName() : "");
		this.setVisible(true);
		imageComponent.setImage(file);
		imageComponent.requestFocusInWindow();
	}

	public void close() {
		imageComponent.setImage((Image) null);
		imageComponent.initShowingMode();
		this.setVisible(false);
	}
	class ChangeFullScreenAction extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent arg0) {
			if(!fullScreen) {
				imageComponent.setExpands(false);
				fullScreen = true;
			} else {
				if(!imageComponent.isExpands()) {
					imageComponent.setExpands(true);
					return;
				}
				fullScreen = false;
			}
			
			setVisible(false);
			dispose();
			setUndecorated(fullScreen);
			scroll
					.setHorizontalScrollBarPolicy(fullScreen ? JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
							: JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			scroll
					.setVerticalScrollBarPolicy(fullScreen ? JScrollPane.VERTICAL_SCROLLBAR_NEVER
							: JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			
			if (fullScreen) {
				setExtendedState(JFrame.MAXIMIZED_BOTH);
				Dimension size = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
				Insets inset = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());
				size.height = size.height - inset.top - inset.bottom; 
				size.width = size.width - inset.left - inset.right; 
				imageComponent.fixSize(size);
			} else {
				setExtendedState(JFrame.NORMAL);
				imageComponent.unfixSize();
			}

			nameLabel.setVisible(fullScreen);
			
			imageComponent.revalidate();
			RepaintManager.currentManager(scroll).markCompletelyDirty(scroll);
			scroll.getViewport().revalidate();
			
			pack();
			setVisible(true);
			imageComponent.repaint();
		}
	}

	private class SlideshowAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			viewer.startSlideShow();
		}
	};
	
	public boolean isFullScreen() {
		return fullScreen;
	}
	
	public void changeFullScreen() {
		new ChangeFullScreenAction().actionPerformed(null);
	}
}
