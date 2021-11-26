/*
 * Created on 2005/01/17
 *
 */
package com.nullfish.app.jfd2.ui.container2;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.JFD2;
import com.nullfish.app.jfd2.command.JFDException;
import com.nullfish.app.jfd2.config.Configuration;
import com.nullfish.app.jfd2.resource.JFDResource;
import com.nullfish.app.jfd2.user.GrobalUserInfoManager;
import com.nullfish.app.jfd2.viewer.FileViewerManager;
import com.nullfish.lib.plugin.PluginManager;
import com.nullfish.lib.tablelayout.BgImagePainter;
import com.nullfish.lib.vfs.VFS;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.FileNotExistsException;
import com.nullfish.lib.vfs.exception.VFSException;

/**
 * @author shunji
 * 
 * Preferences - Java - Code Style - Code Templates
 */
public class NumberedJFD2 extends JFD2 {
	{
		VFS.setUserInfoManager(new GrobalUserInfoManager());
	}

	private static List jfds = new ArrayList();

	private List listeners = new ArrayList();

	private static NumberedJFD2 activeJfd = null;

	private static Image bgImage;

	public NumberedJFD2() {
		jfds.add(this);

		// VFS.getInstance(this).setUserInfoManager(new
		// JFD2UserInfoManager(this));

		addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				activeJfd = NumberedJFD2.this;
			}

			public void focusLost(FocusEvent e) {
			}
		});

		PluginManager.getInstance().jfdCreated(this);
	}

	public void init(VFile baseDir) throws VFSException {
		super.init(baseDir);
		setBgImage(bgImage, (String) getCommonConfiguration().getParam(
				"bg_image_align", BgImagePainter.DEFAULT
						.getName()));
		
		PluginManager.getInstance().jfdInited(this, baseDir);
	}

	public static void staticInit(VFile baseDir) throws VFSException {
		// 背景
		try {
			Configuration commonConfig = Configuration.getInstance(baseDir
					.getChild(JFD.COMMON_PARAM_FILE));

			String imagePath = (String) commonConfig.getParam("bg_image", "");
			if(imagePath == null || imagePath.length() == 0) {
				bgImage = null;
				return;
			}
			
			float transparency = ((Integer) commonConfig.getParam(
					"bg_image_transparency", new Integer(100))).floatValue() / 100;
			if (imagePath != null && imagePath.length() > 0) {
				bgImage = createBgImage(imagePath, transparency);
			}
		} catch (VFSException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new JFDException(JFDResource.MESSAGES
					.getString("read_config_failed"), null);
		}
		
	}

	private static Image createBgImage(String imagePath, float alpha)
			throws IOException, VFSException {
		Graphics2D g = null;
		try {
			Image bgImage = ImageIO.read(VFS.getInstance().getFile(imagePath)
					.getInputStream());
			AlphaComposite alphaComposite = AlphaComposite.getInstance(
					AlphaComposite.SRC_OVER, alpha);

			GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
			GraphicsDevice d = e.getDefaultScreenDevice();
			GraphicsConfiguration c = d.getDefaultConfiguration();
			
			BufferedImage image = c.createCompatibleImage(bgImage
					.getWidth(null), bgImage.getHeight(null));

			g = (Graphics2D) image.getGraphics();
			g.setComposite(alphaComposite);
			g.drawImage(bgImage, 0, 0, null);

			return image;
		} catch (FileNotExistsException e) {
			return null;
		} finally {
			try {
				bgImage.flush();
			} catch (Exception e) {
			}

			if (g != null) {
				g.dispose();
			}
		}
	}

	public void dispose() {
		jfds.remove(this);
		listeners = null;
		super.dispose();

		callNumberChangeListener();

		FileViewerManager.getInstance().dispose(this);

		PluginManager.getInstance().jfdDisposed(this);
	}

	public int getIndexOfJfd() {
		return jfds.indexOf(this);
	}

	public static NumberedJFD2 getJfdAt(int index) {
		return (NumberedJFD2) jfds.get(index);
	}

	public void addNumberChangeListener(JFDNumberChangeListener listener) {
		listeners.add(listener);
	}

	public void removeNumberChangeListener(JFDNumberChangeListener listener) {
		listeners.remove(listener);
	}

	private static void callNumberChangeListener() {
		for (int i = 0; i < getCount(); i++) {
			NumberedJFD2 jfd2 = (NumberedJFD2) jfds.get(i);
			for (int j = 0; j < jfd2.listeners.size(); j++) {
				((JFDNumberChangeListener) jfd2.listeners.get(j))
						.numberChanged(jfds.size());
			}
		}
	}

	public static NumberedJFD2 getActiveJFD() {
		return activeJfd;
	}

	public static int getCount() {
		return jfds.size();
	}

	public String toString() {
		return ("numberedJFD2:" + getIndexOfJfd());
	}
//
//	public void finalize() throws Throwable {
//		System.out.println("finalize NumberedJFD2");
//		super.finalize();
//	}
}
