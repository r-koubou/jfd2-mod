package com.nullfish.app.jfd2.config;

import com.nullfish.lib.vfs.VFile;

public interface ConfigPanel {
	void loadPreference(VFile file) throws Exception;
	void apply() throws Exception;
	String getTitle();
}
