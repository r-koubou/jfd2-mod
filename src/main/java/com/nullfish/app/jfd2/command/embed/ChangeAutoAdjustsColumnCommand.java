/*
 * Created on 2004/05/31
 *
 */
package com.nullfish.app.jfd2.command.embed;

import com.nullfish.app.jfd2.JFD2;
import com.nullfish.app.jfd2.command.Command;
import com.nullfish.lib.vfs.exception.VFSException;

/**
 * カラム数の自動調整切り替えコマンド。
 * JFD2クラス専用
 * 
 * @author shunji
 */
public class ChangeAutoAdjustsColumnCommand extends Command {
	/* (non-Javadoc)
	 * @see com.nullfish.lib.vfs.manipulation.abst.AbstractManipulation#doExecute()
	 */
	public void doExecute() throws VFSException {
		JFD2 jfd = (JFD2)getJFD();

		int columnWidth = ((Integer)jfd.getCommonConfiguration().getParam("min_column_width", Integer.valueOf(300))).intValue();
		jfd.setAdjustsColumnCountAuto(!jfd.isAdjustsColumnCountAuto(), columnWidth);
	}

	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.command.Command#closesUnusingFileSystem()
	 */
	public boolean closesUnusingFileSystem() {
		return false;
	}
}
