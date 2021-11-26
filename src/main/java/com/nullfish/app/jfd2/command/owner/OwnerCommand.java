/*
 * Created on 2004/05/25
 *
 */
package com.nullfish.app.jfd2.command.owner;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import org.jdom.Element;

import com.nullfish.app.jfd2.ui.container2.JFDOwner;
import com.nullfish.lib.meta_data.MetaDataManager;
import com.nullfish.lib.vfs.Manipulation;
import com.nullfish.lib.vfs.exception.VFSException;
import com.nullfish.lib.vfs.manipulation.abst.AbstractManipulation;

/**
 * @author shunji
 * 
 */
public abstract class OwnerCommand extends AbstractManipulation {
	/**
	 * jFDOwnerの参照
	 */
	private JFDOwner owner;

	/**
	 * 名称
	 */
	private String name;

	/**
	 * パラメータのマップ
	 */
	private Map paramsMap;

	/**
	 * コマンドタグ名
	 */
	public static final String COMMAND_TAG = "command";

	/**
	 * 名称属性
	 */
	public static final String ATTR_NAME = "name";

	/**
	 * キャッシュ使用属性
	 */
	public static final String ATTR_CACHE = "cache";

	/**
	 * 名称属性
	 */
	public static final String ATTR_ASYNCH = "asynch";

	/**
	 * パラメータタグ
	 */
	public static final String PARAM_TAG = "param";

	/**
	 * 全子操作
	 */
	private Manipulation[] manipulations;

	/**
	 * コンストラクタ
	 * 
	 * @param name
	 */
	public OwnerCommand() {
		super(null);
	}

	/**
	 * Manipulation#start()のオーバーライド。
	 * 
	 */
	public void start() throws VFSException {
		try {
			super.start();
		} catch (Exception e) {
			JTextArea text = new JTextArea();

			StringWriter writer = new StringWriter();
			e.printStackTrace(new PrintWriter(writer));
			writer.flush();
			try {
				writer.close();
			} catch (IOException e1) {
			}

			text.setText(writer.toString());
			JOptionPane.showMessageDialog(null, text);
		}
	}

	/**
	 * 操作の名称を返す｡
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return Returns the jfdModel.
	 */
	public JFDOwner getJFDOwner() {
		return owner;
	}

	public Object getParameter(String name) {
		if (paramsMap == null) {
			return null;
		}

		return paramsMap.get(name);
	}

	public void setParameter(String name, Object value) {
		if (paramsMap == null) {
			paramsMap = new HashMap();
		}
		paramsMap.put(name, value);
	}

	/**
	 * 
	 * @param paramsMap
	 */
	public void setParamsMap(Map paramsMap) {
		this.paramsMap = paramsMap;
	}

	/**
	 * 作業経過メッセージを取得する。
	 * 
	 * @return
	 */
	public String getProgressMessage() {
		Manipulation currentManipulation = getCurrentManipulation();
		
		if(currentManipulation == null) {
			return "";
		}
		
		if (currentManipulation != this) {
			return currentManipulation.getProgressMessage();
		}

		return "";
	}

	/**
	 * 子操作を設定する。
	 * 
	 * @param children
	 */
	protected void setChildManipulations(Manipulation[] children) {
		manipulations = children;
	}

	public long getProgressMin() {
		if (manipulations == null) {
			return PROGRESS_INDETERMINED;
		}

		int sum = 0;
		for (int i = 0; i < manipulations.length; i++) {
			long p = manipulations[i].getProgressMin();
			if (p == PROGRESS_INDETERMINED) {
				return PROGRESS_INDETERMINED;
			}
			sum += p;
		}

		return sum;
	}

	public long getProgressMax() {
		if (manipulations == null) {
			return PROGRESS_INDETERMINED;
		}

		int sum = 0;
		for (int i = 0; i < manipulations.length; i++) {
			long p = manipulations[i].getProgressMax();
			if (p == PROGRESS_INDETERMINED) {
				return PROGRESS_INDETERMINED;
			}
			sum += p;
		}

		return sum;
	}

	public long getProgress() {
		if (manipulations == null) {
			return PROGRESS_INDETERMINED;
		}

		int sum = 0;
		for (int i = 0; i < manipulations.length; i++) {
			long p = manipulations[i].getProgress();
			if (p == PROGRESS_INDETERMINED) {
				return PROGRESS_INDETERMINED;
			}
			sum += p;
		}

		return sum;
	}

	public JFDOwner getOwner() {
		return owner;
	}
	

	public void setOwner(JFDOwner owner) {
		this.owner = owner;
	}
	
	public void convertFromNode(Element node) {
		name = node.getAttributeValue(ATTR_NAME);
		
		List params = node.getChildren(PARAM_TAG);
		for(int i=0; i<params.size(); i++) {
			Element param = (Element)params.get(i);
			String name = param.getAttributeValue(MetaDataManager.ATTR_NAME);
			Object value = MetaDataManager.getInstance().paramNode2Object(param);
			setParameter(name, value);
		}
	}
	
}
