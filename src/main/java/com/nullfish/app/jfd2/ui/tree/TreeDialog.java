package com.nullfish.app.jfd2.ui.tree;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.resource.JFDResource;
import com.nullfish.lib.tablelayout.HtmlTablePanel;
import com.nullfish.lib.ui.OneKeyButton;
import com.nullfish.lib.ui.ThreadSafeUtilities;
import com.nullfish.lib.ui.filetree.VFileTree;
import com.nullfish.lib.vfs.VFS;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;

public class TreeDialog extends JDialog {
	private JFD jfd;

	private VFile selectedFile;

	private VFileTree tree = new VFileTree();

	private JList filesList = new JList();
	private DefaultListModel listModel = new DefaultListModel();
	private JScrollPane fileListScrollPane = new JScrollPane(filesList);

	private JLabel pathLabel = new JLabel();

	private OneKeyButton okButton = new OneKeyButton(JFDResource.LABELS
			.getString("ok"), KeyStroke.getKeyStroke(KeyEvent.VK_O, 0));
	private OneKeyButton cancelButton = new OneKeyButton(JFDResource.LABELS
			.getString("cancel"), KeyStroke.getKeyStroke(KeyEvent.VK_C, 0));

	private HtmlTablePanel panel;

	public TreeDialog(Frame owner, JFD jfd) {
		super(owner, true);
		this.jfd = jfd;
		init();
	}

	public TreeDialog(Dialog owner, JFD jfd) {
		super(owner, true);
		this.jfd = jfd;
		init();
	}

	private void init() {
		try {
			setTitle("jFD2 - Tree");
			setResizable(false);
			panel = new HtmlTablePanel(
					"/com/nullfish/app/jfd2/ui/tree/layout.xml");
			add(panel);
			filesList.setModel(listModel);
			filesList.setCellRenderer(new DefaultListCellRenderer() {
				public Component getListCellRendererComponent(JList list,
						Object value, int index, boolean isSelected,
						boolean cellHasFocus) {
					return super.getListCellRendererComponent(list,
							((VFile) value).getName(), index, isSelected,
							cellHasFocus);
				}
			});
			fileListScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

			tree.addTreeSelectionListener(new TreeSelectionListener() {
				public void valueChanged(TreeSelectionEvent e) {
					VFile file = tree.getSelectedFile();
					pathLabel
							.setText(file != null ? file.getSecurePath() : " ");
					updateFiles(file);
				}
			});

			panel.layoutByMemberName(this);

			tree.setRoot(VFS.getInstance(jfd).getFile("root:///"));
			tree.setSelectedFile(jfd.getModel().getCurrentDirectory());

			okButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					selectedFile = tree.getSelectedFile();
					if(selectedFile.getInnerRoot() != null) {
						selectedFile = selectedFile.getInnerRoot();
					}
					close();
				}
			});

			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					close();
				}
			});

			addWindowListener(new WindowAdapter() {
				public void windowClosed(WindowEvent e) {
					close();
				}
			});

			panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
					KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "close");
			panel.getActionMap().put("close", new AbstractAction() {
				public void actionPerformed(ActionEvent arg0) {
					close();
				}
			});
			panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
					KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "select");
			panel.getActionMap().put("select", new AbstractAction() {
				public void actionPerformed(ActionEvent arg0) {
					selectedFile = tree.getSelectedFile();
					if(selectedFile.getInnerRoot() != null) {
						selectedFile = selectedFile.getInnerRoot();
					}
					close();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void close() {
		setVisible(false);
		dispose();
	}

	private void updateFiles(final VFile file) {
		Thread thread = new Thread() {
			public void run() {
				try {
					VFile[] files = file != null ? (file.getInnerRoot() != null ? file
							.getInnerRoot().getChildren()
							: file.getChildren())
							: new VFile[0];
					final List filesOnly = new ArrayList(); 
					for (int i = 0; i < files.length; i++) {
						VFile f = files[i];
						try {
							if (f.isFile()) {
								filesOnly.add(f);
							}
						} catch (VFSException e) {
							e.printStackTrace();
						}
					}

					ThreadSafeUtilities.executeRunnable(new Runnable() {
						public void run() {
							if(file == null) {
								listModel.clear();
								return;
							}
							if (!file.equals(tree.getSelectedFile())) {
								return;
							}
							listModel.clear();
							for (int i = 0; i < filesOnly.size(); i++) {
								listModel.addElement(filesOnly.get(i));
							}
						}
					});
				} catch (VFSException e) {
					e.printStackTrace();
				}
			}
		};

		thread.start();
	}

	public void dispose() {
		tree.dispose();
	}

	public void setSelectedFile(VFile file) {
		tree.setSelectedFile(file);
	}

	public VFile getSelectedFile() {
		return selectedFile;
	}
}
