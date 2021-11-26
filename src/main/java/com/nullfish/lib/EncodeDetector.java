package com.nullfish.lib;

import java.io.BufferedInputStream;
import java.io.IOException;

import org.mozilla.universalchardet.UniversalDetector;

public class EncodeDetector {

	public static String detectEncoding(BufferedInputStream is) throws IOException {
		UniversalDetector detector = new UniversalDetector(null);
		try {
			int read = 0;
			int maxRead = 40960;
			is.mark(maxRead);
			byte[] buffer = new byte[4096];
			
			int l = 0;
			while(read < maxRead && (l = is.read(buffer)) > 0 && !detector.isDone()) {
				read += l;
				detector.handleData(buffer, 0, l);
			}
			detector.dataEnd();
			return detector.getDetectedCharset();
		} finally {
			detector.reset();
			try {
				is.reset();
			} catch (Exception e) {}
		}
	}

	public static String detectEncoding(byte[] bytes, int l) throws IOException {
		UniversalDetector detector = new UniversalDetector(null);
		try {
			detector.handleData(bytes, 0, l);
			detector.dataEnd();
			return detector.getDetectedCharset();
		} finally {
			detector.reset();
		}
	}
}
