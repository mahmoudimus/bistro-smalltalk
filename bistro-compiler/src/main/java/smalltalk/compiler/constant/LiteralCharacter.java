//====================================================================
// LiteralCharacter.java
//====================================================================
package smalltalk.compiler.constant;

import org.antlr.runtime.Token;
import smalltalk.compiler.element.Container;

/**
 * Represents and encodes a literal Character.
 *
 * @author Copyright 1999,2017 Nikolas S. Boyd. All rights reserved.
 */
public class LiteralCharacter extends LiteralNumber {

    /**
     * Returns a new literal Character from the supplied (token).
     *
     * @param token a parsed literal Character.
     * @param container a container
     * @return a LiteralCharacter
     */
    public static LiteralCharacter from(Token token, Container container) {
        LiteralCharacter result = new LiteralCharacter(container, token.getText());
        result.setLine(token.getLine());
        return result;
    }

    /**
     * Constructs a new LiteralCharacter.
     *
     * @param container the container that surrounds this one.
     * @param aValue the value of this literal.
     */
    public LiteralCharacter(Container container, java.lang.String aValue) {
        super(container, aValue);
    }

    /**
     * Returns the primitive type of this scalar.
     *
     * @return the primitive type of this scalar.
     */
    @Override
    public Class primitiveType() {
        return char.class;
    }

    /**
     * Returns the name of the primitive factory used to instantiate this scalar.
     *
     * @return the name of the primitive factory used to instantiate this scalar.
     */
    @Override
    public java.lang.String primitiveFactoryName() {
        return "Character";
    }

    /**
     * Returns the type name of this constant reference.
     *
     * @return the type name of this constant reference.
     */
    @Override
    public String resolvedTypeName() {
        return (container().fileScope().needsMagnitudes()
                ? RootClass : "smalltalk.magnitude.Character");
    }

    /**
     * Returns the encoded value of this literal.
     *
     * @return the encoded value of this literal.
     */
    @Override
    public java.lang.String encodedValue() {
        return "'" + value.substring(1, 2) + "'";
    }
}
