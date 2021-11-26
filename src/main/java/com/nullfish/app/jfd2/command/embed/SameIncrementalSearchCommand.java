package com.nullfish.app.jfd2.command.embed;

import com.nullfish.app.jfd2.command.Command;
import com.nullfish.app.jfd2.util.IncrementalSearcher;
import com.nullfish.lib.vfs.exception.VFSException;

public class SameIncrementalSearchCommand extends Command {

	public boolean closesUnusingFileSystem() {
		return false;
	}

	public void doExecute() throws VFSException {
		IncrementalSearcher searcher = getJFD().getIncrementalSearcher();
		String direction = (String)getParameter("direction");
		String lastSearch = searcher.getLastSearch();
		if(lastSearch == null || lastSearch.length() == 0) {
			return;
		}
		getJFD().setIncrementalSearchMode(true);
		searcher.setBuffer(lastSearch);
		if("next".equals(direction)) {
			searcher.selectNextFile(lastSearch, false);
		} else if("prev".equals(direction)) {
			searcher.selectPrevFile(lastSearch, false);
		}
	}

}
