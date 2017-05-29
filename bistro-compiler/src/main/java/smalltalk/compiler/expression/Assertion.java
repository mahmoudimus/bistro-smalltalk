//====================================================================
// Assertion.java
//====================================================================
package smalltalk.compiler.expression;

import smalltalk.compiler.Emission;
import static smalltalk.compiler.Emission.emit;
import smalltalk.compiler.element.Operand;
import smalltalk.compiler.scope.Block;

/**
 * Represents an assertion and translates it into Java.
 *
 * @author Copyright 1999,2016 Nikolas S. Boyd. All rights reserved.
 */
public class Assertion extends Message {

    /**
     * The assert command.
     */
    public static final String COMMAND = "assert";

    /**
     * Defines an interface for visiting instances.
     */
    public static interface Visitor {

        public void visit(Assertion message);
    }

    /**
     * Constructs a new Assertion.
     *
     * @param blockScope the scope that contains the assertion.
     */
    public Assertion(Block blockScope) {
        super(blockScope);
    }

    /**
     * Indicates whether the message can be optimized.
     */
    @Override
    public boolean canOptimizeInvocation() {
        return true;
    }

    /**
     * Returns whether the operand needs to be a term when used as a message receiver.
     *
     * @return whether the operand needs to be a term when used as a message receiver.
     */
    @Override
    public boolean receiverNeedsTerm() {
        return false;
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
    public Emission emitOptimized() {
        return emit("Assert").value(emitTrueGuard(receiver().emitOperand()))
                .with("message", operandCount() > 1 ? firstArgument().emitOptimized() : null);
    }
}
