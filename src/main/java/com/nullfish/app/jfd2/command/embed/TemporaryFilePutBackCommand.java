/*
 * Created on 2004/05/31
 *
 */
package com.nullfish.app.jfd2.command.embed;

import java.io.File;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.command.Command;
import com.nullfish.app.jfd2.command.JFDException;
import com.nullfish.app.jfd2.dialog.DialogUtilities;
import com.nullfish.app.jfd2.dialog.JFDDialog;
import com.nullfish.app.jfd2.resource.JFDResource;
import com.nullfish.lib.vfs.FileUtil;
import com.nullfish.lib.vfs.VFS;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.ManipulationNotAvailableException;
import com.nullfish.lib.vfs.exception.VFSException;
import com.nullfish.lib.vfs.impl.DefaultOverwritePolicy;
import com.nullfish.lib.vfs.manipulation.CopyFileManipulation;
import com.nullfish.lib.vfs.manipulation.MoveManipulation;

/**
 * リモートファイルのテンポラリコピーを作成し、それを使用するプロセスの
 * 終了を待ってから、もしも更新されているなら元のファイルに変更を反映するコマンド
 * 
 * getTemporaryFileはprepare後使用可能になり、
 * executeはsetProcess後呼び出されなくてはならない。
 * 
 * @author shunji
 */
public class TemporaryFilePutBackCommand extends Command {
	private Process process;

	public static final String DEFAULT_TEMP_DIR = new File(new File(new File(System.getProperty("user.home")), ".jfd2"), "temp").getAbsolutePath();
	
	/**
	 * ローカルのテンポラリファイル
	 */
	private VFile localFile;

	/**
	 * 元のファイル
	 */
	private VFile originalFile;

	/**
	 * 元のファイルのMD5ハッシュ
	 */
	private String md5;
	
	public static final String PARAM_TEMP_DIR = "temp_dir";

	public TemporaryFilePutBackCommand(VFile originalFile) {
		this.originalFile = originalFile;
	}

	public void doPrepare() throws VFSException {
		try {
			localFile = createTemporaryFile(originalFile);
	
			originalFile.getFileSystem().registerUser(this);
			localFile.getFileSystem().registerUser(this);
			
			CopyFileManipulation copyManipulation = FileUtil.prepareCopyTo(
					originalFile, localFile,
					DefaultOverwritePolicy.OVERWRITE, this);
			copyManipulation.start();
			md5 = localFile.getContentHashStr(this);
		} finally {
			if(isStopped()) {
				originalFile.getFileSystem().removeUser(this);
				if(localFile != null) {
					localFile.getFileSystem().removeUser(this);
				}
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nullfish.lib.vfs.manipulation.abst.AbstractManipulation#doExecute()
	 */
	public void doExecute() throws VFSException {
		try {
			try {
				process.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	
			String newMd5 = localFile.getContentHashStr(this);
	
			if (md5.equals(newMd5)) {
				localFile.delete(this);
				return;
			}
	
			getJFD().getJFDOwner().toFront();
			
			
			//	アップロード確認ダイアログの表示
			JFDDialog dialog = null;
			try {
				dialog = DialogUtilities.createYesNoDialog(getJFD(), JFDDialog.YES);
				dialog.addMessage(JFDResource.MESSAGES.getString("message_putback"));
				dialog.pack();
				dialog.setVisible(true);
				
				String answer = dialog.getButtonAnswer();
				if(answer == null || answer.equals(JFDDialog.NO)) {
					return;
				}
			} finally {
				dialog.dispose();
			}
			
			try {
				MoveManipulation moveManipulation = FileUtil.prepareMoveTo(localFile, originalFile,
						DefaultOverwritePolicy.OVERWRITE, this);
				moveManipulation.setCopiesPermission(false);
				moveManipulation.setParentManipulation(this);
				moveManipulation.start();
			} catch (ManipulationNotAvailableException e) {
				if (showNoPutBackDeleteDiaog()) {
					localFile.delete(this);
				}
			}
		} finally {
			originalFile.getFileSystem().removeUser(this);
			if(localFile != null) {
				localFile.getFileSystem().removeUser(this);
			}
		}
	}

	public VFile getTemporaryFile() {
		return localFile;
	}
	
	/**
	 * 更新不可能ダイアログを表示する
	 * @return
	 */
	private boolean showNoPutBackDeleteDiaog() {
		JFDDialog dialog = null;
		try {
			dialog = DialogUtilities.createOkCancelDialog(getJFD());
			dialog.setMessage(JFDResource.MESSAGES.getString(
					"no_temp_file_put_back").split("\n"));
			dialog.pack();
			dialog.setVisible(true);

			return JFDDialog.OK.equals(dialog.getButtonAnswer());
		} finally {
			if (dialog != null) {
				dialog.dispose();
			}
		}
	}
	
	/**
	 * テンポラリファイルを決定する。
	 * @param original
	 * @return
	 * @throws VFSException
	 */
	private VFile createTemporaryFile(VFile original) throws VFSException {
		VFile tempDir = getTempDir();
		if(!tempDir.exists(this)) {
			tempDir.createDirectory(this);
		}
		
		String fileName = original.getName();
		int cummaIndex = fileName.lastIndexOf('.');
		
		String firstPart;
		String lastPart;
		if(cummaIndex >= 0) {
			firstPart = fileName.substring(0, cummaIndex);
			lastPart = fileName.substring(cummaIndex);
		} else {
			firstPart = fileName;
			lastPart = "";
		}
		
		for(int i=0; true; i++) {
			VFile tempFile = tempDir.getChild(firstPart + "[" + i + "]" + lastPart);
			if(!tempFile.exists(this)) {
				return tempFile;
			}
		}
	}

	private VFile getTempDir() throws VFSException {
		JFD jfd = getJFD();
		String tempDirStr = (String) jfd.getCommonConfiguration().getParam(
				PARAM_TEMP_DIR, DEFAULT_TEMP_DIR);
		if (tempDirStr == null || tempDirStr.length() == 0) {
			throw new JFDException(JFDResource.MESSAGES.getString("temp_dir_not_specified"));
		}

		return VFS.getInstance(jfd).getFile(tempDirStr);
	}
	
	public void setProcess(Process process) {
		this.process = process;
	}

	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.command.Command#closesUnusingFileSystem()
	 */
	public boolean closesUnusingFileSystem() {
		return true;
	}
}
