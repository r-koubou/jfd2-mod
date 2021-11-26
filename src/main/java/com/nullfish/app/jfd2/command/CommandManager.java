/*
 * Created on 2004/05/26
 *
 */
package com.nullfish.app.jfd2.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.KeyStroke;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import com.nullfish.app.jfd2.Initable;
import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.config.Configuration;
import com.nullfish.app.jfd2.ui.container2.JFDOwner;
import com.nullfish.app.jfd2.util.DomCache;
import com.nullfish.lib.ui.KeyStrokeUtility;
import com.nullfish.lib.vfs.VFS;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;

/**
 * @author shunji
 * 
 */
public class CommandManager implements Initable {
	private JFD jfd;

	/**
	 * コマンド名称、コマンドのマップ
	 */
	private Map nameCommandMap = new HashMap();

	/**
	 * KeyStrokeとコマンド名称のマップ
	 */
	private Map keyNameMap = new HashMap();

	/**
	 * コマンドのマッピングタグ
	 */
	public static final String COMMAND_MAP_TAG = "commandmap";

	/**
	 * コマンド名属性
	 */
	public static final String ATTR_COMMANDNAME = "name";

	/**
	 * コマンド定義ファイル
	 */
	public static final String COMMAND_FILE = "command.xml";

	/**
	 * キー定義ファイル
	 */
	public static final String KEY_FILE = "keys.xml";

	/**
	 * コンストラクタ
	 * 
	 */
	public CommandManager(JFD jfd) {
		this.jfd = jfd;
	}

	/**
	 * コマンドをXMLのストリームから読み込み、初期化する。
	 * 
	 * @param is
	 * @throws JDOMException
	 * @throws IOException
	 * @throws VFSException 
	 */
	public void initCommands(VFile file, boolean override)
			throws JDOMException, IOException, VFSException {
		if (!override) {
			nameCommandMap.clear();
		}

		Document doc = DomCache.getInstance().getDocument(file);
		List commandNodes = doc.getRootElement().getChildren(
				Command.COMMAND_TAG);
		for (int i = 0; i < commandNodes.size(); i++) {
			CommandFactory factory = node2CommandFactory((Element) commandNodes
					.get(i));
			factory.setJFD(jfd);
			nameCommandMap.put(factory.getCommandName(), factory);
		}
	}

	public void addCommandFactory(CommandFactory factory) {
		factory.setJFD(jfd);
		nameCommandMap.put(factory.getCommandName(), factory);
	}

	/**
	 * 入力キーをXMLのストリームから読み込み、初期化する。
	 * 
	 * @param is
	 * @param override
	 * @throws JDOMException
	 * @throws IOException
	 * @throws VFSException 
	 */
	public void initKeyMap(VFile file, boolean override)
			throws JDOMException, IOException, VFSException {
		if (!override) {
			keyNameMap.clear();
		}

		Document doc = DomCache.getInstance().getDocument(file);
		List commandMapList = doc.getRootElement().getChildren(COMMAND_MAP_TAG);
		for (int i = 0; i < commandMapList.size(); i++) {
			Element mapping = (Element) commandMapList.get(i);
			String commandName = mapping.getAttributeValue(ATTR_COMMANDNAME);
			List keys = mapping.getChildren(KeyStrokeUtility.KEY_NODE);
			for (int j = 0; j < keys.size(); j++) {
				try {
					Element keyNode = (Element) keys.get(j);
					KeyStroke keyStroke = KeyStrokeUtility
							.node2KeyStroke(keyNode);
					keyNameMap.put(keyStroke, commandName);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * ノードからコマンドのファクトリを生成する。
	 * 
	 * @param element
	 * @return
	 */
	private CommandFactory node2CommandFactory(Element element) {
		CommandFactory factory = new DefaultCommandFactory();
		factory.convertFromNode(element);

		return factory;
	}

	/**
	 * キーに関連付けられたコマンドを実行する｡ もしも関連付けられてない場合はfalseを返す｡
	 * 
	 * @param keyStroke
	 * @return
	 */
	public boolean execute(KeyStroke keyStroke) {
		String name = (String) keyNameMap.get(keyStroke);
		if (name == null) {
			return false;
		}

		return execute(name);
	}

	/**
	 * コマンド名称に関連付けられたコマンドを実行する｡ もしも関連付けられてない場合はfalseを返す｡
	 * 
	 * @param name
	 * @return
	 */
	public boolean execute(String name) {
		try {
			CommandFactory factory = (CommandFactory) nameCommandMap.get(name);
			if (factory == null) {
				// 対応するコマンドが無い場合はオーナーコマンド実行を試す。
				JFDOwner owner = jfd.getJFDOwner();
				if (owner == null) {
					return false;
				}

				return owner.getCommandManager().execute(name);
			}

			Command command = factory.getCommand();
			command
					.addManipulationListener(new JFDManipulationListener(
							command));
			/*
			 * if(command.isShowsProgress()) {
			 * jfd.getProgressViewer().addCommand(command); }
			 */
			if (command.isLocks()) {
				command.getJFD().setLocked(true);
			}

			if (command.isAsynch()) {
				command.startAsync();
			} else {
				command.start();
			}
		} catch (VFSException e) {
			e.printStackTrace();
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nullfish.app.jfd2.Initable#init(com.nullfish.lib.vfs.VFile)
	 */
	public void init(VFile baseDir) throws VFSException {
		try {
			initCommands(baseDir.getChild(COMMAND_FILE), false);
			initKeyMap(baseDir.getChild(KEY_FILE), false);

			Configuration commonConfig = Configuration.getInstance(baseDir.getChild(JFD.COMMON_PARAM_FILE));
			VFile userConfDir = VFS.getInstance(jfd).getFile(
					(String) commonConfig.getParam(
							"user_conf_dir",
							baseDir.getRelativeFile("../.jfd2_user/conf")
									.getAbsolutePath()));
			try {
				VFile userCommandFile = userConfDir.getChild(COMMAND_FILE);
				if (userCommandFile.exists()) {
					initCommands(userCommandFile, true);
				}
			} catch (JDOMException e) {
				e.printStackTrace();
			}
			try {
				VFile userKeyFile = userConfDir.getChild(KEY_FILE);
				if (userKeyFile.exists()) {
					initKeyMap(userKeyFile, true);
				}
			} catch (JDOMException e) {
				e.printStackTrace();
			}
		} catch (VFSException e) {
			throw e;
		} catch (Exception e) {
			throw new JFDException(e, "", null);
		}
	}

	/**
	 * コマンド名からキーのリストを取得する。
	 * 
	 * @param command
	 * @return
	 */
	public List getKeys(String command) {
		List rtn = new ArrayList();
		Iterator entriesIte = keyNameMap.entrySet().iterator();
		while (entriesIte.hasNext()) {
			Entry entry = (Entry) entriesIte.next();
			if (command.equals(entry.getValue())) {
				rtn.add(entry.getKey());
			}
		}

		return rtn;
	}
	
	public List getCommmandNames() {
		List rtn = new ArrayList(nameCommandMap.keySet());
		Collections.sort(rtn);
		
		return rtn;
	}
}
