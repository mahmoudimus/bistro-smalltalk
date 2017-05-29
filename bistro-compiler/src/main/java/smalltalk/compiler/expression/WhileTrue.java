//====================================================================
// WhileTrue.java
//====================================================================
package smalltalk.compiler.expression;

import smalltalk.compiler.Emission;
import smalltalk.compiler.element.Operand;
import smalltalk.compiler.scope.Block;

/**
 * Optimizes the translation of a positive loop into Java.
 *
 * @author Copyright 1999,2016 Nikolas S. Boyd. All rights reserved.
 */
public class WhileTrue extends Message {

    /**
     * Defines an interface for visiting instances.
     */
    public static interface Visitor {

        public void visit(WhileTrue message);
    }

    /**
     * Constructs a new WhileTrue control message.
     *
     * @param blockScope the scope that contains the message.
     */
    public WhileTrue(Block blockScope) {
        super(blockScope);
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
    public void acceptVisitor(Operand.Visitor aVisitor) {
        acceptVisitor((Visitor) aVisitor);
    }

    @Override
    public Emission emitStatement() {
        return emitWhileLoop(true, firstArgument());
    }
}
