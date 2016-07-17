//====================================================================
// PrimitiveLiteral.java
//====================================================================
package smalltalk.compiler.expression;

import smalltalk.compiler.element.Operand;
import smalltalk.compiler.constant.Scalar;
import smalltalk.compiler.scope.Block;

/**
 * Represents primitive access of a literal object and translates it into Java.
 *
 * @author Copyright 1999,2016 Nikolas S. Boyd. All rights reserved.
 */
public class PrimitiveLiteral extends Message {

    /**
     * Defines an interface for visiting instances.
     */
    public static interface Visitor {

        public void visit(PrimitiveLiteral message);
    }

    /**
     * Constructs a new Instantiation.
     *
     * @param blockScope the scope that contains the message.
     */
    public PrimitiveLiteral(Block blockScope) {
        super(blockScope);
    }

    /**
     * Returns the type of this message.
     *
     * @return the type of this message.
     */
    @Override
    public Class resolvedType() {
        Operand receiver = receiver();
        return (receiver.isLiteral()
                ? ((Scalar) receiver).primitiveType()
                : super.resolvedType());
    }

    /**
     * Returns the type name of this message.
     *
     * @return the type name of this message.
     */
    @Override
    public String resolvedTypeName() {
        return this.resolvedType().getName();
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
