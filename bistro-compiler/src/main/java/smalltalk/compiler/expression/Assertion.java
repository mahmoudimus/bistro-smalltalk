//====================================================================
// Assertion.java
//====================================================================
package smalltalk.compiler.expression;

import smalltalk.compiler.Emission;
import static smalltalk.compiler.Emission.emit;
import smalltalk.compiler.scope.Block;

/**
 * Represents an assertion and translates it into Java.
 *
 * @author Copyright 1999,2017 Nikolas S. Boyd. All rights reserved.
 */
public class Assertion extends Message {

    /**
     * The assert command.
     */
    public static final String COMMAND = "assert";

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


    @Override
    public Emission emitOptimized() {
        return emit("Assert").value(emitTrueGuard(receiver().emitOperand()))
                .with("message", operandCount() > 1 ? firstArgument().emitOptimized() : null);
    }
}
