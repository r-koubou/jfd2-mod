package com.nullfish.app.jfd2.viewer.jlayer;

import java.io.InputStream;

import com.nullfish.lib.vfs.exception.VFSException;

public interface StreamFactory {
	/**
	 * インプットストリームを取得する。
	 * 
	 * @return
	 */
	public InputStream getInputStream() throws VFSException;
	
	/**
	 * 表示する名称を取得する。
	 * @return
	 */
	public String getName();
	
}
