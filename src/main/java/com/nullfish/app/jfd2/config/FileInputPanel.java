package com.nullfish.app.jfd2.config;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.nullfish.app.jfd2.resource.JFDResource;

public class FileInputPanel extends JPanel {
	private JLabel label = new JLabel();
	
	private JTextField text = new JTextField();
	
	private JButton button = new JButton(JFDResource.LABELS.getString("refer"));
	
	private int mode = JFileChooser.FILES_ONLY;
	
	public FileInputPanel(String orgFile, String message, int mode) {
		text.setText(orgFile);
		label.setText(message);
		this.mode = mode;
		
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File orgFile = null;
				try {
					orgFile = new File(text.getText());
				} catch (Exception ex) {}
				JFileChooser chooser = new JFileChooser(orgFile);
				chooser.setFileSelectionMode(FileInputPanel.this.mode);
				
				int returnVal = chooser.showOpenDialog(button);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					text.setText(chooser.getSelectedFile().getAbsolutePath());
				}
			}
		});
		
		setLayout(new GridBagLayout());
		add(label, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		add(text, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		add(button, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
	}
	
	public String getText() {
		return text.getText();
	}
}
