/*
 * Created on 2004/05/31
 *
 */
package com.nullfish.app.jfd2.command.embed;

import com.nullfish.app.jfd2.command.Command;
import com.nullfish.app.jfd2.dialog.DialogUtilities;
import com.nullfish.app.jfd2.dialog.JFDDialog;
import com.nullfish.app.jfd2.resource.JFDResource;
import com.nullfish.lib.vfs.exception.VFSException;

/**
 * 書きかけ
 * 
 * @author shunji
 */
public class DownloadPluginCommand extends Command {
	/* (non-Javadoc)
	 * @see com.nullfish.lib.vfs.manipulation.abst.AbstractManipulation#doExecute()
	 */
	public void doExecute() throws VFSException {
		JFDDialog dialog = null;
		try {
			//String pluginDirPath = (String)getJFD().getCommonConfiguration().getParam("plugin_dir", null);
			
			dialog = DialogUtilities.createOkCancelDialog(getJFD(), JFDResource.LABELS.getString("import_plugin"));
			
			String[] message = {
					JFDResource.MESSAGES.getString("input_plugin_path")
			};
			dialog.setMessage(message);
			dialog.addTextField("path", "", true);
			
			dialog.pack();
			dialog.setVisible(true);
			
		} finally {
			dialog.dispose();
		}
    }

	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.command.Command#closesUnusingFileSystem()
	 */
	public boolean closesUnusingFileSystem() {
		return false;
	}
}
