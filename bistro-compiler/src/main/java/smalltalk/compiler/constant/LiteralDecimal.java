//====================================================================
// LiteralDecimal.java
//====================================================================
package smalltalk.compiler.constant;

import org.antlr.runtime.Token;
import smalltalk.compiler.element.Container;
import smalltalk.compiler.element.Operand;

/**
 * Represents and encodes a literal Fixed.
 *
 * @author Copyright 1999,2016 Nikolas S. Boyd. All rights reserved.
 */
public class LiteralDecimal extends LiteralNumber {

    /**
     * Defines an interface for visiting instances.
     */
    public static interface Visitor {

        public void visit(LiteralDecimal constant);
    }

    /**
     * Returns a new literal Fixed decimal from the supplied (token).
     *
     * @param token a parsed literal Fixed decimal.
     * @param container a container
     * @return a LiteralDecimal
     */
    public static LiteralDecimal from(Token token, Container container) {
        LiteralDecimal result = new LiteralDecimal(container, token.getText());
        result.setLine(token.getLine());
        return result;
    }

    /**
     * Constructs a new LiteralDecimal.
     *
     * @param container the container that surrounds this one.
     * @param aValue the value of this literal.
     */
    public LiteralDecimal(Container container, java.lang.String aValue) {
        super(container, "\"" + aValue + "\"");
    }

    /**
     * Returns the primitive type of this scalar.
     *
     * @return the primitive type of this scalar.
     */
    @Override
    public Class primitiveType() {
        return java.math.BigDecimal.class;
    }

    /**
     * Returns the name of the primitive factory used to instantiate this scalar.
     *
     * @return the name of the primitive factory used to instantiate this scalar.
     */
    @Override
    public java.lang.String primitiveFactoryName() {
        return "Fixed";
    }

    /**
     * Returns the type name of this constant reference.
     *
     * @return the type name of this constant reference.
     */
    @Override
    public String resolvedTypeName() {
        return (container().fileScope().needsMagnitudes()
                ? RootClass : "smalltalk.magnitude.Fixed");
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
}
