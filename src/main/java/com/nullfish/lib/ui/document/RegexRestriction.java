package com.nullfish.lib.ui.document;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexRestriction implements Restriction {
	private Pattern pattern;
	
	private RegexRestriction(Pattern pattern) {
		this.pattern = pattern;
	}
	
	public boolean isAllowed(String newText) {
		Matcher matcher = pattern.matcher(newText);
		return matcher.matches();
	}

	/**
	 * インスタンスを取得する。
	 * @param pattern
	 * @return
	 */
	public static RegexRestriction getInstance(String pattern) {
		Pattern p = Pattern.compile(pattern);
		return new RegexRestriction(p);
	}
}
