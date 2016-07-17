//====================================================================
// Constant.java
//====================================================================
package smalltalk.compiler.element;

/**
 * Represents and encodes a Bistro constant value in Java.
 *
 * @author Copyright 1999,2016 Nikolas S. Boyd. All rights reserved.
 */
public class Constant extends Operand {

    /**
     * Defines an interface for visiting instances.
     */
    public static interface Visitor {

        public void visit(Constant constant);
    }

    /**
     * Constructs a new Constant.
     *
     * @param container the container that surrounds this one.
     */
    public Constant(Container container) {
        super(container);
    }

    /**
     * Returns whether the receiver is a literal.
     *
     * @return whether the receiver is a literal.
     */
    @Override
    public boolean isLiteral() {
        return true;
    }

    /**
     * Returns whether a (selector) can be optimized against this operand.
     *
     * @param selector a method selector.
     * @return whether a (selector) can be optimized against this operand.
     */
    @Override
    public boolean optimizes(Selector selector) {
        return true;
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
