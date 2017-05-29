//====================================================================
// Reference.java
//====================================================================
package smalltalk.compiler.element;

import java.util.*;
import org.antlr.runtime.Token;
import smalltalk.Name;
import smalltalk.compiler.Emission;
import static smalltalk.compiler.Emission.emit;
import smalltalk.compiler.scope.*;

/**
 * Represents and encodes a symbolic reference to a variable.
 *
 * @author Copyright 1999,2016 Nikolas S. Boyd. All rights reserved.
 */
public class Reference extends Operand {

    /**
     * Defines an interface for visiting instances.
     */
    public static interface Visitor {

        public void visit(String encodedName);
    }

    /**
     * Identifies self.
     */
    public static final String Self = "self";

    /**
     * Identifies super.
     */
    public static final String Super = "super";

    /**
     * Identifies a data member.
     */
    protected static final String SelfPrefix = "self.";
    protected static final String SystemPrefix = "System.";

    /**
     * Java package root.
     */
    protected static final String RootJava = "java.";
    protected static final String RootJavaX = "javax.";
    protected static final String[] ElementaryPackages = { RootJava, RootJavaX, };
    public static final List<String> ElementaryRoots = Arrays.asList(ElementaryPackages);

    protected static String trim(String value) { return value.replace(".", ""); }
    static final String[] RootPackages = { trim(RootJava), trim(RootJavaX), "smalltalk", };
    public static final List<String> PackageRoots = Arrays.asList(RootPackages);

    /**
     * Returns whether the supplied (typeName) is elementary.
     *
     * @param typeName an elementary candidate name.
     * @return whether the supplied (typeName) is elementary.
     */
    public static boolean isElementary(String typeName) {
        return (ElementaryRoots.stream().anyMatch((root) -> (typeName.startsWith(root))));
    }

    /**
     * Contains translations for the reserved identifiers.
     */
    protected static final Map<String, String> ReservedNames = new HashMap();

    /**
     * Contains translations for Java's reserved identifiers.
     */
    protected static final Map<String, String> RevisedNames = new HashMap();

    /**
     * Initializes the table of reserved identifiers.
     */
    static {
        ReservedNames.put(Self, "this");
        ReservedNames.put(Super, Super);
        ReservedNames.put(Primitive, "Object." + Primitive);

        RevisedNames.put("new", "$new");
        RevisedNames.put("null", "$null");
        RevisedNames.put("case", "$case");
        RevisedNames.put("class", "$class");
        RevisedNames.put("return", "$return");
        RevisedNames.put("interface", "$interface");
    }

    /**
     * Returns a new reference named (name) in the (container) scope.
     *
     * @param name identifies a named object.
     * @param container the container for the reference.
     * @return a new reference named (name) in the (container) scope.
     */
    public static Reference named(String name, Container container) {
        if (name.isEmpty()) return null;
        Reference result = new Reference(container);
        result.name(name);
        return result;
    }

    /**
     * Returns a new reference in the (container) scope.
     *
     * @param token the reference token.
     * @param container the container for the reference.
     * @return a new reference named (name) in the (container) scope.
     */
    public static Reference named(Token token, Container container) {
        if (token == null || token.getText().isEmpty()) return null;
        Reference result = new Reference(container);
        result.name(token.getText());
        result.setLine(token.getLine());
        return result;
    }

    /**
     * Returns the root type.
     *
     * @return the root type.
     */
    public static Class rootType() {
        try {
            return Class.forName(RootClass);
        } catch (Throwable ex) {
            return java.lang.Object.class;
        }
    }

    /**
     * Returns the type named (typeName).
     *
     * @param typeName a fully qualified type name.
     * @return the type named (typeName).
     */
    public static Class typeNamed(String typeName) {
        try {
            if (typeName.endsWith("[]")) {
                typeName = ArrayClass;
            }
            return Class.forName(typeName);
        } catch (Throwable ex) {
            return rootType();
        }
    }

    /**
     * The name of a reference.
     */
    String name;

    /**
     * Constructs a new Reference.
     *
     * @param container the container for the reference.
     */
    public Reference(Container container) {
        super(container);
        name = EmptyString;
    }

    /**
     * Returns whether the symbolic reference has been defined.
     *
     * @return whether the symbolic reference has been defined.
     */
    public boolean isEmpty() {
        return name.isEmpty();
    }

    public boolean isDeeper(Container c) {
        return container().nestLevel() > c.nestLevel();
    }

    public boolean isSimple() {
        if (name.isEmpty()) return false;
        if (Character.isUpperCase(name.charAt(0))) return false;
        return Name.packageName(name).isEmpty();
    }

    public boolean isSystemic() {
        if (name.startsWith(SystemPrefix)) return true;
        if (name.equals(SystemPrefix.replace("\\.", " ").trim())) return true;
        return false;
    }

    /**
     * Returns whether the name refers to self or super.
     *
     * @return whether the name refers to self or super.
     */
    @Override
    public boolean isSelfish() {
        return (name.equals(Self) || name.equals(Super));
    }

    /**
     * Returns whether the name refers to a data member.
     *
     * @return whether the name refers to a data member.
     */
    public boolean isMember() {
        return (name.startsWith(SelfPrefix));
    }

    /**
     * Indicates whether the containing face resolved this reference.
     * @return
     */
    public boolean isHeritage() {
        return facialScope().resolves(this);
    }

    /**
     * Returns whether the name is a reserved word.
     *
     * @return whether the name is a reserved word.
     */
    public boolean isReserved() {
        return (ReservedNames.get(name) != null);
    }

    /**
     * Returns whether the name refers to an elementary type.
     *
     * @return whether the name refers to an elementary type.
     */
    public boolean isElementary() {
        return isElementary(name);
    }

    /**
     * Returns whether the name refers to the primitive factory.
     *
     * @return whether the name refers to the primitive factory.
     */
    public boolean isPrimitive() {
        return (name.equals(Primitive));
    }

    /**
     * Indicates whether this reference has a package name qualifier.
     * @return whether this has a package name
     */
    public boolean isQualified() {
        return !Name.packageName(name).isEmpty();
    }

    /**
     * Returns whether the named reference is untyped.
     *
     * @return whether the named reference is untyped.
     */
    public boolean isGeneric() {
        String typeName = resolvedTypeName();
        if (RootClass.equals(typeName)) {
            return true;
        }
        return false;
    }

    public boolean isTransient() {
        Scope s = containerScope().scopeResolving(this);
        if (s == null || !s.isBlock()) return false;

        Variable v = s.localNamed(name());
        if (v == null) return false;
        return v.isTransient();
    }

    public boolean isLocal() {
        if (!isSimple()) return false;
        if (!containerScope().isBlock()) return false;
        return containerScope().resolves(this);
    }

    /**
     * Returns whether the named reference is a scoped global.
     *
     * @return whether the named reference is a scoped global.
     */
    public boolean isGlobal() {
//        if (this.isSystemic()) {
//            return true;
//        }
        Reference global = globalReference();
        if (global == null) return false;

        Class globalClass = fileScope().resolveType(global);
        return (globalClass != null);
    }

    @Override
    public boolean isReference() {
        return true;
    }

    /**
     * Returns whether the name refers to a Bistro metaclass.
     *
     * @return whether the name refers to a Bistro metaclass.
     */
    @Override
    public boolean refersToMetaclass() {
        String typeName = resolvedTypeName();
        return RootMetaclass.equals(typeName);
    }

    /**
     * Indicates whether the reference needs a definition in the local scope.
     * @return whether this needs resolution
     */
    public boolean needsLocalResolution() {
        if (this.isReserved())   return false;
        if (this.isMember())     return false;
        if (this.isLocal())      return false;
        if (this.isHeritage())   return false;
        if (this.isGlobal())     return false;
        if (this.isElementary()) return false;

        Container c = resolvingScope();
        return (c == null);
    }

    public void resolveUndefined() {
//        if (this.isSystemic()) {
//            return;
//        }
        container().resolveUndefined(this);
    }

    /**
     * Describes the receiver, esp. for instrumentation purposes.
     * @return a description
     */
    @Override
    public String description() {
        return getClass().getName() + " = " + name();
    }

    /**
     * Cleans out any residue left from the parsing process and
     * prepares the receiver for code generation.
     */
    @Override
    public void clean() {
        super.clean();
        if (this.needsLocalResolution()) {
            resolveUndefined();
        }
    }

    /**
     * Returns the global name identified by this reference.
     *
     * @return the global name identified by this reference.
     */
    protected String globalName() {
        return Name.packageName(name);
    }

    /**
     * Returns a reference to the global identified by this reference.
     *
     * @return a reference to the global identified by this reference.
     */
    protected Reference globalReference() {
        String globalName = globalName();
        if (globalName.isEmpty()) return null;
        return Reference.named(globalName, fileScope());
    }

    /**
     * Returns the member name identified by this reference.
     *
     * @return the member name identified by this reference.
     */
    protected String memberName() {
        if (isMember()) {
            return name.substring(SelfPrefix.length());
        }

        return name;
    }

    /**
     * Returns a reference to the member identified by this reference.
     *
     * @return a reference to the member identified by this reference.
     */
    protected Reference memberReference() {
        return Reference.named(memberName(), facialScope());
    }

    /**
     * Returns the scope that resolves this reference.
     *
     * @return the scope that resolves this reference.
     */
    public Container resolvingScope() {
        if ("result".equals(name()) && facialScope().name().equals("Behavior")) {
            getLogger().info("");
        }

        if (this.isReserved()) return null;
        if (this.isMember())   return facialScope();
        if (this.isLocal())    return containerScope();
        if (this.isHeritage()) return facialScope();
        if (this.isGlobal())   return fileScope();

        return containerScope().scopeResolving(this);
    }

    /**
     * Returns the type of the variable to which this reference resolves.
     *
     * @return the type of the variable to which this reference resolves.
     */
    @Override
    public Class resolvedType() {
        if (this.isSelfish()) {
            return rootType();
        }

//        if (this.isSystemic()) {
//            return typeNamed(JavaRoot);
//        }

        if (this.isPrimitive()) {
            return typeNamed(RootPackage + PrimitiveFactory);
        }

        Container scope = resolvingScope();
        if (scope == null) {
            return typeNamed(RootClass);
        }

        if (this.isMember()) {
            return scope.resolveType(memberReference());
        }

        return scope.resolveType(this);
    }

    /**
     * Returns the type name of the variable to which this reference resolves.
     *
     * @return the type name of the variable to which this reference resolves.
     */
    @Override
    public String resolvedTypeName() {
        if (this.isSelfish()) {
            return name;
        }

        if (this.isPrimitive()) {
            return PrimitiveFactory;
        }

        if (this.isElementary()) {
            return name;
        }

        Container scope = resolvingScope();
        if (scope == null) {
            return RootClass;
        }

        if (this.isMember()) {
            return scope.resolveTypeName(memberReference());
        }

        return scope.resolveTypeName(this);
    }

    /**
     * Returns whether a (selector) can be optimized against this operand.
     *
     * @param selector a method selector.
     * @return whether a (selector) can be optimized against this operand.
     */
    @Override
    public boolean optimizes(Selector selector) {
        if (this.isSelfish()) {
            return true;
        }
        if (this.isGlobal()) {
            return true;
        }
        if (selector.isOptimized()) {
            return true;
        }
        return (!this.isGeneric());
    }

    /**
     * Returns the name of the reference.
     *
     * @return the name of the reference.
     */
    @Override
    public String name() {
        String realName = (String) RevisedNames.get(name);
        return (realName == null ? name : realName);
    }

    /**
     * Establishes the name of the reference.
     *
     * @param aName the name of the reference.
     */
    public void name(String aName) {
        name = aName;
    }

    /**
     * Returns the name of the outermost facial scope.
     * @return a face type name
     */
    public String facialTypeName() {
        Container face = facialScope();
        Container c = face.container();
        return (c.isFacial() ? c.name() : face.name());
    }

    /**
     * Returns the Java name of the reference.
     *
     * @return the Java name of the reference.
     */
    public String encodedName() {
        String result = null;
        if (this.isEmpty()) {
            return name;
        }

        if (this.isGlobal()) {
            return name;
        }

        if (this.isSelfish()) {
            return container().revised(ReservedNames.get(name));
        }

        if (this.isLocal()) {
            return this.isTransient() ? name + "[0]" : name;
        }

        if (this.isHeritage()) {
            return facialScope().revised(memberName());
        }

        if (this.isMember()) {
            return facialScope().revised(memberName());
        }

        result = ReservedNames.get(name);
        if (result != null) {
            return result;
        }

        result = RevisedNames.get(name);
        if (result != null) {
            return result;
        }

        Container scope = resolvingScope();
        Container face = facialScope();
        if (scope == null || scope != face) {
            result = name;
        } else {
            result = (scope.isFacial() ? scope.revised(name) : name);
        }

        if (this.refersToMetaclass()) {
            result = result + Dot + MetaclassMember;
        }
        return result;
    }

    /**
     * Accepts a visitor for inspection of the receiver.
     *
     * @param aVisitor visits the receiver for its information.
     */
    public void acceptVisitor(Visitor aVisitor) {
        aVisitor.visit(encodedName());
    }

    /**
     * Accepts a visitor for inspection of the receiver.
     *
     * @param aVisitor visits the receiver for its information.
     */
    @Override
    public void acceptVisitor(Operand.Visitor aVisitor) {
        acceptVisitor((Visitor) aVisitor);
    }

    @Override
    public Emission emitOperand() {
        return emitItem(encodedName());
    }

    @Override
    public Emission emitItem() {
        return emitItem(encodedName());
    }

    public Emission emitArgument(boolean useFinal) {
        Emission makeFinal = useFinal ? emitEmpty() : null;
        String typeName = revisedTypeName();
        String valueName = encodedName();
        return emit("Argument")
                .with("useFinal", makeFinal)
                .type(typeName)
                .name(valueName);
    }

    public String revisedTypeName() {
        String typeName = type();
        return RootClass.equals(typeName) ? SimpleRoot : typeName;
    }

    public String type() {
        return SimpleRoot;
    }
}
