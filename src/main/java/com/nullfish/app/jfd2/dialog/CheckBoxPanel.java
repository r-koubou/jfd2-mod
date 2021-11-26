/*
 * Created on 2004/06/06
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

import javax.swing.JCheckBox;

import com.nullfish.app.jfd2.dialog.components.ConfigCheckBox;
import com.nullfish.app.jfd2.dialog.focus.FocusOrderManagingPanel;
import com.nullfish.lib.ui.ReturnableRunnable;
import com.nullfish.lib.ui.ThreadSafeUtilities;

/**
 * チェックボックスを保持するパネル。
 * n行2列で順番にチェックボックスを配置する。
 * 
 * @author shunji
 */
public class CheckBoxPanel extends FocusOrderManagingPanel {
	/**
	 * 名称とチェックボックスのマップ
	 */
	Map nameCheckBoxMap = new HashMap();

	/**
	 * チェックボックスのリスト
	 */
	List checkBoxList = new ArrayList();
	
	/**
	 * コンストラクタ
	 *
	 */
	public CheckBoxPanel() {
		setLayout(new GridBagLayout());
	}

	/**
	 * チェックボックスを追加する。
	 * 
	 * @param name
	 * @param label
	 * @param mnemonic
	 * @param defaultValue
	 */
	public void addCheckBox(String name, ConfigCheckBox checkBox) {
		int index = nameCheckBoxMap.size();
		this.add(checkBox, new GridBagConstraints(index % 2, index / 2, 1, 1,
				1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 0, 0));
		super.addMember(checkBox);
		nameCheckBoxMap.put(name, checkBox);
		checkBoxList.add(checkBox);
	}

	/**
	 * 指定名称のチェックボックスがチェックされているならtrueを返す。
	 * スレッドセーフ。
	 * @param name
	 * @return
	 */
	public boolean isChecked(String name) {
		final JCheckBox checkBox = (JCheckBox)nameCheckBoxMap.get(name);
		
		ReturnableRunnable runnable = new ReturnableRunnable() {
			private boolean checked;
			
			public void run() {
				checked = checkBox.isSelected();
			}
			
			public Object getReturnValue() {
				return Boolean.valueOf(checked);
			}
		};
		
		ThreadSafeUtilities.executeReturnableRunnable(runnable);

		return ((Boolean)runnable.getReturnValue()).booleanValue();
	}
	
	/**
	 * 変更を設定に反映する。
	 *
	 */
	public void applyConfig() {
		for(int i=0; i<checkBoxList.size(); i++) {
			((DialogComponent)checkBoxList.get(i)).applyConfiguration();
		}
	}
	
	public boolean focusFirstComponent() {
		if(checkBoxList.size() > 0) {
			((ConfigCheckBox)checkBoxList.get(0)).requestFocusInWindow();
			return true;
		}
		
		return false;
	}
}
