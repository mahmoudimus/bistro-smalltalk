//====================================================================
// LiteralSymbol.java
//====================================================================
package smalltalk.compiler.constant;

import org.antlr.runtime.Token;
import smalltalk.compiler.element.Container;

/**
 * Represents and encodes a literal Symbol.
 *
 * @author Copyright 1999,2017 Nikolas S. Boyd. All rights reserved.
 */
public class LiteralSymbol extends LiteralString {

    /**
     * Returns a new literal Symbol from the supplied (token).
     *
     * @param token a parsed literal Symbol.
     * @param container a container
     * @return a LiteralString
     */
    public static LiteralString from(Token token, Container container) {
        String contents = token.getText();
        if (contents.startsWith("#")) {
            contents = contents.substring(1);
        }

        if (contents.length() > 0 && contents.charAt(0) != '\'') {
            contents = "\'" + contents + "\'";
        }
        LiteralSymbol result = new LiteralSymbol(container, contents);
        result.setLine(token.getLine());
        return result;
    }

    /**
     * Constructs a new LiteralSymbol.
     *
     * @param container the container that surrounds this one.
     * @param aString the contents of the symbol.
     */
    public LiteralSymbol(Container container, String aString) {
        super(container, aString);
    }

    /**
     * Establishes the value of the symbol.
     *
     * @param aString the contents of the symbol.
     */
    @Override
    public void value(String aString) {
        value = aString;
    }

    /**
     * Returns the primitive type of this scalar.
     *
     * @return the primitive type of this scalar.
     */
    @Override
    public Class primitiveType() {
        return java.lang.String.class;
    }

    /**
     * Returns the name of the primitive factory used to instantiate this scalar.
     *
     * @return the name of the primitive factory used to instantiate this scalar.
     */
    @Override
    public String primitiveFactoryName() {
        return "Symbol";
    }

    /**
     * Returns the type name of this constant reference.
     *
     * @return the type name of this constant reference.
     */
    @Override
    public String resolvedTypeName() {
        return (container().fileScope().needsCollections()
                ? RootClass : "smalltalk.collection.Symbol");
    }
}
