//====================================================================
// BistroJavaEncoder.java
//====================================================================
package smalltalk.compiler;

import java.io.*;
import java.util.*;
import smalltalk.compiler.element.*;
import smalltalk.compiler.constant.*;
import smalltalk.compiler.expression.*;
import smalltalk.compiler.scope.*;
import smalltalk.compiler.scope.File;

/**
 * Generates Java code from a parsed syntax tree of a Bistro face definition.
 */
public abstract class BistroJavaEncoder {

    /**
     * Holds a space character.
     */
    protected static final String SPACE = " ";

    /**
     * Holds a semicolon character.
     */
    protected static final String SEMICOLON = ";";

    /**
     * Holds a colon character.
     */
    protected static final String COLON = ":";

    /**
     * Holds a comma character.
     */
    protected static final String COMMA = ",";

    /**
     * Holds a period character.
     */
    protected static final String DOT = ".";

    /**
     * Holds a bang character.
     */
    protected static final String BANG = "!";

    /**
     * Holds a dollar character.
     */
    protected static final String DOLLAR = "$";

    /**
     * Holds a left parenthesis character.
     */
    protected static final String NEW_TERM = "(";

    /**
     * Holds a right parenthesis character.
     */
    protected static final String END_TERM = ")";

    /**
     * Holds a left curly brace character.
     */
    protected static final String NEW_BLOCK = "{";

    /**
     * Holds a right curly brace character.
     */
    protected static final String END_BLOCK = "}";

    /**
     * Holds a quote character.
     */
    protected static final String QUOTE = "\"";

    /**
     * Holds a tab character for indenting code.
     */
    protected static final String TAB = "\t";

    /**
     * Holds a line feed character.
     */
    protected static final int LF = '\n';

    /**
     * Holds a carriage return character.
     */
    protected static final int CR = '\r';

    /**
     * Holds a method return.
     */
    protected static final String RETURN = "return";

    /**
     * Contains the Boolean type name.
     */
    protected static final String BooleanType = "(smalltalk.behavior.Boolean)";

    /**
     * Refers to the target stream for Java code generation.
     */
    protected PrintWriter oStream;

    /**
     * Indicates the identation level of the output code.
     */
    protected int tabCount;

    /**
     * Constructs a new BistroJavaEncoder.
     *
     * @param aStream for the output Java code.
     */
    public BistroJavaEncoder(PrintWriter aStream) {
        oStream = aStream;
        tabCount = 0;
    }

    /**
     * Returns the encoder for primitives.
     */
    public abstract Operand.Visitor primitiveEncoder();

    /**
     * Returns the encoder for optimizations.
     */
    public abstract Operand.Visitor optimizedEncoder();

    /**
     * Returns the encoder for statements.
     */
    public abstract Operand.Visitor statementEncoder();

    /**
     * Returns the encoder for results.
     */
    public abstract Operand.Visitor resultantEncoder();

    /**
     * Returns the encoder for operands.
     */
    public abstract Operand.Visitor operandEncoder();

    // punctuation
    /**
     * Prints a new line and the indentation tabs.
     *
     * @param tabDelta changes the tab indentation.
     */
    public void encodeNewLine(int tabDelta) {
        tabCount += tabDelta;
        oStream.println();
        for (int i = 0; i < tabCount; i++) {
            oStream.print(TAB);
        }
    }

    /**
     * Prints a new line and the indentation tabs.
     */
    public void encodeNewLine() {
        encodeNewLine(0);
    }

    /**
     * Appends the code for a new block onto (oStream).
     */
    public void encodeNewBlock() {
        oStream.print(SPACE);
        oStream.print(NEW_BLOCK);
        encodeNewLine(1);
    }

    /**
     * Appends the code for a block end onto (oStream).
     */
    public void encodeEndBlock() {
        encodeNewLine(-1);
        oStream.print(END_BLOCK);
    }

    /**
     * Appends the code for a new term onto (oStream).
     */
    public void encodeNewTerm() {
        oStream.print(NEW_TERM);
        encodeNewLine(1);
    }

    /**
     * Appends the code for a term end onto (oStream).
     */
    public void encodeEndTerm() {
        encodeNewLine(-1);
        oStream.print(END_TERM);
    }

    // decorations
    /**
     * Appends the modifiers for a (variable) onto (oStream).
     *
     * @param variable a Variable.
     */
    public void encodeModifiers(Variable variable) {
        variable.acceptModifierVisitor(
                new Reference.Visitor() {
            public void visit(String modifier) {
                oStream.print(modifier);
                oStream.print(SPACE);
            }
        }
        );
    }

    /**
     * Appends any modifiers for a (code) scope onto (oStream).
     *
     * @param code a Code scope.
     */
    public void encodeModifiers(Code code) {
        code.acceptModifierVisitor(
                new Reference.Visitor() {
            public void visit(String modifier) {
                oStream.print(modifier);
                oStream.print(SPACE);
            }
        }
        );
    }

    // references
    /**
     * Appends a (typeName) onto (oStream).
     *
     * @param typeName identifies a type.
     */
    public void encodeTypeName(String typeName) {
        if (typeName.equals(Base.RootClass)) {
            oStream.print(Base.SimpleRoot);
        } else {
            oStream.print(typeName);
        }
    }

    /**
     * Appends a type annotation for a (typeName) onto (oStream).
     *
     * @param typeName identifies a type.
     */
    public void encodeType(String typeName) {
        oStream.print(NEW_TERM);
        encodeTypeName(typeName);
        oStream.print(END_TERM);
    }

    /**
     * Appends a casted (argument) name onto (oStream).
     *
     * @param argument a Variable argument.
     */
    public void encodeCast(Variable argument) {
        if (argument.type().equals(Base.RootClass)) {
            oStream.print(argument.name());
        } else {
            encodeType(argument.type());
            oStream.print(SPACE);
            oStream.print(argument.name());
        }
    }

    /**
     * Appends a (selector) method name onto (oStream).
     *
     * @param selector a Selector.
     */
    public void encodeSelector(Selector selector) {
        oStream.print(selector.methodName());
    }

    /**
     * Appends an (encodedName) onto (oStream).
     *
     * @param encodedName the name of a variable or argument.
     */
    public void encodeReference(String encodedName) {
        oStream.print(encodedName);
    }

    /**
     * Appends the code for a (variable) onto (oStream).
     *
     * @param variable a Variable.
     */
    public void encodeVariable(Variable variable) {
        if (variable.hasComment()) {
            oStream.print(variable.comment());
            encodeNewLine();
        }
        encodeModifiers(variable);
        encodeTypeName(variable.type());
        oStream.print(SPACE);
        oStream.print(variable.name());
        if (variable.value() == null) {
            return;
        }

        oStream.print(" = ");
        if (!variable.type().equals(variable.value().resolvedTypeName())) {
            encodeType(variable.type());
            oStream.print(SPACE);
        }
        encodeOperand(variable.value());
    }

    /**
     * Appends the code for an (argument) onto (oStream).
     *
     * @param argument a Variable argument.
     * @param asFinal indicates whether to declare it final.
     */
    public void encodeArgument(Variable argument, boolean asFinal) {
        if (asFinal) {
            oStream.print("final ");
        }
        encodeTypeName(argument.type());
        oStream.print(SPACE);
        oStream.print(argument.name());
    }

    /**
     * Appends any exceptions thrown by a (block) onto (oStream).
     *
     * @param block a Block scope.
     */
    public void encodeExceptions(Block block) {
        if (block.exceptionCount() == 0) {
            return;
        }
        oStream.print(" throws");
        block.acceptExceptionVisitor(
                new Reference.Visitor() {
            int count = 0;

            public void visit(String exceptionName) {
                oStream.print(SPACE);
                if (count++ > 0) {
                    oStream.print(COMMA);
                }
                oStream.print(exceptionName);
            }
        }
        );
    }

    /**
     * Appends any locals for a (code) scope onto (oStream).
     *
     * @param code a Code scope.
     */
    public void encodeLocals(final Code code) {
        code.acceptLocalVisitor(new Variable.Visitor() {
            int count = 0;

            public void visit(Variable variable) {
                if (count++ > 0) {
                    encodeNewLine();
                }

//                System.out.println("encoding " + variable.scopeDescription() + " " + variable.name());

                encodeVariable(variable);
                oStream.print(SEMICOLON);
                oStream.flush();
            }
        });
        if (code.hasLocals() && !code.isFacial()) {
            encodeNewLine();
        }
    }

    /**
     * Appends the arguments for a (message) onto (oStream).
     *
     * @param message a Message.
     */
    public void encodeArguments(Message message) {
        int count = 0;
        for (Operand operand : message.arguments()) {
            if (count > 0) {
                oStream.print(COMMA);
                encodeNewLine();
            }
            encodeOperand(operand);
            count++;
        }
    }

    /**
     * Appends the argument list for a (message) onto (oStream).
     *
     * @param message a Message.
     */
    public void encodeArgumentList(Message message) {
        if (message.operandCount() == 1) {
            oStream.print(NEW_TERM);
            oStream.print(END_TERM);
        } else {
            encodeNewTerm();
            encodeArguments(message);
            encodeEndTerm();
        }
    }

    /**
     * Appends the arguments of a (block) onto (oStream).
     *
     * @param block a Block scope.
     * @param argumentCount indicates the number of arguments to include.
     */
    public void encodeFinalArguments(Block block, int argumentCount) {
        oStream.print(NEW_TERM);
        if (argumentCount > 0) {
            block.acceptArgumentVisitor(
                    new Variable.Visitor() {
                int count = 0;

                public void visit(Variable argument) {
                    if (count++ > 0) {
                        oStream.print(COMMA);
                    }
                    encodeArgument(argument, true);
                }
            }
            );
        }
        oStream.print(END_TERM);
    }

    /**
     * Appends the arguments of a (block) onto (oStream).
     *
     * @param block a Block scope.
     */
    public void encodeArguments(Block block) {
        oStream.print(NEW_TERM);
        block.acceptArgumentVisitor(
                new Variable.Visitor() {
            int count = 0;

            public void visit(Variable argument) {
                if (count++ > 0) {
                    oStream.print(COMMA);
                }
                encodeArgument(argument, false);
            }
        }
        );
        oStream.print(END_TERM);
    }

    /**
     * Appends the argument names of a (block) onto (oStream).
     *
     * @param block a Block scope.
     */
    public void encodeArgumentNames(Block block) {
        oStream.print(NEW_TERM);
        block.acceptArgumentVisitor(
                new Variable.Visitor() {
            int count = 0;

            public void visit(Variable argument) {
                if (count++ > 0) {
                    oStream.print(COMMA);
                }
                oStream.print(argument.name());
            }
        }
        );
        oStream.print(END_TERM);
    }

    /**
     * Appends the arguments of a (block) onto (oStream) with type erasures.
     *
     * @param block a Block scope.
     */
    public void encodeErasedArguments(Block block) {
        block.acceptArgumentVisitor(
                new Variable.Visitor() {
            int count = 0;

            public void visit(Variable argument) {
                if (count++ > 0) {
                    oStream.print(COMMA);
                }
                oStream.print("final Object ");
                oStream.print(argument.name());
            }
        }
        );
    }

    /**
     * Appends the arguments of a (block) onto (oStream) as casted types.
     *
     * @param block a Block scope.
     */
    public void encodeCastedArguments(Block block) {
        block.acceptArgumentVisitor(
                new Variable.Visitor() {
            int count = 0;

            public void visit(Variable argument) {
                if (count++ > 0) {
                    oStream.print(COMMA);
                }
                encodeCast(argument);
            }
        }
        );
    }

    // primitives
    /**
     * Appends the code for an (operand) onto (oStream).
     *
     * @param operand an Operand.
     */
    public void encodePrimitive(Operand operand) {
        encodeOperand(operand);
    }

    /**
     * Appends the code for an (operand) onto (oStream).
     *
     * @param operand a LiteralNil.
     */
    public void encodePrimitive(LiteralNil operand) {
        oStream.print(operand.rawValue());
    }

    /**
     * Appends the code for an (operand) onto (oStream).
     *
     * @param operand a LiteralBoolean.
     */
    public void encodePrimitive(LiteralBoolean operand) {
        oStream.print(operand.rawValue());
    }

    /**
     * Appends the code for an (operand) onto (oStream).
     *
     * @param operand a LiteralCharacter.
     */
    public void encodePrimitive(LiteralCharacter operand) {
        oStream.print(operand.encodedValue());
    }

    /**
     * Appends the code for an (operand) onto (oStream).
     *
     * @param operand a LiteralInteger.
     */
    public void encodePrimitive(LiteralInteger operand) {
        oStream.print(operand.encodedValue());
    }

    /**
     * Appends the code for an (operand) onto (oStream).
     *
     * @param operand a LiteralFloat.
     */
    public void encodePrimitive(LiteralFloat operand) {
        oStream.print(operand.encodedValue());
    }

    /**
     * Appends the code for an (operand) onto (oStream).
     *
     * @param operand a LiteralString.
     */
    public void encodePrimitive(LiteralString operand) {
        oStream.print(operand.encodedValue());
    }

    /**
     * Appends the code for an (operand) onto (oStream).
     *
     * @param operand a LiteralSymbol.
     */
    public void encodePrimitive(LiteralSymbol operand) {
        oStream.print(operand.encodedValue());
    }

    /**
     * Appends the code for a primitive (message) onto (oStream).
     *
     * @param message a Message.
     */
    public void encodePrimitive(Message message) {
        message.receiver().acceptVisitor(primitiveEncoder());
        oStream.print(message.selector().asPrimitiveOperator());
        message.finalOperand().acceptVisitor(primitiveEncoder());
    }

    /**
     * Appends the code for an assignment (message) onto (oStream).
     *
     * @param message an Assignment message.
     */
    public void encodePrimitive(Assignment message) {
        Operand receiver = message.receiver();
        Operand argument = message.firstArgument();
        receiver.acceptVisitor(operandEncoder());
        oStream.print(" = ");
        argument.acceptVisitor(primitiveEncoder());
    }

    /**
     * Appends the code for an instantiation (message) onto (oStream).
     *
     * @param message an Instantiation message.
     */
    public void encodePrimitive(Instantiation message) {
        encodeOperand(message);
    }

    /**
     * Appends the code of a primitive (method) onto (oStream).
     *
     * @param method a Method scope.
     */
    public void encodePrimitive(Method method) {
        encodeSignature(method);
        encodeNewLine();
        try {
            StringReader iStream = new StringReader(method.code());
            while (true) {
                int c = iStream.read();
                if (c < 0) {
                    iStream.close();
                    return;
                }
                if (c == LF) {
                    oStream.println();
                    if (method.facialScope().isMetaface()) {
                        oStream.print(TAB);
                    }
                } else if (c != CR) {
                    oStream.print((char) c);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // optimizations
    /**
     * Appends the code for an (operand) onto (oStream).
     *
     * @param operand an Operand.
     */
    public void encodeOptimized(Operand operand) {
        encodeOperand(operand);
    }

    /**
     * Appends the code for an (operand) onto (oStream).
     *
     * @param operand a LiteralCollection.
     */
    public void encodeOptimized(LiteralCollection operand) {
        encodeOperand(operand);
    }

    /**
     * Appends the code for an optimized (message) onto (oStream).
     *
     * @param message a Message.
     */
    public void encodeOptimized(Message message) {
        if (message.selector().isEmpty()) {
            encodeOperand(message.receiver());
            return;
        }
        if (!message.selector().methodName().equals(Reference.Super)) {
            encodeReceiver(message.receiver());
            oStream.print(DOT);
        }
        oStream.print(message.selector().methodName());
        encodeArgumentList(message);
    }

    /**
     * Appends the code for an assignment (message) onto (oStream).
     *
     * @param message an Assignment message.
     */
    public void encodeOptimized(Assignment message) {
        Operand receiver = message.receiver();
        Operand argument = message.firstArgument();
        String type = receiver.resolvedTypeName();
        String argumentType = argument.resolvedTypeName();
        receiver.acceptVisitor(operandEncoder());
        oStream.print(" = ");
        if (!type.equals(argumentType)) {
            encodeType(type);
        }
        argument.acceptVisitor(operandEncoder());
    }

    /**
     * Appends the code for a (message) as a statement onto (oStream).
     *
     * @param message an Assertion message.
     */
    public void encodeOptimized(Assertion message) {
        int count = message.operandCount();
        Operand receiver = message.receiver();
        oStream.print(Assertion.COMMAND);
        oStream.print(SPACE);
        if (receiver.isMessage()) {
            Message m = (Message) receiver;
            if (!m.isPrimitiveOperation()) {
                oStream.print(message.localPrimitive());
                oStream.print(".booleanFrom");
            }
        }
        encodeNewTerm();
        encodeOperand(receiver);
        encodeEndTerm();
        if (count > 1) {
            Operand failure = message.firstArgument();
            oStream.print(SPACE);
            oStream.print(COLON);
            oStream.print(SPACE);
            oStream.print(NEW_TERM);
            failure.acceptVisitor(optimizedEncoder());
            oStream.print(END_TERM);
            oStream.print(".value()");
        }
    }

    /**
     * Appends the code for an optimized (block) onto (oStream).
     *
     * @param block a Block scope.
     */
    public void encodeOptimized(Block block) {
        encodeNewClosure(block.argumentCount());
        encodeNewBlock();
        encodeLocals(block);
        encodeSignature(block, block.argumentCount());
        encodeNewBlock();
        if (block.returnsVoid()) {
            encodeOnlyStatements(block);
        } else {
            encodeAllStatements(block);
        }
        encodeEndBlock();
        encodeEndBlock();
    }

    // operands
    /**
     * Appends the code for an (operand) onto (oStream).
     *
     * @param operand an Operand.
     */
    public void encodeOperand(Operand operand) {
        // should be resolved by (operand) type refinement.
        operand.acceptVisitor(operandEncoder());
    }

    /**
     * Appends the code for an (operand) onto (oStream).
     *
     * @param operand a Scalar.
     */
    public void encodeOperand(Scalar operand) {
        oStream.print("Object.primitive.");
        oStream.print(operand.primitiveFactoryName());
        oStream.print("From");
        oStream.print(NEW_TERM);
        oStream.print(operand.encodedValue());
        oStream.print(END_TERM);
    }

    /**
     * Appends the code for an (operand) onto (oStream).
     *
     * @param operand a LiteralNil.
     */
    public void encodeOperand(LiteralNil operand) {
        oStream.print(operand.encodedValue());
    }

    /**
     * Appends the code for an (operand) onto (oStream).
     *
     * @param operand a LiteralBoolean.
     */
    public void encodeOperand(LiteralBoolean operand) {
        oStream.print(operand.encodedValue());
    }

    /**
     * Appends the code for an (operand) onto (oStream).
     *
     * @param operand a LiteralNumber.
     */
    public void encodeOperand(LiteralNumber operand) {
        if (operand.container().fileScope().needsMagnitudes()) {
            encodeOperand((Scalar) operand);
            return;
        }
        oStream.print(NEW_TERM);
        encodeType(operand.declaredType());
        encodeOperand((Scalar) operand);
        oStream.print(END_TERM);
    }

    /**
     * Appends the code for an (operand) onto (oStream).
     *
     * @param operand a Scalar.
     */
    public void encodeOperand(LiteralCollection operand) {
        if (operand.container().fileScope().needsCollections()) {
            encodeOperand((Scalar) operand);
            return;
        }
        oStream.print(NEW_TERM);
        encodeType(operand.declaredType());
        encodeOperand((Scalar) operand);
        oStream.print(END_TERM);
    }

    /**
     * Appends the code for an (operand) onto (oStream).
     *
     * @param element an ObjectArray.
     */
    public void encodeOperand(ObjectArray operand) {
        encodeNewLine(1);
        oStream.print("Array.$class.withAll");
        encodeNewTerm();
        oStream.print("new Object[]");
        encodeNewBlock();
        int count = 0;
        List<Constant> constants = operand.contents();
        for (Constant c : constants) {
            if (count > 0) {
                encodeNewLine();
            }

            encodeOperand((Operand) c);

            if (count < constants.size()) {
                oStream.print(COMMA);
            }

            count++;
        }
        encodeEndBlock();
        encodeEndTerm();
    }

    /**
     * Appends the code for a (message) onto (oStream).
     *
     * @param message a Message.
     */
    public void encodeOperand(Message message) {
        if (message.elementaryReceiver()) {
            if (message.selector().isPrimitive()) {
                encodePrimitive(message);
            } else {
                message.acceptVisitor(optimizedEncoder());
            }
            return;
        }
        if (message.canOptimizeInvocation()) {
            message.acceptVisitor(optimizedEncoder());
        } else {
            encodePerform(message);
        }
    }

    /**
     * Appends the code for a (message) onto (oStream).
     *
     * @param message an And message.
     */
    public void encodeOperand(And message) {
        encodeBooleanTerm(message.receiver());
        oStream.print(DOT);
        oStream.print("and");
        encodeTerm(message.finalOperand());
    }

    /**
     * Appends the code for a (message) onto (oStream).
     *
     * @param message an Or message.
     */
    public void encodeOperand(Or message) {
        encodeBooleanTerm(message.receiver());
        oStream.print(DOT);
        oStream.print("or");
        encodeTerm(message.finalOperand());
    }

    /**
     * Appends the code for an assignment (message) onto (oStream).
     *
     * @param message an Assignment message.
     */
    public void encodeOperand(Assignment message) {
        encodeOptimized(message);
    }

    /**
     * Appends the code for a (message) as a statement onto (oStream).
     *
     * @param message an Assertion message.
     */
    public void encodeOperand(Assertion message) {
        encodeOptimized(message);
    }

    /**
     * Appends the code for a (message) onto (oStream).
     *
     * @param message a Cast message.
     */
    public void encodeOperand(Cast message) {
        Reference type = (Reference) message.finalOperand();
        encodeType(type.name());
        encodeTerm(message.receiver());
    }

    /**
     * Appends the code for a (message) onto (oStream).
     *
     * @param message an Exit message.
     */
    public void encodeOperand(Exit message) {
        message.receiver().acceptVisitor(operandEncoder());
    }

    /**
     * Appends the code for a (message) onto (oStream).
     *
     * @param message an Instantiation message.
     */
    public void encodeOperand(Instantiation message) {
        if (message.selector().isBasicNew()) {
            encodeBasicNew(message);
        } else {
            encodeCastedNew(message);
        }
    }

    /**
     * Appends the code for an alternative (message) onto (oStream).
     *
     * @param message an alternative Message.
     * @param type indicates whether to negate the condition.
     * @param trueBlock the true alternative.
     * @param falseBlock the false alternative.
     */
    public void encodeOperand(
            Message message, boolean type,
            Operand trueBlock, Operand falseBlock) {
        encodeNewTerm();
        if (!type) {
            oStream.print(BANG);
        }
        oStream.print(message.localPrimitive());
        oStream.print(".booleanFrom");
        encodeNewTerm();
        encodeOperand(message.receiver());
        encodeEndTerm();
        oStream.print(" ? ");
        encodeNewTerm();
        trueBlock.acceptVisitor(optimizedEncoder());
        encodeEndTerm();
        oStream.print(".value() : ");
        if (falseBlock == null) {
            oStream.print(message.localPrimitive());
            oStream.print(".literalNil()");
        } else {
            encodeNewTerm();
            falseBlock.acceptVisitor(optimizedEncoder());
            encodeEndTerm();
            oStream.print(".value()");
        }
        encodeEndTerm();
    }

    /**
     * Appends the code for a (message) onto (oStream).
     *
     * @param message an IfTrue message.
     */
    public void encodeOperand(IfTrue message) {
        Operand block = message.firstArgument();
        encodeOperand(message, true, block, null);
    }

    /**
     * Appends the code for a (message) onto (oStream).
     *
     * @param message an IfTrueIfFalse message.
     */
    public void encodeOperand(IfTrueIfFalse message) {
        List<Operand> arguments = message.arguments();
        Operand trueBlock = arguments.get(0);
        Operand falseBlock = arguments.get(1);
        encodeOperand(message, true, trueBlock, falseBlock);
    }

    /**
     * Appends the code for a (message) onto (oStream).
     *
     * @param message an IfFalse message.
     */
    public void encodeOperand(IfFalse message) {
        Operand block = message.firstArgument();
        encodeOperand(message, false, block, null);
    }

    /**
     * Appends the code for a (message) onto (oStream).
     *
     * @param message an IfFalseIfTrue message.
     */
    public void encodeOperand(IfFalseIfTrue message) {
        List<Operand> arguments = message.arguments();
        Operand falseBlock = arguments.get(0);
        Operand trueBlock = arguments.get(1);
        encodeOperand(message, true, trueBlock, falseBlock);
    }

    /**
     * Appends the code for an exception (message) idiom onto (oStream).
     *
     * @param message a TryCatch message.
     */
    public void encodeOperand(TryCatch message) {
        encodeNewTerm();
        Nest receiver = (Nest) message.receiver();
        Nest finalBlock = message.finalBlock();
        encodeNewClosure(0);
        encodeNewBlock();
        encodeSignature(receiver.nestedBlock(), 0);
        encodeNewBlock();
        encodeTry(receiver);
        for (Operand argument : message.arguments()) {
            Nest block = (Nest) argument;
            if (block != finalBlock) {
                encodeCatch(block);
            }
        }
        if (finalBlock != null) {
            encodeFinally(finalBlock);
        }
        encodeEndBlock();
        encodeEndBlock();
        encodeEndTerm();
        oStream.print(".value()");
    }

    /**
     * Appends the code for a (message) as an operand onto (oStream).
     *
     * @param message a PrimitiveLiteral message.
     */
    public void encodeOperand(PrimitiveLiteral message) {
        Operand receiver = message.receiver();
        if (receiver.isLiteral()) {
            receiver.acceptVisitor(primitiveEncoder());
        } else {
            encodeOptimized((Message) message);
        }
    }

    /**
     * Appends the code for a message (cascade) onto (oStream).
     *
     * @param cascade a message Cascade.
     */
    public void encodeOperand(Cascade cascade) {
        int count = 0;
        List<Message> messages = cascade.messages();
        for (Message message : messages) {
            message.acceptVisitor(operandEncoder());
            count++;
            if (count < messages.size()) {
                oStream.print(SEMICOLON);
                encodeNewLine();
            }
        }
    }

    /**
     * Appends the code for a nested (block) onto (oStream).
     *
     * @param operand a block Nest.
     */
    public void encodeOperand(Nest operand) {
        encodeClosure(operand.nestedBlock());
    }

    /**
     * Appends the code for a (block) as an operand onto (oStream).
     *
     * @param block a Block.
     */
    public void encodeOperand(Block block) {
        if (block.statementCount() != 1) {
            return;
        }
        block.acceptStatementVisitor(operandEncoder());
    }

    /**
     * Appends the code of an inner class (innard) onto (oStream).
     *
     * @param innard an Innard.
     */
    public void encodeOperand(Innard innard) {
        Face face = innard.nestedClass();
        oStream.print("new ");
        oStream.print(face.baseName());
        oStream.print(NEW_TERM);
        oStream.print(END_TERM);
        encodeNewBlock();
        face.acceptMethodVisitor((Method.Visitor) operandEncoder());
        encodeEndBlock();
    }

    /**
     * Appends the code for an (operand) term onto (oStream).
     *
     * @param operand an Operand.
     */
    public void encodeTerm(Operand operand) {
        oStream.print(NEW_TERM);
        operand.acceptVisitor(operandEncoder());
        oStream.print(END_TERM);
    }

    /**
     * Appends a Boolean (operand) term onto (oStream).
     *
     * @param operand an Operand.
     */
    public void encodeBooleanTerm(Operand operand) {
        oStream.print(NEW_TERM);
        oStream.print(BooleanType);
        encodeTerm(operand);
        oStream.print(END_TERM);
    }

    /**
     * Appends the code for an (operand) as a message receiver onto (oStream).
     *
     * @param operand an Operand.
     */
    public void encodeReceiver(Operand operand) {
        if (operand.receiverNeedsTerm()) {
            encodeTerm(operand);
        } else {
            encodeOperand(operand);
        }
    }

    /**
     * Appends the code for an (operand) result onto (oStream).
     *
     * @param operand an Operand.
     */
    public void encodeResult(Operand operand) {
        oStream.print(RETURN);
        encodeNewTerm();
        encodeOperand(operand);
        encodeEndTerm();
        oStream.print(SEMICOLON);
    }

    // expressions
    /**
     * Appends the code for a null result onto (oStream).
     */
    public void encodeResultNull() {
        oStream.print(RETURN);
        oStream.print(NEW_TERM);
        oStream.print("null");
        oStream.print(END_TERM);
        oStream.print(SEMICOLON);
    }

    /**
     * Appends the code for a result (message) onto (oStream).
     *
     * @param message a Message.
     */
    public void encodeResult(Message message) {
        oStream.print(RETURN);
        encodeNewTerm();
        encodeOperand(message);
        encodeEndTerm();
        oStream.print(SEMICOLON);
    }

    /**
     * Appends the code for a result (message) onto (oStream).
     *
     * @param message a TryCatch message.
     */
    public void encodeResult(TryCatch message) {
        oStream.print(RETURN);
        encodeNewTerm();
        encodeOperand(message);
        encodeEndTerm();
        oStream.print(SEMICOLON);
    }

    /**
     * Appends the code for a result (message) onto (oStream).
     *
     * @param message a Message.
     */
    public void encodeOptimizedResult(Message message) {
        oStream.print(RETURN);
        encodeNewTerm();
        message.acceptVisitor(optimizedEncoder());
        encodeEndTerm();
        oStream.print(SEMICOLON);
    }

    /**
     * Appends the code for a result (message) onto (oStream).
     *
     * @param message an Exit message.
     */
    public void encodeResult(Exit message) {
        Block block = message.blockScope();
        Method method = block.currentMethod();
        if (block.isMethod()) {
            if (message.receiver().isSelfish()
                    && method.returnsVoid()) {
                if (method.hasLocals()) {
                    encodeResultNull();
                }
                return;
            }
            if (method.exits()) {
                encodeMethodExit(message);
            } else {
                encodeResultExit(message);
            }
        } else {
            encodeMethodExit(message);
        }
    }

    /**
     * Appends a method exit onto (oStream) as an exception.
     *
     * @param message an Exit message.
     */
    public void encodeMethodExit(Exit message) {
        oStream.print("throw new MethodExit");
        encodeNewTerm();
        oStream.print(message.blockScope().currentMethod().scopeID());
        oStream.print(COMMA);
        encodeNewLine();
        message.receiver().acceptVisitor(operandEncoder());
        encodeEndTerm();
        oStream.print(SEMICOLON);
    }

    /**
     * Appends a method result onto (oStream).
     *
     * @param message an Exit message.
     */
    public void encodeResultExit(Exit message) {
        oStream.print(RETURN);
        encodeNewTerm();
        message.receiver().acceptVisitor(operandEncoder());
        encodeEndTerm();
        oStream.print(SEMICOLON);
    }

    /**
     * Appends the code for a dynamic (message) onto (oStream).
     *
     * @param message a Message.
     */
    public void encodePerform(Message message) {
        if (message.selector().isEmpty()) {
            encodeOperand(message.receiver());
            return;
        }
        encodeReceiver(message.receiver());
        oStream.print(message.performString());
        if (message.operandCount() > 1) {
            encodeNewTerm();
            oStream.print(QUOTE);
            oStream.print(message.performedMethodName());
            oStream.print(QUOTE);
            oStream.print(COMMA);
            encodeNewLine();
            encodeArguments(message);
            encodeEndTerm();
        } else {
            oStream.print(NEW_TERM);
            oStream.print(QUOTE);
            oStream.print(message.performedMethodName());
            oStream.print(QUOTE);
            oStream.print(END_TERM);
        }
    }

    /**
     * Appends the statements of a try block onto (oStream).
     *
     * @param nest a block Nest.
     */
    public void encodeTry(Nest nest) {
        //encodeNewLine();
        oStream.print("try");
        encodeElementary(nest.nestedBlock());
    }

    /**
     * Appends the statements of a catch block onto (oStream).
     *
     * @param nest a block Nest.
     */
    public void encodeCatch(Nest nest) {
        encodeNewLine();
        oStream.print("catch");
        encodeArguments(nest.nestedBlock());
        encodeElementary(nest.nestedBlock());
    }

    /**
     * Appends the statements of a finally block onto (oStream).
     *
     * @param nest a block Nest.
     */
    public void encodeFinally(Nest nest) {
        encodeNewLine();
        oStream.print("finally");
        encodeElementary(nest.nestedBlock(), true);
    }

    /**
     * Appends the code for conditional alternatives onto (oStream).
     *
     * @param message a conditional message.
     * @param type indicates whether the condition is positive.
     * @param trueBlock a block to evaluate when true.
     * @param falseBlock a block to evaluate when false.
     */
    public void encodeGuarded(
            Message message, boolean type,
            Operand trueBlock, Operand falseBlock) {
        oStream.print("if ");
        encodeNewTerm();
        if (!type) {
            oStream.print(BANG);
        }
        oStream.print(message.localPrimitive());
        oStream.print(".booleanFrom");
        encodeNewTerm();
        encodeOperand(message.receiver());
        encodeEndTerm();
        encodeEndTerm();
        encodeNewBlock();
        oStream.print(NEW_TERM);
        trueBlock.acceptVisitor(optimizedEncoder());
        oStream.print(END_TERM);
        oStream.print(".value();");
        encodeEndBlock();
        if (falseBlock != null) {
            oStream.print(" else ");
            encodeNewBlock();
            oStream.print(NEW_TERM);
            falseBlock.acceptVisitor(optimizedEncoder());
            oStream.print(END_TERM);
            oStream.print(".value();");
            encodeEndBlock();
        }
    }

    /**
     * Appends the code for a looping (message) onto (oStream).
     *
     * @param message a loop message.
     * @param block a loop Block.
     * @param type indicates whether to negate the loop condition.
     */
    public void encodeLoop(Block block, Message message, boolean type) {
        Operand loop = message.firstArgument();
        oStream.print("while");
        encodeNewTerm();
        if (!type) {
            oStream.print(BANG);
        }
        oStream.print(message.localPrimitive());
        oStream.print(".booleanFrom");
        encodeNewTerm();
        encodeOperand(block);
        encodeEndTerm();
        encodeEndTerm();
        encodeNewBlock();
        encodeTerm(loop);
        oStream.print(".value()");
        oStream.print(SEMICOLON);
        encodeEndBlock();
    }

    /**
     * Appends the code for a looping (message) onto (oStream).
     *
     * @param message a looping Message.
     * @param type indicates whether to negate the loop condition.
     */
    public void encodeLoop(Message message, boolean type) {
        Operand receiver = message.receiver();
        if (!receiver.resolvedTypeName().equals(Base.RootClass)) {
            message.acceptVisitor(optimizedEncoder());
            oStream.print(SEMICOLON);
            return;
        }
        Block aBlock = ((Nest) receiver).nestedBlock();
        if (aBlock.statementCount() > 1
                || aBlock.localCount() > 0) {
            message.acceptVisitor(optimizedEncoder());
            oStream.print(SEMICOLON);
            return;
        }
        encodeLoop(aBlock, message, type);
    }

    /**
     * Appends a typed instantiation message onto (oStream).
     *
     * @param reference identifies a class to instantiate.
     */
    public void encodeTypedNew(Reference reference) {
        encodeType(reference.facialTypeName());
        encodeOperand(reference);
        oStream.print(".$new");
    }

    /**
     * Appends a behavior instantiation message onto (oStream).
     *
     * @param reference identifies a class to instantiate.
     */
    public void encodeBehaviorNew(Reference reference) {
        oStream.print(NEW_TERM);
        encodeType("Behavior");
        encodeOperand(reference);
        oStream.print(END_TERM);
        oStream.print(".$new");
    }

    /**
     * Appends a behavior instantiation message onto (oStream).
     *
     * @param operand indicates the behavior to instantiate.
     */
    public void encodeBehaviorNew(Operand operand) {
        oStream.print(NEW_TERM);
        encodeType("Behavior");
        encodeOperand(operand);
        oStream.print(END_TERM);
        oStream.print(".$new");
    }

    /**
     * Appends a primitive instantiation message onto (oStream).
     *
     * @param message an Instantiation message.
     */
    public void encodeBasicNew(Instantiation message) {
        try {
            Reference receiver = (Reference) message.receiver();
            oStream.print("new ");
            oStream.print(receiver.name());
            encodeArgumentList(message);
        } catch (ClassCastException e) {
            // can't determine the class, so emit error
            oStream.print("/* basicNew requires a class name. */ ");
            oStream.print("new Object()");
        }
    }

    /**
     * Appends a simple instantiation message onto (oStream).
     *
     * @param message an Instantiation message.
     */
    public void encodeSimpleNew(Instantiation message) {
        try {
            Reference receiver = (Reference) message.receiver();
            if (receiver.isSelfish()) {
                encodeTypedNew(receiver);
            } else {
                encodeBehaviorNew(receiver);
            }
        } catch (ClassCastException e) {
            encodeBehaviorNew(message.receiver());
        }
        encodeArgumentList(message);
    }

    /**
     * Appends a typed instantiation message onto (oStream).
     *
     * @param message an Instantiation message.
     */
    public void encodeCastedNew(Instantiation message) {
        try {
            Reference receiver = (Reference) message.receiver();
            if (receiver.refersToMetaclass()) {
                encodeType(receiver.name());
            }
        } catch (ClassCastException e) {
            // can't determine the class, so skip it
        }
        encodeSimpleNew(message);
    }

    // statements
    /**
     * Appends the code for a reference as a statement onto (oStream).
     *
     * @param encodedName a reference name.
     */
    public void encodeStatement(String encodedName) {
        encodeReference(encodedName);
        oStream.print(SEMICOLON);
    }

    /**
     * Appends the code for an (operand) as a statement onto (oStream).
     *
     * @param operand an Operand.
     */
    public void encodeStatement(Operand operand) {
        encodeOperand(operand);
        oStream.print(SEMICOLON);
    }

    /**
     * Appends the code for an (operand) as a statement onto (oStream).
     *
     * @param operand a message Cascade.
     */
    public void encodeStatement(Cascade operand) {
        encodeOperand(operand);
        oStream.print(SEMICOLON);
    }

    /**
     * Appends the code for a (message) as a statement onto (oStream).
     *
     * @param message an Instantiation message.
     */
    public void encodeStatement(Instantiation message) {
        if (message.selector().isBasicNew()) {
            encodeBasicNew(message);
        } else {
            encodeSimpleNew(message);
        }
        oStream.print(SEMICOLON);
    }

    /**
     * Appends the code for a (message) as a statement onto (oStream).
     *
     * @param message an Assertion message.
     */
    public void encodeStatement(Assertion message) {
        encodeOperand(message);
        oStream.print(SEMICOLON);
    }

    /**
     * Appends the code for a (message) as a statement onto (oStream).
     *
     * @param message an IfTrue message.
     */
    public void encodeStatement(IfTrue message) {
        Operand block = message.firstArgument();
        encodeGuarded(message, true, block, null);
    }

    /**
     * Appends the code for a (message) as a statement onto (oStream).
     *
     * @param message an IfTrueIfFalse message.
     */
    public void encodeStatement(IfTrueIfFalse message) {
        List<Operand> arguments = message.arguments();
        Operand trueBlock = arguments.get(0);
        Operand falseBlock = arguments.get(1);
        encodeGuarded(message, true, trueBlock, falseBlock);
    }

    /**
     * Appends the code for a (message) as a statement onto (oStream).
     *
     * @param message an IfFalse message.
     */
    public void encodeStatement(IfFalse message) {
        Operand block = message.firstArgument();
        encodeGuarded(message, false, block, null);
    }

    /**
     * Appends the code for a (message) as a statement onto (oStream).
     *
     * @param message an IfFalseIfTrue message.
     */
    public void encodeStatement(IfFalseIfTrue message) {
        List<Operand> arguments = message.arguments();
        Operand falseBlock = arguments.get(0);
        Operand trueBlock = arguments.get(1);
        encodeGuarded(message, true, trueBlock, falseBlock);
    }

    /**
     * Appends the code for a (message) as a statement onto (oStream).
     *
     * @param message a WhileTrue message.
     */
    public void encodeStatement(WhileTrue message) {
        encodeLoop(message, true);
    }

    /**
     * Appends the code for a (message) as a statement onto (oStream).
     *
     * @param message a WhileFalse message.
     */
    public void encodeStatement(WhileFalse message) {
        encodeLoop(message, false);
    }

    /**
     * Appends the statements of a (block) onto (oStream).
     *
     * @param block a Block scope.
     */
    public void encodeAllStatements(final Block block) {
        final int count = block.statementCount();
        block.acceptStatementVisitor(
                new Operand.Visitor() {
            int s = 1;

            public void visit(Operand statement) {
                if (s > 1) {
                    encodeNewLine();
                }
                if (s < count || block.isConstructor()) {
                    statement.acceptVisitor(statementEncoder());
                } else {
                    statement.acceptVisitor(resultantEncoder());
                }
                s++;
            }
        }
        );
    }

    /**
     * Appends the statements of a (block) onto (oStream) without a return.
     *
     * @param block a Block scope.
     */
    public void encodeOnlyStatements(final Block block) {
        block.acceptStatementVisitor(
                new Operand.Visitor() {
            int s = 0;

            @Override 
            public void visit(Operand statement) {
                if (s > 0) {
                    encodeNewLine();
                }
                statement.acceptVisitor(statementEncoder());
                s++;
            }
        }
        );
        if (!block.returnsVoid()) {
            encodeNewLine();
            encodeResultNull();
        }
    }

    /**
     * Appends the inner class erasure call for a (block) onto (oStream).
     *
     * @param block a Block scope.
     * @param argumentCount the number of block arguments.
     */
    public void encodeErasedCall(Block block, int argumentCount) {
        if (!block.returnsVoid()) {
            oStream.print(RETURN);
            oStream.print(SPACE);
        }
        oStream.print(DOLLAR);
        oStream.print(Block.signatureValues[argumentCount]);
        oStream.print(NEW_TERM);
        encodeCastedArguments(block);
        oStream.print(END_TERM);
        oStream.print(SEMICOLON);
        if (block.returnsVoid()) {
            encodeNewLine();
            oStream.print(RETURN);
            oStream.print(" null;");
        }
    }

    /**
     * Appends the type erasure for a (block) onto (oStream).
     *
     * @param block a Block scope.
     * @param argumentCount the number of block arguments.
     */
    public void encodeErasure(Block block, int argumentCount) {
        oStream.print("public Object ");
        oStream.print(Block.signatureValues[argumentCount]);
        oStream.print(NEW_TERM);
        encodeErasedArguments(block);
        oStream.print(END_TERM);
        oStream.print(SPACE);
        encodeNewBlock();
        if (block.exceptionCount() == 0) {
            encodeErasedCall(block, argumentCount);
        } else {
            oStream.print("try ");
            encodeNewBlock();
            encodeErasedCall(block, argumentCount);
            encodeEndBlock();
            oStream.print(" catch( Throwable e ) ");
            encodeNewBlock();
            oStream.print("throw new UnhandledJavaException( e );");
            encodeEndBlock();
        }
        encodeEndBlock();
    }

    /**
     * Appends the instantiation for a (block) closure onto (oStream).
     *
     * @param block a Block scope.
     * @param argumentCount the number of block arguments.
     */
    public void encodeNewClosure(int argumentCount) {
        if (argumentCount > 2) {
            oStream.print("new UnsupportedBlock()");
            return;
        }
        oStream.print("new ");
        oStream.print(Block.signatureTypes[argumentCount]);
        oStream.print(NEW_TERM);
        oStream.print(END_TERM);
        oStream.print(SPACE);
    }

    /**
     * Appends the signature for a (block) onto (oStream).
     *
     * @param block a Block scope.
     * @param argumentCount the number of block arguments.
     */
    public void encodeSignature(Block block, int argumentCount) {
        if (block.needsErasure(argumentCount)) {
            encodeErasure(block, argumentCount);
            encodeNewLine();
        }
        oStream.print("public Object ");
        if (block.needsErasure(argumentCount)) {
            oStream.print(DOLLAR);
        }
        oStream.print(Block.signatureValues[argumentCount]);
        encodeFinalArguments(block, argumentCount);
        encodeExceptions(block);
    }

    /**
     * Appends the code for a (block) onto (oStream).
     *
     * @param block a Block scope.
     * @param argumentCount the number of block arguments.
     */
    public void encodeClosure(Block block, int argumentCount) {
        encodeNewClosure(argumentCount);
        encodeNewBlock();
        encodeLocals(block);
        encodeSignature(block, argumentCount);
        encodeNewBlock();
        encodeAllStatements(block);
        encodeEndBlock();
        encodeEndBlock();
    }

    /**
     * Appends the code for a (block) onto (oStream).
     *
     * @param block a Block scope.
     */
    public void encodeClosure(Block block) {
        encodeClosure(block, block.argumentCount());
    }

    /**
     * Appends elementary code for a (block) onto (oStream).
     *
     * @param block a Block scope.
     * @param onlyStatements indicates whether to encode a return result.
     */
    public void encodeElementary(Block block, boolean onlyStatements) {
        encodeNewBlock();
        encodeLocals(block);
        if (onlyStatements) {
            encodeOnlyStatements(block);
        } else {
            encodeAllStatements(block);
        }
        encodeEndBlock();
    }

    /**
     * Appends elementary code for a (block) onto (oStream).
     *
     * @param block a Block scope.
     */
    public void encodeElementary(Block block) {
        encodeElementary(block, false);
    }

    /**
     * Appends the code for a (block) term onto (oStream).
     *
     * @param block a Block scope.
     * @param argumentCount the number of block arguments.
     */
    public void encodeClosureTerm(Block block, int argumentCount) {
        encodeNewTerm();
        encodeClosure(block, argumentCount);
        encodeEndTerm();
    }

    /**
     * Appends the code for a (block) onto (oStream).
     *
     * @param block a Block scope.
     */
    public void encodeScope(Block block) {
        encodeClosure(block);
    }

    /**
     * Appends the comment for a (method) onto (oStream).
     *
     * @param method a Method scope.
     */
    public void encodeComment(Method method) {
        if (method.hasComment()) {
            oStream.print(method.comment());
            encodeNewLine();
        }
    }

    /**
     * Appends the signature of a (method) onto (oStream).
     *
     * @param method a Method scope.
     */
    public void encodeSignature(Method method) {
        encodeComment(method);
        encodeModifiers(method);
        encodeTypeName(method.type());
        oStream.print(SPACE);
        oStream.print(method.name());
        encodeFinalArguments(method, method.argumentCount());
        encodeExceptions(method);
    }

    /**
     * Appends the code for a (method) as a block evaluation onto (oStream).
     *
     * @param method a Method scope.
     */
    public void encodeEvaluation(Method method) {
        if (method.returnsVoid()) {
            encodeClosureTerm(method, 0);
            oStream.print(".value();");
        } else {
            oStream.print(RETURN);
            encodeNewTerm();
            encodeClosureTerm(method, 0);
            oStream.print(".value()");
            encodeEndTerm();
            oStream.print(SEMICOLON);
        }
    }

    /**
     * Appends the code of an exited (method) onto (oStream).
     *
     * @param method a Method scope.
     */
    public void encodeExited(Method method) {
        encodeSignature(method);
        encodeNewBlock();
        encodeNewLine();
        oStream.print("try ");
        encodeNewBlock();
        if (method.hasLocals()) {
            encodeEvaluation(method);
        } else {
            encodeAllStatements(method);
        }
        encodeEndBlock();
        oStream.print(" catch( MethodExit e ) ");
        encodeNewBlock();
        oStream.print("return e.exitOn");
        oStream.print(NEW_TERM);
        oStream.print(method.scopeID());
        oStream.print(END_TERM);
        oStream.print(SEMICOLON);
        encodeEndBlock();
        encodeEndBlock();
    }

    /**
     * Appends the code of a simple (method) onto (oStream).
     *
     * @param method a Method scope.
     */
    public void encodeSimple(Method method) {
        encodeSignature(method);
        encodeNewBlock();
        if (method.hasLocals()) {
            encodeEvaluation(method);
        } else if (method.returnsVoid()) {
            encodeOnlyStatements(method);
        } else {
            encodeAllStatements(method);
        }
        encodeEndBlock();
    }

    /**
     * Appends the code for a (method) onto (oStream).
     *
     * @param method a Method scope.
     */
    public void encodeScope(Method method) {
        if (method.isAbstract()) {
            encodeSignature(method);
            oStream.print(SEMICOLON);
        } else if (method.isPrimitive()) {
            encodePrimitive(method);
        } else if (method.exits()) {
            encodeExited(method);
        } else {
            encodeSimple(method);
        }
    }

    /**
     * Appends the code for the methods of a (face) onto (oStream).
     *
     * @param method a Method scope.
     */
    public void encodeMethods(Face face) {
        face.acceptMethodVisitor(
                new Method.Visitor() {
            int count = 0;

            public void visit(Method method) {
                oStream.println();
                encodeNewLine();
                encodeScope(method);
            }
        }
        );
    }

    /**
     * Appends the signature of a (face) onto (oStream).
     *
     * @param face a Face scope.
     */
    public void encodeSignature(Face face) {
        encodeNewLine();
        oStream.print(face.comment());
        encodeNewLine();
        encodeModifiers(face);
        oStream.print(face.type());
        oStream.print(SPACE);
        oStream.print(face.name());
        String base = face.baseName();
        if (base.length() > 0) {
            oStream.print(" extends " + base);
        }
        encodeInterfaces(face);
    }

    /**
     * Appends the metaface of a (face) onto (oStream).
     *
     * @param face a Face scope.
     */
    public void encodeMetaclass(Face face) {
        if (face.isMetaface()) {
            return;
        }
        if (face.hasMetaface()) {
            encodeScope(face.metaFace());
        }
    }

    /**
     * Appends the standard members of a (face) onto (oStream).
     *
     * @param face a Face scope.
     */
    public void encodeMetaclassInstance(Face face) {
        if (face.isInterface()) {
            if (!face.isMetaface()) {
                encodeTypeMembers(face);
            }
            return;
        }
        if (face.isMetaface()) {
            encodeMetaclassMembers(face);
        } else if (face.hasMetaface()) {
            encodeClassMembers(face);
        }
    }

    /**
     * Appends the interfaces implemented by a (face) onto (oStream).
     *
     * @param face a Face scope.
     */
    public void encodeInterfaces(final Face face) {
        if (face.interfaceCount() == 0) {
            return;
        }
        final boolean typeDefinition = face.isInterface();
        if (!typeDefinition) {
            encodeNewLine();
            oStream.print("implements ");
        }
        face.acceptInterfaceVisitor(
                new Reference.Visitor() {
            int count = 0;

            public void visit(String interfaceName) {
                if (typeDefinition || count > 0) {
                    oStream.print(COMMA);
                }
                if (face.isMetaface()) {
                    interfaceName = face.metaclassName(interfaceName);
                }
                oStream.print(interfaceName);
                count++;
            }
        }
        );
    }

    /**
     * Appends the metaclass members for a (face) onto (oStream).
     *
     * @param face a Face scope.
     */
    public void encodeMetaclassMembers(Face face) {
        encodeNewLine();
        oStream.print("/** Constructs a new " + face.name() + " and ");
        oStream.print("establishes the metaclass for this instance. */");
        encodeNewLine();
        oStream.print("protected ");
        oStream.print(face.name());
        oStream.print("( final java.lang.Class aClass ) ");
        oStream.print(NEW_BLOCK);
        encodeNewLine(1);
        oStream.print("super( aClass );");
        encodeNewLine(-1);
        oStream.print(END_BLOCK);
    }

    /**
     * Appends the class members for a (face) onto (oStream).
     *
     * @param face a Face scope.
     */
    public void encodeClassMembers(Face face) {
        encodeNewLine();
        encodeNewLine();
        oStream.print("/** ");
        oStream.print(face.isInterface() ? "Type" : "Class");
        oStream.print(" instance for " + face.name() + ". */");
        encodeNewLine();
        oStream.print("public static final ");
        oStream.print(face.metaFace().name());
        oStream.print(" $class = new ");
        oStream.print(face.metaFace().name());
        oStream.print("( ");
        oStream.print(face.name());
        oStream.print(".class );");
        if (!face.name().equals("Class")) {
            encodeNewLine();
            encodeNewLine();
            oStream.print("/** Returns the class of the receiver. */");
            encodeNewLine();
            oStream.print("public Behavior $class( ) ");
            oStream.print(NEW_BLOCK);
            encodeNewLine(1);
            oStream.print("return $class;");
            encodeNewLine(-1);
            oStream.print(END_BLOCK);
        }
    }

    /**
     * Appends the members for a type (face) onto (oStream).
     *
     * @param face a Face scope.
     */
    public void encodeTypeMembers(Face face) {
        encodeNewLine();
        encodeNewLine();
        oStream.print("/** Indicates a Bistro type definition. */");
        encodeNewLine();
        oStream.print("public static final boolean $class = true;");
    }

    /**
     * Appends the definition of a (face) onto (oStream).
     *
     * @param face a Face scope.
     */
    public void encodeScope(Face face) {
        encodeSignature(face);
        encodeNewBlock();
        encodeMetaclass(face);
        encodeMetaclassInstance(face);
        if (face.hasLocals()) {
            oStream.println();
            encodeNewLine();
            encodeLocals(face);
        }
        encodeMethods(face);
        encodeEndBlock();
    }

    /**
     * Appends the package for a (faceFile) onto (oStream).
     *
     * @param faceFile a File scope.
     */
    public void encodePackage(File faceFile) {
        String name = faceFile.packageName();
        if (name.length() == 0) {
            return;
        }
        oStream.print("package ");
        oStream.print(name);
        oStream.println(SEMICOLON);
    }

    /**
     * Appends the imports for a (faceFile) onto (oStream).
     *
     * @param faceFile a File scope.
     */
    public void encodeImports(File faceFile) {
        faceFile.acceptImportVisitor(
                new Reference.Visitor() {
            public void visit(String importName) {
                encodeNewLine();
                oStream.print("import ");
                oStream.print(importName);
                oStream.print(SEMICOLON);
            }
        }
        );
        encodeNewLine();
    }

    /**
     * Appends the definition of a (face) onto (oStream).
     *
     * @param faceFile a File.
     */
    public void encodeScope(File faceFile) {
        encodePackage(faceFile);
        encodeImports(faceFile);
        encodeScope(faceFile.faceScope());
    }
}
