/*
 * Created on 2004/08/27
 *
 */
package com.nullfish.app.jfd2.command.progress;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JPanel;

import com.nullfish.lib.ui.ThreadSafeUtilities;
import com.nullfish.lib.vfs.Manipulation;
import com.nullfish.lib.vfs.ManipulationAdapter;
import com.nullfish.lib.vfs.ManipulationEvent;

/**
 * @author shunji
 *
 */
public class ProgressListPanel extends JPanel {
	private int yPosition = 0;
	
	private Set manipulations = new HashSet();
	
	public ProgressListPanel() {
		setLayout(new GridBagLayout());
		setBackground(Color.WHITE);
		
		JPanel verticalPanel = new JPanel();
		verticalPanel.setOpaque(false);
		add(verticalPanel, new GridBagConstraints(0, 100, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

		JPanel horizontalPanel = new JPanel();
		horizontalPanel.setOpaque(false);
		add(horizontalPanel, new GridBagConstraints(100, 0, 1, 100, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
	}
	
	public void add(final Manipulation manipulation) {
		Runnable runnable = new Runnable() {
			
			public void run() {
				final ProgressPanel panel = new ProgressPanel(manipulation,ProgressListPanel.this);

				manipulation.addManipulationListener(new ManipulationAdapter() {
					public void finished(ManipulationEvent e) {
						removePanel(panel, manipulation);
					}
					public void manipulationStopping(ManipulationEvent e) {
						removePanel(panel, manipulation);
					}
				});
				
				add(panel, new GridBagConstraints(0, yPosition, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
				yPosition++;
				manipulations.add(manipulation);
				revalidate();
			}
		};
		
		ThreadSafeUtilities.executeRunnable(runnable);
	}
	
	public void removePanel(final ProgressPanel panel, final Manipulation manipulation) {
		Runnable runnable = new Runnable() {
			public void run() {
				remove(panel);
				manipulations.remove(manipulation);
				if(manipulations.size() == 0) {
					yPosition = 0;
				}
				repaint();
				revalidate();
			}
		};
		
		ThreadSafeUtilities.executeRunnable(runnable);
	}
}
