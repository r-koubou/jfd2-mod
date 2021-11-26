/*
 * Created on 2005/01/13
 *
 */
package com.nullfish.app.jfd2.ext_command_panel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.nullfish.app.jfd2.Initable;
import com.nullfish.app.jfd2.JFD;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;

/**
 * @author shunji
 *
 */
public class ExternalCommandTableCellRenderer extends JPanel implements TableCellRenderer, Initable {
	private JFD jfd;
	
	private JLabel labelLabel = new JLabel();
	private JLabel padLabel = new JLabel(":");
	private JLabel textLabel = new JLabel();
	
	public ExternalCommandTableCellRenderer(JFD jfd) {
		this.jfd = jfd;
		
		setOpaque(false);
		labelLabel.setOpaque(false);
		padLabel.setOpaque(false);
		textLabel.setOpaque(false);
		
		setLayout(new GridBagLayout());
		add(labelLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		add(padLabel, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		add(textLabel, new GridBagConstraints(2, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		ExternalCommand command = (ExternalCommand)value;
		if(command == null) {
			labelLabel.setText("");
			textLabel.setText("");
		} else {
			labelLabel.setText(command.getLabel());
			textLabel.setText(command.getTitle());
		}
		return this;
	}

	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.Initable#init(com.nullfish.lib.vfs.VFile)
	 */
	public void init(VFile baseDir) throws VFSException {
		labelLabel.setForeground((Color)jfd.getCommonConfiguration().getParam("read_only_color", Color.YELLOW));
		padLabel.setForeground((Color)jfd.getCommonConfiguration().getParam("read_only_color", Color.YELLOW));
		textLabel.setForeground((Color)jfd.getCommonConfiguration().getParam("default_label_color", Color.WHITE));

		labelLabel.setFont((Font)jfd.getCommonConfiguration().getParam("label_font", new Font("Monospaced", Font.PLAIN, 12)));
		padLabel.setFont((Font)jfd.getCommonConfiguration().getParam("label_font", new Font("Monospaced", Font.PLAIN, 12)));
		textLabel.setFont((Font)jfd.getCommonConfiguration().getParam("label_font", new Font("Monospaced", Font.PLAIN, 12)));
	}

}
