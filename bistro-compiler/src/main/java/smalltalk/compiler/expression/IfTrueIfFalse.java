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
 * @author Copyright 1999,2017 Nikolas S. Boyd. All rights reserved.
 */
public class IfTrueIfFalse extends Message {

    /**
     * Constructs a new IfTrueIfFalse control message.
     *
     * @param blockScope the scope that contains the message.
     */
    public IfTrueIfFalse(Block blockScope) {
        super(blockScope);
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
