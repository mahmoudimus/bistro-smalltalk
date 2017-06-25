//====================================================================
// Table.java
//====================================================================
package smalltalk.compiler.scope;

import java.util.*;
import java.util.stream.Collectors;
import smalltalk.compiler.element.*;

/**
 * Maintains an ordered and indexable collection of local variables as a symbol table. Supports symbol lookup by name
 * and writes variables in the order of their definition.
 *
 * @author Copyright 1999,2017 Nikolas S. Boyd. All rights reserved.
 */
public class Table extends Container {

    /**
     * Maintains an indexed collection of symbols.
     */
    protected Map<String, Variable> contents = new HashMap();

    /**
     * Maintains symbols in the order of their definition.
     */
    protected List<Variable> order = new ArrayList();

    /**
     * Constructs a new Table.
     * @param container a container
     */
    public Table(Container container) {
        super(container);
    }

    /**
     * Establishes (container) as the container for this scope.
     *
     * @param container the container scope.
     */
    @Override
    public void container(Container container) {
        super.container(container);
        for (Variable symbol : symbols()) {
            symbol.container(container);
        }
    }

    public Table withAll(Table table) {
        for (Variable v : table.order) {
            addSymbol(v);
        }
        return this;
    }

    /**
     * Creates a new, uniquely named variable.
     *
     * @return the name of the newly created variable.
     */
    public String createSymbol() {
        int nest = container().nestLevel();
        int size = size();
        String name = "local" + nest + "_" + size;
        Variable v = new Variable(container());
        v.name(name);
        v.clean();
        addSymbol(v);
        return name;
    }

    /**
     * Collects a named (symbol) for the purpose of later lookup.
     *
     * @param symbol a locally defined symbol.
     */
    protected boolean captureSymbol(Variable symbol) {
        if (!symbol.isEmpty()) { // && !symbol.isSystemic()) {
            contents.put(symbol.name(), symbol);
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Adds a new (empty) symbol to the table.
     */
    public void prepareSymbol() {
        if (!order.isEmpty()) {
            // first capture any existing symbol
            captureSymbol(currentSymbol());
        }
        order.add(new Variable(container));
    }

    /**
     * Adds a new (symbol) to the table.
     *
     * @param symbol a named symbol.
     */
    public void addSymbol(Variable symbol) {
        if (captureSymbol(symbol)) {
            order.add(symbol);
        }
    }

    /**
     * Returns the current symbol being defined by the compiler.
     *
     * @return the current symbol being defined by the compiler.
     */
    public Variable currentSymbol() {
        if (order.isEmpty()) {
            order.add(new Variable(container));
        }

        return order.get(order.size() - 1);
    }

    /**
     * Returns whether the local symbol table contains the named symbol.
     *
     * @param name the name of the desired symbol.
     * @return whether the local symbol table contains the named symbol.
     */
    public boolean containsSymbol(String name) {
        return contents.containsKey(name);
    }

    /**
     * Returns the named symbol.
     *
     * @param name the name of the desired symbol.
     * @return the named symbol, or null if the table does not contain the named symbol.
     */
    public Variable symbolNamed(String name) {
        return (Variable) contents.get(name);
    }

    /**
     * Cleans out any residue left from the parsing process and prepares the receiver for code generation.
     */
    @Override
    public void clean() {
        super.clean();
        if (order.isEmpty()) {
            return;
        }

        Variable end = order.get(order.size() - 1);
        if (end.isEmpty()) {
            order.remove(end);
        }

        for (Variable symbol : symbols()) {
            symbol.clean();
        }
    }

    /**
     * Removes all the symbols from the table.
     */
    public void clear() {
        contents.clear();
        order.clear();
    }

    /**
     * Returns the size of the symbol table.
     *
     * @return the size of the symbol table.
     */
    public int size() {
        return contents.size();
    }

    /**
     * Enumerates the symbols in the order they were defined.
     *
     * @return an ordered collection of symbols.
     */
    public List<Variable> symbols() {
        return new ArrayList(order);
    }

    public List<Variable> definedSymbols() {
        return order.stream().filter(v -> !v.isEmpty()).collect(Collectors.toList());
    }

    /**
     * Returns whether any of the symbols has a resolved type.
     *
     * @return whether any of the symbols has a resolved type.
     */
    public boolean hasTypedNames() {
        for (Variable symbol : symbols()) {
            if (!symbol.type().equals(Base.RootClass)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns whether all of the symbols have erasable types.
     *
     * @return whether all of the symbols have erasable types.
     */
    public boolean hasErasableTypes() {
        for (Variable symbol : symbols()) {
            String typeName = symbol.resolvedTypeName();
            if (typeName.isEmpty()) return false;
            if (PrimitiveWrappers.containsKey(typeName)) {
                // some primitive types are erasable
            } else if (ObjectWrappers.containsKey(typeName)) {
                // some elementary object types are erasable
            } else {
                Face typeFace = Face.named(typeName);
                if (typeFace == null) return false;
                if (!typeFace.isEraseable()) return false;
            }
        }
        return true;
    }

    /**
     * Copies the symbols from the receiver to (aTable) without types.
     *
     * @param aTable a symbol table.
     */
    public void eraseTypes(Table aTable) {
        for (Variable symbol : symbols()) {
            Variable newSymbol = Variable.named(symbol.name(), "", aTable.container());
            aTable.addSymbol(newSymbol);
        }
    }

    /**
     * Returns whether any of the symbols resolve to elementary type.
     *
     * @return whether any of the symbols resolve to elementary type.
     */
    public boolean hasElementaryNames() {
        for (Variable symbol : symbols()) {
            String typeName = symbol.type();
            if (Reference.isElementary(typeName)) {
                return true;
            }
            Face typeFace = Face.named(symbol.type());
            if (typeFace != null && typeFace.isElementary()) {
                return true;
            }
        }
        return false;
    }
}
