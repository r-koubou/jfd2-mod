/*
 * Created on 2004/06/16
 *
 */
package com.nullfish.app.jfd2.ui.function;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;

import com.nullfish.app.jfd2.JFD;

/**
 * @author shunji
 *
 */
public class CommandLabel extends JLabel {
	private CommandHolder commandHolder;
	
	JFD jfd;
	
	/**
	 * コンストラクタ
	 * @param manager
	 */
	public CommandLabel(JFD jfd) {
		super("     ");
		this.jfd = jfd;
		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) {
				if(CommandLabel.this.jfd != null && commandHolder != null) {
					CommandLabel.this.jfd.getCommandManager().execute(commandHolder.getCommand());
				}
			}
		});
	}
	
	public void setCommandHolder(CommandHolder commandHolder) {
		this.commandHolder = commandHolder;
		if(commandHolder != null) {
			String label = commandHolder.getLabelText();
			setText(label == null || label.length() == 0 ? "     " : label);
		} else {
			setText("     ");
		}
	}
}
