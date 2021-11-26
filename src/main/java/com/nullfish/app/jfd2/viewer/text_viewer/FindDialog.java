package com.nullfish.app.jfd2.viewer.text_viewer;

import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.monazilla.migemo.Migemo;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.command.embed.GrepCommand;
import com.nullfish.app.jfd2.dialog.ConfigurationInfo;
import com.nullfish.app.jfd2.dialog.components.ConfigCheckBox;
import com.nullfish.app.jfd2.dialog.components.DialogComboBox;
import com.nullfish.app.jfd2.util.MigemoInfo;
import com.nullfish.app.jfd2.util.StringHistory;
import com.nullfish.lib.keymap.KeyStrokeMap;
import com.nullfish.lib.tablelayout.HtmlTablePanel;

public class FindDialog extends JDialog {
	private JLabel comboLabel = new JLabel(TextFileViewer.RESOURCE
			.getString("search_text"));

	private DialogComboBox comboBox = new DialogComboBox(this);

	private ConfigCheckBox caseSensitiveCheckBox = new ConfigCheckBox(
			TextFileViewer.RESOURCE.getString("case_sensitive"));

	private ConfigCheckBox regexCheckBox = new ConfigCheckBox(TextFileViewer.RESOURCE
			.getString("use_regex"));

	private ConfigCheckBox migemoCheckBox = new ConfigCheckBox(TextFileViewer.RESOURCE
			.getString("use_migemo"));

	private JButton prevButton = new JButton(TextFileViewer.RESOURCE
			.getString("search_prev") + "(B)");

	private JButton nextButton = new JButton(TextFileViewer.RESOURCE
			.getString("search_next") +"(F)");

	private JButton closeButton = new JButton(TextFileViewer.RESOURCE
			.getString(CLOSE) + "(C)");

	private JTextArea textArea;
	
	private JScrollPane scroll;

	private static final String LAYOUT_FILE = "/textviewer_dialog_layout.xml";

	private JFD jfd;

	public static final String CLOSE ="close";
	
	public FindDialog(JTextArea textArea, JScrollPane scroll) {
		this.textArea = textArea;
		this.scroll = scroll;
		initGui();
	}

	public FindDialog(JTextArea textArea, JScrollPane scroll, Dialog owner) {
		super(owner);
		this.textArea = textArea;
		this.scroll = scroll;
		initGui();
	}

	public FindDialog(JTextArea textArea, JScrollPane scroll, Frame owner) {
		super(owner);
		this.textArea = textArea;
		this.scroll = scroll;
		initGui();
	}

	private void initGui() {
		comboBox.setEditable(true);
		comboBox.setClosesOnDecision(false);
		
		caseSensitiveCheckBox.setMnemonic('i');
		regexCheckBox.setMnemonic('r');
		migemoCheckBox.setMnemonic('m');

		prevButton.setMnemonic(KeyEvent.VK_B);
		nextButton.setMnemonic(KeyEvent.VK_F);
		closeButton.setMnemonic(KeyEvent.VK_C);
		
		HtmlTablePanel rootPanel = null;
		try {
			rootPanel = new HtmlTablePanel(LAYOUT_FILE);
		} catch (Exception e) {
			e.printStackTrace();
		}

		getContentPane().add(rootPanel);

		rootPanel.addComponent(comboLabel, "search_label");
		rootPanel.addComponent(comboBox, "combobox");
		rootPanel.addComponent(closeButton, "close_button");
		rootPanel.addComponent(caseSensitiveCheckBox, "checkbox");
		rootPanel.addComponent(prevButton, "prev_button");
		rootPanel.addComponent(nextButton, "next_button");
		rootPanel.addComponent(regexCheckBox, "regex_checkbox");
		rootPanel.addComponent(migemoCheckBox, "migemo_checkbox");

		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("comboBoxEdited")) {
					search(true);
				}
			}
		});

		prevButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				search(false);
			}
		});

		nextButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				search(true);
			}
		});

		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});

		rootPanel.getActionMap().put(CLOSE, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});

		rootPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
				.put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_ESCAPE, 0), CLOSE);
		pack();
	}

	public void setVisible(boolean visible) {
		if (visible) {
			Container c = textArea;
			while (!(c instanceof Window) && c.getParent() != null) {
				c = c.getParent();
			}

			setLocationRelativeTo(c);
		}

		super.setVisible(visible);
	}

	public void initFromJFD() {
		
		StringHistory history = getHistory();
		if (history == null) {
			return;
		}

		String[] histories = history.toArray();

		ComboBoxModel model = new DefaultComboBoxModel(histories);
		comboBox.setModel(model);
		
		caseSensitiveCheckBox.setConfigueationInfo(new ConfigurationInfo(jfd.getLocalConfiguration(), GrepCommand.CASE_SENSITIVE));
		regexCheckBox.setConfigueationInfo(new ConfigurationInfo(jfd.getLocalConfiguration(), "grep_use_regex"));
		migemoCheckBox.setConfigueationInfo(new ConfigurationInfo(jfd.getLocalConfiguration(), "grep_use_migemo"));
		
		comboBox.requestFocusInWindow();
	}

	private void appendSearchHistory(String text) {
		StringHistory history = getHistory();
		if (history == null) {
			return;
		}

		history.add(text);
		
		caseSensitiveCheckBox.applyConfiguration();
		regexCheckBox.applyConfiguration();
		migemoCheckBox.applyConfiguration();
	}

	private StringHistory getHistory() {
		if (jfd == null) {
			return null;
		}

		StringHistory history = (StringHistory) jfd.getLocalConfiguration()
				.getParam("grep_history", null);
		if (history == null) {
			history = new StringHistory(50, true);
			jfd.getLocalConfiguration().setParam("grep_history", history);
		}

		return history;
	}

	public void search(boolean ascend) {
		String text = textArea.getText();
		String searchText = (String) comboBox.getSelectedItem();

		if (searchText == null || searchText.length() == 0) {
			return;
		}

		appendSearchHistory(searchText);
		if (!caseSensitiveCheckBox.isSelected()) {
			searchText = searchText.toLowerCase();
			text = text.toLowerCase();
		}

		initFromJFD();
		if(MigemoInfo.usesMigemo() && migemoCheckBox.isSelected()) {
			if (ascend) {
				searchRegexForward(text, Migemo.lookup(searchText));
			} else {
				searchRegexBack(text, Migemo.lookup(searchText));
			}
		} else if(regexCheckBox.isSelected()) {
			if (ascend) {
				searchRegexForward(text, searchText);
			} else {
				searchRegexBack(text, searchText);
			}
		} else {
			if (ascend) {
				searchForward(text, searchText);
			} else {
				searchBack(text, searchText);
			}
		}
	}

	/**
	 * 前方向検索
	 * 
	 * @param text
	 * @param searchText
	 */
	private void searchForward(String text, String searchText) {
		int pos = getSelectionEnd();

		// 昇順
		int nextPosition = text.indexOf(searchText, pos);
		if (nextPosition < 0) {
			return;
		}

		textArea.setSelectionStart(nextPosition);
		textArea.setSelectionEnd(nextPosition + searchText.length());
	}

	/**
	 * 後方向検索
	 * 
	 * @param text
	 * @param searchText
	 */
	private void searchBack(String text, String searchText) {
		// 降順
		int pos = getSelectionStart();

		int lastStart = -1;
		int start = -1;

		while (true) {
			start = text.indexOf(searchText, start + 1);

			if (start >= pos || start < 0) {
				if (lastStart >= 0) {
					textArea.setSelectionStart(lastStart);
					textArea.setSelectionEnd(lastStart + searchText.length());
				}

				return;
			}

			lastStart = start;
		}
	}

	/**
	 * 正規表現前方向検索
	 * 
	 * @param text
	 * @param searchText
	 */
	private void searchRegexForward(String text, String searchText) {
		int pos = getSelectionEnd();

		Pattern p = Pattern.compile(searchText);
		Matcher matcher = p.matcher(text);
		if (!matcher.find(pos)) {
			return;
		}

		textArea.setSelectionStart(matcher.start());
		textArea.setSelectionEnd(matcher.end());
	}

	/**
	 * 後方向検索
	 * 
	 * @param text
	 * @param searchText
	 */
	private void searchRegexBack(String text, String searchText) {
		// 降順
		int pos = getSelectionStart();

		Pattern p = Pattern.compile(searchText);
		Matcher matcher = p.matcher(text);

		int lastStart = -1;
		int lastEnd = -1;
		int start = -1;

		while (true) {
			if (!matcher.find(lastStart + 1)) {
				if (lastStart < pos) {
					textArea.setSelectionStart(lastStart);
					textArea.setSelectionEnd(lastEnd);
				}
				return;
			}

			start = matcher.start();

			if (start >= pos) {
				if (lastStart >= 0) {
					textArea.setSelectionStart(lastStart);
					textArea.setSelectionEnd(lastEnd);
				}

				return;
			}

			lastStart = start;
			lastEnd = matcher.end();
		}
	}

	public int getSelectionStart() {
		int pos = textArea.getSelectionStart();

		if(scroll == null) {
			return pos;
		}
		Rectangle scrollRect = scroll.getViewport().getViewRect();
		int scrollEndPos = textArea.viewToModel(new Point(scrollRect.x + scrollRect.width, scrollRect.y + scrollRect.height));
		return pos < scrollEndPos ? pos : scrollEndPos;
	}
	
	public int getSelectionEnd() {
		int pos = textArea.getSelectionEnd();

		if(scroll == null) {
			return pos;
		}
		Rectangle scrollRect = scroll.getViewport().getViewRect();
		int scrollStartPos = textArea.viewToModel(new Point(scrollRect.x, scrollRect.y));
		return pos > scrollStartPos ? pos : scrollStartPos;
	}
	
	public void setJfd(JFD jfd) {
		this.jfd = jfd;
	}

}
