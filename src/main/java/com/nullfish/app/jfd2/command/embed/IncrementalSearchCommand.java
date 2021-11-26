package com.nullfish.app.jfd2.command.embed;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.JFDModel;
import com.nullfish.app.jfd2.command.Command;
import com.nullfish.app.jfd2.resource.JFDResource;
import com.nullfish.app.jfd2.util.WildCardUtil;
import com.nullfish.lib.ui.OneKeyButton;
import com.nullfish.lib.vfs.exception.VFSException;

public class IncrementalSearchCommand extends Command {
	/* (non-Javadoc)
	 * @see com.nullfish.lib.vfs.manipulation.abst.AbstractManipulation#doExecute()
	 */
	public void doExecute() throws VFSException {
		SearchDialog dialog = new SearchDialog();
		dialog.pack();
		dialog.setLocationRelativeTo(getJFD().getComponent());
		dialog.setVisible(true);
    }

	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.command.Command#closesUnusingFileSystem()
	 */
	public boolean closesUnusingFileSystem() {
		return false;
	}
	
	private void selectPrevFile(String initial, boolean includeCurrent) {
		if(initial == null || initial.length() == 0) {
			return;
		}
		
		initial = initial.toLowerCase();
		initial = "^" + WildCardUtil.wildCard2Regex(initial) + ".*";

		Pattern pattern = Pattern.compile(initial);
		
		JFD jfd = getJFD();
		JFDModel model = jfd.getModel();
		int selectedIndex = model.getSelectedIndex();
		
		for(int i=selectedIndex - (includeCurrent ? 0 : 1); i >= 0; i--) {
			if(pattern.matcher(model.getFileAt(i).getName().toLowerCase()).matches()) {
				model.setSelectedIndex(i);
				return;
			}
		}
		
		return;
	}
	
	private void selectNextFile(String initial, boolean includeCurrent) {
		if(initial == null || initial.length() == 0) {
			return;
		}
		
		initial = initial.toLowerCase();
		initial = initial.toLowerCase();
		initial = "^" + WildCardUtil.wildCard2Regex(initial) + ".*";

		Pattern pattern = Pattern.compile(initial);
		
		JFD jfd = getJFD();
		JFDModel model = jfd.getModel();
		int selectedIndex = model.getSelectedIndex();
		
		for(int i=selectedIndex + (includeCurrent ? 0 : 1); i<model.getFilesCount(); i++) {
			if(pattern.matcher(model.getFileAt(i).getName().toLowerCase()).matches()) {
				model.setSelectedIndex(i);
				return;
			}
		}
		
		return;
	}
	
	private class SearchDialog extends JDialog {
		private JTextField text = new JTextField();
		private OneKeyButton nextButton = new OneKeyButton(JFDResource.LABELS.getString("search_next"), KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.ALT_MASK));
		private OneKeyButton prevButton = new OneKeyButton(JFDResource.LABELS.getString("search_prev"), KeyStroke.getKeyStroke(KeyEvent.VK_B, KeyEvent.ALT_MASK));
		private OneKeyButton cancelButton = new OneKeyButton(JFDResource.LABELS.getString("cancel"), KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.ALT_MASK));
		
		SearchDialog() {
			super((Frame)getJFD().getJFDOwner(), true);
			initGui();
		}

		private void initGui() {
			setTitle(JFDResource.LABELS.getString("title_incremental_search"));
			setLayout(new GridBagLayout());
			add(text, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
					GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
					new Insets(0, 0, 0, 0), 0, 0));

			JPanel buttonsPanel = new JPanel(new FlowLayout());
			add(buttonsPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0,
					GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
					new Insets(0, 0, 0, 0), 0, 0));
			buttonsPanel.add(nextButton);
			buttonsPanel.add(prevButton);
			buttonsPanel.add(cancelButton);

			text.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					selectNextFile(text.getText(), false);
				}
			});
			
			nextButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					selectNextFile(text.getText(), false);
				}
			});
			
			prevButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					selectPrevFile(text.getText(), false);
				}
			});
			
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					setVisible(false);
					dispose();
				}
			});
			
			text.getActionMap().put("close", new AbstractAction() {
				public void actionPerformed(ActionEvent arg0) {
					setVisible(false);
					dispose();
				}
			});
			
			text.getDocument().addDocumentListener(new DocumentListener() {
				public void insertUpdate(DocumentEvent arg0) {
					selectNextFile(text.getText(), true);
				}

				public void removeUpdate(DocumentEvent arg0) {
				}

				public void changedUpdate(DocumentEvent arg0) {
					selectNextFile(text.getText(), true);
				}
			});
			
			text.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "close");
			text.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "close");
		}
	}
}

