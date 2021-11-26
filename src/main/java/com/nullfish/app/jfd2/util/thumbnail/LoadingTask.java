package com.nullfish.app.jfd2.util.thumbnail;

import com.nullfish.lib.ui.list_table.ListTableModel;
import com.nullfish.lib.vfs.VFile;

public class LoadingTask {
	private VFile file;
	
	private ListTableModel model;
	
	public LoadingTask(VFile file, ListTableModel model) {
		this.file = file;
		this.model = model;
	}

	protected VFile getFile() {
		return file;
	}

	protected ListTableModel getModel() {
		return model;
	}
	
	public boolean equals(Object o) {
		if(o == null) {
			return false;
		}
		
		if(o.getClass() != LoadingTask.class) {
			return false;
		}
		
		return file.equals(((LoadingTask)o).file);
	}
	
	public int hashCode() {
		return file.hashCode();
	}
}
