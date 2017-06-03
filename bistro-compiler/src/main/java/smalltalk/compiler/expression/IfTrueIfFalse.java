//====================================================================
// IfTrueIfFalse.java
//====================================================================
package smalltalk.compiler.expression;

import java.util.List;
import smalltalk.compiler.Emission;
import smalltalk.compiler.element.Operand;
import smalltalk.compiler.scope.Block;

/**
 * Optimizes the translation of a complete guard into Java.
 *
 * @author Copyright 1999,2016 Nikolas S. Boyd. All rights reserved.
 */
public class IfTrueIfFalse extends Message {

    /**
     * Defines an interface for visiting instances.
     */
    public static interface Visitor {

        public void visit(IfTrueIfFalse message);
    }

    /**
     * Constructs a new IfTrueIfFalse control message.
     *
     * @param blockScope the scope that contains the message.
     */
    public IfTrueIfFalse(Block blockScope) {
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
    public Emission emitOperand() {
        // maintain block order here
        List<Operand> arguments = arguments();
        Operand trueBlock = arguments.get(0);
        Operand falseBlock = arguments.get(1);
        return emitAlternatives(true, trueBlock, falseBlock);
    }

    @Override
    public Emission emitStatement() {
        // maintain block order here
        List<Operand> arguments = arguments();
        Operand trueBlock = arguments.get(0);
        Operand falseBlock = arguments.get(1);
        return emitGuardedStatement(true, trueBlock, falseBlock);
    }
}
