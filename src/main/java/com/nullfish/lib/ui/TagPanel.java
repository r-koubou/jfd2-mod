package com.nullfish.lib.ui;

import java.awt.Container;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.dialog.JFDDialog;
import com.nullfish.app.jfd2.resource.JFDResource;
import com.nullfish.lib.ui.tristate_checkbox.TristateCheckBox;
import com.nullfish.lib.ui.tristate_checkbox.TristateState;
import com.nullfish.lib.vfs.VFS;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;

public class TagPanel extends JPanel {
	private JFD jfd;
	
	private List tags = new ArrayList();
	
	private Map tagCheckBoxMap = new HashMap();
	
	private JButton addButton = new OneKeyButton(JFDResource.LABELS.getString("add_tag"), null, KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, true));
	
	private JPanel checkBoxsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	
	public TagPanel(JFD jfd) {
		super(new GridBagLayout());
		this.jfd = jfd;
		add(addButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		add(checkBoxsPanel, new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		
		addButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				showNewTagInputDialog();
//				String tag = JOptionPane.showInputDialog(TagPanel.this, JFDResource.MESSAGES.getString("input_new_tag"));
//				if(tag == null || tag.length() == 0 || tags.contains(tag)) {
//					return;
//				}
//				
//				addCheckBox(tag, TristateState.SELECTED, true);
//				tags.add(tag);
			}
			
		});
	}
	
	private void showNewTagInputDialog() {
		JFDDialog dialog = null;
		try {
			dialog = createDialog();
			dialog.setTitle("jFD2 - Tag");
			
			dialog.addButton(JFDDialog.OK, JFDResource.LABELS.getString("ok"), 'o', true);
			dialog.addButton(JFDDialog.CANCEL, JFDResource.LABELS.getString("cancel"), 'c', false);
			
			List allTags = VFS.getInstance(jfd).getTagDataBase().findAllTags();
			String lastSelection = (String) jfd.getLocalConfiguration().getParam("new_tag_last", "");
			allTags.add(0, lastSelection);
			dialog.addComboBox("tag_name", allTags, null, true, true);

			dialog.pack();
			dialog.setVisible(true);

			String answer = dialog.getButtonAnswer();
			if (answer == null || JFDDialog.CANCEL.equals(answer)) {
				return;
			}

			dialog.applyConfig();
			String tag = dialog.getTextFieldAnswer("tag_name");
			
			if(tag == null || tag.length() == 0 || tags.contains(tag)) {
				return;
			}
			
			addCheckBox(tag, TristateState.SELECTED, true);
			tags.add(tag);
		} catch (VFSException e) {
			e.printStackTrace();
		} finally {
			try {dialog.dispose();} catch (Exception e) {}
		}

	}
	
	private JFDDialog createDialog() {
		Container owner = UIUtilities.getTopLevelOwner(this);
		JFDDialog rtn;
		if (owner instanceof Frame) {
			rtn = new JFDDialog((Frame) owner, true, jfd);
		} else if (owner instanceof Dialog) {
			rtn = new JFDDialog((Dialog) owner, true, jfd);
		} else {
			rtn = new JFDDialog((Frame) null, true, jfd);
		}

		return rtn;
	}
	
	public void init(List files) throws VFSException {
		Map defaulValueMap = new HashMap();
		
		for(int i=0; i<files.size(); i++) {
			VFile file = (VFile) files.get(i);
			List fileTags = file.getTag();
			
			for(int j=0; j<fileTags.size(); j++) {
				if(!tags.contains(fileTags.get(j))) {
					tags.add(fileTags.get(j));
				}
			}
		}
		
		Collections.sort(tags);
		
		for(int i=0; i<files.size(); i++) {
			VFile file = (VFile) files.get(i);
			List fileTag = file.getTag();
			
			for(int j=0; j<tags.size(); j++) {
				String tag = (String) tags.get(j);
				Object value = defaulValueMap.get(tag);
				TristateState state = fileTag.contains(tags.get(j)) ? TristateState.SELECTED : TristateState.DESELECTED;
				
				if(value == null) {
					defaulValueMap.put(tag, state);
				} else if(state != value){
					defaulValueMap.put(tag, TristateState.INDETERMINATE);
				}
			}
		}
		
		for(int i=0; i<tags.size(); i++) {
			String tag = (String) tags.get(i);
			TristateState initialState = (TristateState)defaulValueMap.get(tag);
			addCheckBox(tag, initialState, false);
		}
	}
	
	private void addCheckBox(String tag, TristateState initialState, boolean requestFocus) {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		
		TristateCheckBox checkBox = new TristateCheckBox("", null, initialState);
		checkBox.setAllowsIndetermined(initialState == TristateState.INDETERMINATE);
		panel.add(checkBox);
		panel.add(new JLabel(tag));
		checkBoxsPanel.add(panel);
		
		checkBox.setEnabled(isEnabled());
		tagCheckBoxMap.put(tag, checkBox);
		revalidate();
		if(requestFocus) {
			checkBox.requestFocusInWindow();
		}
	}
	
	public TristateState getState(String tag) {
		return ((TristateCheckBox) tagCheckBoxMap.get(tag)).getState();
	}
	
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		
		Iterator checks = tagCheckBoxMap.values().iterator();
		while(checks.hasNext()) {
			((TristateCheckBox) checks.next()).setEnabled(enabled);
		}
		addButton.setEnabled(enabled);
	}
	
	public List getTags() {
		return tags;
	}

}
