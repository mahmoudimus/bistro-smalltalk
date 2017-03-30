//====================================================================
// Block.java
//====================================================================
package smalltalk.compiler.scope;

import java.util.*;
import smalltalk.compiler.Emission;

import smalltalk.compiler.element.Selector;
import smalltalk.compiler.element.Reference;
import smalltalk.compiler.element.Operand;

/**
 * Represents and encodes a block as a sequence of statements.
 *
 * @author Copyright 1999,2016 Nikolas S. Boyd. All rights reserved.
 */
public class Block extends Code {

    static {
        Reference.BlockClass = Block.class;
    }

    /**
     * Defines an interface for visiting instances.
     */
    public static interface Visitor {

        public void visit(Block scope);
    }

    /**
     * Identifies the classes used to implement blocks.
     */
    public static final String signatureTypes[] = {
        "ZeroArgumentBlock",
        "OneArgumentBlock",
        "TwoArgumentBlock"
    };

    /**
     * Identifies the evaluation protocols used by blocks.
     */
    public static final String signatureValues[] = {
        "value",
        "value",
        "value_value"
    };

    /**
     * Contains the block or method name.
     */
    Selector selector;

    /**
     * Maintains the block arguments.
     */
    Table arguments;

    /**
     * Contains the names of the exception classes thrown by this method.
     */
    List<String> exceptions = new ArrayList();

    /**
     * Contains the block statements.
     */
    List<Operand> statements = new ArrayList();

    /**
     * Indicates whether the method contains an exit expression.
     */
    boolean containsExit;

    /**
     * Constructs a new Block.
     * @param aScope a block container
     */
    public Block(Scope aScope) {
        super(aScope);
        selector = new Selector();
        arguments = new Table(this);
        containsExit = false;
    }

    /**
     * Returns the ordered list of arguments.
     *
     * @return the ordered list of arguments.
     */
    public List<Variable> arguments() {
        return arguments.symbols();
    }

    /**
     * Returns whether the operand needs to be a term when used as a message receiver.
     *
     * @return whether the operand needs to be a term when used as a message receiver.
     */
    public boolean receiverNeedsTerm() {
        return true;
    }

    /**
     * Removes all the statements from the block.
     */
    public void clear() {
        statements.clear();
    }

    /**
     * Cleans out any residue left from the parsing process and prepares the receiver for code generation.
     */
    @Override
    public void clean() {
        super.clean();
        arguments.clean();
        if (statements.isEmpty()) {
            return;
        }

        Operand result = statements.get(statements.size() - 1);
        statements.remove(result);
        for (Operand s : statements) {
            s.clean();
        }
        // results get a special cleaning - see Cascade
        statements.add(result.cleanTerm());
    }

    /**
     * Returns the name of a newly created local variable.
     *
     * @return the name of a newly created local variable.
     */
    public String createLocal() {
        return locals.createSymbol();
    }

    /**
     * Returns the selector of this scope.
     *
     * @return the selector of this scope.
     */
    public Selector selector() {
        return selector;
    }

    /**
     * Returns the name of this scope.
     *
     * @return the name of this scope.
     */
    public String name() {
        return selector.methodName();
    }

    /**
     * Establishes the name of this code scope.
     *
     * @param aName the name of this code scope.
     */
    @Override
    public void name(String aName) {
        selector.append(aName);
    }

    /**
     * Returns the number of arguments defined in this block.
     *
     * @return the number of arguments defined in this block.
     */
    public int argumentCount() {
        return arguments.size();
    }

    /**
     * Adds a new (undefined) argument to this block.
     */
    public void addArgument() {
        arguments.prepareSymbol();
        arguments.currentSymbol().container(this);
    }

    /**
     * Returns the most recently defined argument.
     *
     * @return the most recently defined argument.
     */
    public Variable currentArgument() {
        return arguments.currentSymbol();
    }

    public boolean hasArgument(String symbol) {
        return arguments.containsSymbol(symbol);
    }

    /**
     * Adds (exception) to those thrown by this method.
     *
     * @param exception name of a thrown exception.
     */
    public void throwsException(String exception) {
        exceptions.add(exception);
    }

    public int exceptionCount() {
        return exceptions.size();
    }

    /**
     * Returns the number of statements contained in the block.
     *
     * @return the number of statements contained in the block.
     */
    public int statementCount() {
        return statements.size();
    }

    /**
     * Adds a (statement) to those contained in the block.
     *
     * @param statement a representation of a Bistro expression.
     */
    public void addStatement(Operand statement) {
        if (statement != null) {
            statements.add(statement);
        }
    }

    /**
     * Adds a (statement) to those contained in the block.
     *
     * @param statement a representation of a Bistro expression.
     */
    public void addStatement(Object statement) {
        addStatement((Operand) statement);
    }

    /**
     * Returns whether this container has primitive available.
     *
     * @return whether this container has primitive available.
     */
    @Override
    public boolean hasPrimitiveFactory() {
        return true;
    }

    /**
     * Returns whether the method is a constructor.
     *
     * @return whether the method is a constructor.
     */
    public boolean isConstructor() {
        return false;
    }

    /**
     * Returns whether this block returns no result.
     *
     * @return whether this block returns no result.
     */
    public boolean returnsVoid() {
        return type().equals("void");
    }

    /**
     * Returns whether this block contains an exit expression.
     *
     * @return whether this block contains an exit expression.
     */
    public boolean exits() {
        return containsExit;
    }

    /**
     * Establishes whether this block contains an exit expression.
     *
     * @param aBoolean indicates whether this block contains an exit expression.
     */
    public void exits(boolean aBoolean) {
        containsExit(aBoolean);
    }

    /**
     * Establishes whether this block contains an exit expression.
     *
     * @param aBoolean indicates whether this block contains an exit expression.
     */
    protected void containsExit(boolean aBoolean) {
        containsExit = aBoolean;
        currentMethod().containsExit(aBoolean);
    }

    /**
     * Returns a revised identifier derived from the supplied (identifier).
     *
     * @param identifier identifies a named entity.
     * @return a revised identifier derived from the supplied (identifier).
     */
    @Override
    public String revised(String identifier) {
        return currentFace().revised(identifier);
    }

    /**
     * Returns whether the container can resolve a symbolic (reference).
     *
     * @param reference a symbolic reference to be resolved.
     * @return whether the container can resolve a symbolic (reference).
     */
    @Override
    public boolean resolves(Reference reference) {
        String symbol = reference.name();
        if (reference.isSelfish()) return true;
        if (this.hasLocal(symbol)) return true;
        if (this.hasArgument(symbol)) return true;

        return super.resolves(reference);
    }

    /**
     * Returns the type of the variable to which a (reference) resolves.
     *
     * @param reference refers to a variable in some compiler scope.
     * @return the type of the variable to which a (reference) resolves.
     */
    @Override
    public Class resolveType(Reference reference) {
        String symbol = reference.name();
        if (reference.isSelfish()) {
            return currentFace().typeClass();
        }

        if (this.hasLocal(symbol)) {
            return locals.symbolNamed(symbol).resolvedType();
        }

        if (this.hasArgument(symbol)) {
            return arguments.symbolNamed(symbol).resolvedType();
        }
        return super.resolveType(reference);
    }

    /**
     * Returns the type name of the variable to which a (reference) resolves.
     *
     * @param reference refers to a variable in some compiler scope.
     * @return the type name of the variable to which a (reference) resolves.
     */
    @Override
    public String resolveTypeName(Reference reference) {
        String symbol = reference.name();
        if (reference.isSelfish()) {
            return currentFace().name();
        }

        if (this.hasLocal(symbol)) {
            return locals.symbolNamed(symbol).type();
        }

        if (this.hasArgument(symbol)) {
            return arguments.symbolNamed(symbol).type();
        }
        return super.resolveTypeName(reference);
    }

    /**
     * Resolves an undefined (reference) by defining a local variable.
     *
     * @param reference a symbolic reference to be resolved.
     */
    @Override
    public void resolveUndefined(Reference reference) {
        Variable local = new Variable(this);
        local.name(reference.name());
        local.clean();
        addLocal(local);
//        System.out.println(name() + " resolved undefined " + reference.name());
    }

    @Override
    public void addLocal(Variable local) {
//        if (local.isSystemic()) return;
        super.addLocal(local);
    }

    /**
     * Indicates whether the block needs erasure(s).
     * @param argumentCount an argument count
     * @return whether this block needs erasure(s)
     */
    public boolean needsErasure(int argumentCount) {
        if (this.isMethod()) {
            return false;
        }
        if (!exceptions.isEmpty()) {
            return true;
        }
        if (argumentCount == 0) {
            return false;
        }
        return (arguments.hasTypedNames()
                && !arguments.hasElementaryNames());
    }

    /**
     * Removes the final statement from the receiver.
     */
    public void truncateStatements() {
        if (statements.isEmpty()) {
            return;
        }
        statements.remove(statements.size() - 1);
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
     * Accepts a visitor for inspection of the receiver arguments.
     *
     * @param aVisitor visits the receiver for its information.
     */
    public void acceptArgumentVisitor(Variable.Visitor aVisitor) {
        arguments.acceptVisitor(aVisitor);
    }

    /**
     * Accepts a visitor for inspection of the receiver exceptions.
     *
     * @param aVisitor visits the receiver for its information.
     */
    public void acceptExceptionVisitor(Reference.Visitor aVisitor) {
        if (exceptions.isEmpty()) {
            return;
        }
        for (String exceptionType : exceptions) {
            aVisitor.visit(exceptionType);
        }
    }

    /**
     * Accepts a visitor for inspection of the receiver statements.
     *
     * @param aVisitor visits the receiver for its information.
     */
    public void acceptStatementVisitor(Operand.Visitor aVisitor) {
        if (!statements.isEmpty()) {
            statements.stream().forEach((s) -> {
                aVisitor.visit(s);
            });
        }
    }

    public void statementVisitResult(Operand.Emitter aVisitor) {
        List<Emission> list = new ArrayList();
        if (!statements.isEmpty()) {
            statements.stream().forEach((s) -> {
                list.add(aVisitor.visitResult(s));
            });
        }
    }
}
