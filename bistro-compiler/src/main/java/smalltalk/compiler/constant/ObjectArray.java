//====================================================================
// ObjectArray.java
//====================================================================
package smalltalk.compiler.constant;

import java.util.*;
import java.util.stream.Collectors;
import smalltalk.compiler.element.*;
import smalltalk.compiler.Emission;
import static smalltalk.compiler.Emission.Values;
import static smalltalk.compiler.Emission.emit;

/**
 * Represents and encodes a literal object array.
 *
 * @author Copyright 1999,2016 Nikolas S. Boyd. All rights reserved.
 */
public class ObjectArray extends LiteralArray {

    /**
     * Defines an interface for visiting instances.
     */
    public static interface Visitor {

        public void visit(ObjectArray array);
    }

    /**
     * Contains the array elements.
     */
    List<Constant> contents = new ArrayList();

    /**
     * Constructs a new ObjectArray.
     *
     * @param container the container that surrounds this one.
     */
    public ObjectArray(Container container) {
        super(container);
    }

    /**
     * Returns the elements of the receiver.
     * @return a list of constants
     */
    public List<Constant> contents() {
        return new ArrayList(contents);
    }

    /**
     * Returns the size of the receiver.
     */
    public int size() {
        return contents.size();
    }

    /**
     * Adds (aConstant) to the array.
     *
     * @param aConstant the additional constant.
     */
    public void add(Constant aConstant) {
        contents.add(aConstant);
    }

    /**
     * Returns an optimized version of the array.
     *
     * @return an optimized version of the array.
     */
    public LiteralArray optimized() {
        return this;
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
     * Accepts a visitor for inspection of the receiver.
     *
     * @param aVisitor visits the receiver for its information.
     */
    @Override
    public void acceptVisitor(Operand.Visitor aVisitor) {
        acceptVisitor((Visitor) aVisitor);
    }

    @Override
    public Emission emitOperand() {
        return emit("NewArray").with(Values, contents().stream()
                .map(item -> item.emitOperand()).collect(Collectors.toList()));
    }
}
