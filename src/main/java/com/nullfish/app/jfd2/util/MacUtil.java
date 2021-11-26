package com.nullfish.app.jfd2.util;

import com.apple.eawt.Application;
import com.apple.eawt.ApplicationAdapter;
import com.apple.eawt.ApplicationEvent;
import com.nullfish.app.jfd2.ui.container2.NumberedJFD2;

public class MacUtil {
	public static void initShutDown() {
		// MacのCommand+Qを無効にする
        Application app = Application.getApplication();
        app.addApplicationListener(new ApplicationAdapter() {
            public void handleQuit(ApplicationEvent e) {
                e.setHandled(false);
                NumberedJFD2.getActiveJFD().getCommandManager().execute("exit_all");
            }
        });
	}
}
