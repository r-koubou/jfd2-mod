package com.nullfish.app.jfd2.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.nullfish.app.jfd2.Launcher;
import com.nullfish.app.jfd2.ui.container2.JFDFrame;
import com.nullfish.app.jfd2.util.CommandLineParameters;
import com.nullfish.lib.vfs.VFile;

/**
 * jFD2サーバー。 常駐してオープン、クローズを管理する。
 * 
 * @author shunji
 * 
 */
public class JFD2Server {
	private VFile configDir;

	private ServerSocket serverSocket;
	
	private boolean stopped = false;

	public static final int PORT = 40960;

	public static final String QUIT = "-quit";

	private ListenerThread listener;
	
	/**
	 * コンストラクタ
	 * 
	 * @param configDir
	 */
	public JFD2Server(VFile configDir) {
		this.configDir = configDir;
	}

	public void start() {
		try {
			serverSocket = new ServerSocket(PORT);
			listener = new ListenerThread();
			listener.start();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	private void receive() {
		Socket socket = null;

		try {
			socket = serverSocket.accept();
			ReceiverThread thread = new ReceiverThread(socket);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		}

	}

	private class ListenerThread extends Thread {
		public void run() {
			while(!stopped) {
				Socket socket = null;

				try {
					socket = serverSocket.accept();
					ReceiverThread thread = new ReceiverThread(socket);
					thread.start();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	private class ReceiverThread extends Thread {
		Socket socket;

		public ReceiverThread(Socket socket) {
			this.socket = socket;
		}

		public void run() {
			BufferedReader reader = null;

			try {
				reader = new BufferedReader(new InputStreamReader(socket
						.getInputStream()));
				String line = reader.readLine();

				CommandLineParameters param = new CommandLineParameters(
						spritParamater(line));

				if (param.getParameter(QUIT) != null) {
					quit();
				}

				try {
					Launcher.openJFD(param, configDir);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
			}
		}
	}

	private String[] spritParamater(String param) {
		boolean inQuate = false;

		List commands = new ArrayList();
		StringBuffer buffer = new StringBuffer();

		for (int i = 0; i < param.length(); i++) {
			char c = param.charAt(i);

			if (c == ' ' && !inQuate && buffer.length() > 0) {
				commands.add(buffer.toString());
				buffer.setLength(0);

				continue;
			}

			if (c == '\"') {
				inQuate = !inQuate;
				continue;
			}

			buffer.append(c);
		}

		if (buffer.length() > 0) {
			commands.add(buffer.toString());
		}

		return (String[]) commands.toArray(new String[0]);
	}

	/**
	 * 終了処理
	 * 
	 */
	private void quit() {
		while (JFDFrame.getInstanceCount() > 0) {
			JFDFrame.getInstance(0).dispose();
		}

		System.exit(0);
	}
}
