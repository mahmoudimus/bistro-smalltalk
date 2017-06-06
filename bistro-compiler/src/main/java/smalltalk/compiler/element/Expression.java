//====================================================================
// Expression.java
//====================================================================
package smalltalk.compiler.element;

/**
 * Represents a Bistro expression and translates it into Java.
 *
 * @author Copyright 1999,2017 Nikolas S. Boyd. All rights reserved.
 */
public class Expression extends Operand {

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
}
