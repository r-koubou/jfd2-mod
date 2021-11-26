package com.nullfish.app.jfd2.filelist;

import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.dialog.DialogUtilities;
import com.nullfish.app.jfd2.resource.JFDResource;
import com.nullfish.lib.keymap.KeyStrokeMap;
import com.nullfish.lib.ui.document.RestrictedDocument;
import com.nullfish.lib.vfs.VFS;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;
import com.nullfish.lib.vfs.impl.filelist.condition.SearchCondition;

public class SmartFileListEditorDialog extends JDialog {
	private VFile file;

	private JFD jfd;
	
	private SearchCondition condition;
	
	private FileSourceEditorsPanel sourceEditorsPanel = new FileSourceEditorsPanel();
	
	private ConditionEditorsPanel conditionEditorsPanel;

	private JPanel panel = new JPanel(new GridBagLayout());
	private JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
	
	private JLabel title = new JLabel();
	private JLabel maxCountLabel = new JLabel(JFDResource.LABELS.getString("max_count"));
	private JTextField maxCountText = new JTextField(10);
	
	private JButton okButton = new JButton(JFDResource.LABELS.getString("ok"));
	private JButton cancelButton = new JButton(JFDResource.LABELS.getString("cancel"));
	
	public SmartFileListEditorDialog(JFD jfd, Dialog parent, VFile file) {
		super(parent, true);
		init(jfd, file);
	}
	
	public SmartFileListEditorDialog(JFD jfd, Frame parent, VFile file) {
		super(parent, true);
		init(jfd, file);
	}
	
	private void init(JFD jfd, VFile file) {
		this.jfd = jfd;
		this.file = file;
		
		initGui();
		title.setText(file.getSecurePath());
		
		try {
			if(file.exists()) {
				init();
			} else {
				initDefault();
			}
		} catch (VFSException e) {
			e.printStackTrace();
			initDefault();
		}
	}
	
	private void initGui() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle("jFD2 - Smart File List Editor");
		
		conditionEditorsPanel = new ConditionEditorsPanel(jfd);
		
		getContentPane().add(panel);
		buttonsPanel.add(okButton);
		buttonsPanel.add(cancelButton);
		
		RestrictedDocument numberDoc = new RestrictedDocument();
		maxCountText.setDocument(numberDoc);
		
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveAndClose();
			}
		});
		
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "dispose");
		panel.getActionMap().put("dispose", new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});

		JPanel maxCountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		maxCountPanel.add(maxCountLabel);
		maxCountPanel.add(maxCountText);
		
		panel.add(title, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 10, 5, 10), 0, 0));
		panel.add(sourceEditorsPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 10, 5, 10), 0, 0));
		panel.add(conditionEditorsPanel, new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 10, 5, 10), 0, 0));
		panel.add(maxCountPanel, new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 10, 5, 10), 0, 0));
		panel.add(buttonsPanel, new GridBagConstraints(0, 4, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
	}
	
	private void init() throws VFSException {
		condition = new SearchCondition(file.getFileSystem().getVFS());
		condition.init(file);
		
		conditionEditorsPanel.setConditions(condition.getConditions());
		sourceEditorsPanel.setFileSources(condition.getSources());
		
		int maxCount = condition.getMaxNumber();
		
		if(maxCount >= 0) {
			maxCountText.setText(Integer.toString(maxCount));
		}
		
		pack();
	}
	
	private void initDefault() {
		conditionEditorsPanel.clear();
		conditionEditorsPanel.addNew();
		
		sourceEditorsPanel.clear();
		sourceEditorsPanel.addNew(jfd.getModel().getCurrentDirectory());
		
		maxCountText.setText("");
		
		pack();
	}
	
	public void saveAndClose() {
		if(sourceEditorsPanel.hasEmptyChild()) {
			DialogUtilities.showMessageDialog(jfd, JFDResource.MESSAGES.getString("empty_file_source"), "jFD2");
			return;
		}
		
		try {
			VFS vfs = file.getFileSystem().getVFS();
			condition = new SearchCondition(vfs);
			
			condition.getConditions().addAll(conditionEditorsPanel.toConditionList(vfs));
			condition.getSources().addAll(sourceEditorsPanel.toFileSourceList(vfs));
			
			condition.setMaxNumber(maxCountText.getText().length() > 0 ? Integer.parseInt(maxCountText.getText()) : -1);
			
			condition.save(file);
		} catch (VFSException e) {
			e.printStackTrace();
		} finally {
			dispose();
		}
	}
}
