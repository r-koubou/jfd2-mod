package com.nullfish.app.jfd2.ui.shortcut_tree;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.nullfish.app.jfd2.comparator.FileComparator;
import com.nullfish.app.jfd2.comparator.FileTypeComparator;
import com.nullfish.app.jfd2.comparator.JFDComparator;
import com.nullfish.app.jfd2.comparator.NameComparator;
import com.nullfish.lib.keymap.KeyStrokeMap;
import com.nullfish.lib.vfs.VFS;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;

public class ShortCutDialog extends JDialog {
	private JTree tree = new JTree();

	private JScrollPane scroll = new JScrollPane(tree);

	private VFile selectedFile;

	private Component owner;
	
	private static JFDComparator comparator;
	{
		FileComparator[] comparators = { new FileTypeComparator(),
				new NameComparator(true) };

		comparator = new JFDComparator(comparators);
	}

	public ShortCutDialog() {
		initGui();
	}

	public ShortCutDialog(Dialog dialog, VFile root, Component owner) throws VFSException {
		super(dialog, true);
		initGui();
		setRoot(root);
		this.owner = owner;
	}

	private void initGui() {
		setPreferredSize(new Dimension(200, 300));
		setUndecorated(true);

		getContentPane().add(scroll);

		tree.getActionMap().put("close", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});

		tree.getActionMap().put("choosen", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
						.getSelectionPath().getLastPathComponent();
				selectedFile = (VFile) node.getUserObject();
				setVisible(false);
			}
		});

		tree.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
				KeyStrokeMap.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "close");
		tree.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
				KeyStrokeMap.getKeyStroke(KeyEvent.VK_ENTER, 0), "choosen");
		tree.getInputMap(JComponent.WHEN_FOCUSED).put(
				KeyStrokeMap.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "close");
		tree.getInputMap(JComponent.WHEN_FOCUSED).put(
				KeyStrokeMap.getKeyStroke(KeyEvent.VK_ENTER, 0), "choosen");
		tree.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStrokeMap.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "close");
		tree.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStrokeMap.getKeyStroke(KeyEvent.VK_ENTER, 0), "choosen");
	}

	public void setRoot(VFile root) throws VFSException {
		DefaultTreeModel model = new DefaultTreeModel(file2Node(root));
		tree.setModel(model);
		pack();
	}

	private static VFileTreeNode file2Node(VFile file) throws VFSException {
		VFileTreeNode rtn = new VFileTreeNode(file);
		if (file.isDirectory()) {
			rtn.setAllowsChildren(true);
			VFile[] children = file.getChildren();
			Arrays.sort(children, comparator);

			for (int i = 0; i < children.length; i++) {
				rtn.add(file2Node(children[i]));
			}
		} else {
			rtn.setAllowsChildren(false);
		}

		return rtn;
	}

	public static void main(String[] args) {
		try {
			ShortCutDialog dialog = new ShortCutDialog();
			dialog.setRoot(VFS.getInstance().getFile(
					"d:\\src\\Java\\jFD2\\shortcut"));
			dialog.setVisible(true);
		} catch (VFSException e) {
			e.printStackTrace();
		}
	}

	public VFile getSelectedFile() {
		return selectedFile;
	}
	
	public void showDialog() {
		Point ownerPoint = owner.getLocationOnScreen();
		Dimension ownerSize = owner.getSize();
		Dimension thisSize = getSize();

		this.setBounds(ownerPoint.x, ownerPoint.y + ownerSize.height, thisSize.width, thisSize.height);
		setVisible(true);
	}
}
