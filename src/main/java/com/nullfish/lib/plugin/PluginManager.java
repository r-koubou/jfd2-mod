/*
 * Created on 2005/01/25
 *
 */
package com.nullfish.lib.plugin;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import com.nullfish.app.jfd2.Initable;
import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.resource.JFDResource;
import com.nullfish.app.jfd2.ui.container2.JFDOwner;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;

/**
 * @author shunji
 * 
 * Preferences - Java - Code Style - Code Templates
 */
public class PluginManager implements Initable {
	/**
	 * プラグインのリスト
	 */
	private List plugins = new ArrayList();

	public static final String PLUGIN_DIR = "plugins";

	private static PluginManager instance = new PluginManager();

	public static PluginManager getInstance() {
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nullfish.app.jfd2.Initable#init(com.nullfish.lib.vfs.VFile)
	 */
	public void init(VFile pluginDir) throws VFSException {
		if(!pluginDir.exists()) {
			pluginDir.createDirectory();
		}
		
		VFile[] pluginFiles = pluginDir.getChildren();
		
		for (int i = 0; i < pluginFiles.length; i++) {
			try {
				PluginClassLoader loader = new PluginClassLoader(pluginFiles[i]);
				Plugin plugin = loader.getPluginInstance();
				if (plugin != null) {
					plugins.add(plugin);
				}
			} catch (Exception e) {
				e.printStackTrace();

				String[] message = {
						JFDResource.MESSAGES.getString("failed_init_plugin"),
						e.getMessage() };

				JOptionPane.showMessageDialog(null, message);
			}
		}
	}

	/**
	 * システム開始時に呼び出される。
	 * 
	 * @throws VFSException
	 */
	public void systemStarted() {
		for (int i = 0; i < plugins.size(); i++) {
			try {
				((Plugin) plugins.get(i)).systemStarted();
			} catch (Exception e) {
				showErrorMessage(e);
			}
		}
	}

	/**
	 * 設定変更時に呼び出される。
	 * 
	 * @throws VFSException
	 */
	public void configurationChanged() {
		for (int i = 0; i < plugins.size(); i++) {
			try {
				((Plugin) plugins.get(i)).configurationChanged();
			} catch (Exception e) {
				showErrorMessage(e);
			}
		}
	}
	
	/**
	 * システム終了時に呼び出される。
	 * 
	 * @throws VFSException
	 */
	public void systemExited() {
		for (int i = 0; i < plugins.size(); i++) {
			try {
				((Plugin) plugins.get(i)).systemExited();
			} catch (Exception e) {
				showErrorMessage(e);
			}
		}
	}

	/**
	 * JFD2インスタンスが生成された際に呼び出される。
	 * 
	 * @param jfd
	 */
	public void jfdCreated(JFD jfd) {
		for (int i = 0; i < plugins.size(); i++) {
			try {
				((Plugin) plugins.get(i)).jfdCreated(jfd);
			} catch (Exception e) {
				showErrorMessage(e);
			}
		}
	}

	/**
	 * JFD2インスタンスが初期化された際に呼び出される。
	 * 
	 * @param jfd
	 */
	public void jfdInited(JFD jfd, VFile baseDir) {
		for (int i = 0; i < plugins.size(); i++) {
			try {
				((Plugin) plugins.get(i)).jfdInited(jfd, baseDir);
			} catch (Exception e) {
				showErrorMessage(e);
			}
		}
	}

	/**
	 * JFD2インスタンスが廃棄された際に呼び出される。
	 * 
	 * @param jfd
	 */
	public void jfdDisposed(JFD jfd) {
		for (int i = 0; i < plugins.size(); i++) {
			try {
				((Plugin) plugins.get(i)).jfdDisposed(jfd);
			} catch (Exception e) {
				showErrorMessage(e);
			}
		}
	}

	/**
	 * JFDオーナーインスタンスが生成された際に呼び出される。
	 * 
	 * @param jfd
	 */
	public void jfdOwnerCreated(JFDOwner owner) {
		for (int i = 0; i < plugins.size(); i++) {
			try {
				((Plugin) plugins.get(i)).jfdOwnerCreated(owner);
			} catch (Exception e) {
				showErrorMessage(e);
			}
		}
	}

	/**
	 * JFD2オーナーインスタンスが廃棄された際に呼び出される。
	 * 
	 * @param jfd
	 */
	public void jfdOwnerDisposed(JFDOwner owner) {
		for (int i = 0; i < plugins.size(); i++) {
			try {
				((Plugin) plugins.get(i)).jfdOwnerDisposed(owner);
			} catch (Exception e) {
				showErrorMessage(e);
			}
		}
	}

	/**
	 * 例外メッセージを表示する。
	 * 
	 * @param e
	 */
	private void showErrorMessage(Throwable e) {
		e.printStackTrace();
		JOptionPane.showMessageDialog(null, e.getStackTrace());
	}
	
	public Plugin getPlugin(int index) {
		return (Plugin)plugins.get(index);
	}
	
	public int getPluginCount() {
		return plugins.size();
	}
}
