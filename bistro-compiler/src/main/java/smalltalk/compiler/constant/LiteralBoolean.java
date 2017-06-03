//====================================================================
// LiteralBoolean.java
//====================================================================
package smalltalk.compiler.constant;

import org.antlr.runtime.Token;
import smalltalk.compiler.Emission;
import smalltalk.compiler.element.Container;
import smalltalk.compiler.element.Operand;

/**
 * Represents and encodes a literal Boolean.
 *
 * @author Copyright 1999,2016 Nikolas S. Boyd. All rights reserved.
 */
public class LiteralBoolean extends Scalar {

    /**
     * Defines an interface for visiting instances.
     */
    public static interface Visitor {

        public void visit(LiteralBoolean constant);
    }

    /**
     * Returns a new literal Boolean from the supplied (token).
     *
     * @param token a parsed literal Boolean.
     * @param container a container
     * @return a LiteralBoolean
     */
    public static LiteralBoolean from(Token token, Container container) {
        LiteralBoolean result = new LiteralBoolean(container, token.getText());
        result.setLine(token.getLine());
        return result;
    }

    /**
     * Constructs a new LiteralBoolean.
     *
     * @param container the container that surrounds this one.
     * @param aValue the value of this literal.
     */
    public LiteralBoolean(Container container, java.lang.String aValue) {
        super(container, aValue);
    }

    /**
     * Returns the primitive type of this scalar.
     *
     * @return the primitive type of this scalar.
     */
    @Override
    public Class primitiveType() {
        return boolean.class;
    }

    /**
     * Returns the name of the primitive factory used to instantiate this scalar.
     *
     * @return the name of the primitive factory used to instantiate this scalar.
     */
    @Override
    public java.lang.String primitiveFactoryName() {
        return "Boolean";
    }

    /**
     * Returns the type name of this constant reference.
     *
     * @return the type name of this constant reference.
     */
    @Override
    public String resolvedTypeName() {
        return "smalltalk.behavior.Boolean";
    }

    /**
     * Returns the translated Java code for the scalar.
     *
     * @return the translated Java code for the scalar.
     */
    @Override
    public java.lang.String encodedValue() {
        return "Object.primitive.literal" + (value.equals("true") ? "True()" : "False()");
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

    @Override
    public Emission emitPrimitive() {
        return emitItem(rawValue());
    }

    @Override
    public Emission emitOperand() {
        return emitItem(encodedValue());
    }
}
