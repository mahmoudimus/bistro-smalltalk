//====================================================================
// Exit.java
//====================================================================
package smalltalk.compiler.expression;

import smalltalk.compiler.Emission;
import static smalltalk.compiler.Emission.emit;
import smalltalk.compiler.scope.Block;
import smalltalk.compiler.scope.Method;

/**
 * Represents a method exit and translates it into Java.
 *
 * @author Copyright 1999,2017 Nikolas S. Boyd. All rights reserved.
 */
public class Exit extends Message {

    /**
     * Constructs a new method Exit.
     *
     * @param blockScope the scope that contains the message.
     */
    public Exit(Block blockScope) {
        super(blockScope);
    }

    /**
     * Returns whether the receiver is an exit message.
     *
     * @return whether the receiver is an exit message.
     */
    @Override
    public boolean isExit() {
        return true;
    }

    @Override
    public Emission emitOperand() {
        return emitResult();
    }

    @Override
    public Emission emitResult() {
        Block block = blockScope();
        Method method = block.currentMethod();

        if (block.isMethod()) {
            if (receiver().isSelfish() && method.returnsVoid() && method.hasLocals()) {
                return emitResult(emitNull());
            }

            return (method.exits()) ? emitMethodExit() : emitBlockExit();
        }

        return emitMethodExit();
    }

    public Emission emitMethodExit() {
        return emit("MethodExit")
                .with("scope", blockScope().currentMethod().scopeID())
                .with("value", receiver().emitOperand());
    }

    public Emission emitBlockExit() {
        return emitResult(emitTerm(receiver().emitOperand()));
    }
}
