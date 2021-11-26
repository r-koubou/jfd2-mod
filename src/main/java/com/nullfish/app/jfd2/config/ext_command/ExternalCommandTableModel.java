package com.nullfish.app.jfd2.config.ext_command;

import java.io.IOException;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.nullfish.app.jfd2.command.JFDException;
import com.nullfish.app.jfd2.ext_command_panel.ExternalCommand;
import com.nullfish.app.jfd2.ext_command_panel.ExternalCommandManager;
import com.nullfish.app.jfd2.resource.JFDResource;
import com.nullfish.app.jfd2.util.DomCache;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;
import com.nullfish.lib.vfs.exception.VFSIOException;

public class ExternalCommandTableModel extends AbstractTableModel {
	private ExternalCommand[][] commands = new ExternalCommand[2][26];

	public static final int COLUMN_LABEL = 0;

	public static final int COLUMN_TITLE = 1;

	public static final int COLUMN_TYPE = 2;

	public static final int COLUMN_COMMAND = 3;

	public static final int COLUMN_WORK_DIR = 4;

	public static final int COLUMN_SCRIPT = 5;

	private static final int COLUMN_COUNT = 6;

	private static final String ROOT_NODE = "ext_commands";

	private static final String COMMAND_SET_NODE = "extcommandset";

	public ExternalCommandTableModel() {
		for (int i = 0; i < commands[0].length; i++) {
			commands[0][i] = new ExternalCommand();
			commands[0][i].setLabel(Character.toString((char) ('A' + i)));
		}

		for (int i = 0; i < commands[1].length; i++) {
			commands[1][i] = new ExternalCommand();
			commands[1][i].setLabel(Character.toString((char) ('a' + i)));
		}
	}

	/**
	 * ロウ数を返す
	 */
	public int getRowCount() {
		return 52;
	}

	/**
	 * カラム数を返す
	 */
	public int getColumnCount() {
		return COLUMN_COUNT;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		ExternalCommand command = getExternalCommand(rowIndex);

		switch (columnIndex) {
		case COLUMN_LABEL:
			return command.getLabel();
		case COLUMN_TITLE:
			return command.getTitle();
		case COLUMN_TYPE:
			if(ExternalCommand.MODE_SHELL.equals(command.getMode())) {
				return JFDResource.LABELS.getString("command_shell");
			} else {
				return JFDResource.LABELS.getString("command_script_file");
			}
		case COLUMN_COMMAND:
			return command.getShellCommand().getShellCommand();
		case COLUMN_WORK_DIR:
			return command.getShellCommand().getWorkDir();
		case COLUMN_SCRIPT:
			return command.getScriptFileCommand().getScriptFile();
		default:
			return "";
		}
	}

	public ExternalCommand getExternalCommand(int index) {
		if (index < 26) {
			return commands[0][index];
		} else {
			return commands[1][index - 26];
		}
	}

	public String getColumnName(int column) {
		switch (column) {
		case COLUMN_LABEL:
			return " ";
		case COLUMN_TITLE:
			return JFDResource.LABELS.getString("command_title");
		case COLUMN_TYPE:
			return " ";
		case COLUMN_COMMAND:
			return JFDResource.LABELS.getString("command");
		case COLUMN_WORK_DIR:
			return JFDResource.LABELS.getString("work_dir");
		case COLUMN_SCRIPT:
			return JFDResource.LABELS.getString("script");
		default:
			return " ";
		}
	}

	/**
	 * 設定ファイルから読み込む
	 * 
	 * @param baseDir
	 * @throws VFSException
	 */
	public void init(VFile baseDir) throws VFSException {
		try {
			Document doc = DomCache.getInstance().getDocument(baseDir.getChild(
					ExternalCommandManager.CONFIG_FILE));

			List commandSetList = doc.getRootElement().getChildren(
					ExternalCommandManager.EXT_COMMAND_SET);
			for (int i = 0; i < commandSetList.size(); i++) {
				List commandsNode = ((Element) commandSetList.get(i))
						.getChildren(ExternalCommand.NODE_NAME);
				for (int j = 0; j < commandsNode.size(); j++) {
					commands[i][j].init((Element) commandsNode.get(j));
				}
			}

			fireTableDataChanged();
		} catch (JDOMException e) {
			throw new JFDException(e.toString());
		} catch (IOException e) {
			throw new VFSIOException(e);
		}
	}

	public void save(VFile baseDir) throws VFSException {
		try {
			Document doc = new Document();
			Element root = new Element(ROOT_NODE);
			doc.setRootElement(root);

			Element set1 = new Element(COMMAND_SET_NODE);
			root.addContent(set1);
			for (int i = 0; i < 26; i++) {
				set1.addContent(commands[0][i].toNode());
			}

			Element set2 = new Element(COMMAND_SET_NODE);
			root.addContent(set2);
			for (int i = 0; i < 26; i++) {
				set2.addContent(commands[1][i].toNode());
			}

			XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
			outputter.output(doc, baseDir.getChild(
					ExternalCommandManager.CONFIG_FILE).getOutputStream());
		} catch (IOException e) {
			throw new VFSIOException(e);
		}
	}
}
