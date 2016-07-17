//====================================================================
// Name.java
//====================================================================
package smalltalk;

import java.util.*;
import org.apache.commons.lang.StringUtils;

/**
 * Maps names between Smalltalk selectors and Java method names.
 *
 * @author Copyright 1999,2016 Nikolas S. Boyd. All rights reserved.
 */
public class Name {

    static char DOT = '.';
    static String Dot = ".";
    static String Escape = "\\";
    static String EmptyString = "";

    /**
     * A colon character.
     */
    static char COLON = ':';

    /**
     * A colon.
     */
    static String Colon = ":";

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
    
    public static final char[] Vowels = { 'a', 'e', 'i', 'o', 'u', 'A', 'E', 'I', 'O', 'U' };

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

    /**
     * Returns the root package name from a (fully qualified) name.
     * @param fullName a (fully qualified) name
     * @return a root name if present, or empty
     */
    public static String rootPackage(String fullName) {
        String packageName = packageName(fullName);
        if (!packageName.contains(Dot)) return packageName;
        String[] parts = packageName.split(Escape + Dot);
        return parts[0];
    }
    
    /**
     * Returns the package name from a (fully qualified) name.
     * @param fullName a (fully qualified) name
     * @return a package name if present, or empty
     */
    public static String packageName(String fullName) {
        if (StringUtils.isEmpty(fullName)) return EmptyString;
        String trim = fullName.trim();

        String[] parts = trim.split(Escape + Dot);
        String lastPart = parts[parts.length - 1];
        if (Character.isLowerCase(lastPart.charAt(0))) return trim;
        if (!trim.contains(Dot)) return EmptyString;

        int headLength = trim.length() - lastPart.length();
        return trim.substring(0, headLength - 1);
    }
    
    /**
     * Returns the full type name from a (fully qualified) name.
     * @param fullName a (fully qualified) name
     * @return a type name if present, or empty
     */
    public static String typeName(String fullName) {
        if (StringUtils.isEmpty(fullName)) return EmptyString;
        String trim = fullName.trim();
        String[] parts = trim.split(Escape + Dot);
        String lastPart = parts[parts.length - 1];
        return (Character.isLowerCase(lastPart.charAt(0))) ? EmptyString : lastPart;
    }
    
    /**
     * Returns the outermost type name from a (fully qualified) name.
     * @param fullName a (fully qualified) name
     * @return a type name if present, or empty
     */
    public static String simpleName(String fullName) {
        String[] parts = typeName(fullName).split(Escape + Dollar);
        return parts[0];
    }
    
    public static boolean isQualified(String name) {
        if (StringUtils.isEmpty(name)) return false;
        return name.contains(Dot);
    }
}
