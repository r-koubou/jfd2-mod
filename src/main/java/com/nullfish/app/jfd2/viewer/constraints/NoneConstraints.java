/*
 * Created on 2004/12/29
 *
 */
package com.nullfish.app.jfd2.viewer.constraints;

import com.nullfish.app.jfd2.JFDComponent;
import com.nullfish.app.jfd2.ui.container2.ContainerPosition;
import com.nullfish.app.jfd2.viewer.FileViewerConstraints;

/**
 * @author shunji
 *
 */
public class NoneConstraints extends FileViewerConstraints {
	public static final String NAME = "none";
	
	/**
	 * @param name
	 */
	public NoneConstraints() {
		super(NAME);
	}

	
	/**
	 * 
	 * @param jfd
	 * @return
	 */
	public ContainerPosition getPosition(JFDComponent component) {
		return null;
	}
}
