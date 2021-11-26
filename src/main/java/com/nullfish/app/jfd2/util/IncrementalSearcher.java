package com.nullfish.app.jfd2.util;

import java.awt.event.KeyEvent;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.KeyStroke;

import org.monazilla.migemo.Migemo;

import com.nullfish.app.jfd2.Initable;
import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.JFDModel;
import com.nullfish.lib.keymap.KeyStrokeMap;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;

public class IncrementalSearcher implements Initable {
	public static final String ACCEPTABLE_CHARS = 
		"ABCDEFGHIJKLMNOPQRSTUVWXYZ01234567890-^!#$%&'()=~[`{:]+*},.?_ ";
		
	public static final String INCREMENTAL_SEARCH_COMMAND = "incremental_search";
	
	public static final String ESCAPE_COMMAND = "escape";
	
	private StringBuffer buffer = new StringBuffer();
	
	private String lastSearch;
	
	private Thread bufferClearThread;

	private JFD jfd;
	
	private List commandKeys;
	private List escapeCommandKeys;

	private KeyStroke backSpace = KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0);
	
	public IncrementalSearcher(JFD jfd) {
		this.jfd = jfd;
	}
	
	public void processKeyEvent(KeyEvent e) {
		KeyStroke keyStroke = KeyStrokeMap.getKeyStrokeForEvent(e);
		if(commandKeys.contains(keyStroke) || escapeCommandKeys.contains(keyStroke)) {
			jfd.setIncrementalSearchMode(false);
			jfd.setMessage("");
			e.consume();
			return;
		}
		
		if(keyStroke.getKeyCode() == KeyEvent.VK_SHIFT) {
			return;
		}
		
		if(keyStroke.equals(backSpace) && e.getID() == KeyEvent.KEY_PRESSED) {
			if(buffer.length() == 0) {
				return;
			}
			buffer.deleteCharAt(buffer.length() - 1);
/*
			Thread t = new Thread() {
				public void run() {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
					if(getBufferClearThread() == this) {
						buffer.setLength(0);
					}
				}
			};
			
			setBufferClearThread(t);
			t.start();
*/			
			jfd.setMessage("@" + buffer.toString());
			e.consume();
			return;
		}
		
		if(e.getID() != KeyEvent.KEY_PRESSED) {
			e.consume();
			return;
		}
		
		char c = Character.toUpperCase(e.getKeyChar());
		if(ACCEPTABLE_CHARS.indexOf(c) == -1) {
			jfd.setIncrementalSearchMode(false);
			jfd.setMessage("");
			return;
		}
		
		if((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0
				|| (e.getModifiersEx() & KeyEvent.ALT_DOWN_MASK) != 0 
				|| (e.getModifiersEx() & KeyEvent.META_DOWN_MASK) != 0 ) {
			return;
		}
		
		if((e.getModifiersEx() & KeyEvent.SHIFT_DOWN_MASK) != 0) {
			buffer.append(Character.toUpperCase(e.getKeyChar()));
		} else {
			buffer.append(e.getKeyChar());
		}
/*		
		Thread t = new Thread() {
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
				if(getBufferClearThread() == this) {
					buffer.setLength(0);
				}
			}
		};
		
		setBufferClearThread(t);
		t.start();
		*/
		selectNextFile(buffer.toString(), buffer.length() != 1);
		e.consume();
	}
	
	
	synchronized Thread getBufferClearThread() {
		return bufferClearThread;
	}
	
	synchronized void setBufferClearThread(Thread bufferClearThread) {
		this.bufferClearThread = bufferClearThread;
	}

	public void init(VFile baseDir) throws VFSException {
		commandKeys = jfd.getCommandManager().getKeys(INCREMENTAL_SEARCH_COMMAND);
		escapeCommandKeys = jfd.getCommandManager().getKeys(ESCAPE_COMMAND);
	}
	
	
	public void selectNextFile(String initial, boolean includeCurrent) {
		if(initial == null || initial.length() == 0) {
			return;
		}
		jfd.setMessage("@" + initial);
		
		lastSearch = initial;
		//initial = initial.toLowerCase();
		if(MigemoInfo.usesMigemo()) {
			initial = Migemo.lookup(initial);
		} else {
			initial = "^" + WildCardUtil.wildCard2Regex(initial).toLowerCase();
		}
		
		Pattern pattern = Pattern.compile(initial);
		
		JFDModel model = jfd.getModel();
		int selectedIndex = model.getSelectedIndex();
		VFile current = model.getCurrentDirectory();
		VFile parent = current.getParent();
		
		for(int i=selectedIndex + (includeCurrent ? 0 : 1); i<model.getFilesCount(); i++) {
			if(pattern.matcher(model.getFileAt(i).getName().toLowerCase()).find()
					&& !model.getFileAt(i).equals(current)
					&& !model.getFileAt(i).equals(parent)) {
				model.setSelectedIndex(i);
				return;
			}
		}
		
		for(int i=0; i < selectedIndex; i++) {
			if(pattern.matcher(model.getFileAt(i).getName().toLowerCase()).find()
					&& !model.getFileAt(i).equals(current)
					&& !model.getFileAt(i).equals(parent)) {
				model.setSelectedIndex(i);
				return;
			}
		}
		
		return;
	}
	
	public void selectPrevFile(String initial, boolean includeCurrent) {
		if(initial == null || initial.length() == 0) {
			return;
		}
		jfd.setMessage("@" + initial, 1000);
		
		lastSearch = initial;
		initial = initial.toLowerCase();
		if(MigemoInfo.usesMigemo()) {
			initial = ".*" + Migemo.lookup(initial) + ".*";
		} else {
			initial = "^" + WildCardUtil.wildCard2Regex(initial);
		}
		
		Pattern pattern = Pattern.compile(initial);
		
		JFDModel model = jfd.getModel();
		int selectedIndex = model.getSelectedIndex();
		VFile current = model.getCurrentDirectory();
		VFile parent = current.getParent();
		
		for(int i=selectedIndex - (includeCurrent ? 0 : 1); i >= 0; i--) {
			if(pattern.matcher(model.getFileAt(i).getName().toLowerCase()).find()
					&& !model.getFileAt(i).equals(current)
					&& !model.getFileAt(i).equals(parent)) {
				model.setSelectedIndex(i);
				return;
			}
		}
		
		for(int i=model.getFilesCount() - 1; i > selectedIndex; i--) {
			if(pattern.matcher(model.getFileAt(i).getName().toLowerCase()).find()
					&& !model.getFileAt(i).equals(current)
					&& !model.getFileAt(i).equals(parent)) {
				model.setSelectedIndex(i);
				return;
			}
		}
		
		return;
	}
	
	public void clearBuffer() {
		buffer.setLength(0);
	}

	public String getLastSearch() {
		return lastSearch;
	}
	
	public void setBuffer(String str) {
		buffer.setLength(0);
		buffer.append(str);
	}
}
