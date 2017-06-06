//====================================================================
// IfFalse.java
//====================================================================
package smalltalk.compiler.expression;

import smalltalk.compiler.Emission;
import smalltalk.compiler.scope.Block;

/**
 * Optimizes the translation of a negative guard into Java.
 *
 * @author Copyright 1999,2017 Nikolas S. Boyd. All rights reserved.
 */
public class IfFalse extends Message {

    /**
     * Constructs a new IfFalse control message.
     *
     * @param blockScope the scope that contains the message.
     */
    public IfFalse(Block blockScope) {
        super(blockScope);
    }

    @Override
    public Emission emitOperand() {
        return emitAlternatives(false, firstArgument(), null);
    }

    @Override
    public Emission emitStatement() {
        return emitGuardedStatement(false, firstArgument());
    }

}
