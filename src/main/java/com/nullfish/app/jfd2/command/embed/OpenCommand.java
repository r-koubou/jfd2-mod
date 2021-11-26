/*
 * Created on 2004/05/31
 *
 */
package com.nullfish.app.jfd2.command.embed;

import java.io.IOException;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.JFDModel;
import com.nullfish.app.jfd2.command.Command;
import com.nullfish.app.jfd2.ext_command.CommandExecuter;
import com.nullfish.app.jfd2.util.ShortCutFile;
import com.nullfish.app.jfd2.viewer.FileViewerManager;
import com.nullfish.lib.vfs.FileSystem;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;
import com.nullfish.lib.vfs.impl.local.LocalFile;

/**
 * ファイルオープンコマンド
 * 
 * @author shunji
 */
public class OpenCommand extends Command {
	private boolean toClose;
	
	/* (non-Javadoc)
	 * @see com.nullfish.lib.vfs.manipulation.abst.AbstractManipulation#doExecute()
	 */
	public void doExecute() throws VFSException {
		JFD jfd = getJFD();
		JFDModel model = jfd.getModel();
		
		VFile selectedFile = model.getSelectedFile();
		toClose = selectedFile.getFileSystem().isLocal();
		
		if(selectedFile.isFile(this)) {
			VFile current = model.getCurrentDirectory();
			
			if(selectedFile.equals(current.getFileSystem().getMountPoint())) {
				model.setDirectoryAsynchIfNecessary(selectedFile.getParent(), selectedFile, jfd);
				return;
			}
			
			VFile innerRoot = selectedFile.getInnerRoot();
			if(innerRoot != null) {
				model.setDirectoryAsynch(innerRoot, selectedFile, jfd);
				toClose = false;
				return;
			}
			
			//	ショートカット
			if(ShortCutFile.EXTENSION.equals(selectedFile.getFileName().getExtension())) {
				try {
					ShortCutFile shortCut = new ShortCutFile(selectedFile);
					shortCut.load(this);
					VFile target = shortCut.getTarget();
					if(target != null) {
						model.setDirectoryAsynchIfNecessary(target, target.getParent(), jfd);
						return;
					}
				} catch (Exception e) {e.printStackTrace();}
			}
				
			//	ファイルオープン処理
			if(FileViewerManager.getInstance().openFile(jfd, selectedFile)) {
				return;
			}
			
			try {
				VFile currentDir = model.getCurrentDirectory();
				if(currentDir instanceof LocalFile) {
					CommandExecuter.getInstance().exec(selectedFile.getAbsolutePath(), true, ((LocalFile)currentDir).getFile());
				} else {
					CommandExecuter.getInstance().exec(selectedFile.getAbsolutePath(), true);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return;
		} else {
			model.setDirectoryAsynchIfNecessary(selectedFile, model.getCurrentDirectory(), jfd);
		}
	}

	public FileSystem[] getUsingFileSystems() {
		if(currentDir.isRoot() && currentDir.getFileSystem().getMountPoint() != null) {
			FileSystem[] rtn = {
					currentDir.getFileSystem(),
					currentDir.getFileSystem().getMountPoint().getFileSystem()
			};
			
			return rtn;
		}
		
		return null;
	}
	
	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.command.Command#closesUnusingFileSystem()
	 */
	public boolean closesUnusingFileSystem() {
		return toClose;
	}
}
