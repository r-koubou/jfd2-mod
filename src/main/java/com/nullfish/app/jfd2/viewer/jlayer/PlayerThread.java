/*
 * Created on 2005/01/25
 *
 */
package com.nullfish.app.jfd2.viewer.jlayer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.lib.vfs.exception.VFSException;

/**
 * MP3再生スレッド
 * 
 * @author shunji
 */
public class PlayerThread extends Thread {

	private AdvancedPlayer player;

	private StringBuffer buffer = new StringBuffer();

	private final StreamFactory[] files;
	
	private int position = 0;

	private final JFD jfd;

	private boolean stopped = false;

	private Timer timer = null;
	
	private JLayerViewer owner;
	
	public PlayerThread(StreamFactory[] files, JFD jfd, JLayerViewer owner) {
		this.files = files;
		this.jfd = jfd;
		this.owner = owner;
	}

	public void run() {
		for (int i = 0; i < files.length && !isStopped(); i++) {
			position = i;
			try {
				final StreamFactory file = files[i];
				player = new AdvancedPlayer(files[i].getInputStream());

				timer = new Timer(200, new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if (player != null && jfd != null) {
							buffer.setLength(0);
							
							if(files.length > 1) {
								buffer.append("(");
								buffer.append(getPosition() + 1);
								buffer.append("/");
								buffer.append(files.length);
								buffer.append(") ");
							}
							
							buffer.append(file.getName());
							buffer.append(" playing ");

							int time = player.getPosition() / 1000;
							buffer.append((time / 60 > 9 ? Integer
									.toString(time / 60) : "0" + time / 60));
							buffer.append(':');
							buffer.append((time % 60 > 9 ? Integer
									.toString(time % 60) : "0" + time % 60));

							jfd.setMessage(buffer.toString());
						}
					}
				});
				timer.start();

				player.setPlayBackListener(new PlaybackListener() {
					public void playbackFinished(PlaybackEvent e) {
						timer.stop();
						jfd.setMessage("");
					}
				});

				player.play();
			} catch (JavaLayerException e) {
				close();
			} catch (VFSException e) {
				close();
			} finally {
				if(player != null) {
					player.close();
				}
			}
		}
		
		owner.closeFromPlayer(this);
	}

	private synchronized boolean isStopped() {
		return stopped;
	}
	
	public synchronized void goNext(boolean stops) {
		if(stops) {
			stopped = true;
		}
		
		if(player != null) {
			player.close();
		}
		
		if(timer != null) {
			timer.stop();
			jfd.setMessage("");
		}
	}
	
	public synchronized void close() {
		goNext(true);
	}
	
	/**
	 * ファイル数を取得する。
	 * @return
	 */
	public int getSize() {
		return files.length;
	}
	
	/**
	 * 現在演奏中のファイルインデックスを取得する。
	 * 
	 * @return
	 */
	public int getPosition() {
		return position;
	}
}
