/*
 * Created on 2005/01/19
 *
 */
package com.nullfish.app.jfd2.ui.container2.components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Window;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import com.nullfish.app.jfd2.JFDComponent;
import com.nullfish.app.jfd2.ui.container2.JFDContainer;
import com.nullfish.lib.ui.ReturnableRunnable;
import com.nullfish.lib.ui.ThreadSafeUtilities;

/**
 * @author shunji
 *
 */
public class TabContainer extends JPanel implements JFDContainer {
	private JTabbedPane tabbedPane;
	
	private JFDComponent component;
	
	private String title;
	
	public static final int MAX_TITLE_LENGTH = 50;
	
	public TabContainer(JTabbedPane tabbedpane) {
		addFocusListener(new FocusListener() {

			public void focusGained(FocusEvent e) {
				component.getComponent().requestFocusInWindow();
			}

			public void focusLost(FocusEvent e) {
			}
		});

		this.tabbedPane = tabbedpane;
		setLayout(new BorderLayout());
	}
	
	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.ui.container2.JFDContainer#setComponent(java.awt.Component)
	 */
	public void setComponent(JFDComponent component) {
		removeAll();
		this.component = component;
		add(component.getComponent(), BorderLayout.CENTER);
	}

	/**
	 * コンポーネントを取得する
	 */
	public JFDComponent getComponent() {
		return component;
	}
	
	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.ui.container2.JFDContainer#clearComponent()
	 */
	public void clearComponent() {
		removeAll();
		component = null;
	}

	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.ui.container2.JFDContainer#setContainerVisible(boolean)
	 */
	public void setContainerVisible(boolean visible) {
		if(visible) {
			if(tabbedPane.indexOfComponent(this) < 0) {
				tabbedPane.addTab("", this);
			}
		} else {
			tabbedPane.remove(this);
		}
	}

	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.ui.container2.JFDContainer#setTitle(java.lang.String)
	 */
	public void setTitle(final String titleText) {
		this.title = titleText;
		
		Runnable runnable = new Runnable() {
			public void run() {
				if(title.length() > MAX_TITLE_LENGTH) {
					StringBuffer buffer = new StringBuffer(title);
					buffer.replace(7, title.length() - MAX_TITLE_LENGTH + 3 + 7, "...");
					title = buffer.toString();
				}
				
				int index = tabbedPane.indexOfComponent(TabContainer.this);
				if(index >= 0) {
					tabbedPane.setTitleAt(index, title);
				}
			}
		};
		
		ThreadSafeUtilities.executeRunnable(runnable);
	}

	/**
	 * タイトルを取得する
	 * @return タイトル
	 */
	public String getTitle() {
		ReturnableRunnable runnable = new ReturnableRunnable() {
			public void run() {
			}

			public Object getReturnValue() {
				return title;
			}
		};
		
		return (String)ThreadSafeUtilities.executeReturnableRunnable(runnable);
	}
	
	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.ui.container2.JFDContainer#dispose()
	 */
	public void dispose() {
		tabbedPane.remove(this);
		this.removeAll();
	}

	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.ui.container2.JFDContainer#requestContainerFocus()
	 */
	public void requestContainerFocus() {
		Runnable runnable = new Runnable() {
			public void run() {
				Component component = tabbedPane.getSelectedComponent();
				
				tabbedPane.setSelectedComponent(TabContainer.this);
				TabContainer.this.setVisible(true);

				if(component != null) {
					((TabContainer)component).getComponent().getComponent().requestFocusInWindow();
				}

				getComponent().getComponent().requestFocusInWindow();
				
				Window window = SwingUtilities.getWindowAncestor(TabContainer.this);
				if(!window.isActive()) {
					window.toFront();
				}
			}
		};
		
		ThreadSafeUtilities.executeRunnable(runnable);
	}

	/*
	private boolean isTabbedPaneFocusOwnerAncestor() {
		Component focusOwner = FocusManager.getCurrentManager().getFocusOwner();
		if(focusOwner == null) {
			return false;
		}
		
		Container container = focusOwner.getParent();
		while(container != null) {
			if(tabbedPane == container) {
				return true;
			}
			
			container = container.getParent()
			;
		}
		
		return false;
	}
*/
}
