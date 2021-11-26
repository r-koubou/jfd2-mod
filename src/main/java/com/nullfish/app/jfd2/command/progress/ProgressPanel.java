/*
 * Created on 2004/08/27
 *
 */
package com.nullfish.app.jfd2.command.progress;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import com.nullfish.app.jfd2.resource.JFDResource;
import com.nullfish.lib.vfs.Manipulation;

/**
 * 個々の操作の状況を表示するパネル
 * @author shunji
 */
public class ProgressPanel extends JPanel {
	private Manipulation manipulation;
	
	private ProgressListPanel listPanel;
	
	private ManipulationProgressBar totalProgress = new ManipulationProgressBar();
	private ManipulationProgressBar currentProgress = new ManipulationProgressBar();
	private JLabel messageLabel = new JLabel(" ");
	
	JButton stopButton = new JButton();
	
	private Timer timer;
	
	JLabel totalLabel = new JLabel(JFDResource.LABELS.getString("total_manipulation"));
	JLabel currentLabel = new JLabel(JFDResource.LABELS.getString("current_manipulation"));
	
	/**
	 * コンストラクタ
	 * @param manipulation
	 */
	public ProgressPanel(Manipulation manipulation, ProgressListPanel listPanel) {
		initGUI();
		initManipulation(manipulation);
		this.listPanel = listPanel;
	}
	
	private void initManipulation(Manipulation manipulation) {
		this.manipulation = manipulation;
		timer = new Timer(200, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				apply();
			}
		});
		
		timer.start();
	}
	
	private void initGUI() {
		setBorder(BorderFactory.createRaisedBevelBorder());
		stopButton.setText(JFDResource.LABELS.getString("cancel"));
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(manipulation != null) {
					manipulation.stop();
				}
			}
		});
		
		setLayout(new GridBagLayout());
		add(totalLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2, 4, 2, 4), 0, 0));
		add(totalProgress, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2, 4, 2, 4), 0, 0));
		add(currentLabel, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2, 4, 2, 4), 0, 0));
		add(currentProgress, new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2, 4, 2, 4), 0, 0));
		add(messageLabel, new GridBagConstraints(0, 2, 2, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2, 4, 2, 4), 0, 0));
		add(stopButton, new GridBagConstraints(0, 3, 2, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2, 4, 2, 4), 0, 0));

		Dimension size = getPreferredSize();
		size.width = 300;
		setPreferredSize(size);
	}
	
	private void apply() {
		try {
			if(manipulation.isStopped() || manipulation.isFinished()) {
				listPanel.removePanel(this, manipulation);
				timer.stop();
				manipulation = null;
				return;
			}
			
			totalProgress.apply(manipulation);
			Manipulation current = 
				manipulation != null ? manipulation.getCurrentManipulation() : null;
			currentProgress.apply(current);
			
//			String message = current != null ? current.getProgressMessage() : " ";
			String message = manipulation != null ? manipulation.getProgressMessage() : " ";
			if(message.length() == 0) {
				message = " ";
			}
			messageLabel.setText(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
