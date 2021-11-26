/*
 * Created on 2004/05/31
 *
 */
package com.nullfish.app.jfd2.command.embed;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.JFD2;
import com.nullfish.app.jfd2.JFDModel;
import com.nullfish.app.jfd2.command.Command;
import com.nullfish.app.jfd2.ui.container2.ContainerPosition;
import com.nullfish.lib.vfs.exception.VFSException;

/**
 * カーソル移動コマンド
 * 
 * @author shunji
 */
public class CursorMoveCommand extends Command {
	public static final int UP = 0;
	public static final int DOWN = 1;
	public static final int RIGHT = 2;
	public static final int LEFT = 3;
	public static final int TOP = 4;
	public static final int END = 5;
	public static final int NEXT_PAGE = 6;
	public static final int PREV_PAGE = 7;
	
	public static final String DIRECTION = "direction";
	
	public static final String CURSOR_LOOPS = "cursor_loops";
	
//	public static final String MULTI_WINDOW_CURSOR = "multi_window_cursor";
	
	public static final String GO_PARENT_CURSOR = "go_parent_cursor";
	public static final String PANE_CHANGE_CURSOR = "pane_change_cursor";
	
	/* (non-Javadoc)
	 * @see com.nullfish.lib.vfs.manipulation.abst.AbstractManipulation#doExecute()
	 */
	public void doExecute() throws VFSException {
		JFD jfd = getJFD();
		JFDModel model = jfd.getModel();
		
		Integer direction = (Integer)getParameter(DIRECTION);
		if(direction == null) {
			return;
		}
		
		boolean cursorLoops = ((Boolean)jfd.getCommonConfiguration().getParam(CURSOR_LOOPS, Boolean.FALSE)).booleanValue();
		ContainerPosition position = jfd.getJFDOwner().getComponentPosition(jfd);
		
		boolean goParent = ((Boolean)jfd.getCommonConfiguration().getParam(GO_PARENT_CURSOR, Boolean.FALSE)).booleanValue();
		boolean paneChange = ((Boolean)jfd.getCommonConfiguration().getParam(PANE_CHANGE_CURSOR, Boolean.FALSE)).booleanValue();
		boolean multiWindowCursor = 
			!(jfd instanceof JFD2 && ((JFD2)jfd).isThumbnailVisible()) && 
			(goParent || paneChange)
			&& jfd.getJFDOwner().getComponent( position.getOpponent() )  != null;
		
		switch(direction.intValue()) {
			case UP : 
				if(model.getSelectedIndex() - 1 >= 0) {
					model.setSelectedIndex(model.getSelectedIndex() - 1);
				} else if(cursorLoops) {
					model.setSelectedIndex(model.getFilesCount() - 1);
				}
				return;
			case DOWN : 
				if(model.getSelectedIndex() + 1 < model.getFilesCount()) {
					model.setSelectedIndex(model.getSelectedIndex() + 1);
				} else if(cursorLoops) {
					model.setSelectedIndex(0);
				}
				return;
			case RIGHT : 
				if(multiWindowCursor) {
					if(position == ContainerPosition.MAIN_PANEL) {
						if(paneChange) {
							jfd.getCommandManager().execute("focus_opponent");
						}
					} else {
						if(goParent) {
							jfd.getCommandManager().execute("go-parent");
						}
					}
				} else {
					model.setSelectedIndex(model.getSelectedIndex() + jfd.getRowCount());
				}
				return;
			case LEFT : 
				if(multiWindowCursor) {
					if(position == ContainerPosition.SUB_PANEL) {
						if(paneChange) {
							jfd.getCommandManager().execute("focus_opponent");
						}
					} else {
						if(goParent) {
							jfd.getCommandManager().execute("go-parent");
						}
					}
				} else {
					model.setSelectedIndex(model.getSelectedIndex() - jfd.getRowCount());
				}
				return;
			case TOP : 
				model.setSelectedIndex(0);
				return;
			case END : 
				model.setSelectedIndex(model.getFilesCount() - 1);
				return;
			case NEXT_PAGE : 
				int nextIndex = model.getSelectedIndex() + (jfd.getColumnCount() * jfd.getRowCount());
				nextIndex = nextIndex - (nextIndex % (jfd.getColumnCount() * jfd.getRowCount()));
				model.setSelectedIndex(nextIndex);
				return;
			case PREV_PAGE : 
				int prevIndex = model.getSelectedIndex() - (jfd.getColumnCount() * jfd.getRowCount());
				prevIndex = prevIndex - (prevIndex % (jfd.getColumnCount() * jfd.getRowCount()));
				model.setSelectedIndex(prevIndex);
				return;
		}
	}

	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.command.Command#closesUnusingFileSystem()
	 */
	public boolean closesUnusingFileSystem() {
		return false;
	}
}
