//====================================================================
// Message.java
//====================================================================
package smalltalk.compiler.expression;

import java.util.*;
import java.util.stream.Collectors;
import static java.lang.Integer.min;
import smalltalk.compiler.Emission;
import static smalltalk.compiler.Emission.emit;

import smalltalk.compiler.element.*;
import smalltalk.compiler.constant.LiteralNil;
import smalltalk.compiler.scope.Face;
import smalltalk.compiler.scope.Block;
import smalltalk.compiler.scope.Method;
import smalltalk.compiler.scope.Variable;

/**
 * Represents a Bistro message (method invocation) and translates it into Java.
 *
 * @author Copyright 1999,2016 Nikolas S. Boyd. All rights reserved.
 */
public class Message extends Expression {

    /**
     * Defines an interface for visiting instances.
     */
    public static interface Visitor {

        public void visit(Message message);
    }

    /**
     * Identifies a primitive instantiation message.
     */
    static final String basicNew = "basicNew";

    /**
     * Final term for an exception message.
     */
    static final String ensurePhrase = "ensure:";

    /**
     * Catch term for an exception message.
     */
    static final String catchPhrase = "catch:";

    /**
     * Provides dynamic binding invocations indexed by operand count.
     */
    static final String performs[] = {
        ".perform",
        ".perform_with",
        ".perform_with_with",
        ".perform_with_with_with",
        ".perform_with_with_with_with",
        ".perform_withArguments"
    };

    /**
     * Used to resolve a message constructor.
     */
    static Class[] blockClass = {Block.class};

    /**
     * Maps method selectors to the specialized message classes that encode them.
     */
    static final Map<String, Class> optimalClasses = new HashMap();

    /**
     * Initialize static members.
     */
    static {
        initializeOptimizers();
        Method.wrapperFactory = new Method.WrapperFactory() {
            @Override public Expression createWrapper(Method method, Method wrapper) {
                return Message.wrapping(method, wrapper);
            }
        };
    }

    /**
     * Initializes the selector to message class map.
     */
    protected static void initializeOptimizers() {
        optimalClasses.put("^", Exit.class);
        optimalClasses.put(Selector.Assign, Assignment.class);
        optimalClasses.put("new", Instantiation.class);
        optimalClasses.put("new:", Instantiation.class);
        optimalClasses.put("and:", And.class);
        optimalClasses.put("or:", Or.class);
        optimalClasses.put("as:", Cast.class);
        optimalClasses.put(basicNew, Instantiation.class);
        optimalClasses.put("ifTrue:", IfTrue.class);
        optimalClasses.put("ifFalse:", IfFalse.class);
        optimalClasses.put("whileTrue:", WhileTrue.class);
        optimalClasses.put("whileFalse:", WhileFalse.class);
        optimalClasses.put("ifTrue:ifFalse:", IfTrueIfFalse.class);
        optimalClasses.put("ifFalse:ifTrue:", IfFalseIfTrue.class);
        optimalClasses.put(ensurePhrase, TryCatch.class);
        optimalClasses.put(catchPhrase, TryCatch.class);
        optimalClasses.put("asPrimitive", PrimitiveLiteral.class);
        optimalClasses.put("assert", Assertion.class);
        optimalClasses.put("assert:", Assertion.class);
    }

    /**
     * Returns the class used to encode the supplied message (selector).
     *
     * @param selector a Bistro method or operator name.
     * @return the class used to encode the supplied message (selector).
     */
    protected static Class
            classForSelector(String selector) {
        String aSelector;
        aSelector = (selector.endsWith(ensurePhrase) ? ensurePhrase : selector);
        aSelector = (selector.endsWith(catchPhrase) ? catchPhrase : aSelector);
        aSelector = (selector.startsWith(basicNew) ? basicNew : aSelector);
        Class optimalClass = (Class) optimalClasses.get(aSelector);
        return (optimalClass == null ? Message.class : optimalClass);
    }

    /**
     * Returns the constructor for the class that corresponds to a (selector).
     *
     * @param selector a Bistro method or operator name.
     * @return the constructor for the class that corresponds to a (selector).
     */
    protected static java.lang.reflect.Constructor
            constructorFor(String selector) {
        try {
            return classForSelector(selector).getConstructor(blockClass);
        } catch (Exception e) {
            e.printStackTrace();
            return null; // never happens
        }
    }

    /**
     * Returns a new message to encode the supplied (selector).
     *
     * @param selector a Bistro method or operator name.
     * @param blockScope the scope that contains the message.
     * @return a new message to encode the supplied (selector).
     */
    public static Message
            sending(String selector, Block blockScope) {
        Object[] block = {blockScope};
        try {
            Message message = (Message) constructorFor(selector).newInstance(block);

            message.selector(selector);
            return message;
        } catch (Exception e) {
            e.printStackTrace();
            return null; // never happens
        }
    }

    /**
     * Returns a new message to encode the supplied (selector).
     *
     * @param selector a Bistro method or operator name.
     * @param operandCount the number of operands on the stack.
     * @param blockScope the scope that contains the message.
     * @return a new message to encode the supplied (selector).
     */
    public static Expression
            from(Selector selector, int operandCount, Block blockScope) {
        Message message = Message.sending(selector.contents(), blockScope);
        message.setLine(selector.getLine());
        return message.consumeOperands(operandCount, blockScope.operands());
    }

    /**
     * Returns a new message to encode the supplied (selector).
     *
     * @param selector a Bistro method or operator name.
     * @param blockScope the scope that contains the message.
     * @return a new message to encode the supplied (selector).
     */
    public static Expression
            from(Selector selector, Block blockScope) {
        return Message.from(selector, selector.operandCount(), blockScope);
    }

    /**
     * Processes the final expression in a block. When ending a method, append a statement that returns self. When
     * ending a block, append a statement that returns nil.
     *
     * @param blockScope the scope to be processed.
     */
    public static void endingBlock(Block blockScope) {
        Operand result = null;
        Stack operands = blockScope.operands();
        if (!operands.empty()) {
            result = (Operand) operands.pop();
            if (result instanceof Expression) {
                blockScope.addStatement(result);
            } else {
                operands.push(result);
                blockScope.addStatement(
                        Message.from(Selector.empty, 1, blockScope)
                );
            }
        }
        if (blockScope.isMethod()) {
            if (blockScope.needsResult()) {
                Message message = Message.sending(Selector.Exit, blockScope);
                message.addOperand(Reference.named(Reference.Self, blockScope));
                blockScope.addStatement(message);
            }
        } else if (result == null) {
            Message message = Message.sending(EmptyString, blockScope);
            message.addOperand(new LiteralNil(blockScope));
            blockScope.addStatement(message);
        }
    }

    /**
     * Returns a wrapper message which casts the arguments to their types declared in a (method).
     *
     * @param method the method to be invoked.
     * @param wrapper the wrapper method that contains the message.
     * @return a wrapper message.
     */
    public static Message wrapping(Method method, Method wrapper) {
        Message message = Message.sending(method.selector().contents(), wrapper);
        message.addOperand(Reference.named(Reference.Self, method));
        for (Variable argument : method.arguments()) {
            message.addOperand(Cast.fromWrapped(argument, wrapper));
        }
        return message;
    }

    /**
     * Process assignments for a (blockScope). Pop assignments from the selector stack and the corresponding pair of
     * operands from the operand stack. Push the assignment onto the operand stack.
     *
     * @param blockScope the scope to be processed.
     */
    public static void assignments(Block blockScope) {
        Stack selectors = blockScope.selectors();
        while (blockScope.hasAssignmentSelector()) {
            selectors.pop();
            blockScope.operands().push(
                    Message.from(Selector.forAssignment, 2, blockScope)
            );
        }
    }

    /**
     * Identifies the method to be invoked.
     */
    Selector selector = new Selector();

    /**
     * Contains the operands of the message, including the receiver.
     */
    List<Operand> operands = new ArrayList();

    /**
     * Indicates the type of the message result (if known).
     */
    Class resultType;

    /**
     * Constructs a new Message.
     *
     * @param blockScope the scope that contains the message.
     */
    public Message(Block blockScope) {
        super(blockScope);
        resultType = null;
    }

    /**
     * Describes the receiver, esp. for instrumentation purposes.
     */
    @Override
    public String description() {
        return getClass().getName() + " = " + name();
    }

    /**
     * Returns the name of this container.
     *
     * @return the name of this container.
     */
    @Override
    public String name() {
        return selector().contents();
    }

    /**
     * Establishes (container) as the container for this scope.
     *
     * @param container the container scope.
     */
    @Override
    public void container(Container container) {
        super.container(container);
        for (Operand o : operands()) {
            o.container(container);
        }
    }

    /**
     * Cleans the message operands.
     */
    public void cleanOperands() {
        List<Operand> copy = operands();
        operands.clear();
        for (Operand o : copy) {
            operands.add(o.cleanTerm());
        }
    }

    /**
     * Cleans out any residue left from the parsing process and prepares the receiver for code generation.
     */
    @Override
    public void clean() {
        super.clean();
        cleanOperands();
        String faceName = this.facialScope().name();
        if (!"Object".equals(faceName) && !selector.isEmpty()) {
            resolveType();
        }
    }

    /**
     * Resolves the type of this message if possible.
     */
    public void resolveType() {
        int ax = 0;
        Class[] argumentTypes = new Class[operandCount() - 1];

        // first, try to resolve a method based on the argument types
        for (Operand argument : arguments()) {
            argumentTypes[ax++] = argument.resolvedType();
        }
        Mirror mirror = Mirror.forClass(receiver().resolvedType());
        resultType = mirror.typeMethodNamed(
                selector.methodName(), argumentTypes
        );
        if (resultType != null) {
            return;
        }
        if (resultType == null) {
            return;
        }

        // finally, try to resolve a method with type erasure
        for (int i = 0; i < argumentTypes.length; i++) {
            Class aClass = argumentTypes[i];
            while ((aClass = aClass.getSuperclass()) != null) {
                if (aClass.getName().equals(RootClass)) {
                    argumentTypes[i] = aClass;
                }
            }
        }
        resultType = mirror.typeMethodNamed(
                selector.methodName(), argumentTypes
        );
    }

    /**
     * Indicates whether the message can be optimized.
     */
    public boolean canOptimizeInvocation() {
        if (receiver().optimizes(selector())) {
            return true;
        }
        return (resultType != null);
    }

    /**
     * Returns the type to which this message resolves.
     *
     * @return the type to which this message resolves.
     */
    @Override
    public Class resolvedType() {
        return (resultType == null
                ? super.resolvedType()
                : resultType);
    }

    /**
     * Returns the type name to which this message resolves.
     *
     * @return the type name to which this message resolves.
     */
    @Override
    public String resolvedTypeName() {
        return (resultType == null
                ? super.resolvedTypeName()
                : resultType.getName());
    }

    /**
     * Returns the block that contains the message.
     *
     * @return the block that contains the message.
     */
    public Block blockScope() {
        return (Block) container;
    }

    /**
     * Returns the receiver of the message.
     *
     * @return the receiver of the message.
     */
    public Operand receiver() {
        return operands.get(0);
    }

    public Operand firstArgument() {
        if (operands.size() < 2) {
            return null;
        }
        return operands.get(1);
    }

    /**
     * Returns the final operand of the message.
     */
    public Operand finalOperand() {
        return operands.get(operands.size() - 1);
    }

    /**
     * Establishes the receiver of the message.
     *
     * @param receiver the message receiver.
     */
    public void receiver(Operand receiver) {
        operands.add(0, receiver);
    }

    /**
     * Returns the receiver of the message after removing it.
     *
     * @return the message receiver.
     */
    public Operand removeReceiver() {
        Operand result = receiver();
        operands.remove(0);
        return result;
    }

    /**
     * Replaces the receiver of the message.
     *
     * @param receiver the message receiver.
     */
    public void replaceReceiver(Operand receiver) {
        removeReceiver();
        receiver(receiver);
    }

    /**
     * Returns an enumeration of the message operands.
     *
     * @return an enumeration of the message operands.
     */
    public List<Operand> operands() {
        return new ArrayList(operands);
    }

    /**
     * Returns an enumeration of the message arguments.
     *
     * @return an enumeration of the message arguments.
     */
    public List<Operand> arguments() {
        List<Operand> results = operands();
        results.remove(0);
        return results;
    }

    /**
     * Adds an operand to those used when sending the message.
     *
     * @param operand a message operand.
     */
    public void addOperand(Operand operand) {
        operands.add(operand);
    }

    /**
     * Adds an operand to those used when sending the message.
     *
     * @param operand a message operand.
     */
    public void addOperand(Object operand) {
        addOperand((Operand)operand);
    }

    /**
     * Returns the number of message operands.
     *
     * @return the number of message operands.
     */
    public int operandCount() {
        return operands.size();
    }

    /**
     * Establishes or appends to the message selector.
     *
     * @param aString the name to be appended.
     */
    public void selector(String aString) {
        selector.append(aString);
    }

    /**
     * Returns the message selector.
     */
    public Selector selector() {
        return selector;
    }

    /**
     * Returns whether the message exits the method.
     *
     * @return whether the message exits the method.
     */
    @Override
    public boolean returnsResult() {
        return selector.isReturn();
    }

    /**
     * Returns whether the receiver is a message.
     *
     * @return whether the receiver is a message.
     */
    @Override
    public boolean isMessage() {
        return true;
    }

    /**
     * Consumes (operandCount) operands from an (operandStack). Adds the operands to the actual arguments used in an
     * object method invocation (this message). If the message receiver is a cascade, it absorbs the message as a
     * cascaded message.
     *
     * @param operandCount the number of operands consumed.
     * @param operandStack a stack of operands.
     * @return the expression to be pushed onto the operand stack.
     */
    public Expression consumeOperands(int operandCount, Stack operandStack) {
        if (operandStack.size() < operandCount) {
            return null;
        }

        // transfer the message operands to a new stack
        Stack aStack = new Stack();
        while (operandCount-- > 0) {
            aStack.push(operandStack.pop());
        }
        Operand top = (Operand) aStack.pop();

        try {
            if (top.consumes(this)) {
                // add the message to the cascade
                Cascade cascade = (Cascade) top;
                cascade.addMessage(this);
                return cascade;
            } else {
                this.addOperand(top);
                return this;
            }
        } finally {
            // add the remaining operands to the message on the way out
            while (!aStack.empty()) {
                this.addOperand(aStack.pop());
            }
        }
    }

    /**
     * Returns the name of the method used to dynamically resolve the type of the message receiver.
     *
     * @return the name of a perform method.
     */
    public String performString() {
        int count = min(operands.size(), 6);
        return performs[count - 1];
    }

    /**
     * Returns the method name performed by the message receiver.
     *
     * @return the name of a performed method.
     */
    public Emission performedMethodName() {
        return selector.emitQuotedMethodName();
    }

    /**
     * Returns whether the receiver has elementary type.
     *
     * @return whether the receiver has elementary type.
     */
    public boolean elementaryReceiver() {
        Operand receiver = receiver();
        if (receiver.resolvesToPrimitive()) return true;

        Face typeFace = Face.named(receiver.resolvedTypeName());
        return (typeFace != null && typeFace.isElementary());
    }

    /**
     * Returns whether the message is primitive.
     *
     * @return whether the message is primitive.
     */
    public boolean isPrimitiveOperation() {
        if (operandCount() != 2) {
            return false;
        }
        if (!selector().isPrimitive()) {
            return false;
        }
        return receiver().resolvesToPrimitive();
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
        return elementaryReceiver() ? emitElementary() : emitInvocation();
    }

    public Emission emitElementary() {
        return selector().isPrimitive() ? emitPrimitive() : emitOptimized();
    }

    public Emission emitInvocation() {
        return canOptimizeInvocation() ? emitOptimized() : emitPerform();
    }

    public Emission emitPerform() {
        if (selector().isEmpty()) {
            return receiver().emitOperand();
        }

        if (facialScope().name().equals("Behavior")) {
            getLogger().info("");
        }

        return emit("Perform")
                .with("operand", receiver().emitOperand())
                .with("name", performString())
                .with("methodName", performedMethodName())
                .with("arguments", emitArguments());
    }

    public List<Emission> emitArguments() {
        return arguments().stream()
                .map(arg -> arg.emitOperand())
                .collect(Collectors.toList());
    }

    public Emission emitOp() {
        return emit("Operation")
                .with("operator", selector().asPrimitiveOperator())
                .with("argument", firstArgument().emitOperand());
    }

    public Emission emitCall() {
        return emitCall(selector().methodName());
    }

    public Emission emitCall(String methodName) {
        return emit("Call")
                .with("methodName", methodName)
                .with("arguments", emitArguments());
    }

    public Emission emitMethodCall() {
        return emitMethodCall(selector().methodName());
    }

    public Emission emitMethodCall(String methodName) {
        return emit("MethodCall")
                .with("methodName", methodName)
                .with("arguments", emitArguments());
    }

    public Emission emitExpression() {
        return emit("Expression")
                .with("operand", receiver().emitOperand())
                .with("messages", emitMethodCall());
    }

    @Override
    public Emission emitPrimitive() {
        return emit("Expression")
                .with("operand", receiver().emitOperand())
                .with("messages", emitOp());
    }

    @Override
    public Emission emitOptimized() {
        if (selector().isEmpty()) {
            return receiver().emitOperand();
        }

        if (selector().isSelfish()) {
            return emitCall();
        }

        return emitExpression();
    }

    public Emission emitAlternatives(boolean positively, Operand trueBlock, Operand falseBlock) {
        return emit("Alternatives")
                .with("condition", emitGuarded(receiver(), positively))
                .with("trueValue", emitOptimizedBlock(trueBlock))
                .with("falseValue", emitOptimizedBlock(falseBlock));
    }

    public Emission emitGuardedStatement(boolean positively, Operand aBlock) {
        return emit("GuardedBlock")
                .with("condition", emitGuarded(receiver(), positively))
                .with("aBlock", emitStatement(emitOptimizedBlock(aBlock)));
    }

    public Emission emitGuardedStatement(boolean positively, Operand trueBlock, Operand falseBlock) {
        return emit("GuardedPair")
                .with("condition", emitGuarded(receiver(), positively))
                .with("trueValue", emitStatement(emitOptimizedBlock(trueBlock)))
                .with("falseValue", emitStatement(emitOptimizedBlock(falseBlock)));
    }

    public Emission emitWhileLoop(boolean positively, Operand guardedBlock) {
        return emit("WhileLoop")
                .with("condition", emitGuarded(receiver(), positively))
                .with("guardedBlock", emitStatement(emitClosureValue(emitOptimizedBlock(guardedBlock))));
    }

    public Emission emitGuarded(Operand value, boolean positively) {
        return positively ? emitTrueGuard(value.emitOperand()) : emitFalseGuard(value.emitOperand());
    }

    public Emission emitOptimizedBlock(Operand aBlock) {
        return aBlock == null ? emitNil() : emitClosureValue(aBlock.emitOptimized());
    }
}
