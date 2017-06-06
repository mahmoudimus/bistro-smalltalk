//====================================================================
// LiteralCollection.java
//====================================================================
package smalltalk.compiler.constant;

import smalltalk.compiler.Emission;
import smalltalk.compiler.element.Container;
import smalltalk.compiler.element.Selector;

/**
 * Represents and encodes a literal collection.
 *
 * @author Copyright 1999,2017 Nikolas S. Boyd. All rights reserved.
 */
public abstract class LiteralCollection extends Scalar {

    /**
     * Constructs a new LiteralCollection.
     *
     * @param container the container that surrounds this one.
     */
    public LiteralCollection(Container container) {
        super(container);
    }

    /**
     * Constructs a new LiteralCollection.
     *
     * @param container the container that surrounds this one.
     * @param aString the scalar value.
     */
    public LiteralCollection(Container container, java.lang.String aString) {
        super(container, aString);
    }

    /**
     * Returns whether a (selector) can be optimized against this operand.
     *
     * @param selector a method selector.
     * @return whether a (selector) can be optimized against this operand.
     */
    @Override
    public boolean optimizes(Selector selector) {
        return !container().fileScope().needsCollections();
    }

    @Override
    public Emission emitOperand() {
        return container().fileScope().needsCollections() ?
                super.emitOperand() : emitCast(declaredType(), super.emitOperand());
    }
}
