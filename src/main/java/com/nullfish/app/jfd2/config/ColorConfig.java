package com.nullfish.app.jfd2.config;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.nullfish.app.jfd2.resource.JFDResource;

public class ColorConfig extends JPanel {
	/**
	 * 設定名
	 */
	private String configName;

	/**
	 * 色
	 */
	private Color color = Color.WHITE;

	/**
	 * デフォルト色
	 */
	private Color defaultColor;

	private JLabel label = new JLabel(" ");

	private JButton changeButton = new JButton();

	public ColorConfig(String configName, String labelName, Color defaultColor) {
		this.configName = configName;
		this.defaultColor = defaultColor;

		initGui();
		label.setText(" " + JFDResource.LABELS.getString(labelName));
		colorChanged();
	}

	private void initGui() {
		label.setOpaque(true);
		Dimension labelSize = label.getPreferredSize();
		labelSize.width = 150;
		label.setPreferredSize(labelSize);
		label.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		changeButton.setAction(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				showColorChooser(color);
			}
		});
		changeButton.setText(JFDResource.LABELS.getString("modify"));

		setLayout(new GridBagLayout());
		add(label, new GridBagConstraints(0, 0, 1, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(1,
						1, 1, 1), 0, 0));
		add(changeButton, new GridBagConstraints(1, 0, 1, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(1,
						1, 1, 1), 0, 0));
	}

	/**
	 * 設定を表示に反映する。
	 * 
	 * @param configuration
	 */
	public void setConfiguration(Configuration configuration) {
		color = (Color) configuration.getParam(configName, defaultColor);
		colorChanged();
	}

	/**
	 * 設定に反映する。
	 * 
	 * @param configuration
	 */
	public void apply(Configuration configuration) {
		configuration.setParam(configName, color);
	}

	/**
	 * 色が変更された際に呼び出される。
	 * 
	 */
	private void colorChanged() {
		label.setBackground(color);
		//label.setForeground(ColorUtility.getComplementaryColor(color));
	}

	/**
	 * 色選択ダイアログを表示する。
	 * 
	 * @param originalColor
	 */
	private void showColorChooser(Color originalColor) {
		Color choosen = JColorChooser.showDialog(this, label.getText(), color);

		if (choosen != null) {
			color = choosen;
			colorChanged();
		}
	}
}
