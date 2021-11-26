/*
 * Created on 2004/12/29
 *
 */
package com.nullfish.app.jfd2.viewer.constraints;

import com.nullfish.app.jfd2.JFDComponent;
import com.nullfish.app.jfd2.ui.container2.ContainerPosition;
import com.nullfish.app.jfd2.ui.container2.JFDOwner;
import com.nullfish.app.jfd2.viewer.FileViewerConstraints;

/**
 * @author shunji
 *
 */
public class OpponentConstraints extends FileViewerConstraints {
	public static final String NAME = "opponent";
	
	/**
	 * @param name
	 */
	public OpponentConstraints() {
		super(NAME);
	}

	
	/**
	 * 
	 * @param jfd
	 * @return
	 */
	public ContainerPosition getPosition(JFDComponent component) {
		JFDOwner owner = component.getJFDOwner();
		if(owner == null) {
			return null;
		}
		
		ContainerPosition position = owner.getComponentPosition(component);
		return position.getOpponent();
	}
}
