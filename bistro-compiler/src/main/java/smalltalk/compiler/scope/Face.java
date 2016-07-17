//====================================================================
// Face.java
//====================================================================
package smalltalk.compiler.scope;

import java.util.*;

import org.antlr.runtime.tree.CommonTree;

import smalltalk.Name;
import smalltalk.compiler.element.Base;
import smalltalk.compiler.element.Container;
import smalltalk.compiler.element.Reference;
import smalltalk.compiler.element.Mirror;

/**
 * Represents and encodes a class or interface definition.
 *
 * @author Copyright 1999,2016 Nikolas S. Boyd. All rights reserved.
 */
public class Face extends Code {

    /**
     * Defines an interface for visiting instances.
     */
    public static interface Visitor {

        public void visit(Face face);
    }

    /**
     * Java package root.
     */
    protected static final String rootPackage = "java.";

    /**
     * Separates metaClass names from their associated class names.
     */
    protected static final String metaSeparator = ".";

    /**
     * Suffix used for metaClass names.
     */
    protected static final String metaName = "mClass";
    public static final String metaSuffix = metaSeparator + metaName;
    public static final String metaNesting = "$" + metaName;

    /**
     * Returns the root base class name.
     *
     * @return the root base class name.
     */
    public static String baseClassName() {
        return Base.RootClass;
    }

    /**
     * Returns the root base metaClass name.
     *
     * @return the root base metaClass name.
     */
    public static String baseMetaclassName() {
        return "smalltalk.behavior.Class";
    }

    /**
     * Returns a meta-name for the supplied (name).
     *
     * @param name the name of some Bistro entity.
     * @return a meta-name
     */
    public static String metaName(String name) {
        return name + metaSuffix;
    }

    /**
     * Returns whether the supplied (faceName) has a metaFace.
     *
     * @param faceName the name of a Bistro face (class or type).
     * @return whether the indicated face has a metaFace
     */
    public static boolean metafaceExists(String faceName) {
        if (faceName.length() == 0) {
            return false;
        }
        if (faceName.equals(Nil)) {
            return false;
        }
        if (faceName.startsWith(rootPackage)) {
            return false;
        }
        Face aFace = Library.current.faceNamed(faceName);
        if (aFace == null) {
            return true; // assumed by default
        }
        Mirror mirror = aFace.typeMirror();
        if (mirror == null) {
            return true; // assumed by default
        }
        return mirror.hasMetaclass();
    }

    /**
     * Returns a metaClass name for the supplied (className).
     *
     * @param className the name of a Bistro class.
     * @return a metaClass name
     */
    public static String metaclassName(String className) {
        return (metafaceExists(className) ? metaName(className) : baseMetaclassName());
    }

    /**
     * Returns a metaType name for the supplied (typeName).
     *
     * @param typeName the name of a Bistro type.
     * @return a metaType name
     */
    public static String metatypeName(String typeName) {
        return (metafaceExists(typeName) ? metaName(typeName) : EmptyString);
    }

    /**
     * Returns the face named (faceName).
     *
     * @param faceName the name of a class or interface.
     * @return the face named (faceName).
     */
    public static Face named(String faceName) {
        return Library.current.faceNamed(faceName);
    }

    /**
     * The name of this face.
     */
    String name;

    /**
     * The name of the face from which this one was derived.
     */
    String baseName = EmptyString;

    /**
     * The class from which this one was derived.
     */
    Class baseClass;

    /**
     * Contains the names of the interfaces implemented (if any).
     */
    List<String> interfaces = new ArrayList();

    /**
     * Refers to the metaFace of this face (if one exists).
     */
    Face metaFace;

    /**
     * Contains the method definitions for this face.
     */
    List<Method> methods = new ArrayList();

    /**
     * Constructs a new Face.
     *
     * @param container the container for this face.
     */
    public Face(Container container) {
        super(container);
        name = EmptyString;
        baseName = EmptyString;
        baseClass = null;
        metaFace = null;
        addLocal();
        addMethod();
    }
    
    @Override
    public String description() {
        return "Face " + name + " -> " + baseName;
    }
    
    public Table memberSymbols() {
        Table result = new Table(this);
        Face aFace = this;
        while (aFace != null) {
            result.withAll(aFace.locals);
            aFace = aFace.baseFace();
        }
        return result;
    }

    /**
     * Returns the metaFace type name.
     */
    public String metaFaceType() {
        return (this.isInterface() ? "Metatype" : "Metaclass");
    }

    /**
     * Returns the metaFace default comment.
     */
    public String defaultComment() {
        return ("/** " + metaFaceType()
                + " for " + typeFace().name() + ". */");
    }

    /**
     * Returns the face comment.
     */
    @Override
    public String comment() {
        return (this.hasComment() ? super.comment() : defaultComment());
    }

    /**
     * Returns the most recently defined file.
     *
     * @return the most recently defined file.
     */
    @Override
    public File currentFile() {
        return (container instanceof File
                ? (File) container() : super.currentFile());
    }

    /**
     * Returns the most recently defined face.
     *
     * @return the most recently defined face.
     */
    @Override
    public Face currentFace() {
        return this;
    }

    /**
     * Returns the facial scope that contains this object.
     *
     * @return the facial scope that contains this object.
     */
    @Override
    public Container facialScope() {
        return this;
    }

    /**
     * Returns the file scope that contains this object.
     *
     * @return the file scope that contains this object.
     */
    @Override
    public Container fileScope() {
        return currentFile();
    }

    /**
     * Returns the metaFace after adding it to the face.
     */
    public Face addMetaface() {
        if (this.hasMetaface()) {
            return metaFace;
        }

        metaFace(new Face(this));
        for (String interfaceName : interfaces) {
            if (metafaceExists(interfaceName)) {
                metaFace.interfaces.add(interfaceName);
            }
        }
        return metaFace;
    }

    /**
     * Cleans out any lint left from the parsing process and prepares the receiver for code generation.
     */
    @Override
    public void clean() {
        resolveBase();
        super.clean();
        if (this.isMetaface()) {
            modifiers.add(0, "static");
        } else if (!this.hasMetaface()) {
            if (!this.hasElementaryBase()) {
                this.addMetaface();
            }
        }

        if (needsAccess()) {
            modifiers.add(0, "public");
        }

        Method method = methods.get(methods.size() - 1);
        if (method.isEmpty()) {
            methods.remove(method);
        }

        boolean foundConstructor = false;
        boolean foundInstantiator = false;

        for (Method m : methods()) {
            m.clean();
            if (m.isAbstract() && !this.isAbstract()) {
                addModifier("abstract");
            }
            if (m.isConstructor()
                    && m.argumentCount() == 0) {
                foundConstructor = true;
            }
            if (m.isInstantiator()) {
                foundInstantiator = true;
            }
        }

        if (this.isInterface()) {
            return;
        }

        List<String> wrappers = new ArrayList();
        for (Method m : methods()) {
            if (m.isWrapper()) {
                wrappers.add(m.name());
            }
        }

        for (Method m : methods()) {
            if (m.isWrapped()) {
                if (wrappers.contains(m.name())) {
                    System.out.println(
                            "Warning! " + className() + " >> "
                            + m.selector().contents()
                            + " was declared wrapped, but already has wrapper"
                    );
                } else if (m.needsWrapper()) {
                    wrappers.add(m.name());
                    System.out.println(
                            "building wrapper method for "
                            + className() + " >> "
                            + m.selector().contents()
                    );
                    Method wrapper = m.buildWrapper();
                    addMethod(wrapper);
                    wrapper.clean();
                } else {
                    System.out.println(
                            "Warning! " + className() + " >> "
                            + m.selector().contents()
                            + " was declared wrapped, but needs no wrapper"
                    );
                }
            }
        }
    }

    /**
     * Cleans out any lint left from the parsing process and prepares the receiver for code generation.
     */
    public void cleanMethods() {
        super.clean();
        Method method = methods.get(methods.size() - 1);
        if (method.isEmpty()) {
            methods.remove(method);
        }

        for (Method m : methods) {
            m.clean();
        }
    }
    
    protected void resolveBase() {
//        System.out.println("base: " + baseName);
        if (baseName.isEmpty()) return;
        if (baseName.contains(".")) {
//            System.out.println("base: " + baseName);
            return;
        }
        Face baseFace = Face.named(baseName);
        if (baseFace != null) baseName = baseFace.fullName();
//        System.out.println("base: " + baseName);
    }

    /**
     * Returns an enumeration of the methods defined in this class.
     */
    public List<Method> methods() {
        return new ArrayList(methods);
    }

    /**
     * Returns the number of methods defined in this class.
     */
    public int methodCount() {
        return methods.size();
    }

    /**
     * Returns the most recently defined method.
     *
     * @return the most recently defined method.
     */
    @Override
    public Method currentMethod() {
        if (methods.isEmpty()) {
            addMethod();
        }
        return (Method) methods.get(methods.size() - 1);
    }

    /**
     * Adds a (method) to those defined by this face.
     *
     * @param method a Bistro method definition.
     */
    protected void addMethod(Method method) {
        method.container(this);
        methods.add(method);
    }

    /**
     * Adds a new method to those defined by this face.
     */
    public void addMethod() {
        addMethod(new Method(this));
    }

    /**
     * Adds a new argument to the current method.
     */
    public void addMethodArgument() {
        currentMethod().addArgument();
    }
    
    public boolean isPackaged() {
        return (typeFace().container() instanceof Package);
    }

    /**
     * Returns whether this face is derived from a Java class.
     *
     * @return whether this face is derived from a Java class.
     */
    public boolean hasElementaryBase() {
        return !baseMirror().hasMetaclass();
    }

    /**
     * Returns whether this face identifies a base Java class.
     *
     * @return whether this face identifies a base Java class.
     */
    public boolean isElementary() {
        return Reference.isElementary(fullName());
    }

    /**
     * Returns whether this face is an inner class.
     *
     * @return whether this face is an inner class.
     */
    public boolean isInnard() {
        return (container instanceof Block);
    }

    /**
     * Returns whether this face is abstract.
     *
     * @return whether this face is abstract.
     */
    @Override
    public boolean isAbstract() {
        if (this.isInterface()) {
            return true;
        }
        return super.isAbstract();
    }

    /**
     * Returns whether this face is an interface.
     *
     * @return whether this face is an interface.
     */
    public boolean isInterface() {
        return "interface".equals(type);
    }

    /**
     * Returns whether this container has facial characteristics.
     *
     * @return whether this container has facial characteristics.
     */
    @Override
    public boolean isFacial() {
        return true;
    }

    /**
     * Returns whether this face is a metaface.
     *
     * @return whether this face is a metaface.
     */
    @Override
    public boolean isMetaface() {
        return container().isFacial();
    }

    /**
     * Returns whether this face has a metaface.
     *
     * @return whether this face has a metaface.
     */
    @Override
    public boolean hasMetaface() {
        return metaFace != null;
    }

    /**
     * Returns whether this container has primitive available.
     *
     * @return whether this container has primitive available.
     */
    @Override
    public boolean hasPrimitiveFactory() {
        return container().hasPrimitiveFactory();
    }

    /**
     * Returns the interfaces defined by this face.
     *
     * @return the interfaces defined by this face.
     */
    public List<String> interfaces() {
        return new ArrayList(interfaces);
    }

    /**
     * Returns the number of interfaces implemented by this face.
     */
    public int interfaceCount() {
        return interfaces.size();
    }

    /**
     * Returns the fully qualified name of this face.
     *
     * @return the fully qualified name of this face.
     */
    public String fullName() {
        return container().nameOf(this);
    }

    /**
     * Returns the name of this face.
     *
     * @return the name of this face.
     */
    public String className() {
        return (this.isMetaface() ? typeFace().name() + " class" : name());
    }

    /**
     * Returns the name of this face.
     *
     * @return the name of this face.
     */
    @Override
    public String name() {
        return name;
    }

    /**
     * Establishes the name of this face.
     *
     * @param faceName the name of this face.
     */
    @Override
    public void name(String faceName) {
        name = faceName;
        if (this.hasMetaface()) {
            metaFace.name(metaName);
        }
    }

    /**
     * Establishes the type of this face.
     *
     * @param faceType the type of this face.
     */
    @Override
    public void type(String faceType) {
        super.type(faceType);
        if (this.hasMetaface()) {
            metaFace.type(faceType);
        }
    }

    /**
     * Establishes this face as a class named (className).
     *
     * @param className the name of this class.
     */
    public void subclass(String className) {
        type("class");
        name(className);
    }

    /**
     * Establishes this face as a type named (typeName).
     *
     * @param typeName the name of this type.
     */
    public void subtype(String typeName) {
        type("interface");
        name(typeName);
    }

    /**
     * Returns the level of this block scope.
     *
     * @return the level of this block scope.
     */
    @Override
    public int blockLevel() {
        return 0;
    }

    /**
     * Returns the name of the Java base from which this face was derived.
     *
     * @return the name of the Java base from which this face was derived.
     */
    public String baseName() {
        if (this.hasNoHeritage()) return defaultBaseClassName();
        if (baseName.equals(metaclassName(Nil))) return defaultBaseMetaclassName();
        return baseName;
    }

    /**
     * Returns the face from which this face was derived.
     *
     * @return the face from which this face was derived.
     */
    public Face baseFace() {
        String baseName = typeFace().baseName();
        return Library.current.faceNamed(baseName);
    }
    
    public boolean hasNoHeritage() {
        return (baseName.isEmpty() || Nil.equals(baseName));
    }

    /**
     * Indicates whether the receiver inherits from a (superFace).
     *
     * @param superFace a candidate super face.
     */
    public boolean inheritsFrom(Face superFace) {
        if (superFace == null) return false;
        if (this == superFace) return true;
        if (this.hasNoHeritage()) return false;
        Face baseFace = baseFace();
        if (baseFace == null) return false;
        return baseFace.inheritsFrom(superFace);
    }

    /**
     * Returns the name of the package that contains the base class.
     *
     * @return the name of the package that contains the base class.
     */
    public String basePackageName() {
        return Name.packageName(fullBaseName());
    }

    /**
     * Returns the fully qualified name of the base class.
     *
     * @return the fully qualified name of the base class.
     */
    public String fullBaseName() {
        if (baseFace() == null) return EmptyString;
        return baseFace().fullName();
    }

    /**
     * Returns the base class from which this face was derived.
     *
     * @return the base class from which this face was derived.
     */
    public Class baseClass() {
        if (baseClass == null) {
            try {
                baseClass = Class.forName(fullBaseName());
            } catch (Throwable ex) {
                return null;
            }
        }
        return baseClass;
    }

    /**
     * Returns the mirror for the base class from which this face was derived.
     *
     * @return the mirror for the base class from which this face was derived.
     */
    public Mirror baseMirror() {
        return Mirror.forClass(baseClass());
    }

    /**
     * Establishes the name of the base from which this face was derived.
     *
     * @param faceName the name of the base from which this face was derived.
     */
    public void baseName(String faceName) {
        baseName = faceName;
//        System.out.println(name + " extends " + baseName);
    }

    /**
     * Establishes the name of the base from which this face was derived. If the supplied (node) includes a comment, use
     * it.
     *
     * @param node an abstract syntax tree node.
     */
    public void baseName(CommonTree node) {
        comment(commentFrom(node));
        baseName(node.getText().trim());
    }
    
    /**
     * Returns the package containing this face.
     * @return a Package
     */
    public Package typePackage() {
        return typeFace().container().asScope(Package.class);
    }

    /**
     * Returns the normal face for this face.
     *
     * @return the normal face for this face.
     */
    public Face typeFace() {
        return (this.isMetaface() ? container().asScope(Face.class) : this);
    }

    /**
     * Returns the type name of this face.
     *
     * @return the type name of this face.
     */
    public String typeName() {
        return (this.isMetaface() ? metaName(typeFace().name()) : name());
    }

    /**
     * Returns the type for this face (if one exists).
     *
     * @return the type for this face (if one exists).
     */
    public Class typeClass() {
        try {
            return Class.forName(typeFace().fullName());
        } catch (Throwable e) {
            return null;
        }
    }

    /**
     * Returns the mirror that reflects the class of this face.
     *
     * @return the mirror that reflects the class of this face.
     */
    public Mirror typeMirror() {
        Class aClass = typeClass();
        if (aClass == null) {
            return null;
        }
        return Mirror.forClass(aClass);
    }

    /**
     * Returns the metaFace for this face.
     *
     * @return the metaFace for this face.
     */
    public Face metaFace() {
        return metaFace;
    }

    /**
     * Establishes (aFace) as the metaFace for this face.
     *
     * @param aFace the metaFace for this face.
     */
    public void metaFace(Face aFace) {
        metaFace = aFace;
        aFace.container(this);
        metaFace.name(metaName);
        metaFace.type(type());
        metaFace.baseName(metaclassName(baseName));
    }

    /**
     * Returns Java code that defines the metaFace instance.
     *
     * @return Java code that defines the metaFace instance.
     */
    public String metaInstance() {
        return (metaFace == null
                ? "new Metaclass( " + typeFace().name() + ".$class() );"
                : "new " + metaFace.name() + "( " + name() + ".class );");
    }

    /**
     * Returns the Java type for the metaFace instance.
     *
     * @return the Java type for the metaFace instance.
     */
    public String metaInstanceTypeName() {
        return (metaFace == null ? baseClassName() : metaFace.name());
    }

    /**
     * Returns the Java base class name.
     *
     * @return the Java base class name.
     */
    public String defaultBaseClassName() {
        return EmptyString;
    }

    /**
     * Returns the Java base metaClass name.
     *
     * @return the Java base metaClass name.
     */
    public String defaultBaseMetaclassName() {
        return (isInterface() ? defaultBaseClassName() : baseMetaclassName());
    }

    /**
     * Returns the names of the interfaces implemented by this class.
     *
     * @return the names of the interfaces implemented by this class.
     */
    public List<String> interfaceNames() {
        List<String> results = new ArrayList();
        for (String interfaceName : interfaces) {
            if (Name.packageName(interfaceName).length() > 0) {
                results.add(interfaceName);
            } else {
                Face face = Library.current.faceNamed(interfaceName);
                if (face != null) {
                    results.add(face.fullName());
                }
            }
        }
        return results;
    }

    /**
     * Adds the supplied (interfaceName) to those implemented by this class.
     *
     * @param interfaceName the interface implemented by this face.
     */
    public void implementsInterface(String interfaceName) {
        interfaces.add(interfaceName);
        if (this.hasMetaface()) {
            if (metafaceExists(interfaceName)) {
                metaFace.implementsInterface(interfaceName);
            }
        }
    }

    /**
     * Returns a revised identifier derived from the supplied (identifier).
     *
     * @param identifier identifies a named entity.
     * @return a revised identifier derived from the supplied (identifier).
     */
    @Override
    public String revised(String identifier) {
        if (identifier.startsWith("this")
                || identifier.startsWith("super")) {
            return typeName() + "." + identifier;
        } else {
            return typeName() + ".this." + identifier;
        }
    }

    /**
     * Returns the type of a reference resolved from a base class (if any).
     *
     * @param reference a symbolic reference to be resolved.
     * @return the type of a reference resolved from a base class (if any).
     */
//    public Class resolveTypeFromInherited(Reference reference) {
//        Face baseFace = baseFace();
//        if (baseFace == null) {
//            if (reference.name().equals("position")) {
//                System.out.println(elementName() + " can't resolve " + reference.name() + " and no further superclass");
//            }
//            return null;
//        }
//
//        Class baseClass = baseClass();
//        Class referenceType = baseFace.resolveType(reference);
//        if (referenceType != null) {
//            if (reference.name().equals("position")) {
//                System.out.println(baseFace.elementName() + "::" + name() + " resolved " + reference.name());
//            }
//            return referenceType;
//        }
//
//        referenceType = baseMirror().typeFieldNamed(reference.name());
//        if (referenceType != null) {
//            if (reference.name().equals("position")) {
//                System.out.println(baseMirror().reflectedClass().getClass().getSimpleName() + "::" + name() + " resolved " + reference.name());
//            }
//            return referenceType;
//        }
//        
//        return referenceType;
//    }

    /**
     * Returns the type name of a reference resolved from a base class (if any).
     *
     * @param reference a symbolic reference to be resolved.
     * @return the type name of a reference resolved from a base class (if any).
     */
//    public String resolveTypeNameFromInherited(Reference reference) {
//        Class referenceType = resolveTypeFromInherited(reference);
//        return (referenceType == null ? null : referenceType.getName());
//    }

    /**
     * Returns whether the container can resolve a symbolic (reference).
     *
     * @param reference a symbolic reference to be resolved.
     * @return whether the container can resolve a symbolic (reference).
     */
    @Override
    public boolean resolves(Reference reference) {
        String symbol = reference.name();
        if (reference.isSelfish()) {
            return true;
        }
        
        if (this.hasLocal(symbol)) {
            return true;
        }

        if (this.hasNoHeritage()) {
            return false;
//            return container().resolves(reference);
        }

        if (baseFace() == null) {
            if (reference.isSimple()) {
//                System.out.println("unresolved " + reference.description() + " at " + description());
            }
            return false;
        }
        return baseFace().resolves(reference);
    }

    /**
     * Returns the type of the variable to which a (reference) resolves.
     *
     * @param reference refers to a variable in some compiler scope.
     * @return the type of the variable to which a (reference) resolves.
     */
    @Override
    public Class resolveType(Reference reference) {
        String symbol = reference.name();
        if (reference.isSelfish()) {
            return currentFace().typeClass();
        }
        
        if (this.hasLocal(symbol)) {
            return locals.symbolNamed(symbol).resolvedType();
        }

        if (this.hasNoHeritage()) {
            return container().resolveType(reference);
        }

        return baseFace().resolveType(reference);
    }

    /**
     * Returns the type name of the variable to which a (reference) resolves.
     *
     * @param reference refers to a variable in some compiler scope.
     * @return the type name of the variable to which a (reference) resolves.
     */
    @Override
    public String resolveTypeName(Reference reference) {
        String symbol = reference.name();
        if (reference.isSelfish()) {
            return currentFace().name();
        }
        
        if (this.hasLocal(symbol)) {
            return locals.symbolNamed(symbol).type();
        }

        if (this.hasNoHeritage()) {
            return container().resolveTypeName(reference);
        }

        return baseFace().resolveTypeName(reference);
    }

    /**
     * Accepts a visitor for inspection of the receiver.
     *
     * @param aVisitor visits the receiver for its information.
     */
    public void acceptVisitor(Visitor aVisitor) {
        aVisitor.visit(this);
    }

    /**
     * Accepts a visitor for inspection of the receiver interfaces.
     *
     * @param aVisitor visits the receiver for its information.
     */
    public void acceptInterfaceVisitor(Reference.Visitor aVisitor) {
        if (interfaces.isEmpty()) {
            return;
        }

        for (String interfaceName : interfaces) {
            aVisitor.visit(interfaceName);
        }
    }

    /**
     * Accepts a visitor for inspection of the receiver methods.
     *
     * @param aVisitor visits the receiver for its information.
     */
    public void acceptMethodVisitor(Method.Visitor aVisitor) {
        for (Method m : methods) {
            aVisitor.visit(m);
        }
    }
}
