package com.nullfish.app.jfd2.config;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JTextArea;
import javax.swing.text.Document;

public class ListConfigTextField extends JTextArea {
	private String configName;

	public ListConfigTextField(String configName) {
		this.configName = configName;
	}
	
	private static String LINE_SEPARATOR = System.getProperty("line.separator");
	
	/**
	 * 設定を表示に反映する。
	 * 
	 * @param configuration
	 */
	public void setConfiguration(Configuration configuration) {
		try {
		List list = (List) configuration.getParam(configName, null);
		setText("");
		
		Document doc = getDocument();
		
		for(int i=0; list != null && i<list.size(); i++) {
			doc.insertString(doc.getLength(), (String)list.get(i), null);
			if(i != list.size() - 1) {
				doc.insertString(doc.getLength(), LINE_SEPARATOR, null);
			}
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 設定に反映する。
	 * 
	 * @param configuration
	 */
	public void apply(Configuration configuration) {
		StringTokenizer tokenizer = new StringTokenizer(getText(), "\n\r");
		List list = new ArrayList();
		while(tokenizer.hasMoreTokens()) {
			String line = tokenizer.nextToken().trim();
			if(line.length() > 0) {
				list.add(line);
			}
		}
		configuration.setParam(configName, list);
	}
}
