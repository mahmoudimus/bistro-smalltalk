//====================================================================
// Code.java
//====================================================================
package smalltalk.compiler.scope;

import java.util.*;
import java.util.stream.Collectors;

import static smalltalk.Name.*;
import smalltalk.compiler.Emission;
import smalltalk.compiler.element.*;

/**
 * Represents and encodes a local code scope.
 *
 * @author Copyright 1999,2017 Nikolas S. Boyd. All rights reserved.
 */
public abstract class Code extends Scope {

    /**
     * Lists the access modifiers supported by Bistro.
     */
    public static final List<String> accessModifiers = new ArrayList();

    /**
     * Establishes the access modifiers supported by Bistro.
     */
    static {
        accessModifiers.add(Public);
        accessModifiers.add(Protected);
        accessModifiers.add(Private);
    }

    /**
     * Contains a comment for the code scope.
     */
    String comment;

    /**
     * The result type name of the code scope.
     */
    String type;

    /**
     * Contains any local variable definitions.
     */
    Table locals;

    /**
     * Contains any scope signature modifiers.
     */
    List<String> modifiers = new ArrayList();

    /**
     * Manages any types that appear in the scope signature.
     */
    Stack signatureTypes = new Stack();

    /**
     * Constructs a new Code scope.
     * @param container a container
     */
    public Code(Container container) {
        super(container);
        comment = "";
        type = "";
        locals = new Table(this);
    }

    public String elementName() {
        return getClass().getSimpleName() + "::" + name();
    }

    /**
     * Returns the name of this code scope.
     *
     * @return the name of this code scope.
     */
    @Override
    public abstract String name();

    /**
     * Establishes the name of this code scope.
     *
     * @param aName the name of this code scope.
     */
    public abstract void name(String aName);

    /**
     * Returns whether the code scope has a comment.
     *
     * @return whether the code scope has a comment.
     */
    public boolean hasComment() {
        return comment.length() > 0;
    }

    /**
     * Returns the comment for this code scope.
     *
     * @return the comment for this code scope.
     */
    public String comment() {
        return (comment.length() == 0 ? comment
                : "/** " + comment.substring(1, comment.length() - 1) + " */");
    }

    /**
     * Establishes the comment for this code scope.
     *
     * @param aString the comment for this code scope.
     */
    public void comment(String aString) {
        if (aString == null) {
            return;
        }
        comment = aString;
    }

    /**
     * Returns the result type name of this code scope.
     *
     * @return the result type name of this code scope.
     */
    public String type() {
        return type;
    }

    /**
     * Establishes the result type name of this code scope.
     *
     * @param typeName the result type name of this code scope.
     */
    public void type(String typeName) {
        type = typeName;
    }

    /**
     * Returns whether a type has been defined.
     *
     * @return whether a type has been defined.
     */
    public boolean needsType() {
        return (type().length() == 0);
    }

    public Table locals() {
        return locals;
    }

    /**
     * Returns the number of locals defined in this code scope.
     *
     * @return the number of locals defined in this code scope.
     */
    public int localCount() {
        return locals.size();
    }

    /**
     * Returns whether this code scope has any locally defined symbols.
     *
     * @return whether this code scope has any locally defined symbols.
     */
    @Override
    public boolean hasLocals() {
        return localCount() > 0;
    }

    public boolean hasLocal(String symbol) {
        return locals().containsSymbol(symbol);
    }

    @Override
    public Variable localNamed(String symbol) {
        return locals().symbolNamed(symbol);
    }

    /**
     * Adds a new (undefined) local to this code scope.
     */
    public void addLocal() {
        locals.prepareSymbol();
        locals.currentSymbol().container(this);
        Variable v = locals.currentSymbol();
        if (!v.name().isEmpty()) {
            if (v.isHeritage()) v.container(facialScope());
//            System.out.println("added " + v.scopeDescription() + " " + v.name() + " in " + name());
        }
    }

    /**
     * Adds a (local) to this code scope.
     *
     * @param local a local variable declaration.
     */
    public void addLocal(Variable local) {
        local.container(local.isHeritage() ? facialScope() : this);
        locals.addSymbol(local);
//        System.out.println("added " + local.scopeDescription() + " " + local.name() + " in " + name());
    }

    /**
     * Returns the most recently defined local.
     *
     * @return the most recently defined local.
     */
    public Variable currentLocal() {
        return locals.currentSymbol();
    }

    /**
     * Establishes the initial value of the current local.
     *
     * @param aValue the initial value for the current local.
     */
    public void initializeLocal(Operand aValue) {
        currentLocal().value(aValue);
    }

    /**
     * Cleans out any residue left from the parsing process and prepares the receiver for code generation.
     */
    @Override
    public void clean() {
        super.clean();
        locals.clean();
    }

    /**
     * Adds (aModifier) to the code scope signature.
     *
     * @param aModifier a signature modifier.
     */
    public void addModifier(String aModifier) {
        modifiers.add(aModifier);
    }

    /**
     * Returns the modifiers for this code object.
     *
     * @return the modifiers for this code object.
     */
    public List<String> modifiers() {
        return new ArrayList(modifiers);
    }

    public List<String> modifiersWithoutWrapped() {
        return modifiers.stream().filter(m -> !Wrapped.equals(m)).collect(Collectors.toList());
    }

    /**
     * Establishes the modifiers for the code scope signature.
     *
     * @param newModifiers some signature modifiers.
     */
    public void modifiers(List<String> newModifiers) {
        if (newModifiers.isEmpty()) {
            return;
        }
        String aString = newModifiers.get(0);
        if (aString.charAt(0) == '\"') {
            newModifiers.remove(aString);
            this.comment(aString);
        }
        modifiers = newModifiers;
    }

    /**
     * Returns whether the code signature is declared abstract.
     *
     * @return whether the code signature is declared abstract.
     */
    public boolean isAbstract() {
        return modifiers.contains(Abstract);
    }

    /**
     * Returns whether the code signature is declared static.
     *
     * @return whether the code signature is declared static.
     */
    public boolean isStatic() {
        return modifiers.contains(Static);
    }

    /**
     * Returns whether the code signature is declared public.
     *
     * @return whether the code signature is declared public.
     */
    public boolean isPublic() {
        return modifiers.stream().noneMatch(m -> Protected.equals(m) || Private.equals(m));
    }

    /**
     * Returns whether an access modifier has been defined.
     *
     * @return whether an access modifier has been defined.
     */
    public boolean needsAccess() {
        for (String modifier : modifiers) {
            if (accessModifiers.contains(modifier)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns whether the container can resolve a symbolic (reference).
     *
     * @param reference a symbolic reference to be resolved.
     * @return whether the container can resolve a symbolic (reference).
     */
    @Override
    public boolean resolves(Reference reference) {
        if (this.hasLocal(reference.name())) return true;
        return containerScope().resolves(reference);
    }

    @Override
    public Scope scopeResolving(Reference reference) {
        if (this.hasLocal(reference.name())) return this;
        return containerScope().scopeResolving(reference);
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
        if (this.hasLocal(symbol)) {
            return locals.symbolNamed(symbol).resolvedType();
        }
        return containerScope().resolveType(reference);
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
        if (this.hasLocal(symbol)) {
            return locals.symbolNamed(symbol).type();
        }
        return containerScope().resolveTypeName(reference);
    }


    @Override
    public Emission emitModifiers() {
        return emitSequence(modifiersWithoutWrapped());
    }

    public Emission emitLocals() {
        return emitLines(emitLocalVariables());
    }

    public List<Emission> emitLocalVariables() {
        return locals().definedSymbols().stream()
                .map(v -> emitStatement(v.emitItem()))
                .collect(Collectors.toList());
    }
}
