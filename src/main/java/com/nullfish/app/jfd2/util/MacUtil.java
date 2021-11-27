package com.nullfish.app.jfd2.util;

import java.awt.Desktop;
import java.awt.desktop.QuitEvent;
import java.awt.desktop.QuitHandler;
import java.awt.desktop.QuitResponse;
import com.nullfish.app.jfd2.ui.container2.NumberedJFD2;

public class MacUtil {
	public static void initShutDown() {
        if(!Desktop.isDesktopSupported​()){
            return;
        }

        Desktop desktop = Desktop.getDesktop();
        desktop.disableSuddenTermination​();
        desktop.setQuitHandler​(new QuitHandler() {
            public void handleQuitRequestWith​(QuitEvent e, QuitResponse response) {
                response.cancelQuit​();
                NumberedJFD2.getActiveJFD().getCommandManager().execute("exit_all");
            }
        });
	}
}
