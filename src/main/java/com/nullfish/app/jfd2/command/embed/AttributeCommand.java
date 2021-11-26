/*
 * Created on 2004/05/31
 *
 */
package com.nullfish.app.jfd2.command.embed;

import java.util.Date;
import java.util.List;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.command.Command;
import com.nullfish.app.jfd2.command.embed.attribute.AttributeDialog;
import com.nullfish.app.jfd2.dialog.DialogUtilities;
import com.nullfish.app.jfd2.resource.JFDResource;
import com.nullfish.lib.ui.tristate_checkbox.TristateState;
import com.nullfish.lib.vfs.Permission;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.ManipulationNotAvailableException;
import com.nullfish.lib.vfs.exception.VFSException;
import com.nullfish.lib.vfs.permission.FileAccess;
import com.nullfish.lib.vfs.permission.PermissionType;

/**
 * 属性設定コマンド
 * 
 * @author shunji
 */
public class AttributeCommand extends Command {
	public static final String PERMISISON = "permission";
	
	/* (non-Javadoc)
	 * @see com.nullfish.lib.vfs.manipulation.abst.AbstractManipulation#doExecute()
	 */
	public void doExecute() throws VFSException {
		doExecuteNew();
		/*
		JFD jfd = getJFD();
		VFile currentDir = jfd.getModel().getCurrentDirectory();
		
		JFDDialog dialog = null;
		
		try {
			VFile[] files = jfd.getModel().getMarkedFiles();
			
			if(files == null || files.length == 0) {
				files = new VFile[1];
				files[0] = jfd.getModel().getSelectedFile();
				if(files[0].equals(currentDir) || files[0].equals(currentDir.getParent())) {
					return;
				}
			}
			
			dialog = DialogUtilities.createOkCancelDialog(jfd, "jFD2");
			dialog.addTextField(PERMISISON, files[0].getPermission(this).getPermissionString(), true);
			dialog.pack();
			dialog.setVisible(true);
			
			String buttonAnswer = dialog.getButtonAnswer();
			if(buttonAnswer == null || buttonAnswer.equals("")) {
				return;
			}
			
			String permissionStr = dialog.getTextFieldAnswer(PERMISISON);
			for(int i=0; i<files.length; i++) {
				Permission perm = files[i].getPermission(this);
				perm.initFromString(permissionStr);
				files[i].setPermission(perm, this);
			}
		} catch (VFSException e) {
			e.printStackTrace();
			
			DialogUtilities.showMessageDialog(getJFD(), JFDResource.MESSAGES.getString("fail_set_permission"), "jFD2");
		}
		*/
	}
	
	private void doExecuteNew() throws VFSException {
		JFD jfd = getJFD();
		
		AttributeDialog attrDialog = null;
		try {
			VFile[] files = jfd.getModel().getMarkedOrSelectedFiles();

			attrDialog = new AttributeDialog(jfd);
			attrDialog.init(files);
			attrDialog.pack();
			attrDialog.setLocationRelativeTo(jfd.getComponent());
			attrDialog.setVisible(true);
			
			if(!attrDialog.isOkPressed()) {
				return;
			}
			
			if(!attrDialog.isMultiFileMode() || attrDialog.isEditingPermission()) {
				for(int i=0; i<files.length; i++) {
					boolean changed = false;
					Permission permission = files[i].getPermission();
					PermissionType[] types = permission.getTypes();
					FileAccess[] accesses = permission.getAccess();
					for(int j=0; j<types.length; j++) {
						for(int k=0; k<accesses.length; k++) {
							TristateState state = attrDialog.getState(types[j], accesses[k]);
							TristateState currentState = permission.hasPermission(types[j], accesses[k]) ? TristateState.SELECTED : TristateState.DESELECTED;
							if(state != TristateState.INDETERMINATE && state != currentState) {
								permission.setPermission(types[j], accesses[k], state == TristateState.SELECTED);
								changed = true;
							}
						}
	 				}
					
					if(changed) {
						files[i].setPermission(permission, this);
					}
				}
			}
			
			if(!attrDialog.isMultiFileMode() || !attrDialog.isEditingTimestamp()) {
				Date date = attrDialog.getTimestamp();
				for(int i=0; i<files.length; i++) {
					try {
						files[i].setTimestamp(date);
					} catch (ManipulationNotAvailableException e) {
						// タイムスタンプ設定操作を持たないファイルシステムがある。
					}
				}
			}
			
			if(!attrDialog.isMultiFileMode() || attrDialog.isEditingTag()) {
				List tags = attrDialog.getTags();
				for(int i=0; i<files.length; i++) {
					List currentTag = files[i].getTag();
					for(int j=0; j<tags.size(); j++) {
						String tag = (String) tags.get(j);
						TristateState state = attrDialog.getTagState(tag);
						if(state == TristateState.SELECTED && !currentTag.contains(tag)) {
							files[i].addTag(tag);
						} else if(state == TristateState.DESELECTED && currentTag.contains(tag)) {
							files[i].removeTag(tag);
						}
					}
				}
			}
		} catch (VFSException e) {
			e.printStackTrace();
			
			DialogUtilities.showMessageDialog(getJFD(), JFDResource.MESSAGES.getString("fail_set_permission"), "jFD2");
		} finally {
			attrDialog.dispose();
		}
	}

	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.command.Command#closesUnusingFileSystem()
	 */
	public boolean closesUnusingFileSystem() {
		return false;
	}
}
