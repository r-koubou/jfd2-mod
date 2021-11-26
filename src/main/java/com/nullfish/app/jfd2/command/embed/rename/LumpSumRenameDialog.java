package com.nullfish.app.jfd2.command.embed.rename;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import com.nullfish.app.jfd2.resource.JFDResource;
import com.nullfish.lib.keymap.KeyStrokeMap;

public class LumpSumRenameDialog extends JDialog {
	private LumpSumRenamer renamer;

	private JTable table = new JTable();

	private JScrollPane scroll = new JScrollPane(table);

	private LumpSumRenameTableModel model;

	private JButton okButton = new JButton(JFDResource.LABELS.getString("ok")
			+ "(O)");

	private JButton cancelButton = new JButton(JFDResource.LABELS
			.getString("cancel")
			+ "(C)");

	private JPanel mainPanel = new JPanel(new BorderLayout());

	private JLabel messageLabel = new JLabel(" ");

	private int result = -1;

	public static final int OK = 0;

	public static final int CANCEL = 1;

	public LumpSumRenameDialog(Dialog dialog, LumpSumRenamer renamer) {
		super(dialog, true);
		this.renamer = renamer;
		
		init();
	}
	
	public LumpSumRenameDialog(Frame frame, LumpSumRenamer renamer) {
		super(frame, true);
		this.renamer = renamer;
		
		init();
	}
	
	private void init() {
		model = new LumpSumRenameTableModel(renamer);

		table.setModel(model);
		table.getColumnModel().getColumn(1).setCellEditor(
				new VFileRenameCellEditor());

		initGui();
		updateMessage();
	}

	private void initGui() {
		model.addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent e) {
				updateMessage();
			}
		});

		setTitle(JFDResource.LABELS.getString("title_rename"));

		table.getColumnModel().getColumn(0).setCellRenderer(
				new FileRenameTableCellRenderer(renamer));
		table.getColumnModel().getColumn(1).setCellRenderer(
				new FileRenameTableCellRenderer(renamer));
		table.getColumnModel().setSelectionModel(
				new DefaultListSelectionModel() {
					public void setSelectionInterval(int index0, int index1) {
						super.setSelectionInterval(1, 1);
					}
				});
		table.setColumnSelectionAllowed(false);
		table.setRowSelectionAllowed(false);

		scroll
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		okButton.setMnemonic('o');
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!renamer.isAllOK()) {
					JOptionPane.showMessageDialog(LumpSumRenameDialog.this,
							JFDResource.MESSAGES
									.getString("message_repeated_filename"),
							JFDResource.LABELS.getString("title_rename"),
							JOptionPane.OK_OPTION);
					return;
				}

				result = OK;
				setVisible(false);
				dispose();
			}
		});

		cancelButton.setMnemonic('c');
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancel();
			}
		});

		mainPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStrokeMap.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancel");
		mainPanel.getActionMap().put("cancel", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				cancel();
			}
		});

		setContentPane(mainPanel);
		mainPanel.add(messageLabel, BorderLayout.NORTH);
		mainPanel.add(scroll, BorderLayout.CENTER);

		JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonsPanel.add(okButton);
		buttonsPanel.add(cancelButton);
		mainPanel.add(buttonsPanel, BorderLayout.SOUTH);
	}

	private void cancel() {
		result = CANCEL;
		setVisible(false);
		dispose();
	}

	public int getResult() {
		return result;
	}
	
	private void updateMessage() {
		messageLabel
		.setText(LumpSumRenameDialog.this.renamer.isAllOK() ? " "
				: JFDResource.MESSAGES
						.getString("message_repeated_filename"));
		okButton.setEnabled(LumpSumRenameDialog.this.renamer.isAllOK());
	}
}
