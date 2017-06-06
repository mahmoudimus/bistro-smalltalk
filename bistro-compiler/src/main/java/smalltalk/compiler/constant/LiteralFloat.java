//====================================================================
// LiteralFloat.java
//====================================================================
package smalltalk.compiler.constant;

import org.antlr.runtime.Token;
import smalltalk.compiler.element.Container;

/**
 * Represents and encodes a literal Float.
 *
 * @author Copyright 1999,2017 Nikolas S. Boyd. All rights reserved.
 */
public class LiteralFloat extends LiteralNumber {

    /**
     * Returns a new literal Float from the supplied (token).
     *
     * @param token a parsed literal Float.
     * @param container a container
     * @return a LiteralFloat
     */
    public static LiteralFloat from(Token token, Container container) {
        LiteralFloat result = new LiteralFloat(container, token.getText());
        result.setLine(token.getLine());
        return result;
    }

    /**
     * Constructs a new LiteralFloat.
     *
     * @param container the container that surrounds this one.
     * @param aValue the value of this literal.
     */
    public LiteralFloat(Container container, java.lang.String aValue) {
        super(container, aValue);
    }

    /**
     * Returns the primitive type of this scalar.
     *
     * @return the primitive type of this scalar.
     */
    @Override
    public Class primitiveType() {
        return float.class;
    }

    /**
     * Returns the name of the primitive factory used to instantiate this scalar.
     *
     * @return the name of the primitive factory used to instantiate this scalar.
     */
    @Override
    public java.lang.String primitiveFactoryName() {
        return "Float";
    }

    /**
     * Returns the type name of this constant reference.
     *
     * @return the type name of this constant reference.
     */
    @Override
    public String resolvedTypeName() {
        return (container().fileScope().needsMagnitudes()
                ? RootClass : "smalltalk.magnitude.Float");
    }

    /**
     * Returns the encoded value of this literal.
     *
     * @return the encoded value of this literal.
     */
    @Override
    public java.lang.String encodedValue() {
        return super.encodedValue() + "f";
    }
}
