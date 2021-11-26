/*
 * Created on 2005/01/07
 *
 */
package com.nullfish.app.jfd2.ext_command_panel;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.ext_command_panel.translators.CommandTranslator;
import com.nullfish.app.jfd2.ext_command_panel.translators.CurrentDirectoryTranslator;
import com.nullfish.app.jfd2.ext_command_panel.translators.FullPathSelectedFileTranslator;
import com.nullfish.app.jfd2.ext_command_panel.translators.MakeActiveTranslator;
import com.nullfish.app.jfd2.ext_command_panel.translators.MarkedFiles10EachTranslator;
import com.nullfish.app.jfd2.ext_command_panel.translators.MarkedFilesExecAllTranslator;
import com.nullfish.app.jfd2.ext_command_panel.translators.MarkedFilesTranslator;
import com.nullfish.app.jfd2.ext_command_panel.translators.NoDialogTranslator;
import com.nullfish.app.jfd2.ext_command_panel.translators.NoExtensionMarkedFiles10EachTranslator;
import com.nullfish.app.jfd2.ext_command_panel.translators.NoExtensionMarkedFilesExecAllTranslator;
import com.nullfish.app.jfd2.ext_command_panel.translators.NoExtensionMarkedFilesTranslator;
import com.nullfish.app.jfd2.ext_command_panel.translators.NoExtensionTranslator;
import com.nullfish.app.jfd2.ext_command_panel.translators.RootTranslator;
import com.nullfish.app.jfd2.ext_command_panel.translators.SelectedFileTranslator;

/**
 * 
 * @author shunji
 *
 */
public class MacroTranslator {
	
	private CommandTranslator[] translators = {
		new MarkedFiles10EachTranslator(),
		new MarkedFilesExecAllTranslator(),
		new MarkedFilesTranslator(),
		new NoExtensionMarkedFiles10EachTranslator(),
		new NoExtensionMarkedFilesExecAllTranslator(),
		new NoExtensionMarkedFilesTranslator(),
		new CurrentDirectoryTranslator(),
		new FullPathSelectedFileTranslator(),
		new MakeActiveTranslator(),
		new NoDialogTranslator(),
		new NoExtensionTranslator(),
		new RootTranslator(),
		new SelectedFileTranslator()
	};

	private boolean noDialog = false;
	
	private boolean makeActive = false;
	
	public String[] translate(String originalText, JFD jfd) {
		
		noDialog = originalText.indexOf("$R") != -1;
		makeActive = originalText.indexOf("$K") != -1;
		
		String[] rtn = {
			originalText
		};
		
		for(int i=0; i<translators.length; i++) {
			rtn = translators[i].translate(rtn, jfd);
		}
		
		for(int i=0; i<rtn.length; i++) {
			rtn[i] = rtn[i].trim();
		}
		
		return rtn;
	}
	
	public boolean isMakeActive() {
		return makeActive;
	}
	
	public boolean isNoDialog() {
		return noDialog;
	}
}
