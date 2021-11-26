/*
 * Created on 2004/06/11
 *
 */
package com.nullfish.app.jfd2.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nullfish.app.jfd2.dialog.components.ConfigChooserPanel;
import com.nullfish.app.jfd2.dialog.focus.FocusOrderManagingPanel;
import com.nullfish.lib.ui.ChooserPanel;
import com.nullfish.lib.ui.ThreadSafeUtilities;

/**
 * @author shunji
 * 
 * Preferences - Java - Code Style - Code Templates
 */
public class ChoosersContainerPanel extends FocusOrderManagingPanel {
	/**
	 * 名称とチューザーのマップ
	 */
	private Map nameChooserMap = new HashMap();

	/**
	 * チューザーのリスト
	 */
	private List chooserList = new ArrayList();
	
	public ChoosersContainerPanel() {
		setLayout(new GridBagLayout());
	}

	public void addChooser(String name, ConfigChooserPanel chooser) {
		super.add(chooser, new GridBagConstraints(getComponentCount(), 0, 1, 1, 1,
				1, GridBagConstraints.WEST, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 0, 0));
		super.addMember(chooser);
		nameChooserMap.put(name, chooser);
		chooserList.add(chooser);
	}
	
	public String getAnswer(String name) {
		ChooserPanel chooser = (ChooserPanel)nameChooserMap.get(name);
		if(chooser == null) {
			return null;
		}
		
		GetAnswerGetter getter = new GetAnswerGetter(chooser);
		ThreadSafeUtilities.executeRunnable(getter);
		
		return chooser.getSelectedAnswer();
	}
	
	/**
	 * チューザーの状態を取得するクラス。
	 * スレッドセーフにするための実装。
	 * 
	 * @author shunji
	 */
	private class GetAnswerGetter implements Runnable {
		private String answer;
		
		private ChooserPanel chooser;
		
		public GetAnswerGetter(ChooserPanel chooser) {
			this.chooser = chooser;
		}

		public void run() {
			answer = chooser.getSelectedAnswer();
		}
		
		public String getAnswer() {
			return answer;
		}
	}
	
	/**
	 * 変更を設定に反映する
	 *
	 */
	public void applyConfig() {
		for(int i=0; i<chooserList.size(); i++) {
			((DialogComponent)chooserList.get(i)).applyConfiguration();
		}
	}
	
	public boolean focusFirstComponent() {
		if(chooserList.size() > 0) {
			((ChooserPanel)chooserList.get(0)).requestFocusInWindow();
			return true;
		}
		
		return false;
	}
}
