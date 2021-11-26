/*
 * Created on 2005/01/23
 *
 */
package com.nullfish.app.jfd2.viewer.jlayer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.viewer.AbstractFileViewer;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;

/**
 * JLayerを使ったMP3プレイヤー
 * 
 * @author shunji
 */
public class JLayerViewer extends AbstractFileViewer {
	private PlayerThread playerThread;
	
	private VFile playingFile;
	
	public static final String MP3 = "mp3";

	public static final String M3U = "m3u";
	
	public static final String PLS = "pls";
	
	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.viewer.FileViewer#open(com.nullfish.lib.vfs.VFile, com.nullfish.app.jfd2.JFDView)
	 */
	public synchronized void doOpen(VFile file, JFD jfd) {
		if(file.equals(playingFile) && playerThread != null && playerThread.getPosition() != playerThread.getSize() - 1) {
			playerThread.goNext(false);
			return;
		}
		
		close();
		
		String extension = file.getFileName().getExtension().toLowerCase();
		
		StreamFactory[] files = null;
		if(extension.equals(MP3)) {
			files = new StreamFactory[1];
			files[0] = new VFileStreamFactory( file );
		} else if(extension.equals(M3U)) {
			files = m3uToFiles(file);
		} else if(extension.equals(PLS)) {
			files = plsToFiles(file);
		}
		
		playingFile = file; 
		playerThread = new PlayerThread(files, jfd, this);
		playerThread.start();
	}

	public synchronized void closeFromPlayer(PlayerThread caller) {
		if(playerThread == caller) {
			close();
		}
	}
	
	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.viewer.FileViewer#close()
	 */
	public synchronized void doClose() {
		if(playerThread != null) {
			playerThread.close();
			playerThread = null;
		}
		playingFile = null;
	}

	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.viewer.FileViewer#dispose()
	 */
	public void dispose() {
		close();
	}
	
	/**
	 * M3Uファイルを解釈してVFileの配列を返す。
	 * 
	 * @param m3uFile
	 * @return
	 */
	private StreamFactory[] m3uToFiles(VFile m3uFile) {
		List files = new ArrayList();
		BufferedReader reader = null;
		
		try {
			reader = new BufferedReader(new InputStreamReader(m3uFile.getInputStream()));
			
			String line = null;
			while((line = reader.readLine()) != null) {
				if(!line.startsWith("#")) {
					VFile f = null;
					
					try {
						f = m3uFile.getFileSystem().getVFS().getFile(line);
					} catch (VFSException e) {
						f = null;
					}
					
					if(f == null) {
						try {
							f = m3uFile.getParent().getRelativeFile(line);
						} catch (VFSException e) {
							f = null;
						}
					}
					
					if(f != null) {
						files.add(new VFileStreamFactory( f ));
					}
				}
			}
			
			StreamFactory[] rtn = new StreamFactory[files.size()];
			return (StreamFactory[])files.toArray(rtn);
		} catch (Exception e) {
			e.printStackTrace();
			return new StreamFactory[0];
		} finally {
			try {
				reader.close();
			} catch (Exception e) {
			}
		}
	}
	
	private StreamFactory[] plsToFiles(VFile plsFile) {
		List files = new ArrayList();
		
		Properties properties = new Properties();
		
		try {
			properties.load(plsFile.getInputStream());
			String entriesStr = properties.getProperty("NumberOfEntries");
			if(entriesStr == null || entriesStr.length() == 0) {
				entriesStr = properties.getProperty("numberofentries");
			}
			
			int length = Integer.parseInt(entriesStr);
			
			for(int i=0; i<length; i++) {
				URL url = new URL(properties.getProperty("File" + (i + 1)));
				String title = properties.getProperty("Title" + (i + 1));
				StreamFactory file = new UrlStreamFactory(url, title);
				files.add(file);
			}
			
			StreamFactory[] rtn = new StreamFactory[files.size()];
			return (StreamFactory[])files.toArray(rtn);
		} catch (Exception e) {
			e.printStackTrace();
			return new StreamFactory[0];
		}
	}
}
