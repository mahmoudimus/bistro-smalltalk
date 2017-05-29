//====================================================================
// LiteralString.java
//====================================================================
package smalltalk.compiler.constant;

import static java.lang.String.format;
import org.antlr.runtime.Token;
import smalltalk.compiler.Emission;
import smalltalk.compiler.element.Container;
import smalltalk.compiler.element.Operand;

/**
 * Represents and encodes a literal String.
 *
 * @author Copyright 1999,2016 Nikolas S. Boyd. All rights reserved.
 */
public class LiteralString extends LiteralCollection {

    /**
     * Defines an interface for visiting instances.
     */
    public static interface Visitor {

        public void visit(LiteralString constant);
    }

    /**
     * Returns a new literal String from the supplied (token).
     *
     * @param token a parsed literal String.
     * @param container a container
     * @return a LiteralString
     */
    public static LiteralString from(Token token, Container container) {
        LiteralString result = new LiteralString(container, token.getText());
        result.setLine(token.getLine());
        return result;
    }

    /**
     * Constructs a new LiteralString.
     *
     * @param container the container that surrounds this one.
     * @param aString the value of the string.
     */
    public LiteralString(Container container, String aString) {
        super(container, aString);
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
        return "String";
    }

    /**
     * Returns the type name of this constant reference.
     *
     * @return the type name of this constant reference.
     */
    @Override
    public String resolvedTypeName() {
        return (container().fileScope().needsCollections()
                ? RootClass : "smalltalk.collection.String");
    }

    /**
     * Returns the encoded value of this literal.
     *
     * @return the encoded value of this literal.
     */
    @Override
    public String encodedValue() {
        return format(QuotedValue, value.replace(DoubledQuote, SingleQuote).replace(SingleQuote, EmptyString));
    }

    /**
     * Accepts a visitor for inspection of the receiver.
     *
     * @param aVisitor visits the receiver for its information.
     */
    public void acceptVisitor(Visitor aVisitor) {
        aVisitor.visit(this);
    }

    /**
     * Accepts a visitor for inspection of the receiver.
     *
     * @param aVisitor visits the receiver for its information.
     */
    @Override
    public void acceptVisitor(Operand.Visitor aVisitor) {
        acceptVisitor((Visitor) aVisitor);
    }

//    @Override
//    public Emission emitOperand() {
//        return emitScalar();
//    }
}
