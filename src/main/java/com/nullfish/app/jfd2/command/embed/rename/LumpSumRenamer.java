package com.nullfish.app.jfd2.command.embed.rename;

import com.nullfish.lib.vfs.Manipulation;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;
import com.nullfish.lib.vfs.impl.DefaultOverwritePolicy;

public class LumpSumRenamer {
	private VFile[] froms;
	
	private VFile[] dests;
	
	private int[] status;
	
	public static final int STATUS_OK = 0;
	public static final int STATUS_DEST_FILE_EXISTS = 1;
	public static final int STATUS_SAME_DEST_EXISTS = 2;
	public static final int STATUS_UNKNOWN = 3;
	
	public LumpSumRenamer(VFile[] froms, VFile[] dests) {
		this.froms = froms;
		this.dests = dests;
		
		updateStatus();
	}
	
	public int getCounts() {
		return froms.length;
	}
	
	public void updateStatus() {
		status = new int[froms.length];
		
		for(int i=0; i<froms.length; i++) {
			updateStatus(i);
		}
	}
	
	public void updateStatus(int index) {
		try {
			if(dests[index].exists()) {
				status[index] = STATUS_DEST_FILE_EXISTS;
				return;
			}
		} catch (VFSException e) {
			status[index] = STATUS_UNKNOWN;
			return;
		}
		
		for(int i=0; i<dests.length; i++) {
			if(i != index && dests[i].equals(dests[index])) {
				status[index] = STATUS_SAME_DEST_EXISTS;
				return;
			}
		}
		
		status[index] = STATUS_OK;
	}
	
	public VFile getFrom(int index) {
		return froms[index];
	}
	
	public VFile getDest(int index) {
		return dests[index];
	}
	
	public void setDest(VFile dest, int index) {
		dests[index] = dest;
		updateStatus();
	}

	public int getStatus(int index) {
		return status[index];
	}
	
	public boolean isAllOK() {
		for(int i=0; i<status.length; i++) {
			if(status[i] != STATUS_OK) {
				return false;
			}
		}
		
		return true;
	}
	
	public void renameAll(Manipulation parent) throws VFSException {
		for(int i=0; i<froms.length; i++) {
			froms[i].moveTo(dests[i], DefaultOverwritePolicy.NO_OVERWRITE, parent);
		}
	}
}
