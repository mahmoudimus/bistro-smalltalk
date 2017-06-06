//====================================================================
// Nest.java
//====================================================================
package smalltalk.compiler.scope;

import smalltalk.compiler.Emission;
import smalltalk.compiler.element.*;

/**
 * Represents and encodes a nested local code scope.
 *
 * @author Copyright 1999,2017 Nikolas S. Boyd. All rights reserved.
 */
public class Nest extends Operand {

    /**
     * Refers to a nested block scope.
     */
    Block nestedBlock;

    /**
     * Constructs a new block scope Nest.
     * @param aBlock outer block
     */
    public Nest(Block aBlock) {
        super(aBlock.containerScope());
        nestedBlock = aBlock;
    }

    /**
     * Establishes (container) as the container for this scope.
     *
     * @param container the container scope.
     */
    @Override
    public void container(Container container) {
        super.container(container);
        nestedBlock.container(container);
    }

    /**
     * Cleans out any residue left from the parsing process and prepares the receiver for code generation.
     */
    @Override
    public void clean() {
        super.clean();
        nestedBlock.clean();
    }

    @Override
    public boolean isNest() {
        return true;
    }

    /**
     * Returns the nested block.
     *
     * @return the nested block.
     */
    public Block nestedBlock() {
        return nestedBlock;
    }


    @Override
    public Emission emitOperand() {
        return emitOptimized();
    }

    @Override
    public Emission emitOptimized() {
        return nestedBlock().emitOptimized();
    }
}
