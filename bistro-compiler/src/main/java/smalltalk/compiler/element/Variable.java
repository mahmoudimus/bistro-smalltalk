//====================================================================
// Variable.java
//====================================================================
package smalltalk.compiler.element;

import java.util.*;
import java.util.stream.Collectors;
import org.antlr.runtime.tree.CommonTree;

import static smalltalk.Name.*;
import smalltalk.compiler.Emission;
import static smalltalk.compiler.Emission.emit;

import smalltalk.compiler.scope.*;

/**
 * Represents and encodes a variable, including its name, type and initial value.
 *
 * @author Copyright 1999,2017 Nikolas S. Boyd. All rights reserved.
 */
public class Variable extends Reference {

    /**
     * Returns a new reference named (name) in the (container) scope.
     *
     * @param name identifies a named object.
     * @param type a type name
     * @param container the container for the reference.
     * @return a new reference named (name) in the (container) scope.
     */
    public static Variable named(String name, String type, Container container) {
        Variable result = new Variable(container);
        result.name(name);
        result.type(type);
        return result;
    }

    /**
     * Contains a comment for the code scope.
     */
    String comment = EmptyString;

    /**
     * The type of a referenced object or datum.
     */
    String type = EmptyString;

    /**
     * Contains any modifiers associated with a variable.
     */
    List<String> modifiers = new ArrayList();

    /**
     * Contains an initial value for a variable (if one exists).
     */
    Operand value;

    /**
     * Constructs a new Reference.
     * @param container a container
     */
    public Variable(Container container) {
        super(container);
        comment = "";
        type = "";
        value = null;
    }

    public String scopeDescription() {
        return (container.isFacial()) ? "member" : "local";
    }

    /**
     * Cleans out any residue left from the parsing process and prepares the receiver for code generation.
     */
    @Override
    public void clean() {
        super.clean();
        if (container.isFacial() && this.needsAccess()) {
            modifiers.add(0, Protected);
        }
        if (type.length() == 0) {
            type = Base.RootClass;
        }
    }

    public boolean hasValue() {
        return value() != null;
    }

    public boolean valueNeedsCast() {
        if (!hasValue()) return false;
        return !type().equals(value().resolvedTypeName());
    }

    public void makeTransient() {
        if (!this.isTransient()) {
            modifiers.add(Transient);
        }
    }

    @Override
    public boolean isTransient() {
        return modifiers.stream().anyMatch(m -> Transient.equals(m));
    }

    /**
     * Indicates whether the reference needs a definition in the local scope.
     * @return whether this needs resolution
     */
    @Override
    public boolean needsLocalResolution() {
        return false;
    }

    /**
     * Returns whether an access modifier has been defined.
     *
     * @return whether an access modifier has been defined.
     */
    public boolean needsAccess() {
        return !modifiers.stream().anyMatch(m -> Code.accessModifiers.contains(m));
    }

    /**
     * Establishes the name of the reference. If the supplied (node) includes a comment, use it.
     *
     * @param node an abstract syntax tree node.
     */
    public void name(CommonTree node) {
        comment(commentFrom(node));
        name(node.getText());
    }

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
        return (comment.length() == 0 ? comment :
                comment.substring(1, comment.length() - 1));
//                "/** " + comment.substring(1, comment.length() - 1) + " */");
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
     * Returns the type of the referenced object or datum.
     *
     * @return the type of the referenced object or datum.
     */
    @Override
    public String type() {
        return type;
    }

    /**
     * Establishes the type of a referenced object or datum.
     *
     * @param typeName the type of the referenced object or datum.
     */
    public void type(String typeName) {
        type = typeName;
    }

    /**
     * Returns the scope that resolves this reference.
     *
     * @return the scope that resolves this reference.
     */
    @Override
    public Container resolvingScope() {
        return container();
    }

    /**
     * Returns the type of the variable.
     *
     * @return the type of the variable.
     */
    @Override
    public Class resolvedType() {
        String typeName = type();
        if (typeName.endsWith("[]")) {
            return typeNamed(typeName);
        }
        Class type = PrimitiveTypes.get(type());
        if (type != null) {
            return type;
        }
        if (typeName.startsWith(RootJava)) {
            return typeNamed(typeName);
        }
        Face typeFace = Library.current.faceNamed(typeName);
        return (typeFace == null ? rootType() : typeFace.typeClass());
    }

    /**
     * Returns the type name of the variable.
     *
     * @return the type name of the variable.
     */
    @Override
    public String resolvedTypeName() {
        return type();
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

    public List<String> modifiers() {
        return new ArrayList(modifiers);
    }

    public List<String> modifiersWithoutTransient() {
        return modifiers.stream().filter(m -> !Transient.equals(m)).collect(Collectors.toList());
    }

    /**
     * Returns whether the name refers to a static variable.
     *
     * @return whether the name refers to a static variable.
     */
    public boolean isStatic() {
        return modifiers.contains(Static);
    }

    /**
     * Returns the initial value of this variable.
     * @return the held value
     */
    public Operand value() {
        return value;
    }

    /**
     * Establishes the initial value of a variable.
     *
     * @param aValue the initial value.
     */
    public void value(Operand aValue) {
        value = aValue;
    }


    public Emission emitCast() {
        if (type().equals(Base.RootClass)) return emitItem(name());
        return emitCast(type(), emitItem(name()));
    }

    @Override
    public Emission emitModifiers() {
        return emitSequence(modifiersWithoutTransient());
    }

    @Override
    public Emission emitItem() {
        return this.isTransient() ? emitTransientLocal() : emitVariable();
    }

    public Emission emitVariable() {
        return emit("Variable")
                .comment(comment())
                .with("notes", emitModifiers())
                .with("type", emitTypeName(type()))
                .name(name())
                .with("cast", valueNeedsCast() ? emitTerm(type()) : emitEmpty())
                .value(hasValue() ? emitOperand(value()) : null);
    }

    public Emission emitTransientLocal() {
        String valueDefault = "null";
        if (PrimitiveTypes.containsKey(type())) {
            valueDefault = "0";
            if ("boolean".equals(type())) {
                valueDefault = "false";
            }
        }
        return emit("TransientLocal")
                .with("type", emitTypeName(type()))
                .name(name())
                .with("cast", valueNeedsCast() ? emitTerm(type()) : emitEmpty())
                .value(hasValue() ? emitOperand(value()) : emitItem(valueDefault));
    }

    public Emission emitErasedArgument(boolean useFinal) {
        return super.emitArgument(useFinal);
    }
}
