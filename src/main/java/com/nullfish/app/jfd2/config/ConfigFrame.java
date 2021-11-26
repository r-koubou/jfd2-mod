package com.nullfish.app.jfd2.config;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JTabbedPane;

import org.jdom.JDOMException;

import com.nullfish.app.jfd2.resource.JFDResource;
import com.nullfish.lib.keymap.KeyStrokeMap;
import com.nullfish.lib.tablelayout.HtmlTablePanel;
import com.nullfish.lib.vfs.VFS;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;

public class ConfigFrame extends JDialog {
	private HtmlTablePanel mainPanel;

	private boolean modified = false;
	
	public static final String MAIN_LAYOUT = "classpath:///resources/option_layout.xml";

	private JButton okButton = new JButton(JFDResource.LABELS
			.getString("ok") + "(O)");

	private JButton cancelButton = new JButton(JFDResource.LABELS
			.getString("cancel") + "(C)");

	ConfigPanel[] panels = {
			new ViewConfigPanel(this),
			new View2ConfigPanel(),
			new PathConfigPanel(),
			new FileSystemConfigPanel(),
			new ETCConfigPanel()
	};
	
	private JTabbedPane tabbedPane = new JTabbedPane();

	public ConfigFrame() {
		try {
			initGui();
			layoutComponent();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initGui() throws Exception {
		setTitle("jFD2 - Option");
		mainPanel = new HtmlTablePanel(VFS.getInstance().getFile(MAIN_LAYOUT)
				.getInputStream());

		okButton.setMnemonic('o');
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					apply();
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					setVisible(false);
					dispose();
				}
			}
		});
		
		cancelButton.setMnemonic('c');
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dispose();
			}
		});
		
		mainPanel.getActionMap().put("close", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		mainPanel.getInputMap().put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "close");
	}

	private void layoutComponent() {
		getContentPane().add(mainPanel);

		mainPanel.addComponent(okButton, "ok_button");
		mainPanel.addComponent(cancelButton, "cancel_button");
		mainPanel.addComponent(tabbedPane, "tab");

		for(int i=0; i<panels.length; i++) {
			tabbedPane.add(panels[i].getTitle(), (JComponent)panels[i]);
		}
	}

	/**
	 * 設定を読み込む
	 * @param configDir
	 * @throws JDOMException
	 * @throws IOException
	 * @throws VFSException
	 */
	public void loadPreference(VFile configDir) throws Exception {
		for(int i=0; i<panels.length; i++) {
			panels[i].loadPreference(configDir);
		}
	}

	/***
	 * 入力を設定に反映する。
	 * @throws JDOMException
	 * @throws IOException
	 * @throws VFSException
	 */
	public void apply() throws Exception {
		for(int i=0; i<panels.length; i++) {
			panels[i].apply();
		}

		modified = true;
	}

	public boolean isModified() {
		return modified;
	}
}
