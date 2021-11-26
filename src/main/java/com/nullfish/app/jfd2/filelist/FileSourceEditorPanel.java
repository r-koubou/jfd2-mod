package com.nullfish.app.jfd2.filelist;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.nullfish.app.jfd2.resource.JFDResource;
import com.nullfish.lib.vfs.VFS;
import com.nullfish.lib.vfs.exception.VFSException;
import com.nullfish.lib.vfs.impl.filelist.condition.FileSource;

public class FileSourceEditorPanel extends JPanel {
	private FileSourceEditorsPanel parent;
	
	private JLabel titleLabel = new JLabel(JFDResource.LABELS.getString("source_title"));
	private JLabel generationLabel = new JLabel(JFDResource.LABELS.getString("source_generation"));
	
	private JTextField pathText = new JTextField(30);
	private JButton referButton = new JButton(JFDResource.LABELS.getString("refer"));
	
	private JComboBox generationCombo = new JComboBox();
	
	private JButton addButton = new JButton("+");
	private JButton removeButton = new JButton("-");
	
	public FileSourceEditorPanel(FileSourceEditorsPanel parent) {
		super(new GridBagLayout());
		this.parent = parent;
		
		Dimension d = generationCombo.getPreferredSize();
		d.width = 50;
		generationCombo.setPreferredSize(d);

		add(titleLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
		add(pathText, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));
		add(referButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(3, 0, 3, 3), 0, 0));
		add(generationCombo, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 0), 0, 0));
		add(generationLabel, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));
		
		generationCombo.addItem(JFDResource.LABELS.getString("generation_infinity"));
		for(int i=1; i<=20; i++) {
			generationCombo.addItem(new Integer(i));
		}
		
		add(removeButton, new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3, 3, 3, 0), 0, 0));
		add(addButton, new GridBagConstraints(6, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3, 0, 3, 3), 0, 0));
		
		removeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FileSourceEditorPanel.this.parent.removeChild(FileSourceEditorPanel.this);
			}
		});
		
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FileSourceEditorPanel.this.parent.addNew();
			}
		});
		
		referButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				showRefereDialog();
			}
		});
	}
	
	public void init(FileSource source) {
		pathText.setText(source.getDirectory().getAbsolutePath());
		generationCombo.setSelectedIndex(source.getGeneration());
	}
	
	public FileSource getFileSource(VFS vfs) throws VFSException {
		return new FileSource(vfs, vfs.getFile(pathText.getText()), generationCombo.getSelectedIndex());
	}
	
	public void parentUpdated() {
		removeButton.setEnabled(parent.getCount() > 1);
	}
	
	public boolean isEmpty() {
		return pathText.getText().length() == 0;
	}

	public void showRefereDialog() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		try {
			File file = new File(pathText.getText());
			if(file.exists()) {
				chooser.setSelectedFile(file);
			}
		} catch (Exception e) {
		}
		
		int result = chooser.showOpenDialog(this);
		if(result != JFileChooser.APPROVE_OPTION) {
			return;
		}
		
		pathText.setText(chooser.getSelectedFile().getAbsolutePath());
	}
}
