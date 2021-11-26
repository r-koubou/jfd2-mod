package com.nullfish.app.jfd2.util;

// deprecated
/*
import com.apple.eawt.Application;
import com.apple.eawt.ApplicationAdapter;
import com.apple.eawt.ApplicationEvent;
import com.nullfish.app.jfd2.ui.container2.NumberedJFD2;
*/

/**
 * @deprecated macOS 10.15 Catalinaから Apple Java VM のインストールができないため com.apple.* パッケージのAPIは使用できないため、コードをカットしています。
 */
public class MacUtil {
	public static void initShutDown() {
// deprecated
/*
		// MacのCommand+Qを無効にする
        Application app = Application.getApplication();
        app.addApplicationListener(new ApplicationAdapter() {
            public void handleQuit(ApplicationEvent e) {
                e.setHandled(false);
                NumberedJFD2.getActiveJFD().getCommandManager().execute("exit_all");
            }
        });
*/
	}
}
