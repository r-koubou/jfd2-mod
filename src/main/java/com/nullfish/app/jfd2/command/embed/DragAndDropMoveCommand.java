/*
 * Created on 2004/05/31
 *
 */
package com.nullfish.app.jfd2.command.embed;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.JFDModel;
import com.nullfish.app.jfd2.command.Command;
import com.nullfish.lib.vfs.FileSystem;
import com.nullfish.lib.vfs.FileUtil;
import com.nullfish.lib.vfs.Manipulation;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.ManipulationNotAvailableException;
import com.nullfish.lib.vfs.exception.VFSException;

/**
 * ドラッグアンドドロップで使用される移動コマンド
 * 
 * @author shunji
 */
public class DragAndDropMoveCommand extends Command {
	private VFile[] from;

	private VFile to;

	/**
	 * 使用ファイルシステム
	 */
	private FileSystem[] usingFileSystem;
	
	public DragAndDropMoveCommand(VFile[] from, VFile to) {
		this.from = from;
		this.to = to;
		
		Set fileSystemsSet = new HashSet();
		fileSystemsSet.add(to.getFileSystem());
		for(int i=0; i<from.length; i++) {
			fileSystemsSet.add(from[i].getFileSystem());
		}
		
		List list = new ArrayList(fileSystemsSet);
		usingFileSystem = new FileSystem[list.size()]; 
		usingFileSystem = (FileSystem[])list.toArray(usingFileSystem);
	}

	public DragAndDropMoveCommand(VFile from, VFile to) {
		this.from = new VFile[1];
		this.from[0] = from;
		this.to = to;
	}

	/**
	 * doExecuteのオーバーライド。
	 */
	public void doExecute() throws VFSException {
		showProgress(1000);

		JFD jfd = getJFD();
		JFDModel model = jfd.getModel();
		model.lockAutoUpdate(this);

		try {
			Manipulation[] moveManipulations = initMoveManipulations(from, to);
			setChildManipulations(moveManipulations);

			for (int i = 0; i < moveManipulations.length; i++) {
				moveManipulations[i].start();
			}
			model.refresh();
		} finally {
			model.unlockAutoUpdate(this);
		}
	}

	/**
	 * 子移動操作を初期化する。
	 */
	private Manipulation[] initMoveManipulations(VFile[] from, VFile dest)
			throws VFSException {
		CopyOverwritePolicy policy = new CopyOverwritePolicy(this);
		Manipulation[] rtn = new Manipulation[from.length];
		for (int i = 0; i < from.length; i++) {
			try {
				rtn[i] = FileUtil.prepareMoveTo(from[i], dest.getChild(from[i].getName()), policy, this);
			} catch (ManipulationNotAvailableException e) {
				rtn[i] = FileUtil.prepareCopyTo(from[i], dest.getChild(from[i].getName()), policy, this);
			}
		}

		for (int i = 0; i < rtn.length; i++) {
			rtn[i].prepare();
		}

		return rtn;
	}

	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.command.Command#closesUnusingFileSystem()
	 */
	public boolean closesUnusingFileSystem() {
		return true;
	}
	
	public FileSystem[] getUsingFileSystem() {
		return usingFileSystem;
	}
}
