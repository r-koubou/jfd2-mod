package com.nullfish.lib.ui.filetree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.TreeNode;

import com.nullfish.lib.ui.ThreadSafeUtilities;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;

public class VFileTreeNode implements TreeNode {
	private VFile file;

	private boolean childInit = false;

	private List childNodes = new ArrayList();

	private VFileTreeNode parent;

	private boolean showsFile = false;

	private boolean showsArchive = true;

	private VFileTree tree;

	private int status = STATUS_NOT_LOADED;

	public static final int STATUS_NOT_LOADED = 0;

	public static final int STATUS_LOADING = 1;

	public static final int STATUS_LOADED = 2;

	private Object statusLockObj = new Object();
	
	public VFileTreeNode(VFileTree tree, VFileTreeNode parent, VFile file) {
		this.tree = tree;
		this.parent = parent;
		this.file = file;
	}

	private void initChild() {
		if (isChildInit()) {
			return;
		}

		setChildInit(true);
		Thread thread = new Thread() {
			public void run() {
				try {
					setStatus(STATUS_LOADING);
					tree.reload(VFileTreeNode.this);

					if(file.isDirectory()) {
						filterChildren(file.getChildren());
					} else if(file.getInnerRoot() != null) {
						filterChildren(file.getInnerRoot().getChildren());
					}
				} catch (VFSException e) {
					e.printStackTrace();
				} finally {
					setStatus(STATUS_LOADED);
					tree.reload(VFileTreeNode.this);
				}
			}
		};
		tree.invoke(thread);
	}

	public synchronized VFileTreeNode getChild(VFile file) {
		VFileTreeNode rtn = new VFileTreeNode(tree, this, file);
		if(childNodes.contains(rtn)) {
			rtn = (VFileTreeNode)childNodes.get(childNodes.indexOf(rtn));
		} else {
			childNodes.add(rtn);
		}
		sort(childNodes);
		initChild();
		return rtn;
	}

	private void filterChildren(VFile[] children) throws VFSException {
		final List filteredList = new ArrayList();
		
		for (int i = 0; i < children.length; i++) {
			if (showsFile || children[i].isDirectory()
					|| (showsArchive && children[i].getInnerRoot() != null)) {
				VFileTreeNode child = new VFileTreeNode(tree,
						VFileTreeNode.this, children[i]);
				if(childNodes.contains(child)) {
					filteredList.add(childNodes.get(childNodes.indexOf(child)));
				} else {
					filteredList.add(child);
				}
			}
		}
		sort(filteredList);
		
		Runnable runnable = new Runnable() {
			public void run() {
				childNodes = filteredList;
			}
		};
		ThreadSafeUtilities.executeRunnable(runnable);
	}

	public Enumeration children() {
System.out.println("children " + file);
		initChild();
		return new Enumeration() {
			int index = -1;

			public boolean hasMoreElements() {
				return index + 1 < childNodes.size();
			}

			public Object nextElement() {
				index++;
				return childNodes.get(index);
			}
		};
	}
	
	public boolean getAllowsChildren() {
		return true;
/*		try {
			return file.isDirectory();
		} catch (VFSException e) {
			return false;
		}*/
	}

	public TreeNode getChildAt(int childIndex) {
		initChild();
		return (TreeNode) childNodes.get(childIndex);
	}

	public int getChildCount() {
		initChild();
		return childNodes.size();
	}

	public int getIndex(TreeNode node) {
		initChild();
		return childNodes.indexOf(node);
	}

	public TreeNode getParent() {
		return parent;
	}

	public boolean isLeaf() {
return false;
/*
		try {
			if (file.isDirectory()) {
				return false;
			}
			if (file.getInnerRoot() != null) {
				return false;
			}

			return true;
		} catch (VFSException e) {
			e.printStackTrace();
			return true;
		}*/
	}

	public String toString() {
		return (file.isRoot() ? file.getSecurePath() : file.getName())
				+ (getStatus() == STATUS_LOADING ? "(loading)" : "");
	}

	public boolean equals(Object o) {
		if (!(o instanceof VFileTreeNode)) {
			return false;
		}

		return file.equals(((VFileTreeNode) o).file);
	}

	public int hashCode() {
		return file.hashCode();
	}

	public VFile getFile() {
		return file;
	}

	private int getStatus() {
		synchronized(statusLockObj) {
			return status;
		}
	}

	private void setStatus(int status) {
		synchronized(statusLockObj) {
			this.status = status;
		}
	}
	
	private void sort(List childNodes) {
		Collections.sort(childNodes, new Comparator() {
			public int compare(Object o1, Object o2) {
				return (((VFileTreeNode)o1).file.getAbsolutePath().toLowerCase().compareTo(((VFileTreeNode)o2).file.getAbsolutePath().toLowerCase()));
			}
		});
	}

	private boolean isChildInit() {
		synchronized (statusLockObj) {
			return childInit;
		}
	}

	private void setChildInit(boolean childInit) {
		synchronized (statusLockObj) {
			this.childInit = childInit;
		}
	}
}
