/*
 * Created on 2004/05/27
 *
 */
package com.nullfish.app.jfd2.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Element;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.lib.meta_data.MetaDataManager;

/**
 * @author shunji
 *
 */
public abstract class CommandFactory {
	/**
	 * コマンド名
	 */
	private String commandName;

	/**
	 * JFD
	 */
	private JFD jfd;
	
	/**
	 * コマンドのパラメータのマップ
	 */
	private Map paramMap = new HashMap();

	/**
	 * キャッシュ使用フラグ
	 */
	private boolean usesCache = false;
	
	/**
	 * コマンドのキャッシュ
	 */
	private Command cache;
	
	/**
	 * 非同期実行フラグ
	 */
	private boolean asynch = false;
	
	/**
	 * 実行中の画面ロックを行うかのフラグ
	 */
	private boolean locks = false;
	
	/**
	 * 主操作として画面にセットされ、経過表示、中止操作受付を行なうかのフラグ
	 */
	private boolean primary = false;
	
	/**
	 * 中止時にダイアログ表示を行なうかのフラグ
	 */
	private boolean showsStopped = true;
	
	public CommandFactory() {
	}
	
	public void convertFromNode(Element node) {
		commandName = node.getAttributeValue(Command.ATTR_NAME);
		
		String usesCacheStr = node.getAttributeValue(Command.ATTR_CACHE);
		if(usesCacheStr != null && usesCacheStr.length() > 0) {
			usesCache = Boolean.valueOf(usesCacheStr).booleanValue();
		}
		
		String asynchStr = node.getAttributeValue(Command.ATTR_ASYNCH);
		if(asynchStr != null && asynchStr.length() > 0) {
			asynch = Boolean.valueOf(asynchStr).booleanValue();
		}
		
		String locksStr = node.getAttributeValue(Command.ATTR_LOCKS);
		if(locksStr != null && locksStr.length() > 0) {
			locks = Boolean.valueOf(locksStr).booleanValue();
		}

		String primaryStr = node.getAttributeValue(Command.ATTR_PRIMARY);
		if(primaryStr != null && primaryStr.length() > 0) {
			primary = Boolean.valueOf(primaryStr).booleanValue();
		}

		String showsStoppedStr = node.getAttributeValue(Command.ATTR_SHOWS_STOP_DIALOG);
		if(showsStoppedStr != null && showsStoppedStr.length() > 0) {
			showsStopped = Boolean.valueOf(showsStoppedStr).booleanValue();
		}

		List params = node.getChildren(Command.PARAM_TAG);
		for(int i=0; i<params.size(); i++) {
			Element param = (Element)params.get(i);
			String name = param.getAttributeValue(MetaDataManager.ATTR_NAME);
			Object value = MetaDataManager.getInstance().paramNode2Object(param);
			paramMap.put(name, value);
		}
	}
	
	public void init(String commandName, boolean usesCache, boolean asynch, boolean locks, boolean primary, boolean showsStopped) {
		this.commandName = commandName;
		this.usesCache = usesCache;
		this.asynch = asynch;
		this.locks = locks;
		this.primary = primary;
		this.showsStopped = showsStopped;
	}
	
	/**
	 * パラメータをセットする。
	 * @param name
	 * @param value
	 */
	public void setParam(String name, Object value) {
		paramMap.put(name, value);
	}
	
	/**
	 * パラメータを取得する。
	 * @param name
	 * @return
	 */
	public Object getParam(String name) {
		return paramMap.get(name);
	}
	
	public final Command getCommand() {
		//	キャッシュ使用
		if(usesCache && cache != null) {
			return cache;
		}
		
		Command rtn = doGetCommand();
		if(usesCache) {
			cache = rtn;
		}
		
		rtn.setAsynch(asynch);
		rtn.setLocks(locks);
		rtn.setName(commandName);
		rtn.setJFD(jfd);
		rtn.setParamsMap(paramMap);
		rtn.setPrimary(primary);
		rtn.setShowsStopDialog(showsStopped);
		
		return rtn;
	}
	
	/**
	 * コマンドオブジェクトを生成して返す｡
	 * @return
	 */
	public abstract Command doGetCommand();

	/**
	 * @return Returns the commandName.
	 */
	public String getCommandName() {
		return commandName;
	}
	
	/**
	 * @param jfdModel The jfdModel to set.
	 */
	public void setJFD(JFD jfd) {
		this.jfd = jfd;
	}
}
