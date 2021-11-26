/*
 * Created on 2004/05/31
 *
 */
package com.nullfish.app.jfd2.command.embed;

import java.util.ArrayList;
import java.util.List;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.JFDModel;
import com.nullfish.app.jfd2.command.Command;
import com.nullfish.app.jfd2.dialog.ConfigurationInfo;
import com.nullfish.app.jfd2.dialog.DialogUtilities;
import com.nullfish.app.jfd2.dialog.JFDDialog;
import com.nullfish.app.jfd2.resource.JFDResource;
import com.nullfish.lib.vfs.FileSystem;
import com.nullfish.lib.vfs.FileUtil;
import com.nullfish.lib.vfs.Manipulation;
import com.nullfish.lib.vfs.VFS;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;
import com.nullfish.lib.vfs.impl.DefaultOverwritePolicy;
import com.nullfish.lib.vfs.manipulation.OverwritePolicy;

/**
 * 解凍コマンド
 * 
 * @author shunji
 */
public class UnpackCommand extends Command {
	public static final String DIR_NAME = "archive";

	public static final String FORMAT = "format";

	public static final String CREATE_DIR = "unpack_create_dir";

	public static final String SHOW_OVERWRITE_DIALOG = "unpack_overwrite_dialog";

	private Manipulation[] children;
	
	private List fileSystems = new ArrayList();
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nullfish.lib.vfs.manipulation.abst.AbstractManipulation#doExecute()
	 */
	public void doExecute() throws VFSException {
		JFD jfd = getJFD();
		JFDModel model = jfd.getModel();

		VFile selectedFile = model.getSelectedFile();
		VFile[] markedFiles = model.getMarkedFiles();

		if (markedFiles == null || markedFiles.length == 0) {
			markedFiles = new VFile[1];
			markedFiles[0] = selectedFile;
		}

		JFDDialog dialog = null;
		try {
			dialog = jfd.createDialog();
			dialog.setTitle(JFDResource.LABELS.getString("title_unpack"));

			// メッセージ
			dialog.addMessage(JFDResource.MESSAGES.getString("message_unpack"));

			// ボタン
			dialog.addButton(JFDDialog.OK, JFDResource.LABELS.getString("ok"),
					'o', true);
			dialog.addButton(JFDDialog.CANCEL, JFDResource.LABELS
					.getString("cancel"), 'c', false);

			dialog.addFileComboBox(DIR_NAME, model.getNoOverwrapHistory()
					.toArray(), true, true, jfd);

			dialog.addCheckBox(CREATE_DIR, JFDResource.LABELS.getString("unpack_create_dir"), 'r', true, new ConfigurationInfo(
					jfd.getLocalConfiguration(), CREATE_DIR), false);
			
			dialog.addCheckBox(SHOW_OVERWRITE_DIALOG, JFDResource.LABELS.getString("unpack_overwrite_dialog"), 'd', false, new ConfigurationInfo(
					jfd.getLocalConfiguration(), SHOW_OVERWRITE_DIALOG), false);
			
			dialog.pack();
			dialog.setVisible(true);

			String answer = dialog.getButtonAnswer();
			if (answer == null || JFDDialog.CANCEL.equals(answer)) {
				return;
			}

			boolean createsDir = dialog.isChecked(CREATE_DIR);
			VFile target = VFS.getInstance(jfd).getFile(dialog.getTextFieldAnswer(DIR_NAME));

			if(target.exists(this) && !target.isDirectory(this)) {
				DialogUtilities.showMessageDialog(jfd, JFDResource.MESSAGES.getString("unpack_dest_is_file"), JFDResource.LABELS.getString("title_unpack"));
				return;
			}

			dialog.applyConfig();
			
			fileSystems.add(target.getFileSystem());
			
			model.getHistory().add(target);
			model.getNoOverwrapHistory().add(target);

			OverwritePolicy policy = dialog.isChecked(SHOW_OVERWRITE_DIALOG) ? new CopyOverwritePolicy(this) : DefaultOverwritePolicy.OVERWRITE;
			
			List childrenList = new ArrayList();
			
			for(int i=0; i<markedFiles.length; i++) {
				VFile innerRoot = markedFiles[i].getInnerRoot();
				if(innerRoot != null) {
					VFile dest = createsDir ? target.getChild(markedFiles[i].getFileName().getExceptExtension()) : target;
					childrenList.add(FileUtil.prepareCopyTo(innerRoot, dest, policy, this));
					fileSystems.add(innerRoot.getFileSystem());
				}
			}
			
			children = new Manipulation[childrenList.size()];
			children = (Manipulation[])childrenList.toArray(children);

			jfd.setPrimaryCommand(this);
			showProgress(1000);
			
			for(int i=0; i<children.length; i++) {
				children[i].prepare();
			}

			for(int i=0; i<children.length; i++) {
				children[i].execute();
			}
		} finally {
			if (dialog != null) {
				dialog.dispose();
			}
		}
	}

	public FileSystem[] getUsingFileSystems() {
		FileSystem[] rtn = new FileSystem[fileSystems.size()];
		rtn = (FileSystem[])fileSystems.toArray(rtn);
		return rtn;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nullfish.app.jfd2.command.Command#closesUnusingFileSystem()
	 */
	public boolean closesUnusingFileSystem() {
		return true;
	}
}
