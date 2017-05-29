//====================================================================
// Innard.java
//====================================================================
package smalltalk.compiler.scope;

import java.util.ArrayList;
import smalltalk.compiler.Emission;
import static smalltalk.compiler.Emission.emit;
import smalltalk.compiler.element.Operand;
import smalltalk.compiler.element.Container;

/**
 * Represents and encodes a nested anonymous inner class.
 *
 * @author Copyright 1999,2016 Nikolas S. Boyd. All rights reserved.
 */
public class Innard extends Operand {

    /**
     * Defines an interface for visiting instances.
     */
    public static interface Visitor {

        public void visit(Innard innard);
    }

    /**
     * Refers to an inner class scope.
     */
    Face innerClass;

    /**
     * Constructs a new Innard.
     * @param aClass an inner class
     */
    public Innard(Face aClass) {
        super(aClass.containerScope());
        innerClass = aClass;
    }

    /**
     * Establishes (container) as the container for this scope.
     *
     * @param container the container scope.
     */
    @Override
    public void container(Container container) {
        super.container(container);
        innerClass.container(container);
    }

    /**
     * Cleans out any residue left from the parsing process and prepares the receiver for code generation.
     */
    @Override
    public void clean() {
        super.clean();
        innerClass.cleanMethods();
    }

    /**
     * Returns the nested class.
     *
     * @return the nested class.
     */
    public Face nestedClass() {
        return innerClass;
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
     * Accepts a visitor for inspection of the recevier.
     *
     * @param aVisitor visits the receiver for its information.
     */
    @Override
    public void acceptVisitor(Operand.Visitor aVisitor) {
        acceptVisitor((Visitor) aVisitor);
    }

    @Override
    public Emission emitOperand() {
        return emit("New").with("className", nestedClass().baseName()).with("arguments", new ArrayList());
    }
}
