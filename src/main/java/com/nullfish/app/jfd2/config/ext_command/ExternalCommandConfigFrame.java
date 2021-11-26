package com.nullfish.app.jfd2.config.ext_command;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumnModel;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.command.groovy.AbstractGroovyCommand;
import com.nullfish.app.jfd2.config.Configuration;
import com.nullfish.app.jfd2.ext_command_panel.ExternalCommand;
import com.nullfish.app.jfd2.ext_command_panel.ExternalCommandManager;
import com.nullfish.app.jfd2.resource.JFDResource;
import com.nullfish.lib.keymap.KeyStrokeMap;
import com.nullfish.lib.tablelayout.HtmlTablePanel;
import com.nullfish.lib.ui.UIUtilities;
import com.nullfish.lib.vfs.VFS;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;

public class ExternalCommandConfigFrame extends JDialog {
	/**
	 * 表示中のコマンド
	 */
	private ExternalCommand currentCommand;
	
	/**
	 * 設定ディレクトリ
	 */
	private VFile baseDir;
	
	private JFD jfd;
	
	private HtmlTablePanel mainPanel;
	
	private JLabel titleLabel = new JLabel(JFDResource.LABELS.getString("command_title"));
	private JTextField titleField = new JTextField();
	private JButton executeButton = new JButton(JFDResource.LABELS.getString("execute"));
	private JButton deleteButton = new JButton(JFDResource.LABELS.getString("delete"));

	private JRadioButton commandRadio = new JRadioButton(JFDResource.LABELS.getString("shell_command"));
	private JRadioButton scriptRadio = new JRadioButton(JFDResource.LABELS.getString("script"));
	private ButtonGroup radioGroup = new ButtonGroup();
	
	private JLabel commandLabel = new JLabel(JFDResource.LABELS.getString("command"));

	private JTextField commandField = new JTextField();
	private JButton commandReferButton = new JButton(JFDResource.LABELS.getString("refer"));
	private JLabel workDirLabel = new JLabel(JFDResource.LABELS.getString("work_dir"));
	private JTextField workDirField = new JTextField();
	private JButton workDirReferButton = new JButton(JFDResource.LABELS.getString("refer"));
	private JCheckBox useShellCheckBox = new JCheckBox(JFDResource.LABELS.getString("use_shell"));
	
	private JLabel scriptLabel = new JLabel(JFDResource.LABELS.getString("script"));
	private JTextField scriptField = new JTextField();
	private JButton scriptReferButton = new JButton(JFDResource.LABELS.getString("refer"));
	
	private ExternalCommandTableModel tableModel = new ExternalCommandTableModel();
	private JTable commandTable = new JTable(tableModel);
	private JScrollPane tableScroll = new JScrollPane(commandTable);
	
	private JTextPane instructionArea = new JTextPane();
	private JScrollPane instructionScroll = new JScrollPane(instructionArea);
	
	private JButton okButton = new JButton(JFDResource.LABELS.getString("ok")+"(O)");
	private JButton cancelButton = new JButton(JFDResource.LABELS.getString("cancel")+"(C)");
	
	
	
	private static final String MAIN_LAYOUT = "classpath:///resources/command_option_layout.xml";

	public ExternalCommandConfigFrame(JFD jfd) {
		super((Window)UIUtilities.getTopLevelOwner((Container)jfd));
		setModal(true);
		
		this.jfd = jfd;
		
		try {
			initGui();
			layoutPanel();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void load(VFile baseDir) throws VFSException {
		this.baseDir = baseDir;
		tableModel.init(baseDir);
		commandTable.getSelectionModel().setSelectionInterval(0, 0);
	}
	
	public void save(VFile baseDir) throws VFSException {
		tableModel.save(baseDir);
	}
	
	private void initGui() throws FileNotFoundException, SAXException,
			IOException, ParserConfigurationException,
			FactoryConfigurationError, VFSException {
		mainPanel = new HtmlTablePanel(VFS.getInstance().getFile(MAIN_LAYOUT)
				.getInputStream());
		
		okButton.setMnemonic('o');
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					save(baseDir);
					dispose();
					ExternalCommandManager.getInstance().init(baseDir);
				} catch (VFSException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		executeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(currentCommand == null) {
					return;
				}
				
				try {
					currentCommand.execute(jfd);
				} catch (VFSException ex) {
					ex.printStackTrace();
				}
			}
		});
		
		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				titleField.setText("");
				commandRadio.setSelected(true);
				commandField.setText("");
				workDirField.setText("");
				scriptField.setText("");
				applyCurrentCommand();
				commandTable.requestFocusInWindow();
			}
		});
		
		cancelButton.setMnemonic('c');
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		commandReferButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showCommandRefer();
			}
		});
		
		workDirReferButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showWorkDirRefer();
			}
		});
		
		scriptReferButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showScriptRefer();
			}
		});
		
		commandRadio.setSelected(true);
		radioGroup.add(commandRadio);
		radioGroup.add(scriptRadio);
		
		commandRadio.addChangeListener(new ChangeListener() {
		    public void stateChanged(ChangeEvent e) {
		    	boolean enabled = commandRadio.isSelected();
		    	commandField.setEnabled(enabled);
		    	commandReferButton.setEnabled(enabled);
		    	workDirReferButton.setEnabled(enabled);
		    	workDirField.setEnabled(enabled);
		    	useShellCheckBox.setEnabled(enabled);
		    }
		});
		
		scriptRadio.addChangeListener(new ChangeListener() {
		    public void stateChanged(ChangeEvent e) {
		    	boolean enabled = scriptRadio.isSelected();
		    	scriptField.setEnabled(enabled);
		    	scriptReferButton.setEnabled(enabled);
		    }
		});
		
		commandTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		TableColumnModel columnModel = commandTable.getColumnModel();
		columnModel.getColumn(ExternalCommandTableModel.COLUMN_LABEL).setPreferredWidth(20);
		columnModel.getColumn(ExternalCommandTableModel.COLUMN_TITLE).setPreferredWidth(50);
		columnModel.getColumn(ExternalCommandTableModel.COLUMN_TYPE).setPreferredWidth(50);
		columnModel.getColumn(ExternalCommandTableModel.COLUMN_COMMAND).setPreferredWidth(150);
		columnModel.getColumn(ExternalCommandTableModel.COLUMN_SCRIPT).setPreferredWidth(150);
		columnModel.getColumn(ExternalCommandTableModel.COLUMN_WORK_DIR).setPreferredWidth(150);
		
		commandTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				applyCurrentCommand();
				
				int row = commandTable.getSelectedRow();
				if(row < 0) {
					return;
				}
				showCommand(tableModel.getExternalCommand(row));
			}
		});
		
		titleField.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				applyCurrentCommand();
				if(commandRadio.isSelected()) {
					commandRadio.requestFocusInWindow();
				} else {
					scriptRadio.requestFocusInWindow();
				}
			}
		});
		
		FocusAdapter textFieldsFocusListener = new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				applyCurrentCommand();
			}
		};
		titleField.addFocusListener(textFieldsFocusListener);
		commandField.addFocusListener(textFieldsFocusListener);
		workDirField.addFocusListener(textFieldsFocusListener);
		scriptField.addFocusListener(textFieldsFocusListener);
		
		commandField.addActionListener(new ApplyAndFocusNextAction(workDirField));
		workDirField.addActionListener(new ApplyAndFocusNextAction(commandTable));
		scriptField.addActionListener(new ApplyAndFocusNextAction(commandTable));
		
		commandRadio.getInputMap().put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_ENTER, 0), "enter");
		commandRadio.getActionMap().put("enter", new FocusAction(useShellCheckBox));
		commandRadio.getInputMap().put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_RIGHT, 0), "next");
		commandRadio.getInputMap().put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_UP, 0), "next");
		commandRadio.getInputMap().put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_LEFT, 0), "next");
		commandRadio.getInputMap().put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_DOWN, 0), "next");
		commandRadio.getActionMap().put("next", new FocusAndSelectAction(scriptRadio));

		useShellCheckBox.getInputMap().put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_ENTER, 0), "enter");
		useShellCheckBox.getActionMap().put("enter", new FocusAction(commandField));

		scriptRadio.getInputMap().put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_ENTER, 0), "enter");
		scriptRadio.getActionMap().put("enter", new FocusAction(scriptField));
		scriptRadio.getInputMap().put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_RIGHT, 0), "next");
		scriptRadio.getInputMap().put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_UP, 0), "next");
		scriptRadio.getInputMap().put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_LEFT, 0), "next");
		scriptRadio.getInputMap().put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_DOWN, 0), "next");
		scriptRadio.getActionMap().put("next", new FocusAndSelectAction(commandRadio));

		commandTable.getInputMap().put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_ENTER, 0), "enter");
		commandTable.getActionMap().put("enter", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				titleField.requestFocusInWindow();
			}
		});
		tableScroll.setPreferredSize(new Dimension(500, 250));
		
		mainPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "escape");
		mainPanel.getActionMap().put("escape", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				cancelButton.doClick();
			}
		});
		
		instructionScroll.setPreferredSize(new Dimension(500, 150));
		instructionArea.setText(JFDResource.LABELS.getString("all_options_detail"));
	}
	
	private void layoutPanel() {
		this.getContentPane().add(mainPanel);
		
		mainPanel.addComponent(titleLabel, "title_label");
		mainPanel.addComponent(titleField, "title_input");
		mainPanel.addComponent(executeButton, "execute_button");
		mainPanel.addComponent(deleteButton, "delete_button");
		mainPanel.addComponent(commandRadio, "command_radio");
		mainPanel.addComponent(commandLabel, "command_label");
		mainPanel.addComponent(commandField, "command_input");
		mainPanel.addComponent(commandReferButton, "command_refer_button");
		mainPanel.addComponent(workDirLabel, "work_dir_label");
		mainPanel.addComponent(workDirField, "work_dir_input");
		mainPanel.addComponent(workDirReferButton, "work_dir_refer_button");
		mainPanel.addComponent(scriptRadio, "script_radio");
		mainPanel.addComponent(scriptLabel, "script_label");
		mainPanel.addComponent(scriptField, "script_input");
		mainPanel.addComponent(scriptReferButton, "script_refer_button");
		mainPanel.addComponent(tableScroll, "table");
		mainPanel.addComponent(instructionScroll, "instruction");
		mainPanel.addComponent(okButton, "ok_button");
		mainPanel.addComponent(cancelButton, "cancel_button");
		mainPanel.addComponent(tableScroll, "table");
		mainPanel.addComponent(useShellCheckBox, "use_shell_check");
	}
	
	private void applyCurrentCommand() {
		if(currentCommand == null) {
			return;
		}
		
		currentCommand.setTitle(titleField.getText());
		if(commandRadio.isSelected()) {
			currentCommand.setMode(ExternalCommand.MODE_SHELL);
		} else {
			currentCommand.setMode(ExternalCommand.MODE_SCRIPT_FILE);
		}
		
		currentCommand.getShellCommand().setShellCommand(commandField.getText());
		currentCommand.getShellCommand().setWorkDir(workDirField.getText());
		currentCommand.getShellCommand().setUseShell(useShellCheckBox.isSelected());
		
		currentCommand.getScriptFileCommand().setScriptFile(scriptField.getText());
		tableModel.fireTableRowsUpdated(0, 52);
	}
	
	private void showCommand(ExternalCommand command) {
		currentCommand = command;
		
		titleField.setText(command.getTitle());
		if(ExternalCommand.MODE_SHELL.equals(command.getMode())) {
			commandRadio.setSelected(true);
		} else {
			scriptRadio.setSelected(true);
		}
		
		commandField.setText(command.getShellCommand().getShellCommand());
		workDirField.setText(command.getShellCommand().getWorkDir());
		scriptField.setText(command.getScriptFileCommand().getScriptFile());
		useShellCheckBox.setSelected(command.getShellCommand().isUseShell());
	}
	
	/**
	 * コマンドの参照ボタン処理
	 *
	 */
	private void showCommandRefer() {
		File file = showFileChooser(commandReferButton, JFileChooser.FILES_AND_DIRECTORIES, commandField.getText());
		if(file != null) {
			commandField.setText(file.getAbsolutePath());
			applyCurrentCommand();
		}
	}
	
	/**
	 * 作業ディレクトリの参照ボタン処理
	 *
	 */
	private void showWorkDirRefer() {
		File file = showFileChooser(workDirReferButton, JFileChooser.DIRECTORIES_ONLY, commandField.getText());
		if(file != null) {
			workDirField.setText(file.getAbsolutePath());
			applyCurrentCommand();
		}
	}
	
	private void showScriptRefer() {
		try {
			Configuration commonConfig = Configuration.getInstance(baseDir
					.getChild(JFD.COMMON_PARAM_FILE));
			VFile scriptDir = VFS.getInstance().getFile(
					(String) commonConfig.getParam("script_dir", AbstractGroovyCommand.DEFAULT_SCRIPT_DIR));

			File file = showFileChooser(scriptReferButton,
					JFileChooser.FILES_ONLY, scriptDir.getAbsolutePath());
			if (file == null) {
				return;
			}

			VFile selectedFile = VFS.getInstance().getFile(file.getAbsolutePath());
			if (!scriptDir.equals(selectedFile.getParent())) {
				JOptionPane.showMessageDialog(this, JFDResource.MESSAGES
						.getString("script_out_of_script_dir"));
				return;
			}
			
			scriptField.setText(selectedFile.getName());
			applyCurrentCommand();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ファイル選択ダイアログを表示する
	 * @param c
	 * @param mode
	 * @param original
	 */
	private File showFileChooser(Component c, int mode, String original) {
		JFileChooser chooser = new JFileChooser(original);
		chooser.setFileSelectionMode(mode);
		chooser.setDialogTitle("jFD2");
		
		int returnVal = chooser.showOpenDialog(c);
		if(returnVal != JFileChooser.APPROVE_OPTION) {
			return null;
		}
		
		return chooser.getSelectedFile();
	}
	
	private class ApplyAndFocusNextAction extends AbstractAction {
		private Component nextComponent;
		
		private ApplyAndFocusNextAction(Component nextComponent) {
			this.nextComponent = nextComponent;
		}
		
		public void actionPerformed(ActionEvent e) {
			applyCurrentCommand();
			nextComponent.requestFocusInWindow();
		}
	}
	
	private class FocusAction extends AbstractAction {
		private Component component;
		
		private FocusAction(Component component) {
			this.component = component;
		}
		
		public void actionPerformed(ActionEvent e) {
			component.requestFocusInWindow();
		}
	}
	
	private class FocusAndSelectAction extends AbstractAction {
		private AbstractButton button;
		
		private FocusAndSelectAction(AbstractButton button) {
			this.button = button;
		}
		
		public void actionPerformed(ActionEvent e) {
			button.requestFocusInWindow();
			button.setSelected(true);
		}
	}
}
