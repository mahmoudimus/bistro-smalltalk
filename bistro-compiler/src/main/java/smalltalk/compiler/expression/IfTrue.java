//====================================================================
// IfTrue.java
//====================================================================
package smalltalk.compiler.expression;

import smalltalk.compiler.Emission;
import smalltalk.compiler.scope.Block;

/**
 * Optimizes the translation of a positive guard into Java.
 *
 * @author Copyright 1999,2017 Nikolas S. Boyd. All rights reserved.
 */
public class IfTrue extends Message {

    /**
     * Constructs a new IfTrue control message.
     *
     * @param blockScope the scope that contains the message.
     */
    public IfTrue(Block blockScope) {
        super(blockScope);
    }

    @Override
    public Emission emitOperand() {
        return emitAlternatives(true, firstArgument(), null);
    }

    @Override
    public Emission emitStatement() {
        return emitGuardedStatement(true, firstArgument());
    }
}
