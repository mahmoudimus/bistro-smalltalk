//====================================================================
// Mirror.java
//====================================================================
package smalltalk.compiler.element;

import java.util.*;

/**
 * Provides reflective utilities for dealing with primitive Java classes.
 *
 * @author Copyright 1999,2016 Nikolas S. Boyd. All rights reserved.
 */
public class Mirror {

    /**
     * Contains the registered Java class mirrors.
     */
    protected static Map<Class, Mirror> Registry = new HashMap();

    /**
     * Refers to an empty mirror (for null).
     */
    protected static Mirror EmptyMirror = new Mirror();

    /**
     * Returns a mirror for reflecting upon (aClass).
     *
     * @param aClass the class on which to reflect.
     * @return a mirror for reflecting upon (aClass).
     */
    public static Mirror forClass(Class aClass) {
        if (aClass == null) {
            return EmptyMirror;
        }

        if (!Registry.containsKey(aClass)) {
            Registry.put(aClass, new Mirror(aClass));
        }

        return Registry.get(aClass);
    }

    /**
     * The Java class on which to reflect.
     */
    Class aClass;

    /**
     * Constructs a new Mirror.
     *
     * @param reflectedClass the Java class on which to reflect.
     */
    public Mirror(Class reflectedClass) {
        aClass = reflectedClass;
    }

    /**
     * Constructs a new empty Mirror.
     */
    public Mirror() {
        this(null);
    }

    /**
     * Returns a mirror for the superclass of the reflected class.
     *
     * @return a mirror for the superclass of the reflected class.
     */
    public Mirror superior() {
        return Mirror.forClass(aClass.getSuperclass());
    }
    
    public Class reflectedClass() {
        return aClass;
    }

    /**
     * Returns the type of the field named (fieldName).
     *
     * @param fieldName a member field name.
     * @return the type of the field named (fieldName).
     */
    public Class typeFieldNamed(String fieldName) {
        if (aClass == null) {
            return null;
        }
        try {
            return aClass.getDeclaredField(fieldName).getType();
        } catch (NoSuchFieldException e) {
            return superior().typeFieldNamed(fieldName);
        }
    }

    /**
     * Returns the type of the method named (methodName).
     *
     * @param methodName a member method name.
     * @param arguments method argument types
     * @return the type of the method named (methodName).
     */
    public Class typeMethodNamed(String methodName, Class[] arguments) {
        if (aClass == null) {
            return null;
        }
        try {
            return aClass.getDeclaredMethod(methodName, arguments).getReturnType();
        } catch (Throwable ex) {
            return superior().typeMethodNamed(methodName, arguments);
        }
    }

    /**
     * Returns whether the reflected class has a meta-class.
     *
     * @return whether the reflected class has a meta-class.
     */
    public boolean hasMetaclass() {
        return (typeFieldNamed(Base.MetaclassMember) != null);
    }
}
