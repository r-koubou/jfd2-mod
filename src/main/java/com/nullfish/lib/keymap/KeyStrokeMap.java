package com.nullfish.lib.keymap;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.KeyStroke;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import com.nullfish.app.jfd2.util.DomCache;
import com.nullfish.lib.ui.KeyStrokeUtility;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;

public class KeyStrokeMap {
	private static KeyStrokeMap instance = new KeyStrokeMap();

	private Map keyCodeMap = new HashMap();
	
	private Map reverseKeyCodeMap = new HashMap();
	
	private KeyStrokeMap() {
	}
	
	public static KeyStrokeMap getInstance() {
		return instance;
	}
	
	public KeyStroke convert(KeyStroke keyStroke) {
		Integer keyCode = (Integer)keyCodeMap.get(Integer.valueOf(keyStroke.getKeyCode()));
		if(keyCode == null) {
			return keyStroke;
		}
		return KeyStroke.getKeyStroke(keyCode.intValue(), keyStroke.getModifiers(), keyStroke.isOnKeyRelease());
	}
	
	public KeyStroke convertReverse(KeyStroke keyStroke) {
		Integer keyCode = (Integer)reverseKeyCodeMap.get(new Integer(keyStroke.getKeyCode()));
		if(keyCode == null) {
			return keyStroke;
		}
		return KeyStroke.getKeyStroke(keyCode.intValue(), keyStroke.getModifiers(), keyStroke.isOnKeyRelease());
	}
	
	public void clear() {
		keyCodeMap.clear();
		reverseKeyCodeMap.clear();
	}
	
	public void init(VFile file) throws JDOMException, IOException, VFSException {
		clear();

		Document doc = DomCache.getInstance().getDocument(file);
		List mappings = doc.getRootElement().getChildren("mapping");
		for (int i = 0; i < mappings.size(); i++) {
			Element mapping = (Element)mappings.get(i);
			List keys = mapping.getChildren(KeyStrokeUtility.KEY_NODE);

			keyCodeMap.put(
					new Integer(KeyStrokeUtility.node2KeyStroke((Element)keys.get(0)).getKeyCode()), 
					new Integer(KeyStrokeUtility.node2KeyStroke((Element)keys.get(1)).getKeyCode()));
			reverseKeyCodeMap.put(
					new Integer(KeyStrokeUtility.node2KeyStroke((Element)keys.get(1)).getKeyCode()),
					new Integer(KeyStrokeUtility.node2KeyStroke((Element)keys.get(0)).getKeyCode()));
		}
	}
	
	public static KeyStroke getKeyStrokeForEvent(KeyEvent e) {
		return KeyStrokeMap.getInstance().convert(KeyStroke.getKeyStrokeForEvent(e));
	}
	
	public static KeyStroke getKeyStroke(int keyCode, int modifiers) {
		return KeyStrokeMap.getInstance().convertReverse(KeyStroke.getKeyStroke(keyCode, modifiers));
	}
	
	public static KeyStroke getKeyStroke(int keyCode, int modifiers, boolean onKeyRelease) {
		return KeyStrokeMap.getInstance().convertReverse(KeyStroke.getKeyStroke(keyCode, modifiers, onKeyRelease));
	}
	
	
}
