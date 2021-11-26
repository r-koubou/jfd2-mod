package com.nullfish.app.jfd2.config;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jdom.JDOMException;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.resource.JFDResource;
import com.nullfish.lib.tablelayout.HtmlTablePanel;
import com.nullfish.lib.ui.Choice;
import com.nullfish.lib.ui.ChooserPanel;
import com.nullfish.lib.ui.document.RegexRestriction;
import com.nullfish.lib.ui.document.RestrictedDocument;
import com.nullfish.lib.vfs.VFS;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;
import com.nullfish.lib.vfs.impl.ftp.FTPFileSystem;

public class FileSystemConfigPanel extends JPanel implements ConfigPanel {
	private VFile configDir;

	public static final String FTP_LAYOUT = "classpath:///resources/option_layout_ftp_tab.xml";

	//
	//	FTPタブ
	private HtmlTablePanel ftpPanel;
	private ChooserPanel transferModeChooser;
	
	private JLabel asciiExtensionLabel = new JLabel(JFDResource.LABELS.getString("ascii_mode_extension"));
	private JTextArea asciiExtensionTextArea = new JTextArea();
	private JCheckBox passiveModeCheckBox = new JCheckBox(JFDResource.LABELS.getString("passive_mode"));
	
	private JCheckBox proxyCheckBox = new JCheckBox(JFDResource.LABELS.getString("use_proxy")) {
		public void setSelected(boolean selected) {
			super.setSelected(selected);
			proxyCheckBoxChanged();
		}
	};
	
	private JLabel ftpEncodeLabel = new JLabel(JFDResource.LABELS.getString("ftp_encoding"));
	private JTextField ftpEncodeTextArea = new JTextField();
	
	private JTextField proxyHostText = new JTextField();
	private JTextField proxyPortText = new JTextField();

	private JLabel ftpFileSystemEncodeLabel = new JLabel(JFDResource.LABELS.getString("ftp_filesystem_encoding"));
	private JTextArea ftpFileSystemEncodeTextArea = new JTextArea(3, 30);
	
	//	HTTP
	private JCheckBox httpProxyCheckBox = new JCheckBox(JFDResource.LABELS.getString("use_proxy")) {
		public void setSelected(boolean selected) {
			super.setSelected(selected);
			httpProxyCheckBoxChanged();
		}
	};
	
	private JLabel httpProxyHostLabel = new JLabel(JFDResource.LABELS.getString("proxy_host"));
	private JLabel httpProxyPortLabel = new JLabel(JFDResource.LABELS.getString("proxy_port"));
	private JTextField httpProxyHostText = new JTextField();
	private JTextField httpProxyPortText = new JTextField();

	//	ZIP
	private JLabel zipEncodingLabel = new JLabel(JFDResource.LABELS.getString("zip_encoding"));
	private JTextField zipEncodingText = new JTextField();
	private JLabel zipPackEncodingLabel = new JLabel(JFDResource.LABELS.getString("zip_pack_encoding"));
	private JTextField zipPackEncodingText = new JTextField();

	public FileSystemConfigPanel() {
		super(new BorderLayout());
		try {
			initGui();
			layoutComponent();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void initGui() throws Exception {
		ftpPanel = new HtmlTablePanel(VFS.getInstance().getFile(
				FTP_LAYOUT).getInputStream());
		asciiExtensionTextArea.setRows(5);
		
		Choice[] choices = {
				new Choice(FTPFileSystem.TRANSFER_MODE_EXTENSION, JFDResource.LABELS.getString("depends_extension")), 
				new Choice(FTPFileSystem.TRANSFER_MODE_BINARY, JFDResource.LABELS.getString("binary_transfer")),
				new Choice(FTPFileSystem.TRANSFER_MODE_ASCII, JFDResource.LABELS.getString("ascii_transfer"))
		};
		
		transferModeChooser = new ChooserPanel(JFDResource.LABELS.getString("transfer_mode"), choices, 1, FTPFileSystem.TRANSFER_MODE_EXTENSION);
		transferModeChooser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				asciiExtensionTextArea
						.setEditable(FTPFileSystem.TRANSFER_MODE_EXTENSION
								.equals(transferModeChooser.getSelectedAnswer()));
			}
		});
		
		RestrictedDocument numberDoc = new RestrictedDocument();
		numberDoc.addRestriction(RegexRestriction.getInstance("\\d*"));
		proxyPortText.setDocument(numberDoc);
		proxyPortText.setColumns(6);
		
		proxyCheckBox.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				proxyCheckBoxChanged();
			}
		});
		
		// HTTP
		RestrictedDocument numberDoc2 = new RestrictedDocument();
		numberDoc2.addRestriction(RegexRestriction.getInstance("\\d*"));
		httpProxyPortText.setDocument(numberDoc2);
		httpProxyPortText.setColumns(6);
		
		httpProxyCheckBox.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				httpProxyCheckBoxChanged();
			}
		});
	}

	public void layoutComponent() {
		add(ftpPanel);
		ftpPanel.addComponent(transferModeChooser, "transfer_mode_chooser");

		ftpPanel.addComponent(asciiExtensionLabel, "ascii_extension");
		ftpPanel.addComponent(new JScrollPane( asciiExtensionTextArea ), "ascii_extension_input");
		ftpPanel.addComponent(passiveModeCheckBox, "passive_check");
		ftpPanel.addComponent(ftpEncodeLabel, "ftp_encoding_label");
		ftpPanel.addComponent(ftpEncodeTextArea, "ftp_encoding_text");
		ftpPanel.addComponent(ftpFileSystemEncodeLabel, "ftp_filesystem_encoding_label");
		ftpPanel.addComponent(new JScrollPane(ftpFileSystemEncodeTextArea), "ftp_filesystem_encoding_text");
/*
		ftpPanel.addComponent(proxyCheckBox, "use_ftp_proxy_check");
		ftpPanel.addComponent(proxyHostLabel, "proxy_host_label");
		ftpPanel.addComponent(proxyPortLabel, "proxy_port_label");
		ftpPanel.addComponent(proxyHostText, "proxy_host_input");
		ftpPanel.addComponent(proxyPortText, "proxy_port_input");
*/

		ftpPanel.addComponent(httpProxyCheckBox, "use_http_proxy_check");
		ftpPanel.addComponent(httpProxyHostLabel, "http_proxy_host_label");
		ftpPanel.addComponent(httpProxyPortLabel, "http_proxy_port_label");
		ftpPanel.addComponent(httpProxyHostText, "http_proxy_host_input");
		ftpPanel.addComponent(httpProxyPortText, "http_proxy_port_input");
		
		ftpPanel.addComponent(zipEncodingLabel, "zip_encoding_label");
		ftpPanel.addComponent(zipEncodingText,  "zip_encoding_input");
		ftpPanel.addComponent(zipPackEncodingLabel, "zip_pack_encoding_label");
		ftpPanel.addComponent(zipPackEncodingText,  "zip_pack_encoding_input");
	}

	/**
	 * 設定を読み込む
	 * @param configDir
	 * @throws JDOMException
	 * @throws IOException
	 * @throws VFSException
	 */
	public void loadPreference(VFile configDir) throws JDOMException,
			IOException, VFSException {
		this.configDir = configDir;
		Configuration commonConfig = Configuration.getInstance(configDir
				.getChild(JFD.COMMON_PARAM_FILE));

		transferModeChooser.setSelectedAnser((String)commonConfig.getParam("ftp_transfer_mode", FTPFileSystem.TRANSFER_MODE_EXTENSION));
		asciiExtensionTextArea.setText("");
		Iterator extensions = ((List)commonConfig.getParam("auto_ascii_mode_extension", new ArrayList())).iterator();
		while(extensions.hasNext()) {
			asciiExtensionTextArea.append(extensions.next().toString() + " ");
		}
		passiveModeCheckBox.setSelected(((Boolean)commonConfig.getParam("passive_mode", Boolean.FALSE)).booleanValue());
		ftpEncodeTextArea.setText((String)commonConfig.getParam("ftp_encoding", "EUC-JP"));
		ftpFileSystemEncodeTextArea.setText((String)commonConfig.getParam("ftp_filesystem_encoding", "EUC-JP"));
		
		proxyCheckBox.setSelected(((Boolean)commonConfig.getParam("ftp_use_proxy", Boolean.FALSE)).booleanValue());
		proxyHostText.setText((String)commonConfig.getParam("ftp_proxy_host", ""));
		proxyPortText.setText((String)commonConfig.getParam("ftp_proxy_port", ""));

		httpProxyCheckBox.setSelected(((Boolean)commonConfig.getParam("http_use_proxy", Boolean.FALSE)).booleanValue());
		httpProxyHostText.setText((String)commonConfig.getParam("http_proxy_host", ""));
		httpProxyPortText.setText(((Integer)commonConfig.getParam("http_proxy_port", new Integer(8080))).toString());

		zipEncodingText.setText(((String)commonConfig.getParam("zip_encoding", System.getProperty("file.encoding"))).toString());
		zipPackEncodingText.setText(((String)commonConfig.getParam("zip_pack_encoding", System.getProperty("file.encoding"))).toString());
	}

	/***
	 * 入力を設定に反映する。
	 * @throws JDOMException
	 * @throws IOException
	 * @throws VFSException
	 */
	public void apply() throws JDOMException, IOException, VFSException {
		Configuration commonConfig = Configuration.getInstance(configDir
				.getChild(JFD.COMMON_PARAM_FILE));

		commonConfig.setParam("ftp_transfer_mode", transferModeChooser.getSelectedAnswer());
		List autoAsciiList = new ArrayList();
		StringTokenizer tokenizer = new StringTokenizer(asciiExtensionTextArea.getText(), " \t\n");
		while(tokenizer.hasMoreTokens()) {
			autoAsciiList.add(tokenizer.nextToken());
		}
		commonConfig.setParam("auto_ascii_mode_extension", autoAsciiList);
		commonConfig.setParam("passive_mode", Boolean.valueOf(passiveModeCheckBox.isSelected()));
		commonConfig.setParam("ftp_encoding", ftpEncodeTextArea.getText());
		commonConfig.setParam("ftp_filesystem_encoding", ftpFileSystemEncodeTextArea.getText());
		
		commonConfig.setParam("ftp_use_proxy",  Boolean.valueOf(proxyCheckBox.isSelected()));
		commonConfig.setParam("ftp_proxy_host", proxyHostText.getText());
		commonConfig.setParam("ftp_proxy_port", proxyPortText.getText());

		commonConfig.setParam("http_use_proxy",  Boolean.valueOf(httpProxyCheckBox.isSelected()));
		commonConfig.setParam("http_proxy_host", httpProxyHostText.getText());
		String httpProxyPortStr = httpProxyPortText.getText();
		commonConfig.setParam("http_proxy_port", httpProxyPortStr.length() > 0 ? Integer.valueOf(httpProxyPortStr) : new Integer(8080));

		commonConfig.setParam("zip_encoding", zipEncodingText.getText());
		commonConfig.setParam("zip_pack_encoding", zipPackEncodingText.getText());
	}
	
	public 	String getTitle() {
		return JFDResource.LABELS.getString("filesystem");
	}
	
	private void proxyCheckBoxChanged() {
		proxyHostText.setEditable(proxyCheckBox.isSelected());
		proxyPortText.setEditable(proxyCheckBox.isSelected());
	}
	
	private void httpProxyCheckBoxChanged() {
		httpProxyHostText.setEditable(httpProxyCheckBox.isSelected());
		httpProxyPortText.setEditable(httpProxyCheckBox.isSelected());
	}
}
