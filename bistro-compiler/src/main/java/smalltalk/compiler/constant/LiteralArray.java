//====================================================================
// LiteralArray.java
//====================================================================
package smalltalk.compiler.constant;

import smalltalk.compiler.element.Container;

/**
 * Represents and encodes a literal array.
 *
 * @author Copyright 1999,2017 Nikolas S. Boyd. All rights reserved.
 */
public class LiteralArray extends LiteralCollection {

    /**
     * Constructs a new LiteralArray.
     *
     * @param container the container that surrounds this one.
     */
    public LiteralArray(Container container) {
        super(container);
    }

    /**
     * Returns the primitive type of this scalar.
     *
     * @return the primitive type of this scalar.
     */
    @Override
    public Class primitiveType() {
        return Object[].class;
    }

    /**
     * Returns the name of the primitive factory used to instantiate this scalar.
     *
     * @return the name of the primitive factory used to instantiate this scalar.
     */
    @Override
    public java.lang.String primitiveFactoryName() {
        return "Array";
    }

    /**
     * Returns the type name of this constant reference.
     *
     * @return the type name of this constant reference.
     */
    @Override
    public String resolvedTypeName() {
        return (container().fileScope().needsCollections()
                ? RootClass : "smalltalk.collection.Array");
    }
}
