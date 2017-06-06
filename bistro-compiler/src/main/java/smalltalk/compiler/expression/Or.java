//====================================================================
// Or.java
//====================================================================
package smalltalk.compiler.expression;

import smalltalk.compiler.Emission;
import static smalltalk.compiler.Emission.emit;
import smalltalk.compiler.scope.Block;

/**
 * Optimizes the translation of a disjunction into Java.
 *
 * @author Copyright 1999,2017 Nikolas S. Boyd. All rights reserved.
 */
public class Or extends Message {

    /**
     * Constructs a new And control message.
     *
     * @param blockScope the scope that contains the message.
     */
    public Or(Block blockScope) {
        super(blockScope);
    }

    @Override
    public Emission emitExpression() {
        return emit("Expression")
                .with("operand", receiver().emitBooleanTerm())
                .with("messages", emitMethodCall());
    }
}
