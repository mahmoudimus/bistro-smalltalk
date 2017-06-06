//====================================================================
// IfFalseIfTrue.java
//====================================================================
package smalltalk.compiler.expression;

import java.util.List;
import smalltalk.compiler.Emission;
import smalltalk.compiler.element.Operand;
import smalltalk.compiler.scope.Block;

/**
 * Optimizes the translation of a complete guard into Java.
 *
 * @author Copyright 1999,2017 Nikolas S. Boyd. All rights reserved.
 */
public class IfFalseIfTrue extends Message {

    /**
     * Constructs a new IfFalseIfTrue control message.
     *
     * @param blockScope the scope that contains the message.
     */
    public IfFalseIfTrue(Block blockScope) {
        super(blockScope);
    }

    @Override
    public Emission emitOperand() {
        // swap block order here
        List<Operand> arguments = arguments();
        Operand falseBlock = arguments.get(0);
        Operand trueBlock = arguments.get(1);
        return emitAlternatives(true, trueBlock, falseBlock);
    }

    @Override
    public Emission emitStatement() {
        // swap block order here
        List<Operand> arguments = arguments();
        Operand falseBlock = arguments.get(0);
        Operand trueBlock = arguments.get(1);
        return emitGuardedStatement(true, trueBlock, falseBlock);
    }
}
