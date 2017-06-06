//====================================================================
// Scope.java
//====================================================================
package smalltalk.compiler.element;

import java.util.*;
import org.antlr.runtime.tree.CommonTree;
import smalltalk.compiler.Emission;

import smalltalk.compiler.scope.*;

/**
 * Represents a language scope.
 *
 * @author Copyright 1999,2017 Nikolas S. Boyd. All rights reserved.
 */
public class Scope extends Container {

    /**
     * Refers to the active compiler scope.
     */
    public static Scope current = null;

    /**
     * Returns the current scope as a File scope.
     *
     * @return the current scope as a File scope.
     */
    public static final File asFile() {
        return (File) current;
    }

    /**
     * Returns the current scope as a Face scope.
     *
     * @return the current scope as a Face scope.
     */
    public static final Face asFace() {
        return (Face) current;
    }

    /**
     * Returns the current scope as a Method scope.
     *
     * @return the current scope as a Method scope.
     */
    public static final Method asMethod() {
        return (Method) current;
    }

    /**
     * Returns the current scope as a Block scope.
     *
     * @return the current scope as a Block scope.
     */
    public static final Block asBlock() {
        return (Block) current;
    }

    /**
     * The message operand stack for this scope.
     */
    Stack operands = new Stack();

    /**
     * The message selector stack for this scope.
     */
    Stack selectors = new Stack();

    /**
     * Contains signature options found during parsing.
     */
    List<String> options = new ArrayList();

    /**
     * Constructs a new Scope.
     *
     * @param container the scope that contains the new one.
     */
    public Scope(Container container) {
        super(container);
    }

    /**
     * Constructs a new Scope.
     */
    public Scope() {
        this(null);
    }

    /**
     * Returns the container for this scope.
     *
     * @return the container for this scope.
     */
    public Scope containerScope() {
        return (Scope) container;
    }

    /**
     * Returns the block level for this scope.
     *
     * @return the block level for this scope.
     */
    public int blockLevel() {
        return containerScope().blockLevel() + 1;
    }

    /**
     * Returns the file scope which contains this scope.
     *
     * @return the file scope which contains this scope.
     */
    public File currentFile() {
        return containerScope().currentFile();
    }

    /**
     * Returns the face scope which contains this scope.
     *
     * @return the face scope which contains this scope.
     */
    public Face currentFace() {
        return containerScope().currentFace();
    }

    /**
     * Returns the method scope which contains this scope.
     *
     * @return the method scope which contains this scope.
     */
    public Method currentMethod() {
        return containerScope().currentMethod();
    }

    /**
     * Adds an (option) to those accumulated by this scope.
     *
     * @param option an option.
     */
    public void addOption(String option) {
        options.add(option);
    }

    /**
     * Adds an option to those accumulated by this scope.
     *
     * @param node an abstract syntax tree node.
     */
    public void addOption(CommonTree node) {
        addOption(node.getText().trim());
    }

    /**
     * Returns the message selector stack for this scope.
     *
     * @return the message selector stack for this scope.
     */
    public Stack selectors() {
        return selectors;
    }

    /**
     * Indicates whether the top selector is an assignment.
     */
    public boolean hasAssignmentSelector() {
        if (selectors().empty()) {
            return false;
        }
        Selector top = (Selector) selectors().peek();
        return (top.contents().equals(Selector.forAssignment.contents()));
    }

    /**
     * Returns the message operand stack for this scope.
     *
     * @return the message operand stack for this scope.
     */
    public Stack operands() {
        return operands;
    }

    /**
     * Returns the accumulated options and clears the set.
     *
     * @return the accumulated options and clears the set.
     */
    public List<String> consumeOptions() {
        List<String> result = options;
        options = new ArrayList();
        return result;
    }


    public Emission emitScope() {
        return null; // override this!
    }

    public boolean resolves(Reference reference) {
        return false;
    }

    public Scope scopeResolving(Reference reference) {
        return null;
    }

    public boolean hasLocals() {
        return false;
    }

    public Variable localNamed(String symbol) {
        return null;
    }
}
