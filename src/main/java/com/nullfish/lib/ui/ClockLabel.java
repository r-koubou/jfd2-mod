/*
 * Created on 2004/05/24
 *
 */
package com.nullfish.lib.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.Timer;

/**
 * 時計表示ラベル
 * 
 * @author shunji
 */
public class ClockLabel extends JLabel {
	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	Timer timer;
	
	Date date = new Date();
	
	/**
	 * コンストラクタ
	 *
	 */
	public ClockLabel() {
		timer = new Timer(1000, new Updater());
		timer.start();
	}

	/**
	 * 表示フォーマットをセットする。
	 * @param formatStr
	 */
	public void setFormat(String formatStr) {
		format = new SimpleDateFormat(formatStr);
	}
	
	/**
	 * 表示更新スレッド
	 * 
	 * @author shunji
	 */
	private class Updater implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			date.setTime(System.currentTimeMillis());
			setText(format.format(date));
		}
	}
	
	/**
	 * 時計を停止する。
	 * このオブジェクトは自動では解放されないので、
	 * 必要なくなったら呼ぶこと。
	 *
	 */
	public void stop() {
		timer.stop();
	}
}
