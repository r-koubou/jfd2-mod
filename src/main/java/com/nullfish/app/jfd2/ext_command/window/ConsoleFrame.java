package com.nullfish.app.jfd2.ext_command.window;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;

import com.nullfish.app.jfd2.resource.JFDResource;
import com.nullfish.lib.keymap.KeyStrokeMap;

public class ConsoleFrame extends JFrame {

	private JTextArea textArea = new JTextArea();
	
	private JScrollPane scroll = new JScrollPane(textArea);
	
	private JButton closeButton = new JButton(JFDResource.LABELS.getString("exit") + "(C)");
	private JButton clearButton = new JButton(JFDResource.LABELS.getString("clear") + "(L)");
	
	private boolean showsAutomatic = true;
	
    private static final ConsoleFrame instance = new ConsoleFrame();
	
	public ConsoleFrame() {
		initGui();
		pack();
		setLocationRelativeTo(null);
	}
	
	public static ConsoleFrame getInstance() {
		return instance;
	}
	
	private void initGui() {
		setTitle("jFD2 - Console");
		scroll.setPreferredSize(new Dimension(640,400));
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		textArea.setLineWrap(true);
		
		JPanel basePanel = new JPanel();
		this.setContentPane(basePanel);
		basePanel.setLayout(new BorderLayout());
		
		basePanel.add(scroll, BorderLayout.CENTER);
		
		basePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "close");
		basePanel.getActionMap().put("close", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		
		JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonsPanel.add(closeButton);
		buttonsPanel.add(clearButton);
		
		basePanel.add(buttonsPanel, BorderLayout.SOUTH);
		
		closeButton.setMnemonic('c');
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		
		clearButton.setMnemonic('l');
		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clear();
			}
		});
	}
	
	public void clear() {
		textArea.setText("");
	}
	
	public void copy() {
		textArea.copy();
	}

	public void printStackTrace(Throwable e) {
		 StringWriter sw = new StringWriter();
		 PrintWriter pw = new PrintWriter(sw);
		 e.printStackTrace(pw);
		 pw.flush();
		 print(sw.getBuffer().toString());
		 pw.close();
	}
	
	public void print(int o) {
		print(Integer.valueOf(o));
	}
	
	public void print(double o) {
		print(Double.valueOf(o));
	}
	
	public void print(long o) {
		print(Long.valueOf(o));
	}
	
	public void print(float o) {
		print(Float.valueOf(o));
	}
	
	public void print(char o) {
		print(Character.valueOf(o));
	}
	
	public void println(int o) {
		print(Integer.valueOf(o));
		print("\n");
	}
	
	public void println(double o) {
		print(Double.valueOf(o));
		print("\n");
	}
	
	public void println(long o) {
		print(Long.valueOf(o));
		print("\n");
	}
	
	public void println(float o) {
		print(Float.valueOf(o));
		print("\n");
	}
	
	public void println(char o) {
		print(Character.valueOf(o));
		print("\n");
	}
	
	public void println(Object o) {
		print(o);
		print("\n");
	}
	
	public void print(Object o) {
		if(showsAutomatic && !isVisible()) {
			setVisible(true);
		}
		
		try {
			textArea.getDocument().insertString(textArea.getDocument().getLength(), String.valueOf(o), null);
			textArea.setCaretPosition(textArea.getDocument().getLength());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		
		toFront();
	}
	
	public void setShowsAutomatic(boolean shows) {
		this.showsAutomatic = shows;
	}
}
