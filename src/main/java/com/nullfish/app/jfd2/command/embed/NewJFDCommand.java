/*
 * Created on 2004/05/31
 *
 */
package com.nullfish.app.jfd2.command.embed;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.JFD2;
import com.nullfish.app.jfd2.command.Command;
import com.nullfish.app.jfd2.ui.container2.ContainerPosition;
import com.nullfish.app.jfd2.ui.container2.JFD2TitleUpdater;
import com.nullfish.app.jfd2.ui.container2.JFDOwner;
import com.nullfish.app.jfd2.ui.container2.NumberedJFD2;
import com.nullfish.lib.vfs.VFS;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;

/**
 * 
 * 
 * @author shunji
 */
public class NewJFDCommand extends Command {
	/**
	 * オープン条件
	 */
	public static final String CONSTRAINTS = "constraints";

	/**
	 * ディレクトリの場合はそのディレクトリを開く
	 */
	public static final String OPENS_DIR = "opens_dir";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nullfish.lib.vfs.manipulation.abst.AbstractManipulation#doExecute()
	 */
	public void doExecute() throws VFSException {
		JFD jfd = getJFD();
		JFDOwner owner = jfd.getJFDOwner();
		if (owner == null) {
			return;
		}

		VFile configDir = owner.getConfigDirectory();
		try {
			jfd.save(configDir);
		} catch (VFSException e) {
			e.printStackTrace();
		}

		final NumberedJFD2 newJFD = new NumberedJFD2();

		try {
			newJFD.init(configDir);
		} catch (VFSException e) {
			e.printStackTrace();
		}

		owner.addComponent(newJFD, ContainerPosition
				.getInstance((String) getParameter(CONSTRAINTS)),
				new JFD2TitleUpdater(newJFD));

		try {
			String path = (String) newJFD.getLocalConfiguration().getParam(
					JFD2.CONFIG_LAST_OPENED, System.getProperty("user.home"));
			if (getParameter(OPENS_DIR) != null
					&& ((Boolean) getParameter(OPENS_DIR)).booleanValue()
					&& jfd.getModel().getSelectedFile().isDirectory(this)) {
				path = jfd.getModel().getSelectedFile().getAbsolutePath();
			}

			VFile dir = VFS.getInstance(newJFD).getFile(path);
			if (!dir.exists()) {
				dir = VFS.getInstance(newJFD).getFile(
						System.getProperty("user.home"));
			}

			newJFD.getModel().setDirectoryAsynchIfNecessary(dir,
					dir.getParent(), newJFD);
		} catch (Exception e) {
			// e.printStackTrace();
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
