/*
 * Created on 2004/11/13
 *
 */
package com.nullfish.app.jfd2.viewer;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.lib.vfs.VFile;

/**
 * @author shunji
 *
 */
public interface FileViewer {
	public void init(VFile baseDir);
	
	public void open(VFile file, JFD jfd);
	
	public void close();
	
	public JFD getJFD();
	
	public void dispose();
	
	public void setParam(String name, Object value);
	
	public Object getParam(String name);
	
	public void setConstraints(FileViewerConstraints constraints);
	
	public FileViewerConstraints getConstraints();
	
	public void setPosition(FileViewerPosition position);
	
	public FileViewerPosition getPosition();
	
	public void setFollowsCursor(boolean follows);
	
	public void setController(ViewerController controlle);

	public ViewerController getController();
}
