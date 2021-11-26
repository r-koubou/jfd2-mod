/*
 * Created on 2004/10/10
 *
 */
package com.nullfish.app.jfd2.aliase;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import com.nullfish.app.jfd2.Initable;
import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.command.JFDException;
import com.nullfish.app.jfd2.config.Configuration;
import com.nullfish.app.jfd2.util.DomCache;
import com.nullfish.lib.vfs.VFS;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;
import com.nullfish.lib.vfs.exception.VFSIOException;

/**
 * ファイルエイリアス管理クラス
 * 
 * @author shunji
 */
public class AliaseManager implements Initable {
	JFD jfd;
	
	/**
	 * 設定ファイル名
	 */
	public static final String CONFIG_FILE = "aliase.xml";
	
	public static final String TAG_ALIASE = "aliase";
	public static final String TAG_NAME = "name";
	public static final String ATTR_PATH = "path";
	public static final String ATTR_LAST_OPENED = "last-opened-dir";
	
	
	/**
	 * 名称とファイルのマップ
	 */
	private Map nameAliaseMap = new HashMap();

	/**
	 * コンストラクタ
	 * 
	 * @param jfd
	 */
	public AliaseManager(JFD jfd) {
		this.jfd = jfd;
	}
	
	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.Initable#init(com.nullfish.lib.vfs.VFile)
	 */
	public void init(VFile baseDir) throws VFSException {
		try {
			initFromFile(baseDir.getChild(CONFIG_FILE));
			
			Configuration commonConfig = Configuration.getInstance(baseDir.getChild(JFD.COMMON_PARAM_FILE));
			VFile userConfDir = VFS.getInstance(jfd).getFile(
					(String) commonConfig.getParam(
							"user_conf_dir",
							baseDir.getRelativeFile("../.jfd2_user/conf")
									.getAbsolutePath()));
			initFromFile(userConfDir.getChild(CONFIG_FILE));
		} catch (JDOMException e) {
			throw new JFDException("failed to init aliase");
		} catch (IOException e) {
			throw new VFSIOException(e);
		}
	}
	
	private void initFromFile(VFile configFile) throws VFSException {
		try {
			if(!configFile.exists()) {
				return;
			}
			Document doc = DomCache.getInstance().getDocument(configFile);
			Element root = doc.getRootElement();
			List aliases = root.getChildren(TAG_ALIASE);
			
			for(int i=0; i<aliases.size(); i++) {
				initAliase((Element)aliases.get(i));
			}
		} catch (JDOMException e) {
			throw new JFDException("failed to init aliase");
		} catch (IOException e) {
			throw new VFSIOException(e);
		}
	}
	
	private void initAliase(Element element) throws VFSException {
		String path = element.getAttributeValue(ATTR_PATH);
		boolean lastOpendDir = Boolean.valueOf( element.getAttributeValue(ATTR_LAST_OPENED) ).booleanValue(); 
		
		AliaseInfo aliase = new AliaseInfo(path, lastOpendDir, jfd);
		List names = element.getChildren(TAG_NAME);
		for(int i=0; i<names.size(); i++) {
			nameAliaseMap.put(((Element)names.get(i)).getText().toLowerCase(), aliase);
		}
	}
	
	/**
	 * エイリアスに対応したファイルを取得する。
	 * 
	 * @param aliase
	 * @return
	 */
	public VFile getFile(String aliase) {
		aliase = aliase.toLowerCase();
		AliaseInfo aliaseInfo = (AliaseInfo)nameAliaseMap.get(aliase);
		return aliaseInfo != null ? aliaseInfo.getFile() : null;
	}
}
