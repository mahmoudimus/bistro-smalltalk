//====================================================================
// Selector.java
//====================================================================
package smalltalk.compiler.element;

import java.util.*;
import org.antlr.runtime.Token;
import smalltalk.Name;

/**
 * Represents and manipulates Bistro method names, including conversions to Java method names.
 *
 * @author Copyright 1999,2016 Nikolas S. Boyd. All rights reserved.
 */
public class Selector extends Base {

    /**
     * Defines an interface for visiting instances.
     */
    public static interface Visitor {

        public void visit(Selector selector);
    }

    /**
     * The empty selector.
     */
    public static final Selector empty = Selector.from(EmptyString);

    /**
     * Assignment operator.
     */
    public static final String Assign = ":=";

    /**
     * The assignment selector.
     */
    public static final Selector forAssignment = Selector.from(Assign);

    /**
     * Exit operator.
     */
    public static final String Exit = "^";

    /**
     * The exit selector.
     */
    public static final Selector forExit = Selector.from(Exit);

    /**
     * A colon character.
     */
    public static final char COLON = ':';

    /**
     * A colon.
     */
    public static final String Colon = ":";

    /**
     * An underline character.
     */
    static final char UNDER = '_';

    /**
     * An underline.
     */
    static final String Under = "_";

    /**
     * A dollar character.
     */
    static final char DOLLAR = '$';

    /**
     * A dollar sign.
     */
    static final String Dollar = "$";

    /**
     * Maps binary operators to equivalent primitive operators.
     */
    static final Properties PrimitiveOperators = new Properties();

    /**
     * Contains the standard selectors supported by class Object.
     */
    static final List<String> ObjectSelectors = new ArrayList();

    /**
     * Initializes (reservedWords), (binaryOperators), (controlClasses).
     */
    static {
        PrimitiveOperators.put("&", " & ");
        PrimitiveOperators.put("|", " | ");
        PrimitiveOperators.put("+", " + ");
        PrimitiveOperators.put("+=", " += ");
        PrimitiveOperators.put("-", " - ");
        PrimitiveOperators.put("-=", " -= ");
        PrimitiveOperators.put("*", " * ");
        PrimitiveOperators.put("*=", " *= ");
        PrimitiveOperators.put("/", " / ");
        PrimitiveOperators.put("%", " % ");
        PrimitiveOperators.put("/=", " /= ");
        PrimitiveOperators.put("//", " / ");
        PrimitiveOperators.put("\\\\", " % ");
        PrimitiveOperators.put("==", " == ");
        PrimitiveOperators.put("<", " < ");
        PrimitiveOperators.put("<=", " <= ");
        PrimitiveOperators.put(">", " > ");
        PrimitiveOperators.put(">=", " >= ");
        PrimitiveOperators.put("~~", " != ");
        PrimitiveOperators.put(Assign, " = "); // ??

        ObjectSelectors.add("class");
        ObjectSelectors.add("new");
        ObjectSelectors.add("new:");
        ObjectSelectors.add("==");
        ObjectSelectors.add("~~");
        ObjectSelectors.add("=");
        ObjectSelectors.add("~=");
        ObjectSelectors.add("do:");
        ObjectSelectors.add("hash");
        ObjectSelectors.add("hashCode");
        ObjectSelectors.add("value");
        ObjectSelectors.add("yourself");
        ObjectSelectors.add("species");
        ObjectSelectors.add("isNil");
        ObjectSelectors.add("notNil");
        ObjectSelectors.add(":=");
    }

    /**
     * Returns the number of operands in a message involving the selector.
     * @param selector a selector
     * @return the number of operands in a message involving the selector.
     */
    public static int operandCountFrom(String selector) {
        if (Name.BinaryOperators.containsKey(selector)) {
            return 2;
        }
        return occurrencesOf(COLON, selector) + 1;
    }

    /**
     * Returns a new selector created from the supplied (token).
     *
     * @param token a parsed selector token.
     * @return a Selector
     */
    public static Selector from(Token token) {
        Selector selector = new Selector();
        selector.append(token.getText());
        selector.setLine(token.getLine());
        return selector;
    }

    /**
     * Returns a new selector created from the supplied (name).
     *
     * @param name a selector name.
     * @return a Selector
     */
    public static Selector from(String name) {
        Selector selector = new Selector();
        selector.append(name);
        return selector;
    }

    /**
     * Contains a Bistro method name.
     */
    StringBuffer contents;

    /**
     * Constructs a new Selector.
     */
    public Selector() {
        contents = new StringBuffer();
    }

    /**
     * Returns a Java method name derived from a Bistro method name.
     *
     * @return a Java method name derived from a Bistro method name.
     */
    public String methodName() {
        return Name.from(contents());
    }

    /**
     * Returns a Java primitive operator derived from a Bistro operator.
     *
     * @return a Java primitive operator derived from a Bistro operator.
     */
    public String asPrimitiveOperator() {
        return (String) PrimitiveOperators.get(contents());
    }

    /**
     * Returns the contents of the selector.
     *
     * @return the contents of the selector.
     */
    public String contents() {
        return contents.toString();
    }

    /**
     * Returns whether the selector contains the assignment operator.
     *
     * @return whether the selector contains the assignment operator.
     */
    public boolean isAssign() {
        return contents().equals(Assign);
    }

    /**
     * Returns whether the selector is empty.
     *
     * @return whether the selector is empty.
     */
    public boolean isEmpty() {
        return contents.length() == 0;
    }

    /**
     * Returns whether the selector contains a Bistro keyword.
     *
     * @return whether the selector contains a Bistro keyword.
     */
    public boolean isKeyword() {
        return contents().endsWith(Colon);
    }

    /**
     * Returns whether the selector indicates use of a constructor.
     *
     * @return whether the selector indicates use of a constructor.
     */
    public boolean isBasicNew() {
        return contents().startsWith("basicNew");
    }

    /**
     * Returns whether the selector indicates use of a constructor.
     *
     * @return whether the selector indicates use of a constructor.
     */
    public boolean isNew() {
        return contents().startsWith("new");
    }

    /**
     * Returns whether the selector contains a binary operator.
     *
     * @return whether the selector contains a binary operator.
     */
    public boolean isOperator() {
        return Name.BinaryOperators.containsKey(contents());
    }

    /**
     * Returns whether the selector contains a primitive operator.
     *
     * @return whether the selector contains a primitive operator.
     */
    public boolean isPrimitive() {
        return PrimitiveOperators.containsKey(contents());
    }

    /**
     * Returns whether the selector can be directly invoked rather than performed.
     *
     * @return whether the selector can be directly invoked rather than performed.
     */
    public boolean isOptimized() {
        return ObjectSelectors.contains(contents());
    }

    /**
     * Returns whether the selector indicates a method exit.
     *
     * @return whether the selector indicates a method exit.
     */
    public boolean isReturn() {
        return contents().equals(Exit);
    }

    /**
     * Appends (aString) to the selector.
     *
     * @param aString the string to append.
     */
    public void append(String aString) {
        contents.append(aString);
    }

    /**
     * Returns the number of operands in a message involving the selector.
     *
     * @return the number of operands in a message involving the selector.
     */
    public int operandCount() {
        return operandCountFrom(contents());
    }

    /**
     * Accepts a visitor for inspection of the receiver.
     *
     * @param aVisitor visits the receiver for its information.
     */
    public void acceptVisitor(Visitor aVisitor) {
        aVisitor.visit(this);
    }
}
