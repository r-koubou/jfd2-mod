package com.nullfish.app.jfd2.ui.shortcut_tree;

import javax.swing.tree.DefaultMutableTreeNode;

import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;

public class VFileTreeNode extends DefaultMutableTreeNode {
	public VFileTreeNode(Object userObject) {
		super(userObject);
	}
	
	public String toString() {
		Object userObject = getUserObject();
		if (userObject == null) {
			return null;
		} else {
			try {
				if (!(userObject instanceof VFile)) {
					return userObject.toString();
				}

				VFile file = (VFile) userObject;
				if (file.isDirectory()) {
					file.getName();
				}

				String fileName = file.getName();
				if (fileName.endsWith(".jfdlnk")) {
					return fileName.substring(0, fileName.length() - 7);
				}

				return fileName;
			} catch (VFSException e) {
				return userObject.toString();
			}
		}
	}
}
