//====================================================================
// BistroCompiler.java
//====================================================================
package smalltalk.compiler;

import java.util.*;
import java.util.stream.Collectors;

import javax.tools.*;
import javax.tools.JavaCompiler.CompilationTask;
import org.antlr.runtime.*;

import smalltalk.compiler.scope.File;
import smalltalk.compiler.scope.Package;
import smalltalk.compiler.scope.ClassPath;

/**
 * Translates a Bistro source file into a Java source file.
 *
 * @author Copyright 1999,2017 Nikolas S. Boyd. All rights reserved.
 */
public class BistroCompiler {

    /**
     * Indicates the number of standard arguments.
     */
    public static final int StandardArgumentCount = 3;

    /**
     * Refers to the Bistro lexical analyzer.
     */
    TokenSource lexer;

    /**
     * Refers to the Bistro parser.
     */
    Bistro parser;

    String workFolder = System.getProperty(WorkFolder);

    java.io.File sourceBase;
    java.io.File targetBase;
    java.io.File classBase;

    /**
     * Contains the parsed source file scopes indexed by filename.
     */
    HashMap<String, File> parsedFiles = new HashMap();
    Package parsedPackage;

    /**
     * Constructs a new BistroCompiler.
     */
    public BistroCompiler() { }

    /**
     * Validates the base paths supplied on the command line.
     *
     * @param paths the command line arguments.
     * @return whether paths are valid
     */
    protected boolean validate(String paths[]) {
        if (StandardArgumentCount + 1 > paths.length) {
            displayUsage();
            return false;
        }

        // validate the basepaths
        sourceBase = validatePath(paths[0], "source", false);
        targetBase = validatePath(paths[1], "target", true);
        classBase  = validatePath(paths[2], "class ", true);

        if (sourceBase == null || targetBase == null || classBase == null) {
            displayUsage();
            return false;
        }

        ClassPath.current.loadPaths(sourceBase, targetBase, classBase);
        return true;
    }

    /**
     * Validates a base folder path.
     * @param folderPath a base folder path
     * @param name the path name
     * @param createIfMissing whether to create it
     * @return a validated folder reference
     */
    private java.io.File validatePath(String folderPath, String name, boolean createIfMissing) {
        java.io.File folder = new java.io.File(folderPath);
        if (!folder.isAbsolute()) {
            if (folderPath.startsWith(Parent)) {
                String parent = new java.io.File(workFolder).getParent();
                folder = new java.io.File(parent, folderPath.substring(Parent.length() + 1));
            }
            else {
                folder = new java.io.File(workFolder, folderPath);
            }
        }

        if (folder.exists()) {
            System.out.println(name + " basepath = " + folderPath);
            return folder;
        }

        if (createIfMissing) {
            if (folder.mkdirs()) return folder;

            String message = String.format("Can't create %s basepath", name);
            System.out.println(message);
            return null;
        }

        String message = String.format("Can't locate %s basepath", name);
        System.out.println(message);
        return null;
    }

    /**
     * Returns a list of all the face names contained in a package.
     *
     * @param directory a package source directory.
     * @return a list of all the face names contained in a package.
     */
    public String[] faceNamesFrom(java.io.File directory) {
        String[] faceNames = directory.list(File.sourceFileFilter);
        String[] result = new String[faceNames.length];
        for (int n = 0; n < faceNames.length; n++) {
            result[n] = faceNames[n].substring(
                    0, faceNames[n].length() - File.sourceExtension.length()
            );
        }
        return result;
    }

    /**
     * Compile a specific parsed source file named (fileName).
     *
     * @param fileName identifies a parsed source file.
     * @throws Exception if raised
     */
    public void compileFileNamed(String fileName) throws Exception {
        File fileScope = parsedFiles.get(fileName);
        if (fileScope == null) {
            System.out.println("No parsed file found " + fileName);
            return;
        }

        String baseName = fileScope.faceScope().fullBaseName();
        if (parsedFiles.containsKey(baseName)) {
            compileFileNamed(baseName);
            return;
        }

        List<String> interfaces = fileScope.faceScope().interfaceNames();
        interfaces.retainAll(parsedFiles.keySet());
        if (!interfaces.isEmpty()) {
            compileFileNamed((String) interfaces.get(0));
            return;
        }

        System.out.println("Writing " + fileName);
        fileScope.compile();
        parsedFiles.remove(fileName);
    }

    /**
     * Compiles all the parsed source files.
     * @throws Exception if raised
     */
    public void compileParsedFiles() throws Exception {
        System.out.println();
        if ("smalltalk".equals(parsedPackage.name())) return;
        HashMap<String, File> copy = new HashMap(parsedFiles);
        while (!parsedFiles.isEmpty()) {
            compileFileNamed(parsedFiles.keySet().iterator().next());
        }
        parsedFiles = copy;
    }

    public void compileJavaFiles() throws Exception {
        if (parsedFiles.isEmpty()) {
            System.out.println("Nothing was compiled!");
            return;
        }

        String classPath = classBase.getAbsolutePath();
        String servletPath = workFolder + ServletAPI;
        String completePath = ClassPath.buildPath(classPath, servletPath);
        String[] options = { "-nowarn", "-d", classPath, "-cp", completePath };

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        try (StandardJavaFileManager fileManager =
                compiler.getStandardFileManager(null, null, null)) {

            CompilationTask compile =
                compiler.getTask(null, fileManager, null,
                    Arrays.asList(options), null,
                    fileManager.getJavaFileObjectsFromStrings(getTargetFilePaths()));

            compile.call();
        }
    }

    public List<String> getTargetFilePaths() {
        return parsedFiles.values().stream()
                .map(f -> f.targetFile().getAbsolutePath())
                .collect(Collectors.toList());
    }

    /**
     * Displays a message indicating how to use the Bistro compiler.
     */
    public static void displayUsage() {
        System.out.println(
                "Usage: java " + ClassName + "  sourceBasepath  targetBasepath  classBasepath  className\n"
                + "or     java " + ClassName + "  sourceBasepath  targetBasepath  classBasepath  packageName.*"
        );
    }

    public void parseFiles(String args[]) throws Exception {
        for (int n = StandardArgumentCount; n < args.length; n++) {
            String faceName = args[n];
            parsedPackage = Package.named(Package.nameFrom(faceName));

            if (Package.namesAllFaces(faceName)) {
                parsedFiles.putAll(parsedPackage.parseSources());
            } else {
                parsedFiles.putAll(parsedPackage.parseSource(faceName));
            }
        }
    }

    /**
     * Compiles Bistro source files into a Java class files.
     *
     * @param args the command line arguments.
     */
    public void compileFiles(String args[]) {
        try {
            if (!validate(args)) return;
            parseFiles(args);
            compileParsedFiles();
            compileJavaFiles();
            System.out.println("Done");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Compiles Bistro source files into a Java class files.
     *
     * @param args the command line arguments.
     */
    public static void main(String args[]) {
        new BistroCompiler().compileFiles(args);
    }

    private static final String Parent = "..";
    private static final String WorkFolder = "user.dir";
    private static final String ServletAPI = "/lib/javax.servlet-api-3.1.0.jar";
    private static final String ClassName = BistroCompiler.class.getName();
}
