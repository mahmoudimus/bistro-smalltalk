//====================================================================
// WhileTrue.java
//====================================================================
package smalltalk.compiler.expression;

import smalltalk.compiler.Emission;
import smalltalk.compiler.scope.Block;

/**
 * Optimizes the translation of a positive loop into Java.
 *
 * @author Copyright 1999,2017 Nikolas S. Boyd. All rights reserved.
 */
public class WhileTrue extends Message {

    /**
     * Constructs a new WhileTrue control message.
     *
     * @param blockScope the scope that contains the message.
     */
    public WhileTrue(Block blockScope) {
        super(blockScope);
    }

    @Override
    public Emission emitStatement() {
        return emitWhileLoop(true, firstArgument());
    }
}
