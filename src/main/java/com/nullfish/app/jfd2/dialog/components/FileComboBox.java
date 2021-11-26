/*
 * Created on 2004/09/03
 *
 */
package com.nullfish.app.jfd2.dialog.components;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.dialog.JFDDialog;

/**
 * @author shunji
 * 
 * Preferences - Java - Code Style - Code Templates
 */
public class FileComboBox extends DialogComboBox  {
	public FileComboBox(JFDDialog dialog, JFD jfd) {
		super(new CompletionComboBoxEditor(jfd), dialog);
	}
}
