/*
 * Created on 2005/01/19
 *
 */
package com.nullfish.app.jfd2.ui.container2;

import com.nullfish.app.jfd2.JFDModel;
import com.nullfish.app.jfd2.JFDModelAdapter;
import com.nullfish.lib.vfs.VFile;

/**
 * @author shunji
 *
 */
public class JFD2TitleUpdater implements TitleUpdater {
	private NumberedJFD2 jfd;
	
	private JFDContainer container;
	
	private StringBuffer buffer = new StringBuffer();
	
	public JFD2TitleUpdater(final NumberedJFD2 jfd) {
		this.jfd = jfd;
		jfd.addNumberChangeListener(new JFDNumberChangeListener() {

			public void numberChanged(int newNumber) {
				updateTitle();
			}
		});
		
		jfd.getModel().addJFDModelListener(new JFDModelAdapter() {
			public void directoryChanged(JFDModel model) {
				updateTitle();
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.ui.container2.TitleUpdater#setContainer(com.nullfish.app.jfd2.ui.container2.JFDContainer)
	 */
	public void setContainer(JFDContainer container) {
		this.container = container;
		updateTitle();
	}

	private void updateTitle() {
		if(jfd == null || container == null) {
			return;
		}
		
		buffer.setLength(0);
		buffer.append("(");
		buffer.append(jfd.getIndexOfJfd() + 1);
		buffer.append(") ");
		VFile currentDirectory = jfd.getModel().getCurrentDirectory();
		if(currentDirectory != null) {
			buffer.append(currentDirectory.getSecurePath());
		}
		
		container.setTitle(buffer.toString());
	}
}
