package com.nullfish.app.jfd2.util;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import com.nullfish.lib.vfs.Manipulation;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;
import com.nullfish.lib.vfs.exception.VFSIOException;

public class ShortCutFile {
	private VFile file;
	
	private VFile target;
	
	public static final String EXTENSION = "jfdlnk";
	
	public ShortCutFile(VFile file) {
		this.file = file;
	}
	
	public void setTarget(VFile target) {
		this.target = target;
	}
	
	public VFile getTarget() {
		return target;
	}
	
	public void load(Manipulation manipulation) throws VFSException {
		String shortCutPath = new String(file.getContent(manipulation));
		target = file.getFileSystem().getVFS().getFile(shortCutPath);
	}
	
	public void save(Manipulation manipulation) throws VFSException {
		Writer writer = null;
		try {
			writer = new OutputStreamWriter(file.getOutputStream(manipulation));
			writer.write(target.getAbsolutePath());
		} catch (IOException e) {
			throw new VFSIOException(e);
		} finally {
			if(writer != null) {
				try {
					writer.flush();
					writer.close();
				} catch (Exception e) {}
			}
		}
	}
}
