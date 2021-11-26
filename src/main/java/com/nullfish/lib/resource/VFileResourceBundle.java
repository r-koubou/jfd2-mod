package com.nullfish.lib.resource;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;

public class VFileResourceBundle extends ResourceBundle {
	private Map lookup;

	private static Locale locale = Locale.getDefault();

	public VFileResourceBundle(VFile baseFile) throws VFSException, IOException {
		String exceptFileName = baseFile.getFileName().getExceptExtension();
		String extension = baseFile.getFileName().getExtension();
		VFile file = baseFile.getParent().getChild(
				exceptFileName + "_" + locale.getLanguage() + "." + extension);
		
		if (!file.exists()) {
			file = baseFile;
		}

		Properties properties = new Properties();
		properties.load(file.getInputStream());
		lookup = new HashMap(properties);
	}
	
    // Implements java.util.ResourceBundle.handleGetObject; inherits javadoc specification.
    public Object handleGetObject(String key) {
        if (key == null) {
            throw new NullPointerException();
        }
        return lookup.get(key);
    }

    /**
     * Implementation of ResourceBundle.getKeys.
     */
    public Enumeration getKeys() {
        ResourceBundle parent = this.parent;
        return new VFileResourceBundleEnumeration(lookup.keySet(),
                (parent != null) ? parent.getKeys() : null);
    }

}
