//====================================================================
// BistroJavaGenerator.java
//====================================================================
package smalltalk.compiler;

import java.io.*;
import smalltalk.compiler.element.*;
import smalltalk.compiler.constant.*;
import smalltalk.compiler.expression.*;
import smalltalk.compiler.scope.*;
import smalltalk.compiler.scope.File;
import smalltalk.compiler.scope.Package;

/**
 * Generates Java code from a parsed Bistro face definition.
 * Implements the Visitor pattern over a parsed Composite pattern.
 * Invokes methods in the superclass to generate Java code.
 */
public class BistroJavaGenerator extends BistroJavaEncoder implements
        Reference.Visitor, Selector.Visitor, Constant.Visitor, Scalar.Visitor,
        LiteralString.Visitor, LiteralSymbol.Visitor, LiteralInteger.Visitor,
        LiteralFloat.Visitor, LiteralDecimal.Visitor, LiteralBoolean.Visitor,
        LiteralCharacter.Visitor, LiteralNil.Visitor,
        ObjectArray.Visitor, Operand.Visitor, Operand.Emitter,
        Expression.Visitor, Message.Visitor, Cascade.Visitor,
        And.Visitor, Or.Visitor, Assignment.Visitor, Assertion.Visitor,
        Cast.Visitor, PrimitiveLiteral.Visitor,
        Exit.Visitor, Instantiation.Visitor, TryCatch.Visitor,
        IfTrue.Visitor, IfTrueIfFalse.Visitor,
        IfFalse.Visitor, IfFalseIfTrue.Visitor,
        WhileTrue.Visitor, WhileFalse.Visitor,
        File.Visitor, Face.Visitor, Method.Visitor,
        Block.Visitor, Nest.Visitor, Innard.Visitor {

    // elements

    /**
     * Visits a (selector).
     */
    @Override
    public void visit(Selector selector) {
        encodeSelector(selector);
    }

    /**
     * Visits a reference (encodedName).
     */
    @Override
    public void visit(String encodedName) {
        encodeReference(encodedName);
    }

    @Override
    public Emission visitResult(Operand operand) {
        return emitOperand(operand);
    }

    /**
     * Visits an (operand).
     */
    @Override
    public void visit(Operand operand) {
        encodeOperand(operand);
    }

    /**
     * Visits an (operand).
     */
    @Override
    public void visit(Constant operand) {
        encodeOperand(operand);
    }

    /**
     * Visits an (operand).
     */
    @Override
    public void visit(Expression operand) {
        encodeOperand(operand);
    }

    // constants
    /**
     * Visits an (operand).
     */
    @Override
    public void visit(Scalar operand) {
        encodeOperand(operand);
    }

    /**
     * Visits an (operand).
     */
    @Override
    public void visit(LiteralNil operand) {
        encodeOperand(operand);
    }

    /**
     * Visits an (operand).
     */
    @Override
    public void visit(LiteralBoolean operand) {
        encodeOperand(operand);
    }

    /**
     * Visits an (operand).
     */
    @Override
    public void visit(LiteralCharacter operand) {
        encodeOperand(operand);
    }

    /**
     * Visits an (operand).
     */
    public void visit(LiteralNumber operand) {
        encodeOperand(operand);
    }

    /**
     * Visits an (operand).
     */
    @Override
    public void visit(LiteralInteger operand) {
        encodeOperand(operand);
    }

    /**
     * Visits an (operand).
     */
    @Override
    public void visit(LiteralFloat operand) {
        encodeOperand(operand);
    }

    /**
     * Visits an (operand).
     */
    @Override
    public void visit(LiteralDecimal operand) {
        encodeOperand(operand);
    }

    /**
     * Visits an (operand).
     */
    public void visit(LiteralCollection operand) {
        encodeOperand(operand);
    }

    /**
     * Visits an (operand).
     */
    @Override
    public void visit(LiteralString operand) {
        encodeOperand(operand);
    }

    /**
     * Visits an (operand).
     */
    @Override
    public void visit(LiteralSymbol operand) {
        encodeOperand(operand);
    }

    /**
     * Visits an (operand).
     */
    public void visit(LiteralArray operand) {
        encodeOperand(operand);
    }

    /**
     * Visits an (operand).
     */
    @Override
    public void visit(ObjectArray operand) {
        encodeOperand(operand);
    }

    // expressions
    /**
     * Visits an (operand).
     */
    @Override
    public void visit(Cascade operand) {
        encodeOperand(operand);
    }

    /**
     * Visits a (message).
     */
    @Override
    public void visit(Message message) {
        encodeOperand(message);
    }

    /**
     * Visits a (message).
     */
    @Override
    public void visit(And message) {
        encodeOperand(message);
    }

    /**
     * Visits a (message).
     */
    @Override
    public void visit(Or message) {
        encodeOperand(message);
    }

    /**
     * Visits a (message).
     */
    @Override
    public void visit(Assignment message) {
        encodeOperand(message);
    }

    /**
     * Visits a (message).
     */
    @Override
    public void visit(Assertion message) {
        encodeOperand(message);
    }

    /**
     * Visits a (message).
     */
    @Override
    public void visit(Cast message) {
        encodeOperand(message);
    }

    /**
     * Visits a (message).
     */
    @Override
    public void visit(Exit message) {
        encodeOperand(message);
    }

    /**
     * Visits a (message).
     */
    @Override
    public void visit(IfTrue message) {
        encodeOperand(message);
    }

    /**
     * Visits a (message).
     */
    @Override
    public void visit(IfTrueIfFalse message) {
        encodeOperand(message);
    }

    /**
     * Visits a (message).
     */
    @Override
    public void visit(IfFalse message) {
        encodeOperand(message);
    }

    /**
     * Visits a (message).
     */
    @Override
    public void visit(IfFalseIfTrue message) {
        encodeOperand(message);
    }

    /**
     * Visits a (message).
     */
    @Override
    public void visit(Instantiation message) {
        encodeOperand(message);
    }

    /**
     * Visits a (message).
     */
    @Override
    public void visit(TryCatch message) {
        encodeOperand(message);
    }

    /**
     * Visits a (message).
     */
    @Override
    public void visit(WhileTrue message) {
        encodeOptimized(message);
    }

    /**
     * Visits a (message).
     */
    @Override
    public void visit(WhileFalse message) {
        encodeOptimized(message);
    }

    /**
     * Visits a (message).
     */
    @Override
    public void visit(PrimitiveLiteral message) {
        encodeOperand(message);
    }

    // scopes
    /**
     * Visits an (operand).
     */
    @Override
    public void visit(Nest operand) {
        encodeOperand(operand);
    }

    /**
     * Visits an (operand).
     */
    @Override
    public void visit(Innard operand) {
        encodeOperand(operand);
    }

    /**
     * Visits a (scope).
     */
    @Override
    public void visit(File scope) {
        encodeScope(scope);
    }

    /**
     * Visits a (scope).
     */
    @Override
    public void visit(Face scope) {
        encodeScope(scope);
    }

    /**
     * Visits a (scope).
     */
    @Override
    public void visit(Block scope) {
        encodeScope(scope);
    }

    /**
     * Visits a (scope).
     */
    @Override
    public void visit(Method scope) {
        encodeScope(scope);
    }

    /**
     * Encodes primitive operands and messages.
     */
    protected class PrimitiveEncoder implements
            Operand.Visitor, Operand.Emitter,
            Reference.Visitor, Message.Visitor,
            Assignment.Visitor, Instantiation.Visitor, PrimitiveLiteral.Visitor,
            LiteralNil.Visitor, LiteralBoolean.Visitor, LiteralCharacter.Visitor,
            LiteralInteger.Visitor, LiteralFloat.Visitor,
            LiteralString.Visitor, LiteralSymbol.Visitor {

        @Override
        public Emission visitResult(Operand operand) {
            return emitOperand(operand);
        }

        @Override
        public void visit(Operand operand) {
            encodePrimitive(operand);
        }

        @Override
        public void visit(String encodedName) {
            encodeReference(encodedName);
        }

        @Override
        public void visit(LiteralNil operand) {
            encodePrimitive(operand);
        }

        @Override
        public void visit(LiteralBoolean operand) {
            encodePrimitive(operand);
        }

        @Override
        public void visit(LiteralCharacter operand) {
            encodePrimitive(operand);
        }

        @Override
        public void visit(LiteralInteger operand) {
            encodePrimitive(operand);
        }

        @Override
        public void visit(LiteralFloat operand) {
            encodePrimitive(operand);
        }

        @Override
        public void visit(LiteralString operand) {
            encodePrimitive(operand);
        }

        @Override
        public void visit(LiteralSymbol operand) {
            encodePrimitive(operand);
        }

        @Override
        public void visit(Message message) {
            encodeOperand(message);
        }

        @Override
        public void visit(Assignment message) {
            encodePrimitive(message);
        }

        @Override
        public void visit(Instantiation message) {
            encodePrimitive(message);
        }

        @Override
        public void visit(PrimitiveLiteral message) {
            encodeOperand(message);
        }
    }

    /**
     * Encodes optimized operands and messages.
     */
    protected class OptimizedEncoder implements
            Operand.Visitor, Operand.Emitter,
            Message.Visitor, Reference.Visitor,
            Assignment.Visitor, Assertion.Visitor,
            Exit.Visitor, Instantiation.Visitor,
            IfTrue.Visitor, IfTrueIfFalse.Visitor,
            IfFalse.Visitor, IfFalseIfTrue.Visitor,
            WhileTrue.Visitor, WhileFalse.Visitor,
            Nest.Visitor, Block.Visitor {

        @Override
        public Emission visitResult(Operand operand) {
            return emitOperand(operand);
        }

        @Override
        public void visit(Operand operand) {
            encodeOptimized(operand);
        }

        @Override
        public void visit(String operand) {
            encodeReference(operand);
        }

        @Override
        public void visit(Nest operand) {
            encodeOptimized(operand.nestedBlock());
        }

        @Override
        public void visit(Message message) {
            encodeOptimized(message);
        }

        @Override
        public void visit(Exit message) {
            encodeOptimized(message);
        }

        @Override
        public void visit(Assignment message) {
            encodeOptimized(message);
        }

        @Override
        public void visit(Assertion message) {
            encodeOptimized(message);
        }

        @Override
        public void visit(Instantiation message) {
            encodeOptimized(message);
        }

        @Override
        public void visit(IfTrue message) {
            encodeOperand(message);
        }

        @Override
        public void visit(IfTrueIfFalse message) {
            encodeOperand(message);
        }

        @Override
        public void visit(IfFalse message) {
            encodeOperand(message);
        }

        @Override
        public void visit(IfFalseIfTrue message) {
            encodeOperand(message);
        }

        @Override
        public void visit(WhileTrue message) {
            encodeOptimized(message);
        }

        @Override
        public void visit(WhileFalse message) {
            encodeOptimized(message);
        }

        @Override
        public void visit(Block block) {
            encodeOptimized(block);
        }
    }

    /**
     * Encodes messages as statements.
     */
    protected class StatementEncoder implements
            Operand.Visitor, Operand.Emitter,
            Cascade.Visitor, Message.Visitor,
            Exit.Visitor, Instantiation.Visitor,
            Assertion.Visitor, Assignment.Visitor,
            IfTrue.Visitor, IfTrueIfFalse.Visitor,
            IfFalse.Visitor, IfFalseIfTrue.Visitor,
            WhileTrue.Visitor, WhileFalse.Visitor,
            Reference.Visitor, TryCatch.Visitor {

        @Override
        public Emission visitResult(Operand operand) {
            return emitOperand(operand);
        }

        @Override
        public void visit(Operand operand) {
            encodeStatement(operand);
        }

        @Override
        public void visit(String encodedName) {
            encodeStatement(encodedName);
        }

        @Override
        public void visit(Cascade operand) {
            encodeStatement(operand);
        }

        @Override
        public void visit(Message message) {
            encodeStatement(message);
        }

        @Override
        public void visit(Exit message) {
            encodeResult(message);
        }

        @Override
        public void visit(Assignment message) {
            encodeStatement(message);
        }

        @Override
        public void visit(Assertion message) {
            encodeStatement(message);
        }

        @Override
        public void visit(Instantiation message) {
            encodeStatement(message);
        }

        @Override
        public void visit(IfTrue message) {
            encodeStatement(message);
        }

        @Override
        public void visit(IfTrueIfFalse message) {
            encodeStatement(message);
        }

        @Override
        public void visit(IfFalse message) {
            encodeStatement(message);
        }

        @Override
        public void visit(IfFalseIfTrue message) {
            encodeStatement(message);
        }

        @Override
        public void visit(WhileTrue message) {
            encodeStatement(message);
        }

        @Override
        public void visit(WhileFalse message) {
            encodeStatement(message);
        }

        @Override
        public void visit(TryCatch message) {
            encodeStatement(message);
        }
    }

    /**
     * Encodes resultant operands and messages.
     */
    protected class ResultantEncoder implements
            Operand.Visitor, Operand.Emitter,
            Message.Visitor, And.Visitor, Or.Visitor,
            Assignment.Visitor, Assertion.Visitor,
            Exit.Visitor, Instantiation.Visitor,
            IfTrue.Visitor, IfTrueIfFalse.Visitor,
            IfFalse.Visitor, IfFalseIfTrue.Visitor,
            WhileTrue.Visitor, WhileFalse.Visitor,
            TryCatch.Visitor {

        @Override
        public Emission visitResult(Operand operand) {
            return emitOperand(operand);
        }

        @Override
        public void visit(Operand operand) {
            encodeOperand(operand);
        }

        @Override
        public void visit(Message message) {
            encodeResult(message);
        }

        @Override
        public void visit(And message) {
            encodeResult(message);
        }

        @Override
        public void visit(Or message) {
            encodeResult(message);
        }

        @Override
        public void visit(Exit message) {
            encodeResult(message);
        }

        @Override
        public void visit(Assignment message) {
            encodeResult(message);
        }

        @Override
        public void visit(Assertion message) {
            encodeResult(message);
        }

        @Override
        public void visit(Instantiation message) {
            encodeResult(message);
        }

        @Override
        public void visit(IfTrue message) {
            encodeOptimizedResult(message);
        }

        @Override
        public void visit(IfTrueIfFalse message) {
            encodeOptimizedResult(message);
        }

        @Override
        public void visit(IfFalse message) {
            encodeOptimizedResult(message);
        }

        @Override
        public void visit(IfFalseIfTrue message) {
            encodeOptimizedResult(message);
        }

        @Override
        public void visit(WhileTrue message) {
            encodeOptimizedResult(message);
        }

        @Override
        public void visit(WhileFalse message) {
            encodeOptimizedResult(message);
        }

        @Override
        public void visit(TryCatch message) {
            encodeResult(message);
        }
    }

    /**
     * Encodes primitives.
     */
    protected PrimitiveEncoder primitiveEncoder;

    /**
     * Encodes optimizations.
     */
    protected OptimizedEncoder optimizedEncoder;

    /**
     * Encodes statements.
     */
    protected StatementEncoder statementEncoder;

    /**
     * Encodes results.
     */
    protected ResultantEncoder resultantEncoder;

    /**
     * Constructs a new BistroJavaGenerator.
     */
    public BistroJavaGenerator(PrintWriter aStream) {
        super(aStream);
        primitiveEncoder = new PrimitiveEncoder();
        optimizedEncoder = new OptimizedEncoder();
        statementEncoder = new StatementEncoder();
        resultantEncoder = new ResultantEncoder();
    }

    /**
     * Returns the encoder for primitives.
     */
    @Override
    public Operand.Visitor primitiveEncoder() {
        return primitiveEncoder;
    }

    /**
     * Returns the encoder for optimizations.
     */
    @Override
    public Operand.Visitor optimizedEncoder() {
        return optimizedEncoder;
    }

    /**
     * Returns the encoder for statements.
     */
    @Override
    public Operand.Visitor statementEncoder() {
        return statementEncoder;
    }

    @Override
    public Operand.Emitter statementEmitter() {
        return statementEncoder;
    }

    /**
     * Returns the encoder for results.
     */
    @Override
    public Operand.Visitor resultantEncoder() {
        return resultantEncoder;
    }

    @Override
    public Operand.Emitter resultantEmitter() {
        return resultantEncoder;
    }

    /**
     * Returns the encoder for operands.
     */
    @Override
    public Operand.Visitor operandEncoder() {
        return BistroJavaGenerator.this;
    }

    @Override
    public Operand.Emitter operandEmitter() {
        return BistroJavaGenerator.this;
    }
}
