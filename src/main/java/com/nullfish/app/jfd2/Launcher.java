/*
 * Created on 2004/05/23
 *
 */
package com.nullfish.app.jfd2;

import java.beans.IntrospectionException;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import com.nullfish.app.jfd2.config.ConfigVersionManager;
import com.nullfish.app.jfd2.config.Configuration;
import com.nullfish.app.jfd2.resource.JFDResource;
import com.nullfish.app.jfd2.ui.container2.ContainerPosition;
import com.nullfish.app.jfd2.ui.container2.JFD2TitleUpdater;
import com.nullfish.app.jfd2.ui.container2.JFDFrame;
import com.nullfish.app.jfd2.ui.container2.NumberedJFD2;
import com.nullfish.app.jfd2.util.CommandLineParameters;
import com.nullfish.app.jfd2.util.MacUtil;
import com.nullfish.app.jfd2.util.MigemoInfo;
import com.nullfish.app.jfd2.util.thumbnail.ThumbnailDataBase;
import com.nullfish.app.jfd2.viewer.FileViewerManager;
import com.nullfish.lib.plugin.PluginManager;
import com.nullfish.lib.ui.ThreadSafeUtilities;
import com.nullfish.lib.vfs.VFS;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;
import com.nullfish.lib.vfs.exception.VFSSystemException;

/**
 * @author shunji
 * 
 */
public class Launcher {
	/**
	 * 設定ディレクトリ
	 */
	public static final String ARG_CONFIG = "-config";

	/**
	 * 初期ディレクトリ
	 */
	public static final String ARG_DIR = "-dir";

	public static void main(final String[] args) {
		Runnable runnable = new Runnable() {
			public void run() {
				// HtmlTablePanel.setDebug(true);
				try {
					// Mac向け設定
					System.setProperty("apple.laf.useScreenMenuBar", "true");
					System.setProperty(
							"com.apple.mrj.application.apple.menu.about.name",
							"jFD2");
					System.setProperty("apple.awt.showGrowBox", "true");
					// System.setProperty("apple.awt.brushMetalLook", "true");

					// System.setProperty( "sun.java2d.translaccel", "true");
					// System.setProperty( "sun.java2d.ddscale", "true");

					// static部分（アカウント情報ダイアログ）初期化
					Class clazz = NumberedJFD2.class;

					String configDirStr = getConfigDir(args);
					VFile configDir = VFS.getInstance().getFile(configDirStr);

					try {
						ConfigVersionManager confManager = new ConfigVersionManager();
						confManager.checkVersion(configDir);
					} catch (Exception e) {
						e.printStackTrace();
						JOptionPane
								.showMessageDialog(
										null,
										JFDResource.MESSAGES
												.getString("failed_to_install_config_file"));
						System.exit(1);
					}

					Configuration commonConfig = Configuration
							.getInstance(configDir
									.getChild(JFD.COMMON_PARAM_FILE));
					
					String libDirStr = (String) commonConfig.getParam("lib_dir", configDir.getRelativeFile("../.jfd2_user/lib").getAbsolutePath());
					File libDir =  new File(libDirStr);
					if (!libDir.exists()) {
						libDir.mkdirs();
					}
					File[] libs = libDir.listFiles();
					for (int i = 0; i < libs.length; i++) {
						File lib = libs[i];
						if (lib.isDirectory()) {
							addURLToSystemClassLoader(lib.toURI().toURL());
						} else {
							addURLToSystemClassLoader(new URL("jar:" + lib.toURI().toURL() + "!/"));
						}
					}

					MigemoInfo.init(configDirStr);

					if (System.getProperty("os.name").indexOf("Mac OS") >= 0) {
						MacUtil.initShutDown();
					}

					// プラグイン機能
					String uiName = (String) commonConfig.getParam(
							"look_and_feel", UIManager
									.getSystemLookAndFeelClassName());
					try {
						UIManager.setLookAndFeel(uiName);
					} catch (Exception e) {
						e.printStackTrace();
					}

					ThumbnailDataBase.getInstance().setIconDir(
							configDir.getChild("icon_cache"));

					VFile pluginDir = VFS.getInstance().getFile(
							(String) commonConfig.getParam("plugin_dir", null));

					PluginManager.getInstance().init(pluginDir);
					PluginManager.getInstance().systemStarted();

					Runtime.getRuntime().addShutdownHook(new Thread() {
						public void run() {
							PluginManager.getInstance().systemExited();
						}
					});

					CommandLineParameters params = new CommandLineParameters(
							args);
					openJFD(params, configDir);
					PluginManager.getInstance().configurationChanged();
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(1);
				}
			}
		};

		ThreadSafeUtilities.executeRunnable(runnable);
	}

	public static void openJFD(CommandLineParameters params, VFile configDir)
			throws VFSException {
		NumberedJFD2.staticInit(configDir);
		JFD2.initKeyMap(configDir);

		FileViewerManager.getInstance().init(configDir);
		try {
			JFDFrame.loadFromTabConfig(configDir);
		} catch (VFSException e) {
			throw e;
		} catch (Exception e) {
			throw new VFSSystemException(e);
		}

		String firstDirStr = params.getParameter(ARG_DIR);
		if (firstDirStr != null) {
			JFDFrame frame = new JFDFrame(configDir);
			frame.setLocationByPlatform(true);

			NumberedJFD2 newJFD = new NumberedJFD2();

			try {
				newJFD.init(frame.getConfigDirectory());
			} catch (VFSException e) {
				e.printStackTrace();
			}
			frame.addComponent(newJFD, ContainerPosition.MAIN_PANEL,
					new JFD2TitleUpdater(newJFD));

			try {
				String path;

				boolean firstDirRequested = false;
				if (firstDirStr != null && firstDirStr.length() > 0) {
					path = new File(firstDirStr).getAbsolutePath();
					firstDirRequested = true;
				} else {
					path = (String) newJFD.getLocalConfiguration().getParam(
							JFD2.CONFIG_LAST_OPENED,
							System.getProperty("user.home"));
				}

				VFile dir = VFS.getInstance(newJFD).getFile(path);
				if ((!firstDirRequested && !dir.getFileSystem().isLocal())
						|| !dir.exists()) {
					dir = VFS.getInstance(newJFD).getFile(
							System.getProperty("user.home"));
				}

				newJFD.getModel().setDirectoryAsynchIfNecessary(dir,
						dir.getParent(), newJFD);
			} catch (Exception e) {
				VFile dir = VFS.getInstance(newJFD).getFile(
						System.getProperty("user.home"));
				newJFD.getModel().setDirectoryAsynchIfNecessary(dir,
						dir.getParent(), newJFD);
			}

			frame.pack();
			frame.setVisible(true);
		}
	}

	public static String getConfigDir(String[] args) throws VFSException {
		CommandLineParameters params = new CommandLineParameters(args);
		String configDirStr = params.getParameter(ARG_CONFIG);
		if (configDirStr == null || configDirStr.length() == 0) {
			configDirStr = getDefaultConfigDir();
		} else {
			String realPath = null;
			try {
				realPath = VFS.getInstance().getFile(configDirStr)
						.getAbsolutePath();
			} catch (Exception e) {
			}

			if (realPath == null) {
				configDirStr = new File(configDirStr).getAbsolutePath();
			}
		}
		return configDirStr;
	}

	private static String getDefaultConfigDir() throws VFSException {
		VFile homeDir = VFS.getInstance().getFile(
				new File(System.getProperty("user.home")).getAbsolutePath());

		if (ConfigVersionManager.isWindows) {
			return homeDir.getRelativeFile(
					"Application Data/Nullfish/jFD2/.jfd2").getAbsolutePath();
		} else {
			return homeDir.getRelativeFile(".jfd2").getAbsolutePath();
		}
	}

	public static void addURLToSystemClassLoader(URL url)
			throws IntrospectionException {
		URLClassLoader systemClassLoader = (URLClassLoader) ClassLoader
				.getSystemClassLoader();
		Class classLoaderClass = URLClassLoader.class;

		try {
			Method method = classLoaderClass.getDeclaredMethod("addURL",
					new Class[] { URL.class });
			method.setAccessible(true);
			method.invoke(systemClassLoader, new Object[] { url });
		} catch (Throwable t) {
			t.printStackTrace();
			throw new IntrospectionException(
					"Error when adding url to system ClassLoader ");
		}
	}
}
