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
import com.nullfish.lib.vfs.Manipulation;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;
import com.nullfish.lib.vfs.manipulation.CopyFileManipulation;

/**
 * ドラッグアンドドロップで使用されるコピーコマンド
 * 
 * @author shunji
 */
public class DragAndDropCopyCommand extends Command {
	private VFile[] from;

	private VFile to;

	/**
	 * 使用ファイルシステム
	 */
	private FileSystem[] usingFileSystem;
	
	public DragAndDropCopyCommand(VFile[] from, VFile to) {
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

	public DragAndDropCopyCommand(VFile from, VFile to) {
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
			Manipulation[] copyManipulations = initCopyManipulations(from, to);
			setChildManipulations(copyManipulations);

			for (int i = 0; i < copyManipulations.length; i++) {
				copyManipulations[i].execute();
			}
			model.refresh();
		} finally {
			model.unlockAutoUpdate(this);
		}
	}

	/**
	 * 子コピー操作を初期化する。
	 */
	private CopyFileManipulation[] initCopyManipulations(VFile[] from,
			VFile dest) throws VFSException {
		CopyOverwritePolicy policy = new CopyOverwritePolicy(this);
		CopyFileManipulation[] rtn = new CopyFileManipulation[from.length];
		for (int i = 0; i < from.length; i++) {
			rtn[i] = from[i].getManipulationFactory().getCopyFileManipulation(
					from[i]);
			rtn[i].setParentManipulation(this);
			rtn[i].setDest(dest.getChild(from[i].getName()));
			rtn[i].setOverwritePolicy(policy);
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
