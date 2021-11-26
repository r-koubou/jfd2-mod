/*
 * Created on 2005/01/13
 *
 */
package com.nullfish.app.jfd2.ext_command_panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;

import com.nullfish.app.jfd2.Initable;
import com.nullfish.app.jfd2.JFD;
import com.nullfish.lib.ui.grid.CrossLineGrid;
import com.nullfish.lib.ui.grid.HorizontalLineGrid;
import com.nullfish.lib.ui.grid.VerticalLineGrid;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;

/**
 * @author shunji
 *
 */
public class ExternalCommandPanel extends JPanel implements Initable {
	private JFD jfd;
	
	private VerticalLineGrid leftGrid;
	private VerticalLineGrid rightGrid;
	private HorizontalLineGrid topGrid;
	private HorizontalLineGrid bottomGrid;
	private CrossLineGrid topLeftGrid;
	private CrossLineGrid topRightGrid;
	private CrossLineGrid bottomLeftGrid;
	private CrossLineGrid bottomRightGrid;
	
	private ExternalCommandTable table;
	
	public ExternalCommandPanel(JFD jfd) {
		this.jfd = jfd;
		
		initGui();
	}
	
	private void initGui() {
		setLayout(new GridBagLayout());
		
		table = new ExternalCommandTable(jfd);
		table.setFocusable(false);
		
		leftGrid = new VerticalLineGrid(jfd);
		rightGrid = new VerticalLineGrid(jfd);
		topGrid = new HorizontalLineGrid(jfd);
		bottomGrid = new HorizontalLineGrid(jfd);

		topLeftGrid = new CrossLineGrid(jfd);
		topLeftGrid.setShowDown(true);
		topLeftGrid.setShowRight(true);

		topRightGrid = new CrossLineGrid(jfd);
		topRightGrid.setShowDown(true);
		topRightGrid.setShowLeft(true);

		bottomLeftGrid = new CrossLineGrid(jfd);
		bottomLeftGrid.setShowUp(true);
		bottomLeftGrid.setShowRight(true);

		bottomRightGrid = new CrossLineGrid(jfd);
		bottomRightGrid.setShowUp(true);
		bottomRightGrid.setShowLeft(true);
		
		add(topLeftGrid, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		add(topGrid, new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		add(topRightGrid, new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		
		add(leftGrid, new GridBagConstraints(0, 1, 1, 1, 0, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		add(table, new GridBagConstraints(1, 1, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		add(rightGrid, new GridBagConstraints(2, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		
		add(bottomLeftGrid, new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		add(bottomGrid, new GridBagConstraints(1, 2, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		add(bottomRightGrid, new GridBagConstraints(2, 2, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		
		setMinimumSize(new Dimension(300, getPreferredSize().height));
	}
	
	public void increaseSet() {
		table.increaseSetNumber();
	}
	
	public void initSet() {
		table.initSetNumber();
	}

	public int getSet() {
		return table.getSetNumber();
	}

	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.Initable#init(com.nullfish.lib.vfs.VFile)
	 */
	public void init(VFile baseDir) throws VFSException {
		ExternalCommandManager.getInstance().init(baseDir);
		setBackground((Color) jfd.getCommonConfiguration().getParam("background_color", Color.BLACK));
		
		table.init(baseDir);
	}
}
