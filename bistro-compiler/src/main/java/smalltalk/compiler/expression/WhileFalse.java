//====================================================================
// WhileFalse.java
//====================================================================
package smalltalk.compiler.expression;

import smalltalk.compiler.Emission;
import smalltalk.compiler.scope.Block;

/**
 * Optimizes the translation of a negative loop into Java.
 *
 * @author Copyright 1999,2017 Nikolas S. Boyd. All rights reserved.
 */
public class WhileFalse extends Message {

    /**
     * Constructs a new WhileFalse control message.
     *
     * @param blockScope the scope that contains the message.
     */
    public WhileFalse(Block blockScope) {
        super(blockScope);
    }

    @Override
    public Emission emitStatement() {
        return emitWhileLoop(false, firstArgument());
    }
}
