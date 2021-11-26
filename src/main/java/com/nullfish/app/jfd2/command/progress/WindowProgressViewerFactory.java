/*
 * Created on 2004/08/29
 *
 */
package com.nullfish.app.jfd2.command.progress;

import java.awt.Dimension;
import java.awt.Toolkit;

/**
 * @author shunji
 * 
 * Preferences - Java - Code Style - Code Templates
 */
public class WindowProgressViewerFactory implements ProgressViewerFactory {
	private static ProgressFrame frame;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nullfish.app.jfd2.command.progress.ProgressViewerFactory#getProgressViewer()
	 */
	public synchronized ProgressViewer getProgressViewer() {
		if (frame == null) {
			frame = new ProgressFrame();
			frame.pack();

			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			Dimension windowSize = frame.getSize();

			int x = (int) (Math.random() * (screenSize.width - windowSize.width));
			int y = (int) (Math.random() * (screenSize.height - windowSize.height));

			frame.setBounds(x, y, windowSize.width, windowSize.height);
		}
		
		return frame;
	}

}
