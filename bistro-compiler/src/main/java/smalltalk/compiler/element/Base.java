//====================================================================
// Base.java
//====================================================================
package smalltalk.compiler.element;

import java.util.*;

/**
 * Defines the basic capabilities of a Bistro compiler element.
 *
 * @author Copyright 1999,2016 Nikolas S. Boyd. All rights reserved.
 */
public class Base {

    /**
     * Defines an interface for visiting instances.
     */
    public static interface Visitor {

        public void visit(Base element);
    }

    /**
     * An empty string.
     */
    public static final String EmptyString = "";
    public static final String Nil = "nil";

    /**
     * Names the root object class.
     */
    public static final String SimpleRoot = "Object";
    public static final String RootPackage = "smalltalk.behavior.";

    /**
     * Names the root object class.
     */
    public static final String RootClass = RootPackage + SimpleRoot;
    public static final String JavaRoot = "java.lang." + SimpleRoot;

    /**
     * Names the root object meta-class.
     */
    public static final String RootMetaclass = RootPackage + "Class";
    public static final String BehaviorClass = RootPackage + "Behavior";
    public static final String MetaclassMember = "$class";

    /**
     * The package component separator character.
     */
    public static final char Dot = '.';
    public static final String WildDot = "\\.";

    /**
     * The name of the primitive factory in smalltalk.behavior.Object.
     */
    public static final String Primitive = "primitive";

    /**
     * The name of the primitive factory class.
     */
    public static final String PrimitiveFactory = "PrimitiveFactory";

    /**
     * The name of the primitive array class.
     */
    public static final String ArrayClass = "java.lang.reflect.Array";

    /**
     * Contains the primitive types.
     */
    protected static Map<String, Class> PrimitiveTypes = new HashMap();

    /**
     * Contains the primitive wrappers.
     */
    protected static Map<String, String> PrimitiveWrappers = new HashMap();

    /**
     * Contains the primitive un-wrappers.
     */
    protected static Map<String, String> PrimitiveUnwrappers = new HashMap();

    /**
     * Contains the object wrappers.
     */
    protected static Map<String, String> ObjectWrappers = new HashMap();

    /**
     * Contains the object wrappers.
     */
    protected static Map<String, String> ObjectUnwrappers = new HashMap();

    /**
     * Initializes the static tables.
     */
    static {
        initializePrimitiveTypes();
        initializePrimitiveWrappers();
        initializeObjectWrappers();
    }

    /**
     * Initializes the table of primitive types.
     */
    protected static void initializePrimitiveTypes() {
        PrimitiveTypes.put("void", void.class);
        PrimitiveTypes.put("boolean", boolean.class);
        PrimitiveTypes.put("byte", byte.class);
        PrimitiveTypes.put("char", char.class);
        PrimitiveTypes.put("int", int.class);
        PrimitiveTypes.put("short", short.class);
        PrimitiveTypes.put("long", long.class);
        PrimitiveTypes.put("float", float.class);
        PrimitiveTypes.put("double", double.class);
        PrimitiveTypes.put("string", String.class);
        PrimitiveTypes.put("decimal", java.math.BigDecimal.class);
    }

    /**
     * Initializes the table of primitive wrappers.
     */
    protected static void initializePrimitiveWrappers() {
        PrimitiveWrappers.put("boolean", "Boolean");
        PrimitiveWrappers.put("byte", "Number");
        PrimitiveWrappers.put("char", "Number");
        PrimitiveWrappers.put("short", "Number");
        PrimitiveWrappers.put("int", "Number");
        PrimitiveWrappers.put("long", "Number");
        PrimitiveWrappers.put("float", "Float");
        PrimitiveWrappers.put("double", "Double");

        PrimitiveUnwrappers.put("boolean", "asPrimitive");
        PrimitiveUnwrappers.put("byte", "primitiveByte");
        PrimitiveUnwrappers.put("char", "primitiveCharacter");
        PrimitiveUnwrappers.put("short", "primitiveShort");
        PrimitiveUnwrappers.put("int", "primitiveInteger");
        PrimitiveUnwrappers.put("long", "primitiveLong");
        PrimitiveUnwrappers.put("float", "asPrimitive");
        PrimitiveUnwrappers.put("double", "asPrimitive");
    }

    /**
     * Initializes the table of object wrappers.
     */
    protected static void initializeObjectWrappers() {
        ObjectWrappers.put("java.math.BigDecimal", "Fixed");
        ObjectWrappers.put("java.lang.String", "String");

        ObjectWrappers.put("java.math.BigDecimal", "asPrimitive");
        ObjectWrappers.put("java.lang.String", "asPrimitive");

        // can any others be predetermined?
    }

    /**
     * Returns the root Bistro class, or null if it has not yet been defined.
     */
    public static Class rootObjectClass() {
        try {
            return Class.forName(RootClass);
        } catch (Throwable ex) {
            return null;
        }
    }

    /**
     * Returns the number of occurrences of (aCharacter) within (aString).
     *
     * @param aCharacter the character to count.
     * @param aString the string in which to count characters.
     * @return the number of occurrences of (aCharacter) within (aString).
     */
    public static int occurrencesOf(char aCharacter, String aString) {
        int x = 0;
        int result = 0;
        while (x >= 0) {
            x = aString.indexOf(aCharacter, x);
            if (x >= 0) {
                result++;
                x++;
            }
        }
        return result;
    }

    /**
     * Indicates the source line on which the element first appears.
     */
    protected int sourceLine = 0;

    /**
     * Returns the source line on which the element first appears.
     * @return a line number
     */
    public int getLine() {
        return sourceLine;
    }

    /**
     * Establishes the original element source line.
     *
     * @param lineNumber a source line number (0-based).
     */
    public void setLine(int lineNumber) {
        sourceLine = lineNumber + 1;
    }

    /**
     * Describes the receiver, esp. for instrumentation purposes.
     * @return a description
     */
    public String description() {
        return getClass().getName();
    }

    /**
     * Cleans out any lint left from the parsing process and prepares the receiver for code generation.
     */
    public void clean() {
        // does nothing
    }
}
