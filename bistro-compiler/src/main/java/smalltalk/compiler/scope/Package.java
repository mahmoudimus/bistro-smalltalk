//====================================================================
// Package.java
//====================================================================
package smalltalk.compiler.scope;

import java.util.*;
import java.util.stream.Collectors;
import java.io.FilenameFilter;

import smalltalk.Name;
import smalltalk.compiler.element.Container;

/**
 * Represents a Java package and encodes its identification in a class definition. Also, contains classes imported from
 * the package.
 *
 * @author Copyright 1999,2016 Nikolas S. Boyd. All rights reserved.
 */
public class Package extends Container {

    /**
     * Defines an interface for visiting instances.
     */
    public static interface Visitor {

        public void visit(Package facePackage);
    }

    /**
     * Indicates all classes from a package.
     */
    public static final String wildCard = ".*";

    /**
     * Indicates all classes from a package.
     */
    public static final String rootName = "";

    /**
     * The package component separator.
     */
    public static final String nameSeparator = ".";

    /**
     * The package directory separator character.
     */
    public static final String directorySeparator = "/";

    /**
     * The package directory separator character.
     */
    public static final char directorySeparatorChar = '/';

    /**
     * Returns the package named (packageName), or null.
     *
     * @param packageName a package name
     * @return the package named (packageName), or null.
     */
    public static Package named(String packageName) {
        return Library.current.packageNamed(nameFrom(packageName));
    }
    
    public static String nameFrom(String packageName) {
        if (namesAllFaces(packageName)) {
            int length = packageName.length() - wildCard.length();
            return packageName.substring(0, length).trim();
        }
        else {
            return packageName.trim();
        }
    }

    /**
     * Returns whether the supplied (importName) ends with a wild card.
     *
     * @return whether the supplied (importName) ends with a wild card.
     * @param importName the name of an imported class, interface, or package.
     */
    public static boolean namesAllFaces(String importName) {
        return importName.endsWith(wildCard);
    }

    /**
     * The names of the packages defining literals.
     */
    public static final List<String> LiteralPackageNames = new ArrayList();

    // Initializes the literal package names.
    static {
        LiteralPackageNames.add("smalltalk.behavior");
        LiteralPackageNames.add("smalltalk.magnitude");
        LiteralPackageNames.add("smalltalk.collection");
    }

    /**
     * Contains the name of a package.
     */
    String name;
    
    String baseFolder = EmptyString;

    /**
     * Contains the classes imported from the package.
     */
    Map<String, Face> faces = new HashMap();

    /**
     * Constructs a new Package.
     *
     * @param packageName a package name.
     */
    public Package(String packageName) {
        super();
        name = packageName;
    }

    public Package(String packageName, String folderPath) {
        super();
        name = packageName;
        baseFolder = folderPath;
    }

    /**
     * Constructs a new Package.
     */
    public Package() {
        this("");
    }

    /**
     * Returns whether the package defines behaviors.
     *
     * @return whether the package defines behaviors.
     */
    public boolean definesBehaviors() {
        return (name().equals(LiteralPackageNames.get(0))
                || name().startsWith("org.ansi.smalltalk"));
    }

    /**
     * Returns whether the package defines magnitudes.
     *
     * @return whether the package defines magnitudes.
     */
    public boolean definesMagnitudes() {
        return name().equals(LiteralPackageNames.get(1));
    }

    /**
     * Returns whether the package defines collections.
     *
     * @return whether the package defines collections.
     */
    public boolean definesCollections() {
        return name().equals(LiteralPackageNames.get(2));
    }

    /**
     * Returns whether the package defines literals.
     *
     * @return whether the package defines literals.
     */
    public boolean definesLiterals() {
        return LiteralPackageNames.contains(name());
    }

    /**
     * Returns the package name.
     *
     * @return the package name.
     */
    @Override
    public String name() {
        return name;
    }

    /**
     * Establish the (packageName).
     *
     * @param packageName a package name.
     */
    public void name(String packageName) {
        name = packageName;
    }

    /**
     * Returns the name of the package that contains this one.
     *
     * @return the name of the package that contains this one.
     */
    public String parentName() {
        return Name.packageName(name);
    }

    /**
     * Returns the fully qualified name of a class or interface.
     *
     * @param faceName a type name
     * @return the fully qualified name of a class or interface.
     */
    public String qualify(String faceName) {
        return name + nameSeparator + faceName;
    }

    /**
     * Returns the fully qualified name of a class or interface.
     *
     * @return the fully qualified name of a class or interface.
     */
    @Override
    public String nameOf(Container component) {
        return qualify(component.name());
    }

    /**
     * Returns the directory pathname identified by the package.
     *
     * @return the directory pathname identified by the package.
     */
    public String pathname() {
        return name.replace(Dot, directorySeparatorChar);
    }
    
    public HashMap<String, File> parseSources() throws Exception {
        HashMap<String, File> results = new HashMap();
        java.io.File sourceFolder = sourceFolder();
        if (!sourceFolder.exists()) {
            System.out.println("Failed to locate sources for " + name());
            return results;
        }
        
        if ("smalltalk".equals(name)) {
            List<String> faceNames = targetFaces();
            for (String faceName : faceNames) {
                File fileScope = packageTarget(faceName);
                results.put(fileScope.fullName(), fileScope);
            }
            return results;
        }

        List<String> faceNames = sourceFaces();
        for (String faceName : faceNames) {
            File fileScope = parseSource(faceName, faceNames);
            results.put(fileScope.fullName(), fileScope);
        }
        return results;
    }
    
    public HashMap<String, File> parseSource(String faceName) throws Exception {
        String fullName = name() + nameSeparator + faceName;
        if (faceName.startsWith(name())) {
            fullName = faceName;
        }

        HashMap<String, File> results = new HashMap();
        File fileScope = parseSource(fullName, sourceFaces());
        results.put(fileScope.fullName(), fileScope);
        return results;
    }
    
    private File packageTarget(String fullName) throws Exception {
        File fileScope = new File();
        fileScope.namePackage(name());
        fileScope.faceScope().name(fullName);
        return fileScope;
    }

    private File parseSource(String fullName, List<String> peerFaces) throws Exception {
        System.out.println("Parsing " + fullName);
        File fileScope = new File();
        fileScope.namePackage(name());
        fileScope.faceScope().name(fullName);
        fileScope.peerFaces(peerFaces);
        fileScope.parse();
        return fileScope;
    }

    public List<String> listFaces(java.io.File folder, String type, FilenameFilter filter) {
        List<String> results = Arrays.asList(folder.list(filter));
        return results.stream()
                .map(f -> f.substring(0, f.length() - type.length()))
                .collect(Collectors.toList());
    }
    
    public List<String> sourceFaces() {
        return listFaces(sourceFolder(), File.sourceExtension, File.sourceFileFilter);
    }
    
    public List<String> targetFaces() {
        return listFaces(targetFolder(), File.targetExtension, File.targetFileFilter);
    }
    
    public java.io.File sourceFolder() {
        return new java.io.File(Library.current.sourcePath(), pathname());
    }
    
    public java.io.File createTarget() {
        java.io.File targetFolder = targetFolder();

        if (!targetFolder.exists() && !targetFolder.mkdirs()) {
            System.out.println("Can't create " + targetFolder.getAbsolutePath());
            return null;
        }

        return targetFolder;
    }
    
    public java.io.File targetFolder() {
        return new java.io.File(Library.current.targetPath(), pathname());
    }

    /**
     * Returns the directory identified by the package.
     *
     * @return the directory identified by the package.
     */
    public java.io.File directory() {
        if (!baseFolder.isEmpty()) {
            return new java.io.File(baseFolder + pathname());
        }
        return ClassPath.current.locate(pathname());
    }

    /**
     * Loads all face definitions from the Java package.
     */
    public void loadFaces() {
        for (String faceName : ClassPath.current.classesInPackage(this)) {
            loadFace(faceName);
        }
    }

    /**
     * Loads all faces from a Java source package.
     * @param faceNames a list of type names
     */
    public void loadFaces(List<String> faceNames) {
        if (faceNames != null && !faceNames.isEmpty()) {
            faceNames.stream().forEach(faceName -> loadFace(faceName));
        }
    }

    /**
     * Loads the definition for the named face from the Java package.
     *
     * @param faceName the name of a class or interface.
     */
    public void loadFace(String faceName) {
        if (faces.get(faceName) != null) return;
        Face face = new Face(this);
        face.name(faceName);
        addFace(face);
    }

    /**
     * Adds the named face to this package.
     *
     * @param face a class or interface.
     */
    public void addFace(Face face) {
        faces.put(Name.typeName(face.name()), face);
        Library.current.addFace(face.name(), this);
    }

    /**
     * Returns the names of the faces imported from the package.
     *
     * @return the names of the faces imported from the package.
     */
    public Set<String> faceNames() {
        return faces.keySet();
    }

    /**
     * Returns the imported face named (faceName).
     *
     * @param faceName a type name
     * @return the imported face named (faceName).
     */
    public Face faceNamed(String faceName) {
        return faces.get(Name.typeName(faceName));
    }

    /**
     * Accepts a visitor for inspection of the receiver.
     *
     * @param aVisitor visits the receiver for its information.
     */
    public void acceptVisitor(Visitor aVisitor) {
        aVisitor.visit(this);
    }
}
