/*
 * Created on 2005/01/27
 *
 */
package com.nullfish.lib.plugin;

import java.security.SecureClassLoader;
import java.util.ArrayList;
import java.util.List;

import com.nullfish.lib.vfs.VFS;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;

/**
 * @author shunji
 * 
 * Preferences - Java - Code Style - Code Templates
 */
public class PluginClassLoader extends SecureClassLoader {
	private VFile dir;

	private PluginInformation info;
	
	List classpathes = new ArrayList();
	
	public PluginClassLoader(VFile file) throws VFSException {
		if (file.isFile()) {
			dir = file.getInnerRoot();
		} else {
			dir = file;
		}
		
		classpathes.add(dir);
		
		info = new PluginInformation(dir.getChild(PluginInformation.DEFINITION));
		
		List classpathList = info.getClassPathList();
		for(int i=0; i<classpathList.size(); i++) {
			String path = (String)classpathList.get(i);
			VFile classPathFile = null;
			try {
				classPathFile = dir.getRelativeFile(path);
			} catch (Exception e) {
				try {
					classPathFile = VFS.getInstance().getFile(path);
				} catch (Exception ex) {}
			}
			
			if(classPathFile != null) {
				if(classPathFile.isDirectory()) {
					classpathes.add(classPathFile);
				} else if(classPathFile.getInnerRoot() != null){
					classpathes.add(classPathFile.getInnerRoot());
				}
			}
		}
		
		for(int i=0; i<classpathes.size(); i++) {
			VFile classPathRoot = (VFile)classpathes.get(i);
			classPathRoot.getFileSystem().registerUser(this);
		}
	}

	public Class findClass(String name) {
		byte[] b = loadClassData(name);
		return defineClass(name, b, 0, b.length);
	}

	private byte[] loadClassData(String name) {
		String className = name.replaceAll("\\.", "/") + ".class";
		for(int i=0; i<classpathes.size(); i++) {
			try {
				VFile file = ((VFile)classpathes.get(i)).getRelativeFile(className);
				if(file != null && file.exists()) {
					return file.getContent();
				}
			} catch (VFSException e) {}
		}
		
		return null;
	}
	
	/**
	 * プラグインのインスタンスを取得する。
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public Plugin getPluginInstance() throws InstantiationException, IllegalAccessException {
		String className = info.getClassName();
		Class clazz = findClass(className);
		AbstractPlugin rtn = (AbstractPlugin)clazz.newInstance();
		rtn.init(dir, info);
		
		return rtn;
	}
	
	public void close() {
		for(int i=0; i<classpathes.size(); i++) {
			VFile classPathRoot = (VFile)classpathes.get(i);
			classPathRoot.getFileSystem().removeUser(this);
		}
		
		try {
			dir.getFileSystem().getVFS().closeUnusedFileSystem();
		} catch (VFSException e) {
			e.printStackTrace();
		}
	}
}
