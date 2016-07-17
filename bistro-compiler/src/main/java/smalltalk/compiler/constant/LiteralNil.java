//====================================================================
// LiteralNil.java
//====================================================================
package smalltalk.compiler.constant;

import org.antlr.runtime.Token;
import smalltalk.compiler.element.Container;
import smalltalk.compiler.element.Operand;

/**
 * Represents and encodes a literal UndefinedObject.
 *
 * @author Copyright 1999,2016 Nikolas S. Boyd. All rights reserved.
 */
public class LiteralNil extends Scalar {

    /**
     * Defines an interface for visiting instances.
     */
    public static interface Visitor {

        public void visit(LiteralNil constant);
    }

    /**
     * Returns a new literal nil from the supplied (token).
     *
     * @param token a parsed literal nil.
     * @param container a container
     * @return a LiteralNil
     */
    public static LiteralNil from(Token token, Container container) {
        LiteralNil result = new LiteralNil(container);
        result.setLine(token.getLine());
        return result;
    }

    /**
     * Constructs a new LiteralNil.
     *
     * @param container the container that surrounds this one.
     */
    public LiteralNil(Container container) {
        super(container, "null");
    }

    /**
     * Returns the primitive type of this scalar.
     *
     * @return the primitive type of this scalar.
     */
    @Override
    public Class primitiveType() {
        return void.class;
    }

    /**
     * Returns the name of the primitive factory used to instantiate this scalar.
     *
     * @return the name of the primitive factory used to instantiate this scalar.
     */
    @Override
    public java.lang.String primitiveFactoryName() {
        return "Void";
    }

    /**
     * Returns the declared type of the value represented by the scalar.
     *
     * @return the declared type of the value represented by the scalar.
     */
    @Override
    public java.lang.String declaredType() {
        return "UndefinedObject";
    }

    /**
     * Returns the type name of this constant reference.
     *
     * @return the type name of this constant reference.
     */
    @Override
    public String resolvedTypeName() {
        return "smalltalk.behavior.UndefinedObject";
    }

    /**
     * Returns the translated Java code for the scalar.
     *
     * @return the translated Java code for the scalar.
     */
    @Override
    public java.lang.String encodedValue() {
        return "Object.primitive.literalNil()";
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
