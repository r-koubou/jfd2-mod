package com.nullfish.lib.ui.document;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YyyymmddDateRestriction implements Restriction {
	private Pattern pattern = Pattern.compile("\\d{0,8}");
	
	public boolean isAllowed(String newText) {
		int year = 0;
		int month = 0;
		int day = 0;
		
		Matcher matcher = pattern.matcher(newText);
		if(!matcher.matches()) {
			return false;
		}

		if(newText.length() < 5) {
			return true;
		}
		
		year = Integer.parseInt(newText.substring(0, 4));
		
		if(newText.length() >= 5) {
			char ten = newText.charAt(4);
			if('1' < ten) {
				return false;
			}
		}
		
		if(newText.length() >= 6) {
			char ten = newText.charAt(4);
			char one = newText.charAt(5);
			if(ten == '0' && one == '0') {
				return false;
			}
			
			if(ten == '1' && one > '2') {
				return false;
			}

			month = Integer.parseInt(newText.substring(4, 6));
		}
		
		Calendar c = Calendar.getInstance();
		
		if(newText.length() >= 7) {
			c.set(Calendar.DAY_OF_MONTH, 1);
			c.set(Calendar.YEAR, year);
			c.set(Calendar.MONTH, month - 1);
			
			day = Integer.parseInt(newText.charAt(6) + "0");
			if(day > c.getActualMaximum(Calendar.DAY_OF_MONTH)) {
				return false;
			}
		}
		
		if(newText.length() >= 8) {
			day = Integer.parseInt(newText.substring(6));
			
			return day <= c.getActualMaximum(Calendar.DAY_OF_MONTH);
		}
		
		return true;
	}
}
