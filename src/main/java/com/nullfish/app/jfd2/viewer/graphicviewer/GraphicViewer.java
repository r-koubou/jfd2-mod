package com.nullfish.app.jfd2.viewer.graphicviewer;

import java.util.regex.Pattern;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.dialog.DialogUtilities;
import com.nullfish.app.jfd2.dialog.JFDDialog;
import com.nullfish.app.jfd2.resource.JFDResource;
import com.nullfish.app.jfd2.viewer.AbstractFileViewer;
import com.nullfish.lib.ui.document.Restriction;
import com.nullfish.lib.vfs.VFile;

public class GraphicViewer extends AbstractFileViewer {
	private JFD jfd;
	
	private GraphicViewerWindow window = new GraphicViewerWindow(this);
	
	public static final String INTERVAL = "slideshow_interval";
	
	private Thread slideShowThread;
	
	private String[] supportedExtensions = {
			"jpg",
			"jpeg",
			"gif",
			"png"
	};

	private String[] jaiSupportedExtensions = {
			"bmp",
			"tiff",
			"tif",
			"wbmp",
			"jp2"
	};
	
	public void doClose() {
		slideShowThread = null;
		window.close();
	}

	public void doOpen(VFile file, JFD jfd) {
		this.jfd = jfd;
		window.setVisible(true);
		window.open(isFileSupported(file) ? file : null);
	}

	public void dispose() {
		window.dispose();
		
	}

	/**
	 * ファイルがサポートされているならtrueを返す。
	 * @param file
	 * @return
	 */
	private boolean isFileSupported(VFile file) {
		String extension = file.getFileName().getLowerExtension();
		for(int i=0; i<supportedExtensions.length; i++) {
			if(supportedExtensions[i].equals(extension)) {
				return true;
			}
		}
		
		try {
			Class.forName("com.sun.media.imageio.plugins.bmp.BMPImageWriteParam");
			for(int i=0; i<jaiSupportedExtensions.length; i++) {
				if(jaiSupportedExtensions[i].equals(extension)) {
					return true;
				}
			}
		} catch (ClassNotFoundException e) {
			return false;
		}
		
		return false;
		
	}
	

	public void startSlideShow() {
		if(slideShowThread != null) {
			slideShowThread = null;
			return;
		}
		
		JFDDialog dialog = null;
		try {
			dialog = DialogUtilities.createOkCancelDialog(getJFD());
			dialog.addMessage(JFDResource.MESSAGES.getString("input_slideshow_interval"));
			dialog.addTextField("interval", (String)jfd.getCommonConfiguration().getParam(INTERVAL, "3"), true, "jFD2 - Slideshow", new Restriction() {
				public boolean isAllowed(String newText) {
					return Pattern.matches("\\d*\\.?\\d*", newText);
				}
			});
			dialog.pack();
			dialog.setVisible(true);
			
			String buttonAnswer = dialog.getButtonAnswer();
			if(buttonAnswer == null || JFDDialog.CANCEL.equals(buttonAnswer)) {
				return;
			}
			
			final String intervalStr = dialog.getTextFieldAnswer("interval");
			if(intervalStr == null || intervalStr.length() == 0) {
				return;
			}
			
			jfd.getCommonConfiguration().setParam(INTERVAL, intervalStr);
			
			Thread timerThread = new Thread() {
				public void run() {
					slideShowThread = Thread.currentThread();
					
					long interval = (long)(Double.parseDouble(intervalStr) * 1000);
					
					VFile[] files = jfd.getModel().getFiles();
					int index = jfd.getModel().getSelectedIndex();

					while(slideShowThread == Thread.currentThread()) {
						if(!window.isVisible()) {
							doClose();
							return;
						}
						
						if(!window.isFullScreen()) {
							window.changeFullScreen();
						}
						
						while(index < files.length && !isFileSupported(files[index])) {
							index++;
						}
						if(index >= files.length) {
							doClose();
							return;
						}
						VFile file = files[index];
						jfd.getModel().setSelectedFile(file);
						
						try {
							sleep(interval);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						index++;
					}
				}
			};
			
			timerThread.start();
		} finally {
			try {
				dialog.dispose();
			} catch (Exception e) {}
		}
	}
}
