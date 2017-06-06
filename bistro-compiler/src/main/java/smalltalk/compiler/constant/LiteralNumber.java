//====================================================================
// LiteralNumber.java
//====================================================================
package smalltalk.compiler.constant;

import smalltalk.compiler.Emission;
import smalltalk.compiler.element.Container;
import smalltalk.compiler.element.Selector;

/**
 * Represents and encodes a literal number.
 *
 * @author Copyright 1999,2017 Nikolas S. Boyd. All rights reserved.
 */
public abstract class LiteralNumber extends Scalar {

    /**
     * Constructs a new LiteralNumber.
     *
     * @param container the container that surrounds this one.
     * @param aValue the value of this literal.
     */
    public LiteralNumber(Container container, java.lang.String aValue) {
        super(container, aValue);
    }

    /**
     * Converts the value to a negative number.
     */
    public void negate() {
        value = "-" + value;
    }

    /**
     * Returns whether a (selector) can be optimized against this operand.
     *
     * @param selector a method selector.
     * @return whether a (selector) can be optimized against this operand.
     */
    @Override
    public boolean optimizes(Selector selector) {
        return !container().fileScope().needsMagnitudes();
    }

    @Override
    public Emission emitOperand() {
        if (container().fileScope().needsMagnitudes()) {
            super.emitOperand();
        }

        return emitCast(declaredType(), super.emitOperand());
    }
}
