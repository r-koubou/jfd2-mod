/*
 * Created on 2005/01/24
 *
 */
package com.nullfish.app.jfd2.viewer;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.viewer.controller.EachJFDOwnerViewrController;
import com.nullfish.app.jfd2.viewer.controller.EachJFDViewerController;
import com.nullfish.app.jfd2.viewer.controller.SingletonViewerController;
import com.nullfish.lib.meta_data.MetaDataManager;
import com.nullfish.lib.vfs.VFile;

/**
 * @author shunji TODO To change the template for this generated type comment go
 */
public class DefaultFileViewerFactory implements FileViewerFactory {
	/**
	 * パラメータのリスト
	 */
	private List parameters = new ArrayList();

	/**
	 * 設定ディレクトリ
	 */
	private VFile baseDir;
	
	/**
	 * オープン、クローズ時の振る舞い
	 */
	private ViewerController controller;

	/**
	 * 基準オブジェクトに対するファイルビューアの位置
	 */
	private FileViewerPosition position;

	/**
	 * クラス名
	 */
	protected String className;

	/**
	 * サポートされてる拡張子
	 */
	private String[] supportedExtensions;
	
	/**
	 * クラスローダ
	 */
	private ClassLoader loader = getClass().getClassLoader();
	
	/**
	 * jFD本体のカーソル移動を追うかどうかのフラグ
	 */
	protected boolean followsOwner;

	public static final String ATTR_ClASS_NAME = "class";

	public static final String ATTR_CONSTRAINTS = "constraints";

	public static final String ATTR_UNIT = "unit";

	public static final String ATTR_POSITION = "position";

	public static final String ATTR_FOLLOW_CURSOR = "follows";
	
	public static final String TAG_EXTENSION = "extension";
	
	/**
	 * ビューアを取得する。
	 */
	public FileViewer getFileViewer(JFD jfd) {
		FileViewer rtn = createFileViewer(jfd);

		for (int i = 0; i < parameters.size(); i++) {
			Parameter param = (Parameter) parameters.get(i);
			rtn.setParam(param.getName(), param.getValue());
		}

		rtn.setPosition(position);
		rtn.setFollowsCursor(followsOwner);
		rtn.setController(getController());
		rtn.init(baseDir);
		
		return rtn;
	}

	/**
	 * オープン、クローズ時の振る舞いクラスを設定する。
	 * 
	 * @return
	 */
	public void setController(ViewerController controller) {
		this.controller = controller;
	}

	/**
	 * オープン、クローズ時の振る舞いクラスを返す。
	 * 
	 * @return
	 */
	public ViewerController getController() {
		return controller;
	}

	/**
	 * パラメータを設定する。
	 */
	public void setParam(String name, Object value) {
		parameters.add(new Parameter(name, value));
	}

	/**
	 * パラメータクラス
	 */
	private class Parameter {
		String name;

		Object value;

		public Parameter(String name, Object value) {
			this.name = name;
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public Object getValue() {
			return value;
		}
	}

	public FileViewerPosition getPosition() {
		return position;
	}

	public void setPosition(FileViewerPosition position) {
		this.position = position;
	}

	public FileViewer createFileViewer(JFD jfd) {
		try {
			return (FileViewer) loader.loadClass(className).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void init(VFile baseDir, Element node) {
		this.baseDir = baseDir;
		className = node.getAttributeValue(ATTR_ClASS_NAME);

		setParam(FileViewerContainerPanel.PARAM_CONSTRAINTS, node
				.getAttributeValue(ATTR_CONSTRAINTS));

		String controllerStr = node.getAttributeValue(ATTR_UNIT);
		if ("each_jfd".equals(controllerStr)) {
			setController(new EachJFDViewerController());
		} else if ("each_owner".equals(controllerStr)) {
			setController(new EachJFDOwnerViewrController());
		} else {
			setController(new SingletonViewerController());
		}

		position = FileViewerPosition.getInstance(node
				.getAttributeValue(ATTR_POSITION));

		List paramsList = node.getChildren(MetaDataManager.TAG_NAME);
		for (int i = 0; i < paramsList.size(); i++) {
			Element paramNode = (Element) paramsList.get(i);
			setParam(paramNode.getAttributeValue(MetaDataManager.ATTR_NAME),
					MetaDataManager.getInstance().paramNode2Object(paramNode));
		}
		
		followsOwner = Boolean.valueOf(node.getAttributeValue(ATTR_FOLLOW_CURSOR)).booleanValue();
		
		Element exts = node.getChild("extension");
		if(exts == null) {
			return;
		}
		
		Element lists = exts.getChild("list");
		if(lists == null) {
			return;
		}
		
		if(lists != null) {
			List extensionList = (List)MetaDataManager.getInstance().node2Object(lists);
			supportedExtensions = new String[extensionList.size()];
			supportedExtensions = (String[])extensionList.toArray(supportedExtensions);
		}
	}

	public String[] getSupportedExtensions() {
		return supportedExtensions;
	}

	public void setLoader(ClassLoader loader) {
		this.loader = loader;
	}
}
