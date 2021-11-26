package com.nullfish.app.jfd2.util;

import java.io.File;

import org.monazilla.migemo.Migemo;

public class MigemoInfo {
	
	private static boolean usesMigemo = false;

	private static boolean init = false;
	
	public static void init(String configDir) {
		if(init) {
			return;
		}
		init = true;
		
		load(new File(configDir, "migemo-dict"));
		load(new File("migemo-dict"));
		load(new File(System.getProperty("user.home"), "migemo-dict"));
	}
	
	private static void load(File file) {
		if(file.exists()) {
			Migemo.loadDictionary(file,"EUC_JP");
			usesMigemo = true;
		}
	}

	public static boolean usesMigemo() {
		return usesMigemo;
	}
}
