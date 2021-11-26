package com.nullfish.app.jfd2.filelist;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.nullfish.app.jfd2.resource.JFDResource;
import com.nullfish.lib.vfs.VFS;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;
import com.nullfish.lib.vfs.impl.filelist.condition.FileSource;

public class FileSourceEditorsPanel extends JPanel {
	private List children = new ArrayList();
	
	public FileSourceEditorsPanel() {
		setBorder(BorderFactory.createTitledBorder(JFDResource.LABELS.getString("file_source")));
	}
	
	public int getCount() {
		return children.size();
	}
	
	public void updated() {
		for(int i=0; i<children.size(); i++) {
			((FileSourceEditorPanel)children.get(i)).parentUpdated();
		}
	}
	
	public void addNew() {
		FileSourceEditorPanel newPanel = new FileSourceEditorPanel(this);
		children.add(newPanel);
		
		relayout();
	}
	
	public void addNew(VFile file) {
		FileSourceEditorPanel newPanel = new FileSourceEditorPanel(this);
		newPanel.init(new FileSource(file.getFileSystem().getVFS(), file, 0));
		children.add(newPanel);
		
		relayout();
	}
	
	public void removeChild(FileSourceEditorPanel child) {
		children.remove(child);
		
		relayout();
	}
	
	private void relayout() {
		invalidate();
		updated();
		removeAll();
		setLayout(new GridLayout(children.size(), 1, 3, 3));
		
		for(int i=0; i<children.size(); i++) {
			add(((FileSourceEditorPanel)children.get(i)));
		}
		
		SwingUtilities.getWindowAncestor(this).pack();
		validate();
	}
	
	public List toFileSourceList(VFS vfs) throws VFSException {
		List rtn = new ArrayList();
		for(int i=0; i<children.size(); i++) {
			rtn.add(((FileSourceEditorPanel)children.get(i)).getFileSource(vfs));
		}
		
		return rtn;
	}
	
	public void clear() {
		children.clear();
		relayout();
	}
	
	public void setFileSources(List list) {
		invalidate();
		clear();
		
		for(int i=0; i<list.size(); i++) {
			FileSourceEditorPanel child = new FileSourceEditorPanel(this);
			child.init((FileSource)list.get(i));
			
			children.add(child);
		}
		
		relayout();
		SwingUtilities.getWindowAncestor(this).pack();
		validate();
	}
	
	public boolean hasEmptyChild() {
		for(int i=0; i<children.size(); i++) {
			if(((FileSourceEditorPanel)children.get(i)).isEmpty()) {
				return true;
			}
		}
		
		return false;
	}
}
