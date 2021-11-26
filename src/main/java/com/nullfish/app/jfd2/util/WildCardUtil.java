package com.nullfish.app.jfd2.util;

public class WildCardUtil {
	private static final String[] escapeChars = {
			"\\[",
			"\\]",
			"\\^",
			"\\$",
			"\\{",
			"\\}",
			"\\?",
			"\\+",
			"\\."
	};
	
	public static String wildCard2Regex(String wildCard) {
		for(int i=0; i<escapeChars.length; i++) {
			wildCard = wildCard.replaceAll(escapeChars[i], "\\\\" + escapeChars[i]);
		}
		wildCard = wildCard.replaceAll("\\*", ".*");
		
		return wildCard;
	}
}
