package com.nullfish.app.jfd2.ui.table;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

public class ImagePanel extends JPanel {
	private Image image;
	
	public void paintComponent(Graphics g) {
		if(image != null) {
			g.drawImage(image, (getWidth() - image.getWidth(this)) / 2, (getHeight() - image.getHeight(this)) / 2, this);
//			g.setColor(Color.BLUE);
//			g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		}
	}

	protected Image getImage() {
		return image;
	}

	protected void setImage(Image image) {
		this.image = image;
		repaint();
	}
}
