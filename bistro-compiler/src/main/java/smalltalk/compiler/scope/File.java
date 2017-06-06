//====================================================================
// File.java
//====================================================================
package smalltalk.compiler.scope;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.CommonTree;

import smalltalk.Name;
import smalltalk.compiler.Emission;
import static smalltalk.compiler.Emission.emit;
import smalltalk.compiler.element.*;

/**
 * Represents a class file, including the package name, imports, and a face definition.
 *
 * @author Copyright 1999,2017 Nikolas S. Boyd. All rights reserved.
 */
public class File extends Scope {

    /**
     * The extension for Bistro source files.
     */
    public static String sourceExtension = ".bist";

    /**
     * The extension for Java source files.
     */
    public static String targetExtension = ".java";

    /**
     * The extension for Java class files.
     */
    public static String classExtension = ".class";

    /**
     * The names of the literal magnitudes.
     */
    protected static String literalMagnitudes[] = {
        "SmallInteger", "Float", "Double", "Character"
    };

    /**
     * The names of the literal collections.
     */
    protected static String literalCollections[] = {
        "Array", "String", "Symbol"
    };

    /**
     * A filter for selecting Bistro source files.
     */
    public static FilenameFilter sourceFileFilter = new FilenameFilter() {
        @Override public boolean accept(java.io.File directory, String fileName) {
            return fileName.endsWith(sourceExtension);
        }
    };

    public static FilenameFilter targetFileFilter = new FilenameFilter() {
        @Override public boolean accept(java.io.File directory, String fileName) {
            return fileName.endsWith(targetExtension);
        }
    };

    TokenCompiler tokenCompiler = new TokenCompiler(this);

    /**
     * Contains the imports for a face definition.
     */
    Library faceLibrary;

    /**
     * Identifies the package that contains the face.
     */
    Package facePackage;

    /**
     * Contains a list of imported faces and packages.
     */
    List<String> imports = new ArrayList();

    /**
     * Contains the definition of a face.
     */
    Face faceScope;

    /**
     * Contains the names of the packaged peers.
     */
    List<String> peerFaces = new ArrayList();

    /**
     * Constructs a new File scope.
     */
    public File() {
        super(null);
        faceLibrary = Library.current;
        facePackage = null;
        faceScope = new Face(this);
    }

    @Override
    public TokenStream tokenStream() {
        return tokenCompiler.tokenStream();
    }

    /**
     * Establishes the name of the package.
     *
     * @param packageName a package name.
     */
    public void namePackage(String packageName) {
        facePackage = Package.named(packageName);
    }

    /**
     * Establishes the name of the base class.
     *
     * @param node the parse node that contains the base name.
     */
    public void nameBase(CommonTree node) {
        faceScope.comment(commentFrom(node));
        faceScope.baseName(node.getText().trim());
    }

    /**
     * Establishes the name of a superType.
     *
     * @param supertypeName a superType name.
     */
    public void nameSuper(String supertypeName) {
        faceScope.implementsInterface(supertypeName);
    }

    /**
     * Establishes the name of a subclass.
     *
     * @param subclassName a subclass name.
     */
    public void nameSubclass(String subclassName) {
        faceLibrary.removeFace(subclassName);
        faceScope.subclass(subclassName);
        facePackage.addFace(faceScope);
    }

    /**
     * Establishes the name of a subclass.
     *
     * @param subclassName a subclass name.
     */
    public void nameSubclass(Token subclassName) {
        nameSubclass(subclassName.getText());
        setLine(subclassName.getLine());
    }

    /**
     * Establishes the name of a subtype.
     *
     * @param subtypeName a subtype name.
     */
    public void nameSubtype(String subtypeName) {
        faceLibrary.removeFace(subtypeName);
        faceScope.subtype(subtypeName);
        facePackage.addFace(faceScope);
    }

    /**
     * Establishes the name of a subtype.
     *
     * @param subtypeName a subtype name.
     */
    public void nameSubtype(Token subtypeName) {
        nameSubtype(subtypeName.getText());
        setLine(subtypeName.getLine());
    }

    /**
     * Establishes the list of sources for packaged peers.
     *
     * @param peers a list of face names.
     */
    public void peerFaces(List<String> peers) {
        if (peers == null) return;
        peerFaces.addAll(peers);
    }

    /**
     * Cleans out any residue left from the parsing process and prepares the receiver for code generation.
     */
    @Override
    public void clean() {
        super.clean();
        faceScope.clean();
        if (faceScope.hasMetaface()) {
            faceScope.metaFace().clean();
        }

        if (facePackage.definesSmalltalk()) return;

        imports.add("smalltalk.behavior.Boolean");
        imports.add("smalltalk.magnitude.SmallInteger");
        imports.add("smalltalk.collection.String");

        if (facePackage.definesBehaviors()) return;

        imports.add("smalltalk.behavior.*");
        imports.add("smalltalk.behavior.Object");
        imports.add("smalltalk.behavior.Behavior");
        imports.add("smalltalk.behavior.Class");
        imports.add("smalltalk.behavior.UndefinedObject");
        imports.add("smalltalk.behavior.Exception");
        imports.add("smalltalk.behavior.Error");

        if (facePackage.definesMagnitudes()) return;

        imports.add("smalltalk.magnitude.Float");
        imports.add("smalltalk.magnitude.Double");
        imports.add("smalltalk.magnitude.Number");
        imports.add("smalltalk.magnitude.Integer");
        imports.add("smalltalk.magnitude.Character");
        imports.add("smalltalk.magnitude.*");

        if (facePackage.definesCollections()) return;

        imports.add("smalltalk.collection.Symbol");
        imports.add("smalltalk.collection.Array");
    }

    /**
     * Returns whether this container needs magnitudes defined.
     *
     * @return whether this container needs magnitudes defined.
     */
    @Override
    public boolean needsMagnitudes() {
        return facePackage.definesBehaviors();
    }

    /**
     * Returns whether this container needs collections defined.
     *
     * @return whether this container needs collections defined.
     */
    @Override
    public boolean needsCollections() {
        return facePackage.definesBehaviors() || facePackage.definesMagnitudes();
    }

    /**
     * Returns the nest level for this scope.
     *
     * @return the nest level for this scope.
     */
    @Override
    public int nestLevel() {
        return 0;
    }

    /**
     * Returns the name of the package that contains this file.
     *
     * @return the name of the package that contains this file.
     */
    public String packageName() {
        return facePackage.name();
    }

    /**
     * Returns the package pathname for this file.
     *
     * @return the package pathname for this file.
     */
    public String packagePathname() {
        return facePackage.pathname();
    }

    public boolean imports(Reference reference) {
        return false;
    }

    /**
     * Adds (importName) to the imports for a class.
     *
     * @param importName the name of an imported class or package.
     */
    public void importFace(String importName) {
        imports.add(importName);
        String packageName = Name.packageName(importName);
        Package aPackage = faceLibrary.packageNamed(packageName);
        if (Package.namesAllFaces(importName)) {
            aPackage.loadFaces();
        } else {
            aPackage.loadFace(Name.typeName(importName));
        }
    }

    /**
     * Adds the faces from the current package to the imports for a class.
     */
    public void importCurrentPackage() {
        facePackage.loadFaces();
        if (facePackage.name().equals("smalltalk.behavior")) {
            facePackage.loadFace("PrimitiveFactory"); // force this 1st time
        } else {
            faceLibrary.packageNamed("smalltalk").loadFaces();
            faceLibrary.packageNamed("smalltalk.behavior").loadFaces();
            faceLibrary.packageNamed("smalltalk.magnitude").loadFaces(Arrays.asList(literalMagnitudes));
            faceLibrary.packageNamed("smalltalk.collection").loadFaces(Arrays.asList(literalCollections));
        }

        facePackage.loadFaces(peerFaces);
    }

    /**
     * Returns the scope for the face defined by this file.
     *
     * @return the scope for the face defined by this file.
     */
    public Face faceScope() {
        return faceScope;
    }

    /**
     * Returns the package for the face defined by this file.
     *
     * @return the package for the face defined by this file.
     */
    public Package facePackage() {
        return facePackage;
    }

    /**
     * Returns the faces imported by this file.
     *
     * @return the faces imported by this file.
     */
    public List<String> faceImports() {
        return new ArrayList(imports);
    }

    /**
     * Returns the number of imports.
     * @return the number of imports
     */
    public int importCount() {
        return imports.size();
    }

    /**
     * Returns the full name of the face defined by this file.
     *
     * @return the full name of the face defined by this file.
     */
    @Override
    public String nameOf(Container component) {
        return facePackage.qualify(component.name());
    }

    public String fullName() {
        return facePackage().qualify(faceName());
    }

    /**
     * Returns the name of the face defined by this file.
     *
     * @return the name of the face defined by this file.
     */
    public String faceName() {
        return faceScope.name();
    }

    /**
     * Returns the face named (faceName), or null.
     *
     * @return the face named (faceName), or null.
     * @param faceName the name of a face.
     */
    public Face faceNamed(String faceName) {
        if (Name.isQualified(faceName)) {
            return faceLibrary.faceNamed(faceName);
        }

        if (peerFaces.contains(faceName)) {
            return facePackage.faceNamed(faceName);
        }

        return faceLibrary.faceNamed(faceName);
    }

    /**
     * Compiles the Bistro file indicated by this.
     * @return whether compilation succeeded
     * @throws Exception if raised
     */
    public boolean compile() throws Exception {
        return tokenCompiler.compile();
    }

    /**
     * Parses the Bistro file indicated by this.
     * @throws Exception if raised
     */
    public void parse() throws Exception {
        tokenCompiler.parseTokens();
    }

    /**
     * Returns the source code file represented by this file.
     *
     * @return the source code file represented by this file.
     */
    public java.io.File sourceFile() {
        java.io.File packageFolder = facePackage.sourceFolder();
        if (packageFolder == null) return null;
        return new java.io.File(packageFolder, sourceFilename());
    }

    public String sourceFilename() {
        return Name.typeName(faceName()) + sourceExtension;
    }

    /**
     * Returns the target code file represented by this file.
     *
     * @return the target code file represented by this file.
     */
    public java.io.File targetFile() {
        java.io.File packageFolder = facePackage.targetFolder();
        if (packageFolder == null) return null;
        return new java.io.File(packageFolder, targetFilename());
    }

    /**
     * Returns the target code filename for this file.
     *
     * @return the target code filename for this file.
     */
    public String targetFilename() {
        return Name.typeName(faceName()) + targetExtension;
    }

    /**
     * Returns whether the container can resolve a symbolic (reference).
     *
     * @param reference a symbolic reference to be resolved.
     * @return whether the container can resolve a symbolic (reference).
     */
    @Override
    public boolean resolves(Reference reference) {
        return faceLibrary.resolves(reference);
    }

    /**
     * Returns the container that resolves a symbolic (reference).
     *
     * @param reference a symbolic reference to be resolved.
     * @return the container that resolves a symbolic (reference).
     */
    @Override
    public Scope scopeResolving(Reference reference) {
        return resolves(reference) ? this : null;
    }

    /**
     * Returns the type of the variable to which a (reference) resolves.
     *
     * @param reference refers to a variable in some compiler scope.
     * @return the type of the variable to which a (reference) resolves.
     */
    @Override
    public Class resolveType(Reference reference) {
        return faceLibrary.resolveType(reference);
    }

    /**
     * Returns the type name of the variable to which a (reference) resolves.
     *
     * @param reference refers to a variable in some compiler scope.
     * @return the type name of the variable to which a (reference) resolves.
     */
    @Override
    public String resolveTypeName(Reference reference) {
        return faceLibrary.resolveTypeName(reference);
    }


    @Override
    public Emission emitScope() {
        return faceScope().emitScope(emitLibraryScope());
    }

    public Emission emitLibraryScope() {
        return emit("LibraryScope")
                .with("packageName", emitPackage())
                .with("imports", emitImports());
    }

    public Emission emitPackage() {
        return emitStatement(emit("Package").name(packageName()));
    }

    public List<Emission> emitImports() {
        return faceImports().stream()
                .map(imp -> emitImport(imp))
                .collect(Collectors.toList());
    }

    public Emission emitImport(String typeName) {
        return emit("Import").name(typeName);
    }
}
