//====================================================================
// Operand.java
//====================================================================
package smalltalk.compiler.element;

import smalltalk.compiler.Emission;

/**
 * Represents an operand and translates it into Java.
 *
 * @author Copyright 1999,2016 Nikolas S. Boyd. All rights reserved.
 */
public class Operand extends Container {

    /**
     * Defines an interface for visiting instances.
     */
    public static interface Visitor {

        public void visit(Operand operand);
    }

    public static interface Emitter {

        public Emission visitResult(Operand operand);
    }

    /**
     * Constructs a new Operand.
     *
     * @param container the container for this operand.
     */
    protected Operand(Container container) {
        super(container);
    }

    /**
     * Cleans out any residue left from the parsing process and prepares the receiver for code generation.
     * @return an Operand
     */
    public Operand cleanTerm() {
        this.clean();
        return this;
    }

    /**
     * Returns whether the name refers to self or super.
     *
     * @return whether the name refers to self or super.
     */
    public boolean isSelfish() {
        return false;
    }

    /**
     * Returns whether the receiver is a literal.
     *
     * @return whether the receiver is a literal.
     */
    public boolean isLiteral() {
        return false;
    }

    /**
     * Returns whether the receiver is an exit message.
     *
     * @return whether the receiver is an exit message.
     */
    public boolean isExit() {
        return false;
    }

    /**
     * Returns whether the receiver is a message.
     *
     * @return whether the receiver is a message.
     */
    public boolean isMessage() {
        return false;
    }

    /**
     * Returns whether the operand needs to be a term when used as a message receiver.
     *
     * @return whether the operand needs to be a term when used as a message receiver.
     */
    public boolean receiverNeedsTerm() {
        return false;
    }

    /**
     * Returns the type of the variable to which this reference resolves.
     *
     * @return the type of the variable to which this reference resolves.
     */
    public Class resolvedType() {
        Class root = rootObjectClass();
        return (root == null ? java.lang.Object.class : root);
    }

    /**
     * Returns the type name of the variable to which this reference resolves.
     *
     * @return the type name of the variable to which this reference resolves.
     */
    public String resolvedTypeName() {
        return Base.RootClass;
    }

    /**
     * Returns whether the referenced variable type is a primitive.
     *
     * @return whether the referenced variable type is a primitive.
     */
    public boolean resolvesToPrimitive() {
        Class type = resolvedType();
        if (type == null) return false;
        return PrimitiveTypes.containsValue(resolvedType());
    }

    /**
     * Returns whether a (selector) can be optimized against this operand.
     *
     * @param selector a method selector.
     * @return whether a (selector) can be optimized against this operand.
     */
    public boolean optimizes(Selector selector) {
        if (selector.isOptimized()) return true;
        String typeName = resolvedTypeName();

        if (RootClass.equals(typeName)) {
            return false;
        }

        if (BehaviorClass.equals(typeName)) {
            return false;
        }

        return (true);
    }

    /**
     * Returns whether the receiver consumes an (operand).
     *
     * @param operand a potentially consumable operand.
     * @return whether the receiver consumes an (operand).
     */
    public boolean consumes(Operand operand) {
        return false;
    }

    /**
     * Accepts a visitor for inspection of the receiver.
     *
     * @param aVisitor visits the receiver for its information.
     */
    public void acceptVisitor(Visitor aVisitor) {
        aVisitor.visit(this);
    }

    public Emission visitResult(Emitter aVisitor) {
        return aVisitor.visitResult(this);
    }
}
