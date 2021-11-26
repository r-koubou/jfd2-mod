package com.nullfish.lib.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.Element;

public class LineNumberView extends JComponent {
	private static final int MARGIN = 5;
	private RowHeightAccessibleTextArea text;
	private FontMetrics fontMetrics;
	private int topInset;
	private int fontAscent;
	private int rowHeight;

	private boolean shows = true;

	public LineNumberView(RowHeightAccessibleTextArea textArea) {
		text = textArea;
		init();
		text.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				repaint();
			}

			public void removeUpdate(DocumentEvent e) {
				repaint();
			}

			public void changedUpdate(DocumentEvent e) {
			}
		});
		text.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				revalidate();
				repaint();
			}
		});
		setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.GRAY));
		setOpaque(true);
		setBackground(Color.BLACK);
	}

	public void init() {
		Font font = text.getFont();
		fontMetrics = getFontMetrics(font);
		fontAscent = fontMetrics.getAscent();
		topInset = text.getInsets().top;
		rowHeight = text.getRowHeight();
	}

	private int getComponentWidth() {
		if (!shows) {
			return 0;
		}
		Document doc = text.getDocument();
		Element root = doc.getDefaultRootElement();
		int lineCount = root.getElementIndex(doc.getLength());
		int maxDigits = Math.max(3, String.valueOf(lineCount).length());
		return maxDigits * fontMetrics.stringWidth("0") + MARGIN * 2;
	}

	public int getLineAtPoint(int y) {
		Element root = text.getDocument().getDefaultRootElement();
		int pos = text.viewToModel(new Point(0, y));
		return root.getElementIndex(pos);
	}

	public Dimension getPreferredSize() {
		return new Dimension(getComponentWidth(), text.getHeight());
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (!shows) {
			return;
		}
		Rectangle clip = g.getClipBounds();
		g.setColor(text.getBackground());
		g.fillRect(clip.x, clip.y, clip.width, clip.height);
		g.setColor(text.getForeground());

		int base = clip.y - (clip.y % rowHeight) + fontAscent + topInset;
		g.setColor(text.getForeground());
		for (int y = base; y < base + clip.height + rowHeight; y += rowHeight) {
			int line = getLineAtPoint(y);
			if (y - rowHeight < 0 || line != getLineAtPoint(y - rowHeight)) {
				String text = String.valueOf(line + 1);
				int x = getComponentWidth() - MARGIN
						- fontMetrics.stringWidth(text);
				g.drawString(text, x, y);
			}
		}
	}

	public boolean isShows() {
		return shows;
	}

	public void setShows(boolean shows) {
		this.shows = shows;
		revalidate();
		repaint();
	}

	public static class RowHeightAccessibleTextArea extends JTextArea {
		public int getRowHeight() {
			return super.getRowHeight();
		}
	}
}
