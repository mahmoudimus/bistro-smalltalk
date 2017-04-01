//====================================================================
// Exit.java
//====================================================================
package smalltalk.compiler.expression;

import smalltalk.compiler.Emission;
import smalltalk.compiler.element.Operand;
import smalltalk.compiler.scope.Block;

/**
 * Represents a method exit and translates it into Java.
 *
 * @author Copyright 1999,2016 Nikolas S. Boyd. All rights reserved.
 */
public class Exit extends Message {

    /**
     * Defines an interface for visiting instances.
     */
    public static interface Visitor {

        public void visit(Exit message);
    }

    /**
     * Constructs a new method Exit.
     *
     * @param blockScope the scope that contains the message.
     */
    public Exit(Block blockScope) {
        super(blockScope);
    }

    /**
     * Returns whether the receiver is an exit message.
     *
     * @return whether the receiver is an exit message.
     */
    @Override
    public boolean isExit() {
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
