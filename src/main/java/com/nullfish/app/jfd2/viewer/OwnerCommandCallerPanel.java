package com.nullfish.app.jfd2.viewer;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import com.nullfish.app.jfd2.command.CommandManager;
import com.nullfish.app.jfd2.util.DomCache;
import com.nullfish.lib.ui.KeyStrokeUtility;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;

public class OwnerCommandCallerPanel extends JPanel {
	private FileViewer viewer;
	
	private Map keyCommandMap = new HashMap();
	
	public static final String NODE_MAP = "map";
	
	public void setViewer(FileViewer viewer) {
		this.viewer = viewer;
	}
	
	public void init(VFile file) throws JDOMException, IOException, VFSException {
		Document doc = DomCache.getInstance().getDocument(file);
		init(doc.getRootElement());
	}
	
	public void init(Element node) {
		List commandMapList = node.getChildren(CommandManager.COMMAND_MAP_TAG);
		for (int i = 0; i < commandMapList.size(); i++) {
			Element mapping = (Element)commandMapList.get(i);
			String commandName = mapping.getAttributeValue(CommandManager.ATTR_COMMANDNAME);
			List keys = mapping.getChildren(KeyStrokeUtility.KEY_NODE);
			for(int j=0; j<keys.size(); j++) {
				Element keyNode = (Element)keys.get(j);
				KeyStroke keyStroke = KeyStrokeUtility.node2KeyStroke(keyNode);
				keyCommandMap.put(keyStroke, commandName);
			}
		}
		
	}
	
	public boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed) {
		String command = (String)keyCommandMap.get(ks);
		if(e.isConsumed()
				|| command == null
				|| viewer == null
				|| viewer.getJFD() == null) {
			return super.processKeyBinding(ks, e, condition, pressed);
		}

		if(viewer.getJFD().getCommandManager().execute(command)) {
			e.consume();
			return true;
		}
		
		Action action = (Action)getActionMap().get(command);
		if(action != null) {
			return SwingUtilities.notifyAction(action, ks, e, this,
					   e.getModifiers());
		}
		
		return super.processKeyBinding(ks, e, condition, pressed);
	}
}
