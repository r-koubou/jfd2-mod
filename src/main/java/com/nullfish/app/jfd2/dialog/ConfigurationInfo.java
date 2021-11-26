/*
 * Created on 2004/08/03
 *
 */
package com.nullfish.app.jfd2.dialog;

import com.nullfish.app.jfd2.config.Configuration;

/**
 * @author shunji
 *
 */
public class ConfigurationInfo {
	/**
	 * 設定クラス
	 */
	private Configuration config;
	
	/**
	 * 設定名
	 */
	private String paramName;

	/**
	 * コンストラクタ
	 * @param config
	 * @param name
	 */
	public ConfigurationInfo(Configuration config, String paramName) {
		this.config = config;
		this.paramName = paramName;
	}
	
	public Object getParam() {
		return config.getParam(paramName, null);
	}
	
	public void setParam(Object value) {
		config.setParam(paramName, value);
	}
}
