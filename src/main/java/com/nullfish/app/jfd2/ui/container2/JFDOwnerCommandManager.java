package com.nullfish.app.jfd2.ui.container2;

import org.jdom.JDOMException;

import com.nullfish.app.jfd2.command.CommandManager;
import com.nullfish.app.jfd2.command.JFDException;
import com.nullfish.app.jfd2.config.Configuration;
import com.nullfish.lib.vfs.VFS;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;

public class JFDOwnerCommandManager extends CommandManager {

	/**
	 * コマンド定義ファイル
	 */
	public static final String COMMAND_FILE = "owner_command.xml";
	
	/**
	 * キー定義ファイル
	 */
	public static final String KEY_FILE = "owner_keys.xml";
	
	/**
	 * コンストラクタ
	 *  
	 */
	public JFDOwnerCommandManager() {
		super(null);
	}


	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.Initable#init(com.nullfish.lib.vfs.VFile)
	 */
	public void init(VFile baseDir) throws VFSException {
		try {
			initCommands(baseDir.getChild(COMMAND_FILE), false);
			initKeyMap(baseDir.getChild(KEY_FILE), false);
			VFile userConfDir = VFS.getInstance().getFile(
					(String)Configuration.getInstance(baseDir).getParam(
							"user_conf_dir",
							baseDir.getRelativeFile("../.jfd2_user/conf")
									.getAbsolutePath()));
			try {
				VFile userCommandFile = userConfDir.getChild(COMMAND_FILE);
				if(userCommandFile.exists()) {
					initCommands(userCommandFile, true);
				}
			} catch (JDOMException e) {
				e.printStackTrace();
			}
			try {
				VFile userKeyFile = userConfDir.getChild(KEY_FILE);
				if(userKeyFile.exists()) {
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
}
