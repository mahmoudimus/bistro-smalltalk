//====================================================================
// Scalar.java
//====================================================================
package smalltalk.compiler.constant;

import smalltalk.compiler.Emission;
import static smalltalk.compiler.Emission.emit;
import smalltalk.compiler.element.Container;

/**
 * Represents a scalar constant and translates it into Java.
 *
 * @author Copyright 1999,2017 Nikolas S. Boyd. All rights reserved.
 */
public abstract class Scalar extends Constant {

    /**
     * Contains the scalar value as a string.
     */
    java.lang.String value;

    /**
     * Constructs a new Scalar.
     *
     * @param container the container that surrounds this one.
     */
    public Scalar(Container container) {
        this(container, EmptyString);
    }

    /**
     * Constructs a new Scalar.
     *
     * @param container the container that surrounds this one.
     * @param aString the scalar value.
     */
    public Scalar(Container container, java.lang.String aString) {
        super(container);
        value = aString;
    }

    /**
     * Describes the receiver, esp. for instrumentation purposes.
     * @return a description
     */
    @Override
    public String description() {
        return getClass().getName() + " = " + rawValue();
    }

    /**
     * Establishes the value of the scalar.
     *
     * @param aString the scalar value.
     */
    public void value(java.lang.String aString) {
        value = aString;
    }

    /**
     * Returns the translated Java code for the scalar.
     *
     * @return the translated Java code for the scalar.
     */
    public java.lang.String encodedValue() {
        return value;
    }

    /**
     * Returns the raw value for the scalar.
     *
     * @return the raw value for the scalar.
     */
    public java.lang.String rawValue() {
        return value;
    }

    /**
     * Returns the primitive type of this scalar.
     *
     * @return the primitive type of this scalar.
     */
    public abstract Class primitiveType();

    /**
     * Returns the name of the primitive factory used to instantiate this scalar.
     *
     * @return the name of the primitive factory used to instantiate this scalar.
     */
    public abstract java.lang.String primitiveFactoryName();

    /**
     * Returns the declared type of the value represented by the scalar.
     *
     * @return the declared type of the value represented by the scalar.
     */
    public java.lang.String declaredType() {
        return primitiveFactoryName();
    }

    /**
     * Returns the type of this constant reference.
     *
     * @return the type of this constant reference.
     */
    @Override
    public Class resolvedType() {
        try {
            return Class.forName(resolvedTypeName());
        } catch (Throwable ex) {
            return java.lang.Object.class;
        }
    }

    public Emission emitScalar() {
        return emit("Scalar")
                .value(encodedValue())
                .with("factory", primitiveFactoryName());
    }

    @Override
    public Emission emitOperand() {
        return emitScalar();
    }

    @Override
    public Emission emitPrimitive() {
        return emitItem(encodedValue());
    }
}
