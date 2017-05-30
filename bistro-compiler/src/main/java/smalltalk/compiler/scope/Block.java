//====================================================================
// Block.java
//====================================================================
package smalltalk.compiler.scope;

import java.util.*;
import java.util.stream.Collectors;

import smalltalk.compiler.Emission;
import static smalltalk.compiler.Emission.Items;
import static smalltalk.compiler.Emission.emit;
import smalltalk.compiler.element.*;

/**
 * Represents and encodes a block as a sequence of statements.
 *
 * @author Copyright 1999,2016 Nikolas S. Boyd. All rights reserved.
 */
public class Block extends Code {

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
        "TwoArgumentBlock",
        "UnsupportedBlock",
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

    @Override
    public boolean isBlock() {
        return true;
    }

    @Override
    public boolean hasPrimitiveFactory() {
        return true;
    }

    public boolean receiverNeedsTerm() {
        return true;
    }

    public boolean hasArgument(String symbol) {
        return arguments.containsSymbol(symbol);
    }

    public boolean needsResult() {
        return !this.isConstructor() &&
               !this.returnsVoid() &&
               !this.isAbstract();
    }

    public boolean isConstructor() {
        return false;
    }

    public boolean returnsVoid() {
        return type().equals("void");
    }

    public boolean exits() {
        return containsExit;
    }

    public boolean needsErasure() {
        return needsErasure(argumentCount());
    }

    public boolean needsErasure(int argumentCount) {
        if (this.isMethod()) return false;
        if (!exceptions.isEmpty()) return true;
        if (argumentCount == 0) return false;
        return (arguments.hasTypedNames() &&
                !arguments.hasElementaryNames());
    }

    /**
     * Returns the ordered list of arguments.
     *
     * @return the ordered list of arguments.
     */
    public List<Variable> arguments() {
        return arguments.symbols();
    }

    public Operand finalStatement() {
        return statements.get(statementCount() - 1);
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

    public Variable argumentNamed(String symbol) {
        return arguments.symbolNamed(symbol);
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
            Variable v = localNamed(symbol);
            if (reference.isNestedDeeper(this)) v.makeTransient();
            return v.resolvedType();
        }

        if (this.hasArgument(symbol)) {
            return argumentNamed(symbol).resolvedType();
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
            Variable v = localNamed(symbol);
            if (reference.isNestedDeeper(this)) v.makeTransient();
            return v.type();
        }

        if (this.hasArgument(symbol)) {
            return argumentNamed(symbol).type();
        }

        return super.resolveTypeName(reference);
    }

    /**
     * Resolves an undefined (reference) by defining a local variable.
     *
     * @param reference a symbolic reference to be resolved.
     */
    @Override
    public Variable resolveUndefined(Reference reference) {
        Variable local = new Variable(this);
        local.name(reference.name());
        local.clean();
        addLocal(local);
//        System.out.println(name() + " resolved undefined " + reference.name());
        return local;
    }

    @Override
    public void addLocal(Variable local) {
//        if (local.isSystemic()) return;
        super.addLocal(local);
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



    public Emission emitTry() {
        return emit("OnlyTry")
                .with("locals", emitLocals())
                .with("content", emitContents());
    }

    public Emission emitCatch() {
        return emit("OnlyCatch")
                .with("caught", emitArguments(false))
                .with("locals", emitLocals())
                .with("content", emitContents());
    }

    public Emission emitFinally() {
        return emit("OnlyEnsure")
                .with("locals", emitLocals())
                .with("content", emitContents());
    }

    public Emission emitNewClosure() {
        return emit("NewClosure")
                .with("closureType", signatureTypes[0]);
    }

    public Emission emitClosure() {
        return emitOptimized();
    }

    @Override
    public Emission emitOptimized() {
        return emit("OptimizedBlock")
                .with("closureType", closureType())
                .with("locals", emitLocals())
                .with("signature", emitSignature())
                .with("content", emitContents());
    }

    public List<Emission> emitCastedArguments() {
        return arguments().stream()
                .map(arg -> arg.emitCast()).collect(Collectors.toList());
    }

    public List<Emission> emitArguments() {
        return emitArguments(true);
    }

    public List<Emission> emitArguments(boolean useFinal) {
        return arguments().stream()
                .map(arg -> arg.emitArgument(useFinal))
                .collect(Collectors.toList());
    }

    public List<Emission> emitErasedArguments(boolean useFinal) {
        return arguments().stream()
                .map(arg -> arg.emitErasedArgument(useFinal))
                .collect(Collectors.toList());
    }

    public Emission emitExceptions() {
        return exceptionCount() == 0 ? emitEmpty() : emit("Exceptions").with(Items, exceptions);
    }

    public Emission wrapErasedCall() {
        return emit("WrapErasedCall").with("content", emitErasedCall());
    }

    public Emission emitErasedCall() {
        return returnsVoid() ?
                emit("ErasedVoid").name(blockName()).with("arguments", emitList(emitCastedArguments())) :
                emit("ErasedCall").name(blockName()).with("arguments", emitList(emitCastedArguments())) ;
    }

    public Emission emitErasure() {
        if (exceptionCount() == 0) {
            return emit("ErasedBlock").name(blockName())
                    .with("arguments", emitList(emitErasedArguments(true)))
                    .with("content", emitErasedCall());
        }

        return emit("ErasedBlock").name(blockName())
                    .with("arguments", emitList(emitErasedArguments(true)))
                    .with("content", wrapErasedCall());
    }

    public Emission emitSignature() {
        String blockName = blockName();
        Emission erasure = null;
        if (needsErasure()) {
            blockName = "$"+blockName;
            erasure = emitErasure();
        }

        return emit("BlockSignature").name(blockName)
                .with("erasure", erasure)
                .with("arguments", emitList(emitArguments()))
                .with("exceptions", emitExceptions());
    }

    public Emission emitContents() {
        return emitLines(emitStatements());
    }

    public List<Emission> emitStatements() {
        List<Emission> contents = statements.stream()
            .map(s -> s.emitStatement())
            .collect(Collectors.toList());

        if (returnsVoid() || this.isConstructor()) {
            return contents; // context requires all statements;
        }

        // context requires returned result
        contents.remove(contents.size() - 1);
        contents.add(finalStatement().emitResult());
        return contents;
    }

    public String closureType() {
        return signatureTypes[argumentCount() > 2 ? 3 : argumentCount()];
    }

    public String blockName() {
        return signatureValues[argumentCount()];
    }
}
