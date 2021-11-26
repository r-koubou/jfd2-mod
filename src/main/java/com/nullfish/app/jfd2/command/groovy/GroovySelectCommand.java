/*
 * Created on 2004/07/06
 *
 */
package com.nullfish.app.jfd2.command.groovy;

import groovy.lang.GroovyClassLoader;
import groovy.lang.Script;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.groovy.control.CompilationFailedException;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.dialog.DialogUtilities;
import com.nullfish.app.jfd2.dialog.JFDDialog;
import com.nullfish.app.jfd2.resource.JFDResource;
import com.nullfish.lib.vfs.VFS;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.ManipulationStoppedException;
import com.nullfish.lib.vfs.exception.VFSException;

/**
 * @author shunji
 *
 */
public class GroovySelectCommand extends AbstractGroovyCommand {
	public static final String CONFIG_ENCODING = "script_encoding";
	
	private static Map fileScriptCache = new HashMap();
	
	/* (non-Javadoc)
	 * @see com.nullfish.lib.vfs.manipulation.abst.AbstractManipulation#doExecute()
	 */
	public void doExecute() throws VFSException {
		InputStream is = null;
		JFDDialog dialog = null;
		try {
			VFS vfs = VFS.getInstance(getJFD());
			JFD jfd = getJFD();
			String scriptDirPath = (String)jfd.getCommonConfiguration().getParam(SCRIPT_DIR_PATH, DEFAULT_SCRIPT_DIR);
			VFile scriptDir = vfs.getFile(scriptDirPath);
			if(!scriptDir.exists(this)) {
				scriptDir.createDirectory(this);
			}
			VFile[] files = scriptDir.getChildren(this);
			Arrays.sort(files,  new Comparator() {
				public int compare(Object arg0, Object arg1) {
					VFile file1 = (VFile)arg0;
					VFile file2 = (VFile)arg1;
					return file1.getName().compareTo(file2.getName());
				}
			});
			String[] scripts = new String[files.length];
			for(int i=0; i<files.length; i++) {
				scripts[i] = files[i].getName();
			}
			
			dialog = DialogUtilities.createOkCancelDialog(jfd);
			dialog.addButton(OPEN_SCRIPT_DIR, JFDResource.LABELS.getString("open_script_dir"), 'p', false);
			
			dialog.addMessage(JFDResource.MESSAGES.getString("message_select_script"));
			
			String lastScript = (String)jfd.getLocalConfiguration().getParam("last_script", null);
			dialog.addComboBox(SCRIPT, scripts, lastScript, false, true, null);

			List encodeList = (List)jfd.getCommonConfiguration().getParam("grep_encode_all", null);
			dialog.addComboBox(CONFIG_ENCODING, encodeList, (String)jfd.getCommonConfiguration().getParam(CONFIG_ENCODING, "UTF-8"), false, false, null);
			
			dialog.pack();
			dialog.setVisible(true);
			
			String answer = dialog.getButtonAnswer();
			String scriptName = dialog.getTextFieldAnswer(SCRIPT);
			if(OPEN_SCRIPT_DIR.equals(answer)) {
				if(!scriptDir.exists(this)) {
					scriptDir.createDirectory(this);
				}
				
				jfd.getModel().setDirectory(scriptDir, 0);
				return;
			}
			
			if (answer == null || JFDDialog.CANCEL.equals(answer) || scriptName == null
					|| scriptName.length() == 0) {
				return;
			}
			
			jfd.getLocalConfiguration().setParam("last_script", scriptName);
			VFile scriptFile = scriptDir.getChild(scriptName);

			String encoding = dialog.getTextFieldAnswer(CONFIG_ENCODING);
			jfd.getCommonConfiguration().setParam(CONFIG_ENCODING, encoding);

			CacheKey key = new CacheKey();
			key.file = scriptFile;
			key.encoding = encoding;
			
			Cache cache = (Cache)fileScriptCache.get(key);
			if(cache == null || scriptFile.getTimestamp().after(cache.timestamp)) {
				String scriptText = new String(scriptFile.getContent(this), encoding);
				GroovyClassLoader gcl = new GroovyClassLoader();
				Class scriptClass = gcl.parseClass(scriptText);
				
				cache = new Cache();
				cache.clazz = scriptClass;
				cache.timestamp = scriptFile.getTimestamp();
				
				fileScriptCache.put(key, cache);
			}
			

			Script script = (Script)cache.clazz.newInstance();
			script.setBinding(getBinding());
			script.run();
		} catch (ManipulationStoppedException e) {
		} catch (CompilationFailedException e) {
			showErrorMessage(e);
			//throw new JFDException(JFDResource.MESSAGES.getString("can_not_execute_script"), null);
		} catch (RuntimeException e) {
			showErrorMessage(e);
			//throw new JFDException(e, e.getMessage(), new Object[0]);
		} catch (Exception e) {
			showErrorMessage(e);
			//throw new JFDException(e, e.getMessage(), new Object[0]);
		} finally {
			if(is != null) {
				try {
					is.close();
				} catch (Exception e) {
				}
			}
			
			if(dialog != null) {
				dialog.dispose();
			}
		}
	}
	
	private static class CacheKey {
		VFile file;
		String encoding;

		public int hashCode() {
			return file.hashCode() + encoding.hashCode();
		}
		
		public boolean equals(Object o) {
			if(o == null || o.getClass() != CacheKey.class) {
				return false;
			}
			
			CacheKey other = (CacheKey)o;
			return file.equals(other.file) && encoding.equals(other.encoding);
		}
	}
	
	private static class Cache {
		Class clazz;
		Date timestamp;
	}
}
