package com.nullfish.app.jfd2.dialog;

import java.awt.Dialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.resource.JFDResource;
import com.nullfish.app.jfd2.ui.shortcut_tree.ShortCutDialog;
import com.nullfish.app.jfd2.ui.tree.TreeDialog;
import com.nullfish.app.jfd2.util.ShortCutFile;
import com.nullfish.lib.keymap.KeyStrokeMap;
import com.nullfish.lib.vfs.VFile;

public class TextEditorTreeExtension extends JPanel implements TextEditor {
	public TextEditor editor;

	private JComponent inputComponent;

	private Dialog owner;

	private JFD jfd;
	
	private final VFile root;

	private JButton shortCutTreeButton = new JButton();

	private JButton dirTreeButton = new JButton();

	public TextEditorTreeExtension(final TextEditor editor,
			final JComponent inputComponent, final Dialog owner,
			final VFile root, JFD jfd) {
		this.editor = editor;
		this.inputComponent = inputComponent;
		this.owner = owner;
		this.root = root;
		this.jfd = jfd;

		setFocusable(true);
		addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				editor.getComponent().requestFocusInWindow();
			}

			public void focusLost(FocusEvent e) {
			}
		});
		
		setLayout(new GridBagLayout());

		add(editor.getComponent(), new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,
						0, 0, 0), 0, 0));
		add(shortCutTreeButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,
						0, 0, 0), 0, 0));
		add(dirTreeButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,
						0, 0, 0), 0, 0));

		getActionMap().put("shortcut", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				showDoalog();
			}
		});

		getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
				KeyStrokeMap.getKeyStroke(KeyEvent.VK_Q, KeyEvent.ALT_MASK),
				"shortcut");

		shortCutTreeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showDoalog();
			}
		});

		shortCutTreeButton.setToolTipText(JFDResource.LABELS
				.getString("choose_from_quickaccess"));
		

	
		getActionMap().put("tree", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				showTreeDialog();
			}
		});

		getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
				KeyStrokeMap.getKeyStroke(KeyEvent.VK_T, KeyEvent.ALT_MASK),
				"tree");

		dirTreeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showTreeDialog();
			}
		});

		dirTreeButton.setToolTipText(JFDResource.LABELS
				.getString("choose_from_tree"));
}

	public String getAnswer() {
		return editor.getAnswer();
	}

	public void setAnswer(String text) {
		editor.setAnswer(text);
	}

	public JComponent getComponent() {
		return this;
	}

//	public void requestFocus() {
//		super.requestFocus();
//		editor.getComponent().requestFocus();
//	}
//
//	public boolean requestFocusInWindow() {
//		super.requestFocusInWindow();
//		return editor.getComponent().requestFocusInWindow();
//	}
//
	private void showDoalog() {
		ShortCutDialog dialog = null;
		try {
			dialog = new ShortCutDialog(owner, root, inputComponent);
			dialog.showDialog();
			VFile selectedFile = dialog.getSelectedFile();
			if (selectedFile != null
					&& "jfdlnk".equals(selectedFile.getFileName()
							.getExtension().toLowerCase())) {
				ShortCutFile shortCutFile = new ShortCutFile(selectedFile);
				shortCutFile.load(null);
				editor.setAnswer(shortCutFile.getTarget().getSecurePath());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (dialog != null) {
				dialog.dispose();
			}
		}
	}

	private void showTreeDialog() {
		TreeDialog dialog = null;
		try {
			dialog = new TreeDialog(owner, jfd);
			dialog.pack();
			dialog.setLocationRelativeTo(owner);
			dialog.setSelectedFile(jfd.getModel().getCurrentDirectory());
			dialog.setVisible(true);
			
			VFile selectedFile = dialog.getSelectedFile();
			if(selectedFile == null) {
				return;
			}
			editor.setAnswer(selectedFile.getSecurePath());
		} finally {
			dialog.dispose();
		}
	}
}
