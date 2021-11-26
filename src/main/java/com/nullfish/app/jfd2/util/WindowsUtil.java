package com.nullfish.app.jfd2.util;


public class WindowsUtil {
	private static final boolean isWindows = System.getProperty("os.name").indexOf("Windows") != -1;
	
	public static final String[][] ESCAPE_REGEXS = {
		{"\\^", "^"},
		{" ",   " "},
		{"&",   "&"},
		{"\\(", "("},
		{"\\)", ")"},
		{"\\[",   "["},
		{"\\]",   "]"},
		{"\\{", "{"},
		{"\\}", "}"},
		{"=",   "="},
		{";",   ";"},
		{"!",   "!"},
		{"'",   "'"},
		{"\\+",   "+"},
		{",",   ","},
		{"`",   "`"},
		{"\"",   "\""},
		{"~",   "~"},
		{"\\　",   "　"}
	};
	
	public static final String ESCAPE_CHARS = " &()[]{}^=;!'+,`~　";

	public static String escapeFileName(String fileName) {
		if(isWindows && WindowsUtil.isCommandToEscape(fileName)) {
			for(int i=0; i<WindowsUtil.ESCAPE_REGEXS.length; i++) {
				fileName = fileName.replaceAll(WindowsUtil.ESCAPE_REGEXS[i][0], "^" + WindowsUtil.ESCAPE_REGEXS[i][1]);
			}
		}
		
		return fileName;
	}

	public static boolean isCommandToEscape(String command) {
		for(int i=0; i<WindowsUtil.ESCAPE_CHARS.length(); i++) {
			if(command.indexOf(WindowsUtil.ESCAPE_CHARS.charAt(i)) >= 0) { 
				return true;
			}
		}
		
		return false;
	}

}
