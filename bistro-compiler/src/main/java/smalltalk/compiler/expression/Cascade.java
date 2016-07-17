//====================================================================
// Cascade.java
//====================================================================
package smalltalk.compiler.expression;

import java.util.*;

import smalltalk.compiler.element.Operand;
import smalltalk.compiler.element.Selector;
import smalltalk.compiler.element.Reference;
import smalltalk.compiler.element.Container;
import smalltalk.compiler.element.Expression;
import smalltalk.compiler.scope.Nest;
import smalltalk.compiler.scope.Block;

/**
 * Represents a series of messages. The first message receiver is also the receiver for all the subsequent messages.
 * When initially parsed by the compiler, only the first message has a receiver. So, before code may be generated, the
 * receiver needs to be established for all the subsequent messages - see the clean method. If the first message
 * receiver is not a reference or an assignment, a new local variable is created to hold the receiver value, and the
 * first message receiver is replaced by a new assignment to that new local variable. During code generation, if the
 * cascade is a nested term, it gets wrapped inside a block: [ cascade ] value - i.e., a Java inner class.
 *
 * @author Copyright 1999,2016 Nikolas S. Boyd. All rights reserved.
 */
public class Cascade extends Expression {

    /**
     * Defines an interface for visiting instances.
     */
    public static interface Visitor {

        public void visit(Cascade cascade);
    }

    /**
     * Contains the cascaded messages.
     */
    List<Message> messages = new ArrayList();

    /**
     * Constructs a new Cascade.
     *
     * @param blockScope the block scope that contains the cascade.
     * @param message the first message of the cascade.
     */
    public Cascade(Block blockScope, Operand message) {
        super(blockScope);
        messages.add((Message) message);
    }

    /**
     * Returns an enumeration of the cascaded messages.
     *
     * @return a list of messages
     */
    public List<Message> messages() {
        return new ArrayList(messages);
    }

    /**
     * Establishes (container) as the container for this scope.
     *
     * @param container the container scope.
     */
    @Override
    public void container(Container container) {
        super.container(container);
        for (Message m : messages()) {
            m.container(container);
        }
    }

    /**
     * Returns whether the receiver consumes an (operand).
     *
     * @param operand a potentially consumable operand.
     * @return whether the receiver consumes an (operand).
     */
    @Override
    public boolean consumes(Operand operand) {
        return !operand.isExit();
    }

    /**
     * Cleans out any residue left from the parsing process and prepares the receiver for code generation.
     */
    @Override
    public void clean() {
        super.clean();
        List<Message> cascadedMessages = messages();
        Message m = cascadedMessages.remove(0);
        Operand o = m.receiver();
        Reference r = null;

        if (o instanceof Reference) {
            r = (Reference) o;
            m.clean();
        } else if (o instanceof Assignment) {
            Assignment a = (Assignment) o;
            r = (Reference) a.receiver();
            m.clean();
        } else {
            // create a new local to hold the cascade receiver
            Block block = blockScope();
            String localName = block.createLocal();
            r = new Reference(block);
            r.name(localName);
            // create a new assignment for the receiver
            Assignment a = new Assignment(block);
            a.selector(Selector.Assign);
            a.addOperand(r);
            a.addOperand(o);
            // replace the receiver with the assignment
            m.replaceReceiver(a);
            m.clean();
        }
        // establish the receiver for the remaining messages
        for (Message msg : cascadedMessages) {
            msg.receiver(r);
            msg.clean();
        }
    }

    /**
     * Cleans out any residue left from the parsing process and prepares the receiver for code generation. Create a new
     * block and value message around the cascade.
     *
     * @return a cleaned operand
     */
    @Override
    public Operand cleanTerm() {

        // create a new block for the message sequence
        Block scope = blockScope();
        Block block = new Block(scope);

        // create a new block evaluation message
        Message result = new Message(scope);
        result.selector("value");
        result.addOperand(new Nest(block));

        // attach the cascade to the block
        block.addStatement(this);
        this.container(block);

        // clean the cascade in the new context
        this.clean();

        // replace the cascade with its messages
        block.clear();
        for (Message m : messages()) {
            block.addStatement(m);
        }
        return result;
    }

    /**
     * Returns the block that contains the cascade.
     *
     * @return the block that contains the cascade.
     */
    public Block blockScope() {
        return (Block) container;
    }

    /**
     * Adds another message to the cascade.
     *
     * @param message a cascaded message.
     */
    public void addMessage(Message message) {
        messages.add(message);
    }

    /**
     * Adds another message to the cascade.
     *
     * @param message a cascaded message.
     */
    public void addMessage(Object message) {
        messages.add((Message) message);
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
