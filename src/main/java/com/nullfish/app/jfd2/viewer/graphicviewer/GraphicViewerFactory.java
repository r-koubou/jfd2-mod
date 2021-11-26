package com.nullfish.app.jfd2.viewer.graphicviewer;

import com.nullfish.app.jfd2.viewer.DefaultFileViewerFactory;

public class GraphicViewerFactory extends DefaultFileViewerFactory {
	private String[] supportedExtensions = {
			"jpg",
			"jpeg",
			"gif",
			"png"
	};

	private String[] jaiSupportedExtensions = {
			"jpg",
			"jpeg",
			"gif",
			"png",
			"bmp",
			"tiff",
			"tif",
			"wbmp",
			"jp2"
	};
	
	public String[] getSupportedExtensions() {
		try {
			Class.forName("com.sun.media.imageio.plugins.bmp.BMPImageWriteParam");
			return jaiSupportedExtensions;
		} catch (ClassNotFoundException e) {
			return supportedExtensions;
		}
	}

}
