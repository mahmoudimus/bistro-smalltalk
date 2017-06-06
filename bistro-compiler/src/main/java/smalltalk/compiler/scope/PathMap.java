//====================================================================
// PathMap.java
//====================================================================
package smalltalk.compiler.scope;

import java.util.*;
import java.io.IOException;

/**
 * Maps the classes located in an element of a class path.
 * Locates all package directories relative to a base path from the class path.
 *
 * @author Copyright 1999,2017 Nikolas S. Boyd. All rights reserved.
 */
public class PathMap {

    /**
     * Used to separate elements of a package directory name.
     */
    protected static final String nameSeparator = "/";

    /**
     * Contains the absolute base path mapped by this instance.
     */
    protected String basepath;

    /**
     * Maps a package directory name to a set of class names.
     */
    protected Map<String, List<String>> map = new HashMap();
    protected Map<String, String> faceMap = new HashMap();

    /**
     * Constructs a new PathMap.
     *
     * @param pathname the base path to be mapped.
     */
    public PathMap(String pathname) {
        basepath = pathname;
    }

    /**
     * Loads the mappings for (directoryName) and all the subdirectories under it.
     *
     * @param directoryName the name of a Java/Bistro package directory.
     */
    protected void load(String directoryName) {
        java.io.File directory = new java.io.File(basepath, directoryName);
        List<String> faceNames = new ArrayList();
        String[] list = directory.list();

        int count = list.length;
        for (int n = 0; n < count; n++) {
            String fileName = list[n];
            java.io.File file = new java.io.File(directory, fileName);
            if (file.isDirectory()) {
                this.load( // load subdirectory
                        directoryName.length() == 0 ? fileName
                                : directoryName + nameSeparator + fileName
                );
            } else if (fileName.endsWith(File.classExtension)) {
                int length = fileName.length() - File.classExtension.length();
                String shortName = fileName.substring(0, length);
                faceNames.add(shortName);

                String packageName = directoryName.replace("/", ".");
                Package.named(packageName).loadFace(shortName);
            }
        }

        if (!faceNames.isEmpty()) {
//            System.out.println();
//            System.out.println("loaded " + faceNames.size() + " from " + directoryName);

            map.put(directoryName, faceNames);
            for (String faceName : faceNames) {
                faceMap.put(faceName, directoryName);
            }
        }
    }

    /**
     * Loads the names of all the packaged classes located under the base pathname.
     * @throws IOException if raised
     */
    public void load() throws IOException {
        load("");
    }

    /**
     * Returns the complete directory located by a relative (directoryName).
     *
     * @param directoryName the name of a Java/Bistro package directory.
     * @return the complete directory located by a relative (directoryName).
     */
    public java.io.File locate(String directoryName) {
        java.io.File directory = new java.io.File(basepath, directoryName);
        return (directory.exists() ? directory : null);
    }

    /**
     * Returns the names of the classes found in (directoryName).
     *
     * @param directoryName the name of a Java/Bistro package directory.
     * @return the names of the classes found in (directoryName).
     */
    public List<String> classesInDirectory(String directoryName) {
        return (map.containsKey(directoryName) ? map.get(directoryName) : new ArrayList());
    }

    /**
     * Returns the names of the classes found in the directory of (aPackage).
     *
     * @param aPackage a Java/Bistro package.
     * @return the names of the classes found in the directory of (aPackage).
     */
    public List<String> classesInPackage(Package aPackage) {
        return classesInDirectory(aPackage.pathname());
    }

    public String packageContaining(String faceName) {
        return (!faceMap.containsKey(faceName) ? "" :
                faceMap.get(faceName).replace(nameSeparator, Package.nameSeparator));
    }
}
