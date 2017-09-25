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
 * @author Copyright 1999,2017 Nikolas S. Boyd. All rights reserved.
 */
public class ObjectArray extends LiteralArray {

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

    @Override
    public Emission emitOperand() {
        return emit("ArrayWith").with(Values,
                    contents().stream()
                        .map(item -> item.emitOperand())
                        .collect(Collectors.toList()));
    }
}
