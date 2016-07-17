//====================================================================
// Expression.java
//====================================================================
package smalltalk.compiler.element;

/**
 * Represents a Bistro expression and translates it into Java.
 *
 * @author Copyright 1999,2016 Nikolas S. Boyd. All rights reserved.
 */
public class Expression extends Operand {

    /**
     * Defines an interface for visiting instances.
     */
    public static interface Visitor {

        public void visit(Expression expression);
    }

    /**
     * Reference to the primitive factory.
     */
    protected Reference localPrimitive;

    /**
     * Constructs a new Expression.
     *
     * @param container the container for this expression.
     */
    public Expression(Container container) {
        super(container);
        localPrimitive = Reference.named(Reference.Primitive, container);
    }

    /**
     * Returns whether the message exits the method.
     *
     * @return whether the message exits the method.
     */
    public boolean returnsResult() {
        return false;
    }

    /**
     * Returns the local name for the primitive factory.
     *
     * @return the local name for the primitive factory.
     */
    public String localPrimitive() {
        return localPrimitive.name();
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
