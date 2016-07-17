//====================================================================
// Method.java
//====================================================================
package smalltalk.compiler.scope;

import org.antlr.runtime.tree.CommonTree;

import smalltalk.compiler.element.Base;
import smalltalk.compiler.element.Expression;

/**
 * Represents and encodes a method.
 *
 * @author Copyright 1999,2016 Nikolas S. Boyd. All rights reserved.
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
     * Defines an interface for visiting instances.
     */
    public static interface Visitor {

        public void visit(Method method);
    }

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
        return getClass().getName() + " = " + name();
    }

    /**
     * Returns a unique scope identifier for the receiver.
     * @return a scope ID
     */
    public String scopeID() {
        return "\"" + currentFace().typeName() + "." + name() + "\"";
    }

    /**
     * Cleans out any residue left from the parsing process and prepares the receiver for code generation.
     */
    @Override
    public void clean() {
        super.clean();
        if (!this.isConstructor() && this.needsType()) {
            type(Base.RootClass);
        }
        if (needsAccess()) {
            modifiers.add(0, "public");
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
        return modifiers.contains("wrapped");
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
        wrapper.addStatement(
                wrapperFactory.createWrapper(this, wrapper)
        );
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

    /**
     * Accepts a visitor for inspection of the receiver.
     *
     * @param aVisitor visits the receiver for its information.
     */
    public void acceptVisitor(Visitor aVisitor) {
        aVisitor.visit(this);
    }
}
