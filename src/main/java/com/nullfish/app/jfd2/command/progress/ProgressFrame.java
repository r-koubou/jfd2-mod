/*
 * Created on 2004/08/28
 *
 */
package com.nullfish.app.jfd2.command.progress;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import com.nullfish.app.jfd2.command.Command;
import com.nullfish.app.jfd2.resource.JFDResource;
import com.nullfish.lib.ui.ThreadSafeUtilities;
import com.nullfish.lib.vfs.Manipulation;
import com.nullfish.lib.vfs.ManipulationAdapter;
import com.nullfish.lib.vfs.ManipulationEvent;

/**
 * @author shunji
 * 
 * Preferences - Java - Code Style - Code Templates
 */
public class ProgressFrame extends JFrame implements ProgressViewer {
	private JScrollPane scroll = new JScrollPane();

	private ProgressListPanel listPanel = new ProgressListPanel();

	private List manipulationList = new ArrayList();

	public ProgressFrame() {
		setTitle(JFDResource.LABELS.getString("title_progress_window"));
		initGUI();
		pack();
	}

	private void initGUI() {
		this.getContentPane().setLayout(new BorderLayout());
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		scroll.setPreferredSize(new Dimension(310, 350));
		scroll.setViewportView(listPanel);

		this.getContentPane().add(scroll, BorderLayout.CENTER);
	}

	public void addManipulation(final Manipulation manipulation) {
		Runnable runnable = new Runnable() {
			public void run() {
				manipulationList.add(manipulation);

				manipulation.addManipulationListener(new ManipulationAdapter() {
					public void finished(ManipulationEvent e) {
						ProgressFrame.this.manipulationFinished(manipulation);
					}
					public void manipulationStopping(ManipulationEvent e) {
						ProgressFrame.this.manipulationFinished(manipulation);
					}
				});

				listPanel.add(manipulation);

				Component focusedComponent = KeyboardFocusManager
						.getCurrentKeyboardFocusManager().getFocusOwner();

				setVisible(true);
				
				if(focusedComponent != null) {
					focusRootComponent(focusedComponent);
					focusedComponent.requestFocusInWindow();
				}
			}
		};
		
		ThreadSafeUtilities.executeRunnable(runnable);
	}
	
	private void focusRootComponent(Component comp) {
		Container cont = comp.getParent();
		while(cont.getParent() != null && !(cont instanceof Window)) {
			cont = cont.getParent();
		}
		
		if(cont instanceof Window) {
			((Window)cont).toFront();
		}
		
		cont.requestFocus();
	}

	private void manipulationFinished(final Manipulation manipulation) {
		Runnable runnable = new Runnable() {
			public void run() {
				manipulationList.remove(manipulation);
				if (manipulationList.size() == 0) {
					ProgressFrame.this.setVisible(false);
				}
			}
		};
		
		ThreadSafeUtilities.executeRunnable(runnable);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nullfish.app.jfd2.command.progress.ProgressViewer#addCommand(com.nullfish.app.jfd2.command.Command)
	 */
	public void addCommand(Command command) {
		addManipulation(command);
	}
}
