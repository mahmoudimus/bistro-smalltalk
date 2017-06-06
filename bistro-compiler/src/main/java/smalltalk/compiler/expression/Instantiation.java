//====================================================================
// Instantiation.java
//====================================================================
package smalltalk.compiler.expression;

import java.util.ArrayList;
import smalltalk.compiler.Emission;
import static smalltalk.compiler.Emission.emit;
import smalltalk.compiler.element.Base;
import smalltalk.compiler.element.Reference;
import smalltalk.compiler.scope.Block;

/**
 * Represents a new object instantiation and translates it into Java.
 *
 * @author Copyright 1999,2017 Nikolas S. Boyd. All rights reserved.
 */
public class Instantiation extends Message {

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

    @Override
    public Emission emitPrimitive() {
        return emitOperand();
    }

    @Override
    public Emission emitOperand() {
        if (selector().isBasicNew()) {
            return emitBasicNew();
        } else {
            return emitCastedNew();
        }
    }

    public Emission emitCastedNew() {
        if (receiver().isReference()) {
            Reference receiver = receiver().asReference();
            if (receiver.refersToMetaclass()) {
                return emitCast(receiver.name(),
                        emitNewExpression(emitBehaviorCast()));
            }

            if (receiver.isSelfish()) {
                return emitFacialCast(receiver);
            }
        }

        return emitNewExpression(emitBehaviorCast());
    }

    public Emission emitBasicNew() {
        if (receiver().isReference()) {
            Reference receiver = receiver().asReference();
            return emit("New")
                    .with("className", receiver.name())
                    .with("arguments", emitArguments());
        }

        return emit("New")
                .with("className", "Object")
                .with("arguments", new ArrayList());
    }

    public Emission emitFacialCast(Reference reference) {
        return emitCast(reference.facialTypeName(), emitNewExpression(reference.emitOperand()));
    }

    public Emission emitBehaviorCast() {
        return emitCast("Behavior", receiver().emitOperand());
    }

    public Emission emitNewExpression(Emission operand) {
        return emit("Expression")
                .with("operand", operand)
                .with("messages", emitCallNew());
    }

    public Emission emitCallNew() {
        return emitMethodCall("$new");
    }

    public Emission emitNewObject() {
        return emit("New")
                .with("className", "Object");
    }
}
