/*
 * Created on 2004/05/31
 *
 */
package com.nullfish.app.jfd2.command.embed;

import com.nullfish.app.jfd2.command.Command;
import com.nullfish.app.jfd2.ext_command.window.ConsoleFrame;
import com.nullfish.lib.vfs.exception.VFSException;

/**
 * ファイルマークコマンド
 * 
 * @author shunji
 */
public class GCCommand extends Command {
	/* (non-Javadoc)
	 * @see com.nullfish.lib.vfs.manipulation.abst.AbstractManipulation#doExecute()
	 */
	public void doExecute() throws VFSException {
		ConsoleFrame.getInstance().println("Before");
        showMemory();

		Runtime.getRuntime().gc();
		Runtime.getRuntime().runFinalization();
		Runtime.getRuntime().gc();
		
		ConsoleFrame.getInstance().println("After");
        showMemory();
    }

    public void showMemory() {
        long total = Runtime.getRuntime().totalMemory();
        ConsoleFrame.getInstance().println(
            "Total Memory : " + total + "\n" +
            "Spent Memory : " + (total - Runtime.getRuntime().freeMemory()) + "\n"
        );
    }

	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.command.Command#closesUnusingFileSystem()
	 */
	public boolean closesUnusingFileSystem() {
		return false;
	}
}
