/*
 * Created on 2004/05/08
 *
 */
package com.nullfish.lib.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.UIManager;

import org.jdom.Document;
import org.jdom.input.SAXBuilder;

import com.nullfish.lib.ui.grid.CrossLineGrid;
import com.nullfish.lib.ui.grid.HorizontalLineGrid;
import com.nullfish.lib.ui.grid.VerticalLineGrid;
import com.nullfish.lib.ui.list_table.ListCursorModel;
import com.nullfish.lib.ui.list_table.ListTable;
import com.nullfish.lib.ui.list_table.ListTableModel;
import com.nullfish.lib.ui.xml_menu.XMLMenuBar;

/**
 * @author shunji
 *
 */
public class GuiTest {
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			ListModel model = new AbstractListModel() {
				public Object getElementAt(int index) {
					return new Integer(index);
				}
				
				public int getSize() {
					return 200;
				}
			};
			
			final ListTable table = new ListTable();
			table.setListModel(model);
			table.setColumnCount(2);
			table.setRowMargin(0);
			table.setShowGrid(false);
			
			table.setColumnCount(3);
			table.setShowGrid(false);
			
			JFrame frame = new JFrame();
			JPanel panel = new JPanel(new GridBagLayout());
			
			JButton backButton = new JButton(new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					ListTableModel ltm = table.getListTableModel();
					ListCursorModel cursorModel = ltm.getListCursorModel();
					cursorModel.setSelectedIndex(cursorModel.getSelectedIndex() - (ltm.getColumnCount() * ltm.getRowCount()));
				}
			});
			backButton.setText("<<");
	
			JButton nextButton = new JButton(new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					ListTableModel ltm = table.getListTableModel();
					ListCursorModel cursorModel = ltm.getListCursorModel();
					cursorModel.setSelectedIndex(cursorModel.getSelectedIndex()
							+ (ltm.getColumnCount() * ltm.getRowCount()));
				}
			});
			nextButton.setText(">>");
			
			JButton downButton = new JButton(new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					table.setSelectedIndex(table.getSelectedIndex() + 1);
				}
			});
			downButton.setText("down");
	
			JButton upButton = new JButton(new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					table.setSelectedIndex(table.getSelectedIndex() - 1);
				}
			});
			upButton.setText("up");
	

			JPanel buttonPanel = new JPanel();
			buttonPanel.add(backButton);
			buttonPanel.add(nextButton);
			buttonPanel.add(upButton);
			buttonPanel.add(downButton);
			
			CrossLineGrid topLeft = new CrossLineGrid();
			topLeft.setShowRight(true);
			topLeft.setShowDown(true);
			panel.add(topLeft,new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			
			panel.add(new HorizontalLineGrid(),new GridBagConstraints(1, 0, 3, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
	
			CrossLineGrid topRight = new CrossLineGrid();
			topRight.setShowLeft(true);
			topRight.setShowDown(true);
			panel.add(topRight,new GridBagConstraints(4, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			
			panel.add(new VerticalLineGrid(),new GridBagConstraints(0, 1, 1, 1, 0, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
	
			panel.add(table, new GridBagConstraints(1, 1, 3, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
	
			panel.add(new VerticalLineGrid(),new GridBagConstraints(4, 1, 1, 1, 0, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			
			CrossLineGrid middleLeft = new CrossLineGrid();
			middleLeft.setShowUp(true);
			middleLeft.setShowRight(true);
			middleLeft.setShowDown(true);
			panel.add(middleLeft,new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			
			panel.add(new HorizontalLineGrid(),new GridBagConstraints(1, 2, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
	
			CrossLineGrid middleMiddle = new CrossLineGrid();
			middleMiddle.setShowLeft(true);
			middleMiddle.setShowRight(true);
			middleMiddle.setShowDown(true);
			panel.add(middleMiddle,new GridBagConstraints(2, 2, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
	
			panel.add(new HorizontalLineGrid(),new GridBagConstraints(3, 2, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
	
			CrossLineGrid middleRight = new CrossLineGrid();
			middleRight.setShowLeft(true);
			middleRight.setShowUp(true);
			middleRight.setShowDown(true);
			panel.add(middleRight,new GridBagConstraints(4, 2, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
	
			panel.add(new VerticalLineGrid(),new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			
			panel.add(buttonPanel, new GridBagConstraints(1, 3, 2, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
	
			panel.add(new VerticalLineGrid(),new GridBagConstraints(2, 3, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
	
	
			panel.add(new VerticalLineGrid(),new GridBagConstraints(4, 3, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
	
			CrossLineGrid bottomLeft = new CrossLineGrid();
			bottomLeft.setShowRight(true);
			bottomLeft.setShowUp(true);
			panel.add(bottomLeft,new GridBagConstraints(0, 4, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
	
			panel.add(new HorizontalLineGrid(),new GridBagConstraints(1, 4, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			
			CrossLineGrid bottomMiddle = new CrossLineGrid();
			bottomMiddle.setShowLeft(true);
			bottomMiddle.setShowRight(true);
			bottomMiddle.setShowUp(true);
			panel.add(bottomMiddle,new GridBagConstraints(2, 4, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
	
			panel.add(new HorizontalLineGrid(),new GridBagConstraints(3, 4, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
	
			CrossLineGrid bottomRight = new CrossLineGrid();
			bottomRight.setShowLeft(true);
			bottomRight.setShowUp(true);
			panel.add(bottomRight,new GridBagConstraints(4, 4, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
	
			//LineGrid.setGroupDoubleLine(false, null);
			frame.getContentPane().add(panel);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
			Document doc = new SAXBuilder().build(new File("d:\\test\\menu.xml"));
			XMLMenuBar menuBar = new XMLMenuBar();
			menuBar.convertFromNode(doc.getRootElement());
			
			frame.setJMenuBar(menuBar);
			
			frame.pack();
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
