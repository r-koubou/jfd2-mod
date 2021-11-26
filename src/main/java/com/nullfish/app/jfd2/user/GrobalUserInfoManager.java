package com.nullfish.app.jfd2.user;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.SwingUtilities;

import com.nullfish.app.jfd2.dialog.JFDDialog;
import com.nullfish.app.jfd2.resource.JFDResource;
import com.nullfish.lib.ui.ReturnableRunnable;
import com.nullfish.lib.ui.ThreadSafeUtilities;
import com.nullfish.lib.ui.UIUtilities;
import com.nullfish.lib.vfs.FileName;
import com.nullfish.lib.vfs.UserInfo;
import com.nullfish.lib.vfs.UserInfoManager;

public class GrobalUserInfoManager implements UserInfoManager {
	private static Map fileUserInfoMap = new HashMap();

	/**
	 * ユーザー情報を問い合わせる。
	 * @param file
	 * @param defaultInfo
	 * @return
	 */
	public UserInfo getUserInfo(final FileName fileName, final UserInfo defaultInfo) {
		UserInfo userInfo = (UserInfo)fileUserInfoMap.get(fileName);
		if(userInfo != null) {
			return userInfo;
		} else {
			ReturnableRunnable runnable = new ReturnableRunnable() {
				UserInfo rtn;
				
				public void run() {
					rtn = showDialog(fileName, defaultInfo);
				}
				
				public Object getReturnValue() {
					return rtn;
				}
			};
			
			ThreadSafeUtilities.executeReturnableRunnable(runnable);
			
			return (UserInfo)runnable.getReturnValue();
		}
	}
		
	public UserInfo showDialog(FileName fileName, UserInfo defaultInfo) {
		JFDDialog dialog = null;
		try {
			Container c = UIUtilities.getTopLevelOwner((Container)KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner());
			if(c instanceof Frame) {
				dialog = new JFDDialog((Frame)c, true, null);
			} else {
				dialog = new JFDDialog((Dialog)c, true, null);
			}
			dialog.setTitle("jFD2");
			
			dialog.addButton(JFDDialog.OK, JFDResource.LABELS.getString("ok"), 'o', true);
			dialog.addButton(JFDDialog.CANCEL, JFDResource.LABELS.getString("cancel"), 'c', false);
			
			dialog.addMessage(JFDResource.MESSAGES.getString("input_user_info"));
			
			Set keys = defaultInfo.getKeys();
			if (keys.contains(UserInfo.USER)) {
//				dialog.addLabel(JFDResource.LABELS.getString("user"));
				dialog.addTextField(UserInfo.USER, (String) defaultInfo
						.getInfo(UserInfo.USER), !keys
						.contains(UserInfo.PASSWORD), JFDResource.LABELS.getString("user"));
			}

			if (keys.contains(UserInfo.PASSWORD)) {
//				dialog.addLabel();
				dialog.addPasswordField(UserInfo.PASSWORD, (String) defaultInfo
						.getInfo(UserInfo.PASSWORD), true, JFDResource.LABELS.getString("password"));
			}
			
			dialog.pack();
			dialog.validate();
			dialog.setVisible(true);
			
			String buttonAnswer = dialog.getButtonAnswer();
			if(buttonAnswer == null || buttonAnswer.equals(JFDDialog.CANCEL)) {
				return null;
			}
			
			UserInfo rtn = new UserInfo();
			if (keys.contains(UserInfo.USER)) {
				rtn.setInfo(UserInfo.USER, dialog.getTextFieldAnswer(UserInfo.USER));
			}

			if (keys.contains(UserInfo.PASSWORD)) {
				rtn.setInfo(UserInfo.PASSWORD, dialog.getTextFieldAnswer(UserInfo.PASSWORD));
			}

			return rtn;
		} finally {
			dialog.dispose();
		}
	}

	public void noticeUserInfoCollect(UserInfo userInfo, FileName fileName) {
		fileUserInfoMap.put(fileName, userInfo);
	}

	public void noticeUserInfoIncollect(UserInfo userInfo, FileName fileName) {
		if(userInfo.equals(fileUserInfoMap.get(fileName))) {
			fileUserInfoMap.remove(fileName);
		}
	}

}
