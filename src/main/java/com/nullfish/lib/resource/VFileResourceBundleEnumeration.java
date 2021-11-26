package com.nullfish.lib.resource;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

public class VFileResourceBundleEnumeration implements Enumeration {

    Set set;
    Iterator iterator;
    Enumeration enumeration; // may remain null

    public VFileResourceBundleEnumeration(Set set, Enumeration enumeration) {
        this.set = set;
        this.iterator = set.iterator();
        this.enumeration = enumeration;
    }

    String next = null;
            
    public boolean hasMoreElements() {
        if (next == null) {
            if (iterator.hasNext()) {
                next = (String)iterator.next();
            } else if (enumeration != null) {
                while (next == null && enumeration.hasMoreElements()) {
                    next = (String)enumeration.nextElement();
                    if (set.contains(next)) {
                        next = null;
                    }
                }
            }
        }
        return next != null;
    }

    public Object nextElement() {
        if (hasMoreElements()) {
            String result = next;
            next = null;
            return result;
        } else {
            throw new NoSuchElementException();
        }
    }
}
