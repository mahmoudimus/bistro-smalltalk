//====================================================================
// LiteralInteger.java
//====================================================================
package smalltalk.compiler.constant;

import org.antlr.runtime.Token;
import smalltalk.compiler.element.Container;

/**
 * Represents and encodes a literal SmallInteger.
 *
 * @author Copyright 1999,2017 Nikolas S. Boyd. All rights reserved.
 */
public class LiteralInteger extends LiteralNumber {

    /**
     * Returns a new literal Integer from the supplied (token).
     *
     * @param token a parsed literal Integer.
     * @param container a container
     * @return a LiteralInteger
     */
    public static LiteralInteger from(Token token, Container container) {
        LiteralInteger result = new LiteralInteger(container, token.getText());
        result.setLine(token.getLine());
        return result;
    }

    /**
     * Constructs a new LiteralInteger.
     *
     * @param container the container that surrounds this one.
     * @param aValue the value of this literal.
     */
    public LiteralInteger(Container container, java.lang.String aValue) {
        super(container, aValue);
    }

    /**
     * Returns the primitive type of this scalar.
     *
     * @return the primitive type of this scalar.
     */
    @Override
    public Class primitiveType() {
        return int.class;
    }

    /**
     * Returns the name of the primitive factory used to instantiate this scalar.
     *
     * @return the name of the primitive factory used to instantiate this scalar.
     */
    @Override
    public java.lang.String primitiveFactoryName() {
        return "Integer";
    }

    /**
     * Returns the declared type of the value represented by the scalar.
     *
     * @return the declared type of the value represented by the scalar.
     */
    @Override
    public java.lang.String declaredType() {
        return "SmallInteger";
    }

    /**
     * Returns the type name of this constant reference.
     *
     * @return the type name of this constant reference.
     */
    @Override
    public String resolvedTypeName() {
        return (container().fileScope().needsMagnitudes()
                ? RootClass : "smalltalk.magnitude.SmallInteger");
    }
}
