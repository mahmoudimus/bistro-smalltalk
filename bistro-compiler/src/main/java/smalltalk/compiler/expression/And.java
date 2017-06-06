//====================================================================
// And.java
//====================================================================
package smalltalk.compiler.expression;

import smalltalk.compiler.Emission;
import static smalltalk.compiler.Emission.emit;
import smalltalk.compiler.scope.Block;

/**
 * Optimizes the translation of a conjunction into Java.
 *
 * @author Copyright 1999,2017 Nikolas S. Boyd. All rights reserved.
 */
public class And extends Message {

    /**
     * Constructs a new And control message.
     *
     * @param blockScope the scope that contains the message.
     */
    public And(Block blockScope) {
        super(blockScope);
    }

    @Override
    public Emission emitExpression() {
        return emit("Expression")
                .with("operand", receiver().emitBooleanTerm())
                .with("messages", emitMethodCall());
    }
}
