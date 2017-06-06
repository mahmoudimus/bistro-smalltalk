//====================================================================
// Assignment.java
//====================================================================
package smalltalk.compiler.expression;

import smalltalk.compiler.Emission;
import static smalltalk.compiler.Emission.emit;
import smalltalk.compiler.scope.Block;

/**
 * Represents a variable assignment and translates it into Java.
 *
 * @author Copyright 1999,2017 Nikolas S. Boyd. All rights reserved.
 */
public class Assignment extends Message {

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

    public boolean valueNeedsCast() {
        return (!receiver().resolvedTypeName().equals(firstArgument().resolvedTypeName()));
    }

    @Override
    public Emission emitOperand() {
        return emitOptimized();
    }

    @Override
    public Emission emitPrimitive() {
        return emit("Assignment")
                .name(receiver().emitItem())
                .value(firstArgument().emitPrimitive());
    }

    @Override
    public Emission emitOptimized() {
        return emit("Assignment")
                .name(receiver().emitItem())
                .value(emitOptimizedValue());
    }

    public Emission emitOptimizedValue() {
        boolean casting = valueNeedsCast();
        if (!casting) return firstArgument().emitOperand();
        return emitCast(receiver().resolvedTypeName(), firstArgument().emitOperand());
    }

}
