package com.nullfish.app.jfd2.config;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import com.nullfish.app.jfd2.resource.JFDResource;

public class PathConfig extends JPanel {
	private String configName;
	
	private String labelName;
	
	private int mode = JFileChooser.FILES_AND_DIRECTORIES;
	
	private JLabel label = new JLabel();
	private JTextComponent textEditor = new JTextField();
	private JButton button = new JButton(JFDResource.LABELS.getString("modify"));
	
	private boolean escapesQuate = true;

	private int editor;
	
	public static final int EDITOR_TEXTFIELD = 0;
	public static final int EDITOR_TEXTAREA = 1;
	
	public PathConfig(String configName, String labelName, int mode) {
		this(configName, labelName, mode, EDITOR_TEXTFIELD);
	}
	
	public PathConfig(String configName, String labelName, int mode, int editor) {
		this.configName = configName;
		this.labelName = labelName;
		this.mode = mode;
		this.editor = editor;
		
		initGui();
	}
	
	private void initGui() {
		label.setText(JFDResource.LABELS.getString(labelName));
		JComponent textComp = null;
		switch(editor)
		{
		case EDITOR_TEXTAREA :
			textEditor = new JTextArea();
			((JTextArea)textEditor).setColumns(30);
			((JTextArea)textEditor).setRows(3);
			textComp = new JScrollPane(textEditor);
			break;
		case EDITOR_TEXTFIELD :
		default :
			textEditor = new JTextField();
			((JTextField)textEditor).setColumns(30);
			textComp = textEditor;
		}
		
		button.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				openDialog();
			}
		});
		
		setLayout(new GridBagLayout());
		add(label, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
		add(textComp, new GridBagConstraints(0, 1, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
		add(button, new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
		
	}
	
	private void openDialog() {
		File current = null;
		try {
			current = new File(textEditor.getText());
		} catch (Exception e) {}
		
		JFileChooser chooser = new JFileChooser(current);
		chooser.setFileSelectionMode(mode);
		chooser.setDialogTitle(label.getText());
		
		int returnVal = chooser.showOpenDialog(button);
		if(returnVal != JFileChooser.APPROVE_OPTION) {
			return;
		}
		
		String fileName = chooser.getSelectedFile().toString();
		if(escapesQuate && fileName.indexOf(' ') >= 0 ) {
			if(fileName.indexOf('(') >= 0 || fileName.indexOf(')') >= 0) {
				fileName = "\"\"" + fileName + "\"\"";
			} else {
				fileName = "\"" + fileName + "\"";
			}
		}
		
		textEditor.setText(fileName);
	}

	/**
	 * 設定を表示に反映する。
	 * 
	 * @param configuration
	 */
	public void setConfiguration(Configuration configuration) {
		textEditor.setText((String) configuration.getParam(configName, ""));
	}

	/**
	 * 設定に反映する。
	 * 
	 * @param configuration
	 */
	public void apply(Configuration configuration) {
		configuration.setParam(configName, textEditor.getText());
	}

	public void setEscapesQuate(boolean escapesQuate) {
		this.escapesQuate = escapesQuate;
	}
}
