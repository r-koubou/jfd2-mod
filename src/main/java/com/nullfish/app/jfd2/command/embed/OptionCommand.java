/*
 * Created on 2004/05/31
 *
 */
package com.nullfish.app.jfd2.command.embed;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import com.nullfish.app.jfd2.JFD2;
import com.nullfish.app.jfd2.command.Command;
import com.nullfish.app.jfd2.command.JFDException;
import com.nullfish.app.jfd2.config.ConfigFrame;
import com.nullfish.app.jfd2.ui.container2.NumberedJFD2;
import com.nullfish.app.jfd2.viewer.FileViewerManager;
import com.nullfish.lib.plugin.PluginManager;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;

/**
 * オプション表示コマンド
 * 
 * @author shunji
 */
public class OptionCommand extends Command {
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nullfish.lib.vfs.manipulation.abst.AbstractManipulation#doExecute()
	 */
	public void doExecute() throws VFSException {
		NumberedJFD2 jfd = (NumberedJFD2)getJFD();
		final VFile configDir = jfd.getJFDOwner().getConfigDirectory();
		try {
			final ConfigFrame frame = new ConfigFrame();
			frame.setModal(true);
			frame.loadPreference(configDir);
			frame.pack();
			frame.setLocationRelativeTo(null);
			
			frame.addWindowListener(new WindowAdapter() {
				public void windowClosed(WindowEvent e) {
					if(frame.isModified()) {
						try {
							NumberedJFD2.staticInit(configDir);
						} catch (VFSException e1) {
							e1.printStackTrace();
						}
						
						JFD2.initKeyMap(configDir);
						
						for(int i=0; i<NumberedJFD2.getCount(); i++) {
							try {
								NumberedJFD2.getJfdAt(i).init(configDir);
							} catch (VFSException ex) {
								ex.printStackTrace();
							}
						}
					}
					try {
						FileViewerManager.getInstance().init(configDir);
					} catch (Exception ex) {
						ex.printStackTrace();
					}

					PluginManager.getInstance().configurationChanged();
				}
			});
			
			frame.setVisible(true);
			frame.dispose();
			
			jfd.getJFDOwner().setActiveComponent(jfd);
		} catch (VFSException e) {
			throw e;
		} catch (Exception e) {
			throw new JFDException(e, "", new Object[0]);
		}
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nullfish.app.jfd2.command.Command#closesUnusingFileSystem()
	 */
	public boolean closesUnusingFileSystem() {
		return false;
	}
}
