//====================================================================
// TryCatch.java
//====================================================================
package smalltalk.compiler.expression;

import java.util.*;
import smalltalk.compiler.Emission;
import static smalltalk.compiler.Emission.emit;
import smalltalk.compiler.element.Operand;
import smalltalk.compiler.scope.Nest;
import smalltalk.compiler.scope.Block;

/**
 * Optimizes the translation of an ensure-catch message into Java.
 *
 * @author Copyright 1999,2016 Nikolas S. Boyd. All rights reserved.
 */
public class TryCatch extends Message {

    /**
     * Defines an interface for visiting instances.
     */
    public static interface Visitor {

        public void visit(TryCatch message);
    }

    /**
     * Final term for an exception message.
     */
    public static final String ensurePhrase = "ensure:";

    /**
     * Final term for an exception message.
     */
    public static final String catchPhrase = "catch:";

    /**
     * Constructs a new TryCatch control message.
     *
     * @param blockScope the scope that contains the message.
     */
    public TryCatch(Block blockScope) {
        super(blockScope);
    }

    /**
     * Indicates whether the control message has a final block.
     */
    public boolean hasFinalBlock() {
        return selector.contents().contains(ensurePhrase);
    }

    public Nest receiverBlock() {
        return (Nest) receiver();
    }

    /**
     * Returns the final block of the control message, or null.
     */
    public Nest finalBlock() {
        return (this.hasFinalBlock()
                ? (Nest) finalOperand() : null);
    }

    /**
     * Cleans out any residue left from the parsing process and prepares the receiver for code generation.
     */
    @Override
    public void clean() {
        super.clean();
        if (this.hasFinalBlock()) {
            Block finalBlock = this.finalBlock().nestedBlock();
            if (!finalBlock.returnsVoid()) {
                finalBlock.truncateStatements();
                finalBlock.type("void");
            }
        }
    }

    /**
     * Accepts a visitor for inspection of the receiver.
     *
     * @param aVisitor visits the receiver for its information.
     */
    public void acceptVisitor(Visitor aVisitor) {
        aVisitor.visit(this);
    }

    /**
     * Accepts a visitor for inspection of the receiver.
     *
     * @param aVisitor visits the receiver for its information.
     */
    @Override
    public void acceptVisitor(Operand.Visitor aVisitor) {
        acceptVisitor((Visitor) aVisitor);
    }

    @Override
    public Emission emitOperand() {
        return emitOptimized();
    }

    @Override
    public Emission emitOptimized() {
        return emitClosureValue(emitOuterBlock());
    }

    public Emission emitOuterBlock() {
        Nest firstBlock = receiverBlock();
        return emit("OptimizedBlock")
                .with("closureType", firstBlock.nestedBlock().closureType())
                .with("locals", new ArrayList())
                .with("signature", firstBlock.nestedBlock().emitSignature())
                .with("content", emitBlockContents());
    }

    public Emission emitBlockContents() {
        List<Emission> parts = new ArrayList();
        Nest firstBlock = receiverBlock();
        Nest finalBlock = finalBlock();
        parts.add(firstBlock.nestedBlock().emitTry());
        for (Operand argument : arguments()) {
            Nest block = (Nest) argument;
            if (block != finalBlock) {
                parts.add(block.nestedBlock().emitCatch());
            }
        }
        if (finalBlock != null) {
            parts.add(finalBlock.nestedBlock().emitFinally());
        }

        return emitLines(parts);
    }
}
