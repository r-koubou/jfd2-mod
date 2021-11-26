/*
 * Created on 2004/08/25
 *
 */
package com.nullfish.app.jfd2.dialog;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.util.List;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.resource.JFDResource;

/**
 * ダイアログ生成ユーティリティクラス。 よく使うタイプのダイアログはこれを使うと楽。
 * 
 * @author shunji
 */
public class DialogUtilities {
	/**
	 * メッセージダイアログを表示する。
	 * 
	 * @param jfd
	 * @param message
	 *            メッセージ
	 * @param title 
	 */
	public static void showMessageDialog(JFD jfd, String message, String title) {
		String[] messages = { message };
		showMessageDialog(jfd, messages, "");
	}

	/**
	 * メッセージダイアログを表示する。
	 * 
	 * @param jfd
	 * @param messages
	 * @param title
	 *            メッセージ
	 */
	public static void showMessageDialog(JFD jfd, String[] messages, String title) {
		JFDDialog dialog = jfd.createDialog();

		dialog.addButton(JFDDialog.OK, JFDResource.LABELS.getString("ok"), 'o', true);
		dialog.setMessage(messages);
		dialog.setTitle(title);

		dialog.pack();
		dialog.setVisible(true);
		dialog.dispose();
	}

	/**
	 * メッセージダイアログを表示する。
	 * 
	 * @param jfd
	 * @param messagesList
	 * @param title
	 *            メッセージ
	 */
	public static void showMessageDialog(JFD jfd, List messagesList, String title) {
		String[] messages = new String[messagesList.size()];
		messages = (String[])messagesList.toArray(messages);
		
		showMessageDialog(jfd, messages, title);
	}

	/**
	 * テキストエリアを使用したメッセージダイアログを表示する。
	 * 
	 * @param jfd
	 * @param message
	 *            メッセージ
	 */
//	public static void showTextAreaMessageDialog(JFDView jfd, String message, boolean editable) {
//		showTextAreaMessageDialog(jfd, message, editable, "");
//	}
	
	/**
	 * テキストエリアを使用したメッセージダイアログを表示する。
	 * 
	 * @param jfd
	 * @param message
	 *            メッセージ
	 * @param title タイトル
	 */
	public static void showTextAreaMessageDialog(JFD jfd, String message, boolean editable, String title) {
		JFDDialog dialog = jfd.createDialog();
		dialog.setTitle(title);

		dialog.addButton(JFDDialog.OK, JFDResource.LABELS.getString("ok"), 'o', true);
		dialog.addButton("copy", JFDResource.LABELS.getString("copy"), 'p', true);

		dialog.addTextArea("message", message, editable);

		dialog.pack();
		dialog.setVisible(true);
		
		if("copy".equals(dialog.getButtonAnswer())) {
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
					new StringSelection(dialog.getTextFieldAnswer("message")),
					null);
		}
		
		dialog.dispose();
	}

	/**
	 * 「OK」と「キャンセル」のボタンを持つダイアログを作成する。
	 * デフォルトはOK
	 * @param jfd
	 * @return
	 */
	public static JFDDialog createOkCancelDialog(JFD jfd) {
		return createOkCancelDialog(jfd, "");
	}
	
	/**
	 * 「OK」と「キャンセル」のボタンを持つダイアログを作成する。
	 * デフォルトはOK
	 * @param jfd
	 * @return
	 */
	public static JFDDialog createOkCancelDialog(JFD jfd, String title) {
		JFDDialog dialog = jfd.createDialog();

		dialog.setTitle(title);
		
		dialog.addButton(JFDDialog.OK, JFDResource.LABELS.getString("ok"), 'o', true);
		dialog.addButton(JFDDialog.CANCEL, JFDResource.LABELS.getString("cancel"), 'c', false);
		
		return dialog;
	}

	/**
	 * 「Yes」と「No」のボタンを持つダイアログを作成する。
	 * @param jfd
	 * @return
	 */
	public static JFDDialog createYesNoDialog(JFD jfd, String defaultValue) {
		return createYesNoDialog(jfd, defaultValue, "");
	}
	
	/**
	 * 「Yes」と「No」のボタンを持つダイアログを作成する。
	 * @param jfd
	 * @return
	 */
	public static JFDDialog createYesNoDialog(JFD jfd, String defaultValue, String title) {
		JFDDialog dialog = jfd.createDialog();
		
		dialog.setTitle(title);

		dialog.addButton(JFDDialog.YES, JFDResource.LABELS.getString("ok"), 'y', JFDDialog.YES.equals(defaultValue));
		dialog.addButton(JFDDialog.NO, JFDResource.LABELS.getString("cancel"), 'n', !JFDDialog.YES.equals(defaultValue));
		
		return dialog;
	}
}
