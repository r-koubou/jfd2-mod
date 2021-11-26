package com.nullfish.app.jfd2.ext_command.window;

import java.io.IOException;
import java.io.Writer;

import com.nullfish.app.jfd2.ext_command.ProcessReader;
import com.nullfish.app.jfd2.ext_command.StreamReaderThread;

public class WindowProcessReader implements ProcessReader {
    public static final int READER_ID = 3;

    private boolean showsAutomatic;

    private static final WindowProcessReader instance = new WindowProcessReader();
	
	public WindowProcessReader() {
	}
	
	public static WindowProcessReader getInstance() {
		return instance;
	}
	
	public void addProcess(Process process) {
		StreamReaderThread outReaderThread = new StreamReaderThread(process
				.getInputStream(), new TextAreaWriter());
		outReaderThread.start();
		StreamReaderThread errReaderThread = new StreamReaderThread(process
				.getErrorStream(), new TextAreaWriter());
		errReaderThread.start();
	}

	public int getReaderID() {
		return READER_ID;
	}

	public void showConsole(boolean visible) {
		ConsoleFrame.getInstance().setVisible(visible);
	}

	private class TextAreaWriter extends Writer {
		
		
		public TextAreaWriter() {
		}

		public void write(char[] cbuf, int off, int len) throws IOException {
			if(showsAutomatic && !ConsoleFrame.getInstance().isVisible()) {
				showConsole(true);
			}
			
			String str = new String(cbuf, off, len);
			ConsoleFrame.getInstance().print(str);
		}

		public void flush() throws IOException {
		}

		public void close() throws IOException {
		}
	}

	public void setShowsAutomatic(boolean shows) {
		this.showsAutomatic = shows;
	}
}
