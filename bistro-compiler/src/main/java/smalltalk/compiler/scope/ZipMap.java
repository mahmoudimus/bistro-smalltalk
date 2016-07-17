//====================================================================
// ZipMap.java
//====================================================================
package smalltalk.compiler.scope;

import java.io.*;
import java.util.*;
import java.util.zip.*;

/**
 * Maps the classes located in an element of a class path. 
 * Locates all package directories relative to an archive from the class path.
 *
 * @author Copyright 1999,2016 Nikolas S. Boyd. All rights reserved.
 */
public class ZipMap extends PathMap {

    /**
     * Returns whether the supplied (pathname) identifies a support file type.
     *
     * @param pathname the pathname of a potential archive file.
     * @return whether the supplied (pathname) identifies a support file type.
     */
    public static boolean supports(String pathname) {
        return (pathname.endsWith(".zip")
                || pathname.endsWith(".jar")
                || pathname.endsWith(".ZIP")
                || pathname.endsWith(".JAR"));
    }

    /**
     * Constructs a new ZipMap.
     *
     * @param pathname the pathname of the archive to be mapped.
     */
    public ZipMap(String pathname) {
        super(pathname);
    }

    /**
     * Loads a class name from an archive entry if it identifies a class file.
     *
     * @param entry an archive file entry.
     */
    protected void load(ZipEntry entry) {
        String name = entry.getName();
        int i = name.lastIndexOf(nameSeparator);
        String path = (i < 0 ? "" : name.substring(0, i));
        String file = name.substring(i + 1);

        List<String> classNames = map.get(path);
        if (classNames == null) {
            classNames = new ArrayList();
            map.put(path, classNames);
        }

        if (file.endsWith(File.classExtension)) {
            int length = file.length() - File.classExtension.length();
            classNames.add(file.substring(0, length));
        }
    }

    /**
     * Loads the names of all the packaged classes located in the archive.
     * @throws IOException if raised
     */
    @Override
    public void load() throws IOException {
        try (ZipFile zipFile = new ZipFile(basepath)) {
            Enumeration e = zipFile.entries();
            while (e.hasMoreElements()) {
                load((ZipEntry) e.nextElement());
            }
        }
    }

    /**
     * Returns the complete directory located by a relative (directoryName).
     *
     * @param directoryName the name of a Java/Bistro package directory.
     * @return null - file operations are not supported in archives.
     */
    @Override
    public java.io.File locate(String directoryName) {
        return (null);
    }
}
