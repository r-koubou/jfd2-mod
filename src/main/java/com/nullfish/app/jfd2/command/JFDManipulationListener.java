/*
 * Created on 2004/08/25
 *
 */
package com.nullfish.app.jfd2.command;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.lib.vfs.ManipulationEvent;
import com.nullfish.lib.vfs.ManipulationListener;

/**
 * @author shunji
 *
 */
public class JFDManipulationListener implements ManipulationListener {
	private Command command;
	
	public JFDManipulationListener(Command command) {
		this.command = command;
	}
	
	/* (non-Javadoc)
	 * @see com.nullfish.lib.vfs.ManipulationListener#started(com.nullfish.lib.vfs.ManipulationEvent)
	 */
	public void started(ManipulationEvent e) {
//		if(command.isLocks()) {
//			command.getJFD().setLocked(true);
//		}
		
		if(command.isPrimary()) {
			command.getJFD().setPrimaryCommand(command);
		}
	}

	/* (non-Javadoc)
	 * @see com.nullfish.lib.vfs.ManipulationListener#preparationStarted(com.nullfish.lib.vfs.ManipulationEvent)
	 */
	public void preparationStarted(ManipulationEvent e) {
	}

	/* (non-Javadoc)
	 * @see com.nullfish.lib.vfs.ManipulationListener#preparationFinished(com.nullfish.lib.vfs.ManipulationEvent)
	 */
	public void preparationFinished(ManipulationEvent e) {
	}

	/* (non-Javadoc)
	 * @see com.nullfish.lib.vfs.ManipulationListener#manipulationStarted(com.nullfish.lib.vfs.ManipulationEvent)
	 */
	public void manipulationStarted(ManipulationEvent e) {
	}

	/* (non-Javadoc)
	 * @see com.nullfish.lib.vfs.ManipulationListener#manipulationFinished(com.nullfish.lib.vfs.ManipulationEvent)
	 */
	public void manipulationFinished(ManipulationEvent e) {
	}

	/* (non-Javadoc)
	 * @see com.nullfish.lib.vfs.ManipulationListener#finished(com.nullfish.lib.vfs.ManipulationEvent)
	 */
	public void finished(ManipulationEvent e) {
		JFD jfd = command.getJFD();
		if(command.isLocks()) {
			jfd.setLocked(false);
		}
		
		if(command.isPrimary()) {
			Command primary = jfd.getPrimaryCommand();
			if(primary == command) {
				jfd.setPrimaryCommand(null);
			}
		}
	}

	/**
	 * 中止されたときの処理
	 * @see com.nullfish.lib.vfs.ManipulationListener#manipulationStopped(com.nullfish.lib.vfs.ManipulationEvent)
	 */
	public void manipulationStopped(ManipulationEvent e) {
/*
		String[] messages = {
				//JFDResource.MESSAGES.getString("manipulation_stopped"),
				e.getException().getErrorMessage()
		};
		DialogUtilities.showMessageDialog(command.getJFD(), messages, "jFD2");
*/
	}

	public void manipulationStopping(ManipulationEvent e) {}

}
