/*
 * Created on 2004/08/29
 *
 */
package com.nullfish.app.jfd2;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.Timer;

import com.nullfish.lib.vfs.Manipulation;
import com.nullfish.lib.vfs.ManipulationAdapter;
import com.nullfish.lib.vfs.ManipulationEvent;

/**
 * ファイル操作時のメッセージを画面下部に表示するクラス。
 * 
 * @author shunji
 */
public class ManipulationMessageUpdater {
	/**
	 * 監視操作
	 */
	private Manipulation manipulation;
	
	/**
	 * 更新するjFD
	 */
	private JFD jfd;
	
	private Timer timer;
	
	DecimalFormat format = new DecimalFormat("##,###,###,##0");

	private StringBuffer messageBuffer = new StringBuffer();
	
	/**
	 * コンストラクタ
	 * @param jfd	更新対象jFD
	 */
	public ManipulationMessageUpdater(JFD jfd) {
		this.jfd = jfd;
	}
	
	public synchronized void setManipulation(final Manipulation manipulation) {
		this.manipulation = manipulation;
		if(manipulation == null) {
			return;
		}
		manipulation.addManipulationListener(new ManipulationAdapter() {
			public void finished(ManipulationEvent e) {
				if(manipulation == getManipulation()) {
					setManipulation(null);
				}
			}
		});
		
		if(timer == null) {
			timer = new Timer(100, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					apply();
				}
			});
			
			timer.start();
		}
	}
	
	private synchronized Manipulation getManipulation() {
		return manipulation;
	}
	
	private void apply() {
		if(manipulation == null) {
			jfd.setMessage("");

			if(manipulation == null) {
				timer.stop();
				timer = null;
			}
			
			return;
		}
		
		Manipulation current = manipulation;
//		Manipulation current = manipulation.getCurrentManipulation();
		if(current == null) {
			jfd.setMessage("");
			return;
		}
		
		try {
			messageBuffer.setLength(0);
			messageBuffer.append(current.getProgressMessage());
	
			long max = current.getProgressMax();
			long progress = current.getProgress();
			
			if(max != Manipulation.PROGRESS_INDETERMINED
				&& progress != Manipulation.PROGRESS_INDETERMINED) {
				messageBuffer.append(' ');
				messageBuffer.append(format.format(progress));
				messageBuffer.append("/");
				messageBuffer.append(format.format(max));
				
				if(max/100 != 0) {
					messageBuffer.append(" (");
					messageBuffer.append(progress / (max / 100));
					messageBuffer.append("%)");
				}
			}
				
			jfd.setMessage(messageBuffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
