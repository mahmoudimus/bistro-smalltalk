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
        ObjectArray.Visitor, Operand.Visitor,
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
    public void visit(Selector selector) {
        encodeSelector(selector);
    }

    /**
     * Visits a reference (encodedName).
     */
    public void visit(String encodedName) {
        encodeReference(encodedName);
    }

    /**
     * Visits an (operand).
     */
    public void visit(Operand operand) {
        encodeOperand(operand);
    }

    /**
     * Visits an (operand).
     */
    public void visit(Constant operand) {
        encodeOperand(operand);
    }

    /**
     * Visits an (operand).
     */
    public void visit(Expression operand) {
        encodeOperand(operand);
    }

    // constants
    /**
     * Visits an (operand).
     */
    public void visit(Scalar operand) {
        encodeOperand(operand);
    }

    /**
     * Visits an (operand).
     */
    public void visit(LiteralNil operand) {
        encodeOperand(operand);
    }

    /**
     * Visits an (operand).
     */
    public void visit(LiteralBoolean operand) {
        encodeOperand(operand);
    }

    /**
     * Visits an (operand).
     */
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
    public void visit(LiteralInteger operand) {
        encodeOperand(operand);
    }

    /**
     * Visits an (operand).
     */
    public void visit(LiteralFloat operand) {
        encodeOperand(operand);
    }

    /**
     * Visits an (operand).
     */
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
    public void visit(LiteralString operand) {
        encodeOperand(operand);
    }

    /**
     * Visits an (operand).
     */
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
    public void visit(ObjectArray operand) {
        encodeOperand(operand);
    }

    // expressions
    /**
     * Visits an (operand).
     */
    public void visit(Cascade operand) {
        encodeOperand(operand);
    }

    /**
     * Visits a (message).
     */
    public void visit(Message message) {
        encodeOperand(message);
    }

    /**
     * Visits a (message).
     */
    public void visit(And message) {
        encodeOperand(message);
    }

    /**
     * Visits a (message).
     */
    public void visit(Or message) {
        encodeOperand(message);
    }

    /**
     * Visits a (message).
     */
    public void visit(Assignment message) {
        encodeOperand(message);
    }

    /**
     * Visits a (message).
     */
    public void visit(Assertion message) {
        encodeOperand(message);
    }

    /**
     * Visits a (message).
     */
    public void visit(Cast message) {
        encodeOperand(message);
    }

    /**
     * Visits a (message).
     */
    public void visit(Exit message) {
        encodeOperand(message);
    }

    /**
     * Visits a (message).
     */
    public void visit(IfTrue message) {
        encodeOperand(message);
    }

    /**
     * Visits a (message).
     */
    public void visit(IfTrueIfFalse message) {
        encodeOperand(message);
    }

    /**
     * Visits a (message).
     */
    public void visit(IfFalse message) {
        encodeOperand(message);
    }

    /**
     * Visits a (message).
     */
    public void visit(IfFalseIfTrue message) {
        encodeOperand(message);
    }

    /**
     * Visits a (message).
     */
    public void visit(Instantiation message) {
        encodeOperand(message);
    }

    /**
     * Visits a (message).
     */
    public void visit(TryCatch message) {
        encodeOperand(message);
    }

    /**
     * Visits a (message).
     */
    public void visit(WhileTrue message) {
        encodeOptimized(message);
    }

    /**
     * Visits a (message).
     */
    public void visit(WhileFalse message) {
        encodeOptimized(message);
    }

    /**
     * Visits a (message).
     */
    public void visit(PrimitiveLiteral message) {
        encodeOperand(message);
    }

    // scopes
    /**
     * Visits an (operand).
     */
    public void visit(Nest operand) {
        encodeOperand(operand);
    }

    /**
     * Visits an (operand).
     */
    public void visit(Innard operand) {
        encodeOperand(operand);
    }

    /**
     * Visits a (scope).
     */
    public void visit(File scope) {
        encodeScope(scope);
    }

    /**
     * Visits a (scope).
     */
    public void visit(Face scope) {
        encodeScope(scope);
    }

    /**
     * Visits a (scope).
     */
    public void visit(Block scope) {
        encodeScope(scope);
    }

    /**
     * Visits a (scope).
     */
    public void visit(Method scope) {
        encodeScope(scope);
    }

    /**
     * Encodes primitive operands and messages.
     */
    protected class PrimitiveEncoder implements
            Reference.Visitor, Operand.Visitor, Message.Visitor,
            Assignment.Visitor, Instantiation.Visitor, PrimitiveLiteral.Visitor,
            LiteralNil.Visitor, LiteralBoolean.Visitor, LiteralCharacter.Visitor,
            LiteralInteger.Visitor, LiteralFloat.Visitor,
            LiteralString.Visitor, LiteralSymbol.Visitor {

        public void visit(String encodedName) {
            encodeReference(encodedName);
        }

        public void visit(Operand operand) {
            encodePrimitive(operand);
        }

        public void visit(LiteralNil operand) {
            encodePrimitive(operand);
        }

        public void visit(LiteralBoolean operand) {
            encodePrimitive(operand);
        }

        public void visit(LiteralCharacter operand) {
            encodePrimitive(operand);
        }

        public void visit(LiteralInteger operand) {
            encodePrimitive(operand);
        }

        public void visit(LiteralFloat operand) {
            encodePrimitive(operand);
        }

        public void visit(LiteralString operand) {
            encodePrimitive(operand);
        }

        public void visit(LiteralSymbol operand) {
            encodePrimitive(operand);
        }

        public void visit(Message message) {
            encodeOperand(message);
        }

        public void visit(Assignment message) {
            encodePrimitive(message);
        }

        public void visit(Instantiation message) {
            encodePrimitive(message);
        }

        public void visit(PrimitiveLiteral message) {
            encodeOperand(message);
        }
    }

    /**
     * Encodes optimized operands and messages.
     */
    protected class OptimizedEncoder implements
            Reference.Visitor, Operand.Visitor, Message.Visitor,
            Assignment.Visitor, Assertion.Visitor,
            Exit.Visitor, Instantiation.Visitor,
            IfTrue.Visitor, IfTrueIfFalse.Visitor,
            IfFalse.Visitor, IfFalseIfTrue.Visitor,
            WhileTrue.Visitor, WhileFalse.Visitor,
            Nest.Visitor, Block.Visitor {

        public void visit(String operand) {
            encodeReference(operand);
        }

        public void visit(Operand operand) {
            encodeOptimized(operand);
        }

        public void visit(Nest operand) {
            encodeOptimized(operand.nestedBlock());
        }

        public void visit(Message message) {
            encodeOptimized(message);
        }

        public void visit(Exit message) {
            encodeOptimized(message);
        }

        public void visit(Assignment message) {
            encodeOptimized(message);
        }

        public void visit(Assertion message) {
            encodeOptimized(message);
        }

        public void visit(Instantiation message) {
            encodeOptimized(message);
        }

        public void visit(IfTrue message) {
            encodeOperand(message);
        }

        public void visit(IfTrueIfFalse message) {
            encodeOperand(message);
        }

        public void visit(IfFalse message) {
            encodeOperand(message);
        }

        public void visit(IfFalseIfTrue message) {
            encodeOperand(message);
        }

        public void visit(WhileTrue message) {
            encodeOptimized(message);
        }

        public void visit(WhileFalse message) {
            encodeOptimized(message);
        }

        public void visit(Block block) {
            encodeOptimized(block);
        }
    }

    /**
     * Encodes messages as statements.
     */
    protected class StatementEncoder implements
            Reference.Visitor, Operand.Visitor,
            Cascade.Visitor, Message.Visitor,
            Exit.Visitor, Instantiation.Visitor,
            Assertion.Visitor, Assignment.Visitor,
            IfTrue.Visitor, IfTrueIfFalse.Visitor,
            IfFalse.Visitor, IfFalseIfTrue.Visitor,
            WhileTrue.Visitor, WhileFalse.Visitor, TryCatch.Visitor {

        public void visit(String encodedName) {
            encodeStatement(encodedName);
        }

        public void visit(Operand operand) {
            encodeStatement(operand);
        }

        public void visit(Cascade operand) {
            encodeStatement(operand);
        }

        public void visit(Message message) {
            encodeStatement(message);
        }

        public void visit(Exit message) {
            encodeResult(message);
        }

        public void visit(Assignment message) {
            encodeStatement(message);
        }

        public void visit(Assertion message) {
            encodeStatement(message);
        }

        public void visit(Instantiation message) {
            encodeStatement(message);
        }

        public void visit(IfTrue message) {
            encodeStatement(message);
        }

        public void visit(IfTrueIfFalse message) {
            encodeStatement(message);
        }

        public void visit(IfFalse message) {
            encodeStatement(message);
        }

        public void visit(IfFalseIfTrue message) {
            encodeStatement(message);
        }

        public void visit(WhileTrue message) {
            encodeStatement(message);
        }

        public void visit(WhileFalse message) {
            encodeStatement(message);
        }

        public void visit(TryCatch message) {
            encodeStatement(message);
        }
    }

    /**
     * Encodes resultant operands and messages.
     */
    protected class ResultantEncoder implements
            Operand.Visitor, Message.Visitor, And.Visitor, Or.Visitor,
            Assignment.Visitor, Assertion.Visitor,
            Exit.Visitor, Instantiation.Visitor,
            IfTrue.Visitor, IfTrueIfFalse.Visitor,
            IfFalse.Visitor, IfFalseIfTrue.Visitor,
            WhileTrue.Visitor, WhileFalse.Visitor, TryCatch.Visitor {

        public void visit(Operand operand) {
            encodeOperand(operand);
        }

        public void visit(Message message) {
            encodeResult(message);
        }

        public void visit(And message) {
            encodeResult(message);
        }

        public void visit(Or message) {
            encodeResult(message);
        }

        public void visit(Exit message) {
            encodeResult(message);
        }

        public void visit(Assignment message) {
            encodeResult(message);
        }

        public void visit(Assertion message) {
            encodeResult(message);
        }

        public void visit(Instantiation message) {
            encodeResult(message);
        }

        public void visit(IfTrue message) {
            encodeOptimizedResult(message);
        }

        public void visit(IfTrueIfFalse message) {
            encodeOptimizedResult(message);
        }

        public void visit(IfFalse message) {
            encodeOptimizedResult(message);
        }

        public void visit(IfFalseIfTrue message) {
            encodeOptimizedResult(message);
        }

        public void visit(WhileTrue message) {
            encodeOptimizedResult(message);
        }

        public void visit(WhileFalse message) {
            encodeOptimizedResult(message);
        }

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
    public Operand.Visitor primitiveEncoder() {
        return primitiveEncoder;
    }

    /**
     * Returns the encoder for optimizations.
     */
    public Operand.Visitor optimizedEncoder() {
        return optimizedEncoder;
    }

    /**
     * Returns the encoder for statements.
     */
    public Operand.Visitor statementEncoder() {
        return statementEncoder;
    }

    /**
     * Returns the encoder for results.
     */
    public Operand.Visitor resultantEncoder() {
        return resultantEncoder;
    }

    /**
     * Returns the encoder for operands.
     */
    public Operand.Visitor operandEncoder() {
        return BistroJavaGenerator.this;
    }
}
