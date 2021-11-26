/*
 * Created on 2004/02/29
 *
 */
package com.nullfish.lib.ui;

import java.awt.Color;
import java.lang.reflect.Field;

/**
 * 色関係のユーティリティクラス。
 * 文字列から色への変換を行う。
 * 
 * @author shunji
 */
public class ColorUtility {
	/**
	 * 文字列からColorクラスを作成するメソッド。
	 * 以下の2形式が指定可能。
	 * 1、色名称
	 * 	black, white, redなど。
	 * 	Colorクラスのstaticフィールド名が指定可能。
	 * 
	 * 2、カラーコード
	 * 	#で始まる8桁の16進数として指定する。
	 * 	例：#FF10FF00
	 * 	二桁ずつ、アルファ、赤、緑、青に対応する。
	 */
	public static Color stringToColor(String colorCode) {
		if (colorCode == null || colorCode.length() == 0) {
			return null;
		}
		
		if(colorCode.startsWith("#")) {
			int alpha = 0;
			int r = 0;
			int g = 0;
			int b = 0;
			try {
				alpha = Integer.valueOf(colorCode.substring(1, 3), 16).intValue();
				r = Integer.valueOf(colorCode.substring(3, 5), 16).intValue();
				g = Integer.valueOf(colorCode.substring(5, 7), 16).intValue();
				b = Integer.valueOf(colorCode.substring(7, 9), 16).intValue();
			} catch (Exception e) {
				return null;
			}
			return (new Color(r, g, b, alpha));
		} else {
			return colorName2Color(colorCode);
		}
	}
	
	public static Color colorName2Color(String colorName) {
		try {
			Field field = Class.forName("java.awt.Color").getField(colorName);
			return (Color)field.get(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static String color2String(Color color) {
		return "#" + Integer.toHexString(color.getRGB());
	}
	
	public static Color getComplementaryColor(Color c) {
	    int newRed = getComplement(c.getRed());
	    int newGreen = getComplement(c.getGreen());
	    int newBlue = getComplement(c.getBlue());

	    return new Color(newRed, newGreen, newBlue, c.getAlpha());
	}

	private static int getComplement(int colorVal) {
	    int maxDiff = colorVal >= 128 ? -colorVal : 255 - colorVal;
	    int diff = (int) Math.round(maxDiff / 2.0);
	    int newColorVal = colorVal + diff;

	    return newColorVal;
	}
}
