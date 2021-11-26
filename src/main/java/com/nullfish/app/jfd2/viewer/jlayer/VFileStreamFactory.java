package com.nullfish.app.jfd2.viewer.jlayer;

import java.io.InputStream;

import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;

public class VFileStreamFactory implements StreamFactory {
	private VFile file;
	
	public VFileStreamFactory(VFile file) {
		this.file = file;
	}
	
	public InputStream getInputStream() throws VFSException {
		return file.getInputStream();
	}

	public String getName() {
		return file.getName();
	}

}
