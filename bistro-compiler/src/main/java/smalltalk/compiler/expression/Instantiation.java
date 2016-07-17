//====================================================================
// Instantiation.java
//====================================================================
package smalltalk.compiler.expression;

import smalltalk.compiler.element.Base;
import smalltalk.compiler.element.Operand;
import smalltalk.compiler.element.Reference;
import smalltalk.compiler.scope.Block;

/**
 * Represents a new object instantiation and translates it into Java.
 *
 * @author Copyright 1999,2016 Nikolas S. Boyd. All rights reserved.
 */
public class Instantiation extends Message {

    /**
     * Defines an interface for visiting instances.
     */
    public static interface Visitor {

        public void visit(Instantiation message);
    }

    /**
     * Constructs a new Instantiation.
     *
     * @param blockScope the scope that contains the message.
     */
    public Instantiation(Block blockScope) {
        super(blockScope);
    }

    /**
     * Cleans out any residue left from the parsing process and prepares the receiver for code generation.
     */
    @Override
    public void clean() {
        super.clean();
        if (selector.isBasicNew()) {
            return;
        }
        try {
            Reference r = (Reference) receiver();
            if (!r.isSelfish()) {
                String typeName = r.resolvedTypeName();
                if (!typeName.equals(RootMetaclass)) {
                    System.out.println(
                            "Warning! " + r.name() + " was not imported from a package."
                    );
                }
            }
        } catch (ClassCastException e) {
        }
    }

    /**
     * Returns whether the operand needs to be a term when used as a message receiver.
     *
     * @return whether the operand needs to be a term when used as a message receiver.
     */
    @Override
    public boolean receiverNeedsTerm() {
        return true;
    }

    /**
     * Returns the type of the variable to which this reference resolves.
     *
     * @return the type of the variable to which this reference resolves.
     */
    @Override
    public Class resolvedType() {
        return Reference.typeNamed(resolvedTypeName());
    }

    /**
     * Returns the type name of the variable to which this reference resolves.
     *
     * @return the type name of the variable to which this reference resolves.
     */
    @Override
    public String resolvedTypeName() {
        try {
            Reference r = (Reference) receiver();
            String typeName = r.resolvedTypeName();
            return (typeName.equals(Base.RootClass) ? r.name() : typeName);
        } catch (ClassCastException e) {
            return Base.RootClass;
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
}
