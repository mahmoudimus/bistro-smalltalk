//====================================================================
// Library.java
//====================================================================
package smalltalk.compiler.scope;

import java.util.*;
import smalltalk.Name;
import smalltalk.compiler.element.Base;
import smalltalk.compiler.element.Mirror;
import smalltalk.compiler.element.Reference;

/**
 * Maintains references to all classes and interfaces imported from external packages. 
 * Packages are located relative to the system class path established by the Java environment.
 *
 * @author Copyright 1999,2016 Nikolas S. Boyd. All rights reserved.
 */
public class Library extends Base {

    /**
     * The singleton instance of this class.
     */
    public static final Library current = new Library();

    /**
     * Contains the base paths from the class path.
     */
    ClassPath path = ClassPath.current;

    /**
     * Maps face names to faces. 
     * A face name always maps to the last face loaded from the class path.
     */
    Map<String, Face> faces = new HashMap();

    /**
     * Maps package names to packages.
     */
    Map<String, Package> packages = new HashMap();
    
    java.io.File sourceBase;
    java.io.File targetBase;
    java.io.File classBase;

    /**
     * Constructs a new Library.
     */
    protected Library() { }
    
    void clear() {
        faces.clear();
        packages.clear();
    }
    
    public String[] basePaths() {
        String[] results = { sourcePath(), targetPath(), classPath() };
        return results;
    }
    
    public String sourcePath() {
        return sourceBase.getAbsolutePath();
    }
    
    public String targetPath() {
        return targetBase.getAbsolutePath();
    }
    
    public String classPath() {
        return classBase.getAbsolutePath();
    }
    
    /**
     * Returns whether a package with the supplied (packageName) can be located in the class path.
     *
     * @param aPackage a package.
     * @return whether the named package can be located in the class path.
     */
    public boolean canLocate(Package aPackage) {
        return path.canLocatePackage(aPackage);
    }

    /**
     * Locates the supplied (directoryName) in the class path.
     *
     * @param directoryName a relative directory name for a package.
     * @return a package directory, or null if none found.
     */
    public java.io.File locate(String directoryName) {
        return path.locate(directoryName);
    }
    
    public void addFace(Face face) {
        String fullName = face.fullName();
        String typeName = Name.typeName(fullName);
        if (typeName.endsWith(Face.metaSuffix)) {
            fullName = fullName.replace(Face.metaSuffix, Face.metaNesting);
            typeName = typeName.replace(Face.metaSuffix, Face.metaNesting);
        }

        faces.put(fullName, face);
        faces.put(typeName, face);
//        System.out.println("added " + fullName + " to Library");
    }

    /**
     * Adds a new face to the library (if not defined already).
     *
     * @param faceName the name of a new face.
     * @param aPackage the package that contains the named face.
     */
    public void addFace(String faceName, Package aPackage) {
        addFace(aPackage.faceNamed(faceName));
    }

    /**
     * Removes a face named (faceName) from the library (if defined).
     *
     * @param faceName the name of a new face.
     */
    public void removeFace(String faceName) {
        faces.remove(faceName);
    }

    /**
     * Returns the package named (packageName), or null.
     *
     * @return the package named (packageName), or null.
     * @param packageName the name of a package.
     */
    public Package packageNamed(String packageName) {
        if (packages.containsKey(packageName)) {
            return packages.get(packageName);
        }

        Package result = new Package(packageName);
        packages.put(packageName, result);
        return result;
    }
    
    public Face faceFrom(Reference reference) {
        if (reference == null) return null;
        return faceNamed(reference.name());
    }

    /**
     * Returns the face named (fullName), or null.
     *
     * @return the face named (fullName), or null.
     * @param fullName the name of a face.
     */
    public Face faceNamed(String fullName) {
        if (fullName.isEmpty()) return null;
        String faceName = Name.typeName(fullName);
        if (faceName.endsWith(Face.metaSuffix)) {
            faceName = faceName.replace(Face.metaSuffix, Face.metaNesting);
        }
        String packageName = Name.packageName(fullName);
        if (packageName.isEmpty()) return faces.get(faceName);
        return packageNamed(packageName).faceNamed(faceName);
    }

    /**
     * Returns whether the container can resolve a symbolic (reference).
     *
     * @param reference a symbolic reference to be resolved.
     * @return whether the container can resolve a symbolic (reference).
     */
    public boolean resolves(Reference reference) {
        if (reference.isEmpty()) return false;
        if (reference.name().equals(Primitive)) return true;
        if (reference.isElementary()) return true;
        if (reference.isGlobal()) return true;

        String faceName = Name.typeName(reference.name());
        if (faceName.isEmpty()) return false;
        
        if (faceName.endsWith(Face.metaSuffix)) {
            faceName = faceName.replace(Face.metaSuffix, Face.metaNesting);
        }

        String packageName = Name.packageName(reference.name());
        if (!packageName.isEmpty()) {
            Face face = faceFrom(reference);
            if (face != null) return true;
        }

        boolean result = faces.containsKey(faceName);
        if (result) {
//            System.out.println("Library resolved " + symbol);
        }
        else {
//            System.out.println("Library can't resolve " + faceName + " from " + reference.name());
        }
        return result;
    }

    /**
     * Returns the type of the variable to which a (reference) resolves.
     *
     * @param reference refers to a variable in some compiler scope.
     * @return the type of the variable to which a (reference) resolves.
     */
    public Class resolveType(Reference reference) {
        Face face = faceFrom(reference);
        return (face == null ? null : face.typeClass());
    }

    /**
     * Returns the type name of the variable to which a (reference) resolves.
     *
     * @param reference refers to a variable in some compiler scope.
     * @return the type name of the variable to which a (reference) resolves.
     */
    public String resolveTypeName(Reference reference) {
        String symbol = Name.typeName(reference.name());
        Class faceClass = resolveType(reference);
        if (faceClass != null) {
            if (Mirror.forClass(faceClass).hasMetaclass()) {
                return RootMetaclass;
            } else {
                return faceClass.getName();
            }
        }

        if (faces.containsKey(symbol)) {
            return RootMetaclass;
        }
        return RootClass;
    }

    /**
     * Loads the base Java packages and removes the faces shadowed by Bistro.
     * @param basePaths base paths
     */
    public void loadBasePackages(java.io.File... basePaths) {
        sourceBase = basePaths[0];
        targetBase = basePaths[1];
        classBase  = basePaths[2];

        clear();
        packageNamed("java.lang").loadFaces();
        packageNamed("java.lang.reflect").loadFaces();
        for (String className : CommonClasses) removeFace(className);
    }
    
    private static final String[] CommonClasses = {
        "Object", "Boolean", "Character", "String",
        "Number", "Double", "Float", "Integer",
        "Class", "Exception", "Error", "Array"
    };
}
