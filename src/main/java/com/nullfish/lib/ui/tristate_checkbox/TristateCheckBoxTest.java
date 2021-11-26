package com.nullfish.lib.ui.tristate_checkbox;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

public class TristateCheckBoxTest {
	public static void main(String args[]) throws Exception {
		JFrame frame = new JFrame("TristateCheckBoxTest");
		frame.setLayout(new GridLayout(0, 1, 15, 15));
		UIManager.LookAndFeelInfo[] lfs = UIManager.getInstalledLookAndFeels();
		for (int i = 0; i < lfs.length; i++) {
			UIManager.LookAndFeelInfo lf = lfs[i];
			System.out.println("Look&Feel " + lf.getName());
			UIManager.setLookAndFeel(lf.getClassName());
			frame.add(makePanel(lf));
		}
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

	private static JPanel makePanel(UIManager.LookAndFeelInfo lf) {
		final TristateCheckBox tristateBox = new TristateCheckBox(
				"Tristate checkbox");
		tristateBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (tristateBox.getState() == TristateState.SELECTED) {
					System.out.println("Selected");
				}
				if (tristateBox.getState() == TristateState.DESELECTED) {
					System.out.println("Not Selected");
				}
				if (tristateBox.getState() == TristateState.INDETERMINATE) {
					System.out.println("Tristate Selected");
				}
			}
		});
		tristateBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println(e);
			}
		});
		final JCheckBox normalBox = new JCheckBox("Normal checkbox");
		normalBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println(e);
			}
		});

		final JCheckBox enabledBox = new JCheckBox("Enable", true);
		enabledBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				tristateBox.setEnabled(enabledBox.isSelected());
				normalBox.setEnabled(enabledBox.isSelected());
			}
		});

		JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
		panel.add(new JLabel(UIManager.getLookAndFeel().getName()));
		panel.add(tristateBox);
		panel.add(normalBox);
		panel.add(enabledBox);
		return panel;
	}
}
