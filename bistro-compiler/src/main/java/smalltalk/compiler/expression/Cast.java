//====================================================================
// Cast.java
//====================================================================
package smalltalk.compiler.expression;

import java.util.*;

import smalltalk.compiler.element.Reference;
import smalltalk.compiler.element.Operand;
import smalltalk.compiler.scope.Block;
import smalltalk.compiler.scope.Method;
import smalltalk.compiler.scope.Variable;

/**
 * Optimizes the translation of a cast message into Java.
 *
 * @author Copyright 1999,2016 Nikolas S. Boyd. All rights reserved.
 */
public class Cast extends Message {

    /**
     * Defines an interface for visiting instances.
     */
    public static interface Visitor {

        public void visit(Cast message);
    }

    /**
     * Returns a new Cast message that unwraps the supplied (argument) in the context of a (wrapper) method.
     *
     * @param argument the wrapper method argument to unwrap.
     * @param wrapper the wrapper method.
     * @return a Message
     */
    public static Message from(Variable argument, Method wrapper) {
        Cast aCast = new Cast(wrapper);
        aCast.addOperand(Reference.named(argument.name(), wrapper));
        aCast.addOperand(Reference.named(argument.type(), wrapper));
        return aCast;
    }

    /**
     * Returns a new message that unwraps an argument named (argumentName) of type (typeName) in the context of a
     * (wrapper) method. The wrapper type is obtained from the (wrappers) and the unwrapping message is obtained from
     * the (un-wrappers).
     *
     * @param argumentName the name of the argument to unwrap.
     * @param typeName the argument type name.
     * @param wrapper the wrapper method.
     * @param wrappers the wrapper type names.
     * @param unwrappers the unwrapping method names.
     * @return a Message
     */
    public static Message from(
            String argumentName, String typeName, Method wrapper,
            Map<String, String> wrappers, Map<String, String> unwrappers) {
        Cast aCast = new Cast(wrapper);
        String typeWrapper = wrappers.get(typeName);
        String typeUnwrapper = unwrappers.get(typeName);
        aCast.addOperand(Reference.named(argumentName, wrapper));
        aCast.addOperand(Reference.named(typeWrapper, wrapper));
        Message unwrapper = Message.sending(typeUnwrapper, wrapper);
        unwrapper.addOperand(aCast);
        return unwrapper;
    }

    /**
     * Returns a new message that unwraps the supplied (argument) in the context of a (wrapper) method.
     *
     * @param argument the wrapper method argument to unwrap.
     * @param wrapper the wrapper method.
     * @return a Message
     */
    public static Message fromWrapped(Variable argument, Method wrapper) {
        String typeName = argument.resolvedTypeName();
        if (PrimitiveWrappers.containsKey(typeName)) {
            return Cast.from(argument.name(), typeName, wrapper,
                    PrimitiveWrappers, PrimitiveUnwrappers
            );
        }
        if (ObjectWrappers.containsKey(typeName)) {
            return Cast.from(argument.name(), typeName, wrapper,
                    ObjectWrappers, ObjectUnwrappers
            );
        }
        return Cast.from(argument, wrapper);
    }

    /**
     * Constructs a new Cast message.
     *
     * @param blockScope the scope that contains the message.
     */
    public Cast(Block blockScope) {
        super(blockScope);
    }

    /**
     * Cleans the message operands.
     */
    @Override
    public void cleanOperands() {
        List<Operand> copy = operands();
        operands.clear();
        Operand operand = copy.get(0);
        operands.add(operand.cleanTerm());
        operand = copy.get(1);
        operands.add(operand);
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
        return arguments().get(0).name();
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
