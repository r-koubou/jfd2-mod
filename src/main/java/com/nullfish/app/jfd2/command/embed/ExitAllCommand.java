/*
 * Created on 2004/05/31
 *
 */
package com.nullfish.app.jfd2.command.embed;

import java.io.IOException;

import org.jdom.JDOMException;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.command.Command;
import com.nullfish.app.jfd2.ui.container2.JFDFrame;
import com.nullfish.app.jfd2.ui.container2.JFDOwner;
import com.nullfish.lib.vfs.exception.VFSException;

/**
 * 終了コマンド
 * 
 * @author shunji
 */
public class ExitAllCommand extends Command {
	/* (non-Javadoc)
	 * @see com.nullfish.lib.vfs.manipulation.abst.AbstractManipulation#doExecute()
	 */
	public void doExecute() throws VFSException {
		JFD jfd = getJFD();
		JFDOwner owner = jfd.getJFDOwner();
		
		try {
			jfd.save(owner.getConfigDirectory());
			
			if (owner instanceof JFDFrame) {
				JFDFrame.saveSizeTabConfig(owner.getConfigDirectory());
				while(JFDFrame.getInstanceCount() > 0) {
					JFDFrame frame = JFDFrame.getInstance(0);
					frame.removeAllComponents();
					frame.dispose();
				}
			}
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.command.Command#closesUnusingFileSystem()
	 */
	public boolean closesUnusingFileSystem() {
		return false;
	}
}
