//====================================================================
// And.java
//====================================================================
package smalltalk.compiler.expression;

import smalltalk.compiler.Emission;
import static smalltalk.compiler.Emission.emit;
import smalltalk.compiler.element.Operand;
import smalltalk.compiler.scope.Block;

/**
 * Optimizes the translation of a conjunction into Java.
 *
 * @author Copyright 1999,2016 Nikolas S. Boyd. All rights reserved.
 */
public class And extends Message {

    /**
     * Defines an interface for visiting instances.
     */
    public static interface Visitor {

        public void visit(And message);
    }

    /**
     * Constructs a new And control message.
     *
     * @param blockScope the scope that contains the message.
     */
    public And(Block blockScope) {
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
    @Override
    public void acceptVisitor(Operand.Visitor aVisitor) {
        acceptVisitor((Visitor) aVisitor);
    }

    @Override
    public Emission emitExpression() {
        return emit("Expression")
                .with("operand", receiver().emitBooleanTerm())
                .with("messages", emitMethodCall());
    }
}
