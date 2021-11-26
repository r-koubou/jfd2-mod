package com.nullfish.app.jfd2.util;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import groovy.util.CharsetToolkit;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.groovy.control.CompilerConfiguration;

import com.nullfish.lib.EncodeDetector;
import com.nullfish.lib.ui.ThreadSafeUtilities;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;
import com.nullfish.lib.vfs.exception.VFSIOException;

public class GroovyScriptCache {
	private Map timestampCache = new HashMap();
	private Map scriptCache = new HashMap();

	public Script makeScript(VFile vFile, Binding binding) throws VFSException {
		String fullPath = vFile.getAbsolutePath();
		java.util.Date fileTime = vFile.getTimestamp();
		java.util.Date cacheTime = (java.util.Date) timestampCache
				.get(fullPath);
		Script script = null;
		if (cacheTime == null || fileTime.after(cacheTime)) {
			timestampCache.put(fullPath, fileTime);
			CompilerConfiguration conf = new CompilerConfiguration(); // newでdefault設定が作成される
			BufferedInputStream is = null;
			try {
				is = new BufferedInputStream(vFile.getInputStream());
				conf.setSourceEncoding(EncodeDetector.detectEncoding(is)); // ここでcharsetを指定できる
				script = new GroovyShell(binding, conf).parse(is);
			} catch (IOException e) {
				throw new VFSIOException(e);
			} finally {
				try {
					is.close();
				} catch (Exception e) {}
			}
			scriptCache.put(fullPath, script);
		} else {
			script = (Script) scriptCache.get(fullPath);
			script.setBinding(binding);
		}
		return script;
	}
}