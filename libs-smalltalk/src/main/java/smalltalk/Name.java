//====================================================================
// Name.java
//====================================================================
package smalltalk;

import java.util.*;

/**
 * Maps names between Smalltalk selectors and Java method names.
 *
 * @author Copyright 1999,2016 Nikolas S. Boyd. All rights reserved.
 */
public class Name {

    /**
     * A colon character.
     */
    public static char COLON = ':';

    /**
     * A colon.
     */
    public static String Colon = ":";

    /**
     * An underline character.
     */
    static char UNDER = '_';

    /**
     * An underline.
     */
    static String Under = "_";

    /**
     * A dollar character.
     */
    static char DOLLAR = '$';

    /**
     * A dollar sign.
     */
    static String Dollar = "$";

    /**
     * Contains all the Java reserved words.
     */
    static final List<String> ReservedWords = new ArrayList();

    /**
     * Maps binary operators to equivalent method names.
     */
    public static final Properties BinaryOperators = new Properties();

    public static final Character[] Vowels = { 'a', 'e', 'i', 'o', 'u', 'A', 'E', 'I', 'O', 'U' };
    public static final List<Character> VowelList = Arrays.asList(Vowels);

    /**
     * Initializes (reservedWords), (binaryOperators), (controlClasses).
     */
    static {
        ReservedWords.add("package");
        ReservedWords.add("import");
        ReservedWords.add("class");
        ReservedWords.add("interface");
        ReservedWords.add("if");
        ReservedWords.add("else");
        ReservedWords.add("while");
        ReservedWords.add("new");
        ReservedWords.add("return");
        ReservedWords.add("do");
        ReservedWords.add("case");
        ReservedWords.add("continue");
        ReservedWords.add("switch");
        ReservedWords.add("public");
        ReservedWords.add("private");
        ReservedWords.add("protected");
        ReservedWords.add("synchronized");
        ReservedWords.add("native");
        ReservedWords.add("volatile");
        ReservedWords.add("static");
        ReservedWords.add("null");
        ReservedWords.add("byte");
        ReservedWords.add("char");
        ReservedWords.add("short");
        ReservedWords.add("int");
        ReservedWords.add("long");
        ReservedWords.add("float");
        ReservedWords.add("double");

        BinaryOperators.put(":=", " = ");
        BinaryOperators.put("^", "return");

        BinaryOperators.put("@", "$at");
        BinaryOperators.put(",", "$append");
        BinaryOperators.put("&", "$and");
        BinaryOperators.put("|", "$or");
        BinaryOperators.put("+", "$plus");
        BinaryOperators.put("-", "$minus");
        BinaryOperators.put("+=", "$incremented");
        BinaryOperators.put("-=", "$decremented");
        BinaryOperators.put("*", "$times");
        BinaryOperators.put("*=", "$magnified");
        BinaryOperators.put("/", "$dividedBy");
        BinaryOperators.put("/=", "$divided");
        BinaryOperators.put("//", "$idiv");
        BinaryOperators.put("\\", "$into");
        BinaryOperators.put("\\\\", "$imod");
        BinaryOperators.put("**", "$raised");
        BinaryOperators.put("%", "$modulus");
        BinaryOperators.put("<", "$lessThan");
        BinaryOperators.put("<=", "$lessEqual");
        BinaryOperators.put(">", "$moreThan");
        BinaryOperators.put(">=", "$moreEqual");
        BinaryOperators.put("=", "$equal");
        BinaryOperators.put("~=", "$notEqual");
        BinaryOperators.put("==", "$is");
        BinaryOperators.put("~~", "$isnt");
        BinaryOperators.put(">>", "$associateWith");

        BinaryOperators.put("$at", "@");
        BinaryOperators.put("$append", ",");
        BinaryOperators.put("$and", "&");
        BinaryOperators.put("$or", "|");
        BinaryOperators.put("$plus", "+");
        BinaryOperators.put("$minus", "-");
        BinaryOperators.put("$incremented", "+=");
        BinaryOperators.put("$decremented", "-=");
        BinaryOperators.put("$times", "*");
        BinaryOperators.put("$magnified", "*=");
        BinaryOperators.put("$dividedBy", "/");
        BinaryOperators.put("$divided", "/=");
        BinaryOperators.put("$idiv", "//");
        BinaryOperators.put("$into", "\\");
        BinaryOperators.put("$imod", "\\\\");
        BinaryOperators.put("$raised", "**");
        BinaryOperators.put("$modulus", "%");
        BinaryOperators.put("$lessThan", "<");
        BinaryOperators.put("$lessEqual", "<=");
        BinaryOperators.put("$moreThan", ">");
        BinaryOperators.put("$moreEqual", ">=");
        BinaryOperators.put("$equal", "=");
        BinaryOperators.put("$notEqual", "~=");
        BinaryOperators.put("$is", "==");
        BinaryOperators.put("$isnt", "~~");
        BinaryOperators.put("$associateWith", ">>");
    }

    /**
     * Returns the Java method name derived from a Bistro (selector).
     *
     * @param selector a Bistro selector.
     * @return a primitive String
     */
    public static String from(String selector) {
        String op = BinaryOperators.getProperty(selector);
        if (op != null) {
            return op;
        }
        int tail = selector.length() - 1;
        while (tail > 0 && selector.charAt(tail) == COLON) {
            tail--;
        }
        selector = selector.substring(0, tail + 1);
        selector = selector.replace(COLON, UNDER);
        if (ReservedWords.contains(selector)) {
            selector = Dollar + selector;
        }
        return selector;
    }

    /**
     * Returns the Bistro selector from which a Java (methodName) was derived.
     *
     * @param methodName the name of a Java method.
     * @param argumentCount the number of method arguments.
     * @return a selector
     */
    public static String from(String methodName, int argumentCount) {
        String op = BinaryOperators.getProperty(methodName);
        if (op != null) {
            return op;
        }
        if (methodName.startsWith(Dollar)) {
            methodName = methodName.substring(1);
        }
        methodName = methodName.replace(UNDER, COLON);
        if (argumentCount > 0) {
            methodName = methodName + Colon;
        }
        return methodName;
    }

    public static boolean isVowel(char aValue) {
        return VowelList.contains(aValue);
    }
}
