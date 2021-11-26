package com.nullfish.lib.ui.filetree;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.nullfish.lib.ui.MigemoTree;
import com.nullfish.lib.ui.ThreadSafeUtilities;
import com.nullfish.lib.vfs.VFS;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;

public class VFileTree extends JPanel {
	private JTree tree = new MigemoTree();
	
	private JScrollPane scroll = new JScrollPane(tree);
	
	private DefaultTreeModel model;

	private InitializerThread thread = new InitializerThread();
	
	public VFileTree() {
		super(new BorderLayout());
		add(scroll);
		tree.setExpandsSelectedPaths(true);
		thread.start();
	}
	
	public void dispose() {
		thread.stopWorking();
	}
	
	public void setRoot(VFile root) {
		VFileTreeNode rootNode = new VFileTreeNode(this, null, root);
		model = new DefaultTreeModel(rootNode);
		tree.setModel(model);
	}
	
	public void setSelectedFile(final VFile file) {
		List fileList = new ArrayList();
		VFile f = file;
		while(f != null) {
			VFile mountPoint = f.getFileSystem().getMountPoint();
			if(f.isRoot() && mountPoint != null) {
				f = mountPoint;
			}
			fileList.add(0, f);
			f = f.getParent();
		}
		
		VFile root = null;
		try {
			root = file.getFileSystem().getVFS().getFile("root:///");
		} catch (VFSException e) {
			// 起こりえない
			e.printStackTrace();
		}
		if(!root.equals(file)) {
//			fileList.add(root);
		}
		
		List nodeList = new ArrayList();
		VFileTreeNode node = (VFileTreeNode)tree.getModel().getRoot();
		nodeList.add(node);
		for(int i=0; i<fileList.size(); i++) {
			VFileTreeNode child = findChildNode((VFileTreeNode)nodeList.get(i), (VFile)fileList.get(i));
			model.reload((VFileTreeNode)nodeList.get(i));
			nodeList.add(child);
		}
		for(int i=0; i<nodeList.size(); i++) {
			reload((VFileTreeNode)nodeList.get(i));
		}
		
		TreePath path = new TreePath(nodeList.toArray());
		tree.setSelectionPath(path);
		tree.scrollPathToVisible(path);
	}
	
	private VFileTreeNode findChildNode(VFileTreeNode node, VFile childFile) {
		VFileTreeNode child = node.getChild(childFile);
		return child;
	}

	void reload(final VFileTreeNode node) {
		Runnable runnable = new Runnable() {
			public void run() {
				TreePath path = tree.getSelectionPath();
				model.reload(node);
				tree.setSelectionPath(path);
				tree.scrollPathToVisible(path);
			}
		};
		ThreadSafeUtilities.executeRunnable(runnable);
	}
	
	void invoke(Runnable runnable) {
		thread.addRunnable(runnable);
	}
	
	private class InitializerThread extends Thread {
		private List runnables = new LinkedList();
		
		private boolean working;
		
		synchronized void addRunnable(Runnable runnable) { 
			runnables.add(0, runnable);
			notify();
		}
		
		synchronized Runnable getRunnable() {
			if(runnables.size() == 0) {
				return null;
			}
			
			Runnable rtn = (Runnable)runnables.get(0);
			runnables.remove(0);
			return rtn;
		}
		
		synchronized void stopWorking() {
			working = false;
			notify();
		}
		
		public void run() {
			working = true;
			while(working) {
				Runnable runnable = getRunnable();
				if(runnable == null) {
					synchronized (this) {
						try {
							wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				} else {
					runnable.run();
				}
			}
		}
	}
	
	public void addTreeSelectionListener(TreeSelectionListener tsl) {
		tree.addTreeSelectionListener(tsl);
	}
	
	public VFile getSelectedFile() {
		try {
			TreePath treePath = tree.getSelectionPath();
			if(treePath == null) {
				return null;
			}
			VFileTreeNode node = (VFileTreeNode)treePath.getLastPathComponent();
			return node != null ? node.getFile() : null;
		} catch (Exception e) {
//			System.out.println(tree.getSelectionPath().getLastPathComponent());
			e.printStackTrace();
			return null;
		}
	}
	
	public static void main(String[] args) {
		try {
			JFrame frame = new JFrame();
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			VFileTree tree = new VFileTree();
			tree.setRoot(VFS.getInstance().getFile("root:///"));
//			tree.setRoot(VFS.getInstance().getFile("ftp://hogehoge:tkcstiis@hogehoge.sakura.ne.jp/"));
			tree.setSelectedFile(VFS.getInstance().getFile("C:\\Windows\\System32\\1041\\vsjitdebuggerui.dll"));
			frame.getContentPane().add(tree);
			frame.pack();
			frame.setVisible(true);
//			tree.setSelectedFile(VFS.getInstance().getFile("ftp://hogehoge:tkcstiis@hogehoge.sakura.ne.jp/home/hogehoge/www/ikemen"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
