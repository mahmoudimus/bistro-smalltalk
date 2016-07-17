//====================================================================
// ClassPath.java
//====================================================================
package smalltalk.compiler.scope;

import java.util.*;
import java.io.IOException;
import smalltalk.Name;

/**
 * Provides a directory of the classes located by the Java class path. Locates packages by their directory names and
 * provides a list of the classes contained in a package.
 *
 * @author Copyright 1999,2016 Nikolas S. Boyd. All rights reserved.
 */
public class ClassPath {

    /**
     * Refers to the single instance of this class.
     */
    public static final ClassPath current = new ClassPath();

    /**
     * Separates the components of a class path.
     */
    public static final String separator = System.getProperty("path.separator");

    /**
     * Contains the class path maps.
     */
    List<PathMap> contents = new ArrayList();

    /**
     * Constructs a new ClassPath.
     */
    protected ClassPath() { }
    
    void clear() {
        contents.clear();
    }

    /**
     * Maps the names of the classes under an element of the class path.
     *
     * @param path an element of the class path.
     */
    protected void mapPath(java.io.File path) {
        if (!path.exists()) {
//            System.out.println();
//            System.out.println("Library failed to map " + path);
            return;
        }

        System.out.print(".");
        PathMap map = null;
        if (ZipMap.supports(path.getAbsolutePath())) {
            map = new ZipMap(path.getAbsolutePath());
        } else {
            map = new PathMap(path.getAbsolutePath());
        }
        try {
            map.load();
            contents.add(map);
        } catch (IOException x) {
            System.out.println();
            System.err.println("Library failed to map " + path.getAbsolutePath());
        }
    }

    /**
     * Parses the components of a class (pathString) and adds them to the receiver.
     *
     * @param path a class path string.
     */
    public void parsePath(String path) {
        if (path == null) return;
        String[] folderPaths = path.split(separator);
        for (String folderPath : folderPaths) 
            mapPath(new java.io.File(folderPath));
    }

    /**
     * Loads and maps the elements of the Java class path.
     * @param basePaths the base paths
     */
    public void loadPaths(java.io.File... basePaths) {
        System.out.print("Mapping CLASSPATH");
        clear();
        Library.current.loadBasePackages(basePaths[0], basePaths[1], basePaths[2]);
        parsePath(System.getProperty("sun.boot.class.path"));
        parsePath(System.getProperty("java.ext.dirs"));
        parsePath(System.getProperty("java.class.path"));
        mapPath(basePaths[2]); // classBasepath
        mapPath(basePaths[1]); // targetBasepath
        mapPath(basePaths[0]); // sourceBasepath
        System.out.println();
    }

    /**
     * Returns whether a named face can be located in the class path.
     *
     * @param fullFaceName a fully qualified face name.
     * @return whether a named face can be located in the class path.
     */
    public boolean canLocateFaceNamed(String fullFaceName) {
        String packageName = Name.packageName(fullFaceName);
        String faceName = Name.typeName(fullFaceName);
        for (PathMap map : reversedPath()) {
            List<String> results = map.classesInDirectory(packageName);
            if (!results.isEmpty() && results.contains(faceName)) {
//                System.out.println("found " + faceName + " in " + packageName);
                return true;
            }
        }

        for (PathMap map : reversedPath()) {
            if (map.packageContaining(faceName) != null) return true;
        }

        return false;
    }

    /**
     * Returns whether (aPackage) can be located in the class path.
     *
     * @param aPackage a potential Java/Bistro package.
     * @return whether (aPackage) can be located in the class path.
     */
    public boolean canLocatePackage(Package aPackage) {
        for (PathMap map : reversedPath()) {
            List<String> results = map.classesInDirectory(aPackage.pathname());
            if (!results.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the names of the classes whose files are located the package directories identified by (aPackage).
     *
     * @param aPackage a Java/Bistro package.
     * @return the names of the classes contained in (aPackage).
     */
    public Set<String> classesInPackage(Package aPackage) {
        Set<String> result = new HashSet();
        for (PathMap map : reversedPath()) {
            result.addAll(map.classesInDirectory(aPackage.pathname()));
        }
        return result;
    }

    /**
     * Locates the supplied (directoryName) in the class path.
     *
     * @param directoryName a relative directory name for a package.
     * @return a package directory, or null if none found.
     */
    public java.io.File locate(String directoryName) {
        for (PathMap map : reversedPath()) {
            java.io.File result = map.locate(directoryName);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    private List<PathMap> reversedPath() {
        List<PathMap> results = new ArrayList(contents);
        Collections.reverse(results);
        return results;
    }
    
    public static String buildPath(String... basePaths) {
        int count = 0;
        StringBuilder builder = new StringBuilder();
        for (String basePath : basePaths) {
            if (count > 0) builder.append(separator);
            builder.append(basePath);
            count++;
        }
        return builder.toString();
    }
}
