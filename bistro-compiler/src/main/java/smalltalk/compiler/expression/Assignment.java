//====================================================================
// Assignment.java
//====================================================================
package smalltalk.compiler.expression;

import smalltalk.compiler.element.Operand;
import smalltalk.compiler.scope.Block;

/**
 * Represents a variable assignment and translates it into Java.
 *
 * @author Copyright 1999,2016 Nikolas S. Boyd. All rights reserved.
 */
public class Assignment extends Message {

    /**
     * Defines an interface for visiting instances.
     */
    public static interface Visitor {

        public void visit(Assignment message);
    }

    /**
     * Constructs a new Assignment.
     *
     * @param blockScope the scope that contains the assignment.
     */
    public Assignment(Block blockScope) {
        super(blockScope);
    }

    /**
     * Returns whether the operand needs to be a term when used as a message receiver.
     *
     * @return whether the operand needs to be a term when used as a message receiver.
     */
    @Override
    public boolean receiverNeedsTerm() {
        return true;
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
