/*
 * Created on 2005/01/25
 *
 */
package com.nullfish.app.jfd2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.nullfish.lib.vfs.VFile;

/**
 * @author shunji
 *
 */
public class JFDMarkModel {
	private Set markedFilesSet = new HashSet();
	
	private List markedFilesList = new ArrayList();
	
	public synchronized void init() {
		markedFilesSet.clear();
		markedFilesList.clear();
	}
	
	public synchronized void add(VFile file) {
		if(!isMarked(file)) {
			markedFilesSet.add(file);
			markedFilesList.add(file);
		}
	}
	
	public synchronized void remove(VFile file) {
		if(isMarked(file)) {
			markedFilesSet.remove(file);
			markedFilesList.remove(file);
		}
	}
	
	public synchronized boolean isMarked(VFile file) {
		return markedFilesSet.contains(file);
	}
	
	public void setMarked(VFile file, boolean marked) {
		if(marked) {
			add(file);
		} else {
			remove(file);
		}
	}
	
	public VFile[] getMarkedFiles() {
		VFile[] rtn = new VFile[markedFilesList.size()];
		return (VFile[])markedFilesList.toArray(rtn);
	}
}
