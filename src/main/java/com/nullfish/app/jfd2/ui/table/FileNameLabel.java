package com.nullfish.app.jfd2.ui.table;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.nullfish.lib.vfs.VFile;

public class FileNameLabel extends JPanel {
	private JLabel nameLabel = new JLabel();
	private JLabel dotLabel = new JLabel();
	private JLabel extensionLabel = new JLabel();
	
	public FileNameLabel() {
		setLayout(new FileNameLayout());
		add(nameLabel, FileNameLayout.FILENAME);
		add(dotLabel, FileNameLayout.DOT);
		add(extensionLabel, FileNameLayout.EXTENSION);
	}
	
	public void setFont(Font font) {
		if(nameLabel != null) {
			nameLabel.setFont(font);
		}
		if(dotLabel != null) {
			dotLabel.setFont(font);
		}
		if(extensionLabel != null) {
			extensionLabel.setFont(font);
		}
	}
	
	public void setForeground(Color color) {
		if(nameLabel != null) {
			nameLabel.setForeground(color);
		}
		if(dotLabel != null) {
			dotLabel.setForeground(color);
		}
		if(extensionLabel != null) {
			extensionLabel.setForeground(color);
		}
	}
	
	public void setBackground(Color color) {
		super.setBackground(color);
		if(nameLabel != null) {
			nameLabel.setBackground(color);
		}
		if(dotLabel != null) {
			dotLabel.setForeground(color);
		}
		if(extensionLabel != null) {
			extensionLabel.setBackground(color);
		}
	}

	public void setFile(VFile file, VFile current, boolean relative) {
		if(file == null) {
			nameLabel.setText("");
			dotLabel.setText("");
			extensionLabel.setText("");
			return;
		}
		
		if (file.equals(current.getFileSystem().getMountPoint())) {
			nameLabel.setText("...");
			dotLabel.setText("");
			extensionLabel.setText("");
			return;
		} 

		if(current.equals(file)) {
			nameLabel.setText(".");
			dotLabel.setText("");
			extensionLabel.setText("");
			return;
		}
		
		if(current.getParent() != null && current.getParent().equals(file.getParent())) {
			nameLabel.setText("..");
			dotLabel.setText("");
			extensionLabel.setText("");
			return;
		}
		
		if(relative && file.getParent() != current) {
			nameLabel.setText(current.getRelation(file));
			dotLabel.setText("");
			extensionLabel.setText("");
			return;
		} else {
			nameLabel.setText(file.getFileName().getExceptExtension());
			dotLabel.setText(file.getName().indexOf('.') != -1 ? "." : "");
			extensionLabel.setText(file.getFileName().getExtension());
			return;
		}
	}
}
