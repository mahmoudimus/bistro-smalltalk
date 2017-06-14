//====================================================================
// Method.java
//====================================================================
package smalltalk.compiler.scope;

import java.util.*;
import java.util.stream.Collectors;
import org.antlr.runtime.tree.CommonTree;

import static smalltalk.Name.*;
import smalltalk.compiler.element.*;
import smalltalk.compiler.Emission;
import static smalltalk.compiler.Emission.emit;

/**
 * Represents and encodes a method.
 *
 * @author Copyright 1999,2017 Nikolas S. Boyd. All rights reserved.
 */
public class Method extends Block {

    /**
     * Defines an interface for creating wrapper messages.
     */
    public static interface WrapperFactory {

        public Expression createWrapper(Method method, Method wrapper);
    }

    /**
     * Creates new wrapper messages.
     */
    public static WrapperFactory wrapperFactory = null;

    /**
     * Contains the code of a primitive Java method.
     */
    String primitiveCode;

    /**
     * Constructs a new Method.
     *
     * @param face the face that contains this method.
     */
    public Method(Face face) {
        super(face);
        primitiveCode = null;
    }

    /**
     * Describes the receiver, esp. for instrumentation purposes.
     */
    @Override
    public String description() {
        return currentFace().typeName() + Associate + name();
    }

    /**
     * Returns a unique scope identifier for the receiver.
     * @return a scope ID
     */
    public String scopeID() {
        return quoted(currentFace().typeName() + Dot + name());
    }

    /**
     * Cleans out any residue left from the parsing process and prepares the receiver for code generation.
     */
    @Override
    public void clean() {
        super.clean();
        if (!this.isConstructor() && this.needsType()) {
            type(RootClass);
        }
        if (needsAccess()) {
            modifiers.add(0, Public);
        }
    }

    /**
     * Establishes the name of the method. If the supplied (node) includes a comment, use it.
     *
     * @param node an abstract syntax tree node.
     */
    public void name(CommonTree node) {
        comment(commentFrom(node));
        name(node.getText().trim());
    }

    /**
     * Establishes the type of the method. If the supplied (node) includes a comment, use it.
     *
     * @param node an abstract syntax tree node.
     */
    public void type(CommonTree node) {
        comment(commentFrom(node));
        type(node.getText().trim());
    }

    /**
     * Returns whether this method has been defined.
     *
     * @return whether this method has been defined.
     */
    public boolean isEmpty() {
        return selector.isEmpty();
    }

    /**
     * Returns whether the code signature is abstract.
     *
     * @return whether the code signature is abstract.
     */
    @Override
    public boolean isAbstract() {
        if (currentFace().isInterface()) {
            return true;
        }
        return super.isAbstract();
    }

    /**
     * Returns whether the method is a constructor.
     *
     * @return whether the method is a constructor.
     */
    @Override
    public boolean isConstructor() {
        return (name().equals(currentFace().name()));
    }

    /**
     * Returns whether the method is an instantiator.
     *
     * @return whether the method is an instantiator.
     */
    public boolean isInstantiator() {
        return (selector.isNew());
    }

    /**
     * Returns whether the method has primitive code.
     * @return whether the method is primitive
     */
    public boolean isPrimitive() {
        return primitiveCode != null;
    }

    /**
     * Returns whether this block is a method.
     *
     * @return whether this block is a method.
     */
    @Override
    public boolean isMethod() {
        return true;
    }

    /**
     * Indicates whether this method is a wrapper method.
     *
     * @return whether this method is a wrapper method.
     */
    public boolean isWrapper() {
        if (argumentCount() == 0) {
            return false;
        }
        if (this.isStatic()) {
            return false;
        }
        if (this.isConstructor()) {
            return false;
        }
        return this.isPublic() && !arguments.hasTypedNames();
    }

    /**
     * Indicates whether this method is a wrapped method.
     *
     * @return whether this method is a wrapped method.
     */
    public boolean isWrapped() {
        return modifiers.contains(Wrapped);
    }

    /**
     * Indicates whether this method needs a wrapper method.
     *
     * @return whether this method needs a wrapper method.
     */
    public boolean needsWrapper() {
        if (argumentCount() == 0) {
            return false;
        }
        if (this.isStatic()) {
            return false;
        }
        if (this.isConstructor()) {
            return false;
        }
        return arguments.hasErasableTypes();
    }

    /**
     * Returns a wrapper method with its argument types erased.
     *
     * @return a wrapper method with its argument types erased.
     */
    public Method buildWrapper() {
        Method wrapper = new Method(currentFace());
        wrapper.name(selector.contents());
        wrapper.type(type);
        wrapper.comment(comment);
        arguments.eraseTypes(wrapper.arguments);
        wrapper.addStatement(wrapperFactory.createWrapper(this, wrapper));
        return wrapper;
    }

    /**
     * Returns this method (when it's the current scope).
     *
     * @return this method (when it's the current scope).
     */
    @Override
    public Method currentMethod() {
        return this;
    }

    /**
     * Establishes whether this method contains an exit expression.
     *
     * @param aBoolean indicates whether this method contains an exit expression.
     */
    @Override
    public void exits(boolean aBoolean) {
        // not for method scopes
    }

    /**
     * Establishes whether this method contains an exit expression.
     *
     * @param aBoolean indicates whether this method contains an exit expression.
     */
    @Override
    protected void containsExit(boolean aBoolean) {
        containsExit = aBoolean;
    }

    /**
     * Establishes this method as a primitive Java method.
     *
     * @param code the primitive Java code for this method.
     */
    public void code(String code) {
        primitiveCode = code;
    }

    /**
     * Returns the primitive code of this method.
     * @return the primitive code
     */
    public String code() {
        if (facialScope().isMetaface()) {
            return primitiveCode.replace(LF, LF + TAB);
        }
        return primitiveCode;
    }

    /**
     * Returns a revised identifier derived from the supplied (identifier).
     *
     * @param identifier identifies a named entity.
     * @return a revised identifier derived from the supplied (identifier).
     */
    @Override
    public String revised(String identifier) {
        // will this method use an inner class for its block?
        return (this.hasLocals()
                ? super.revised(identifier) : identifier);
    }

    /**
     * Returns whether this container has primitive available.
     *
     * @return whether this container has primitive available.
     */
    @Override
    public boolean hasPrimitiveFactory() {
        return this.hasLocals(); // || container().hasPrimitiveFactory();
    }

    @Override
    public Face facialScope() {
        return super.facialScope().asScope(Face.class);
    }

    @Override
    public File fileScope() {
        return super.fileScope().asScope(File.class);
    }

    static final String NewSignature = RootClass + ":$new";
    static final String[] OverrideSignatures = {
        "boolean:equals",
        "int:hashCode",
        "void:printStackTrace",
    };

    static final List<String> StandardOverrides = Arrays.asList(OverrideSignatures);

    public boolean operatesOnSame() {
        if (arguments().isEmpty()) return false;
        String argType = arguments().get(0).type();
        String faceName = facialScope().name();
        if (faceName.endsWith("LargeInteger")) {
            getLogger().info("");
        }
        return faceName.equals(argType);
    }

    public boolean overridesArguments(Method m) {
        Face mFace = m.facialScope();
        if (argumentCount() == 1) {
            String argType = arguments().get(0).typeFace().name();
            if (argType.equals(mFace.name())) return false; // concrete operation!
        }

        if (m.argumentCount() == argumentCount()) {
            List<Variable> args = arguments();
            List<Variable> margs = m.arguments();
            for (int index = 0; index < args.size(); index++) {
                if (!args.get(index).typeFace().inheritsFrom(margs.get(index).typeFace())) {
                    return false; // arg type not derived from base method arg type
                }
            }
            return true; // all arg types derive from base method arg types
        }

        return false; // mismatched
    }

    public boolean overrides(Method m) {
        if (facialScope() == m.facialScope()) return false;

        if (facialScope().inheritsFrom(m.facialScope())) {
            if (m.fullSignature().equals(erasedSignature())) {
                
            }
            if (m.shortSignature().equals(shortSignature())) {
                return overridesArguments(m);
            }
        }

        return false;
    }

    public boolean overridesParent() {
        String sig = shortSignature();
        if (StandardOverrides.stream().anyMatch(ovr -> sig.startsWith(ovr))) {
            return true; // override standard signature
        }

        Face facialScope = facialScope();
        if (facialScope.isInterface()) return false;

        if (sig.startsWith(NewSignature)) {
            return !facialScope.name().equals("Behavior");
        }

        if (facialScope.hasNoHeritage()) return false;

//        if (name().equals("$equal") && !arguments().get(0).type().equals(SimpleRoot)) {
//            return false;
//        }

        if (facialScope.baseFace() == null) {
            System.out.println("Warning! method face has no base " + description());
            return false;
        }

        return facialScope.baseFace().overridenBy(this);
    }

    public String shortSignature() {
        return type() + Colon + name();
    }

    public String erasedSignature() {
        return shortSignature() +
                emitTerm(emitList(argumentErasures())).result().render();
    }

    public String fullSignature() {
        return shortSignature() +
                emitTerm(emitList(argumentSignatures())).result().render();
    }

    public List<Emission> argumentErasures() {
        return arguments().stream()
                .map(arg -> emitItem(SimpleRoot))
                .collect(Collectors.toList());
    }

    public List<Emission> argumentSignatures() {
        return arguments().stream()
                .map(arg -> emitItem(arg.shortSignature()))
                .collect(Collectors.toList());
    }

    public Emission emitNotes() {
        return overridesParent() ? emitItem(Override) : emitEmpty();
    }

    @Override
    public Emission emitScope() {
        if (this.isAbstract()) {
            return emitAbstract();
        }
        else if (this.isPrimitive()) {
            return emitPrimitive();
        }
        else if (this.exits()) {
            return emitExited();
        }
        else {
            return emitSimple();
        }
    }

    public Emission emitSimple() {
        return emit("MethodScope")
                .with("comment", comment)
                .with("type", emitTypeName(type()))
                .with("signature", emitSignature())
                .with("locals", emitLocals())
                .with("content", emitExitedContents());
    }

    public Emission emitAbstract() {
        return emit("MethodEmpty")
                .with("comment", comment)
                .with("type", emitTypeName(type()))
                .with("signature", emitSignature());
    }

    @Override
    public Emission emitSignature() {
        return emit("MethodSignature")
                .with("notes", emitNotes())
                .with("details", emitModifiers())
                .with("type", emitTypeName(type()))
                .with("name", name())
                .with("arguments", emitArguments())
                .with("exceptions", emitExceptions())
                ;
    }

    @Override
    public Emission emitPrimitive() {
        return emit("PrimitiveMethod")
                .with("comment", comment)
                .with("type", emitTypeName(type()))
                .with("signature", emitSignature())
                .with("code", code());
    }

    public Emission emitExited() {
        return emit("ExitedMethod")
                .with("comment", comment)
                .with("type", emitTypeName(type()))
                .with("signature", emitSignature())
                .with("locals", emitLocals())
                .with("content", emitExitedContents())
                .with("scope", scopeID());
    }

    public Emission emitExitedContents() {
        if (this.hasLocals()) {
            if (this.returnsVoid()) {
                return emitStatement(emitClosureValue(emitClosure()));
            }
            else {
                return (emitContents());
            }
        }
        else {
            return (emitContents());
        }
    }
}
