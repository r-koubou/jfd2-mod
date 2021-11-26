/*
 * Created on 2005/01/20
 *
 */
package com.nullfish.app.jfd2;

import java.awt.Component;

import com.nullfish.app.jfd2.ui.container2.JFDOwner;

/**
 * @author shunji
 *
 */
public interface JFDComponent {
	public void setOwner(JFDOwner owner);
	
	public JFDOwner getJFDOwner();
	
	public Component getComponent();
	
	public void dispose();
}
