//====================================================================
// Container.java
//====================================================================
package smalltalk.compiler.element;

import java.util.List;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;

import smalltalk.compiler.Emission;
import static smalltalk.compiler.Emission.Items;
import static smalltalk.compiler.Emission.emit;

/**
 * Provides general nesting of containers.
 *
 * @author Copyright 1999,2016 Nikolas S. Boyd. All rights reserved.
 */
public class Container extends Base {

    /**
     * Defines an interface for visiting instances.
     */
    public static interface Visitor {

        public void visit(Container element);
    }

    /**
     * Refers to the container for this container.
     */
    protected Container container;

    /**
     * Constructs a new Container.
     *
     * @param aContainer the container that surrounds this one.
     */
    public Container(Container aContainer) {
        container = aContainer;
    }

    /**
     * Constructs a new Container.
     */
    public Container() {
        this(null);
    }

    /**
     * Returns the container for this scope.
     *
     * @return the container for this scope.
     */
    public Container container() {
        return container;
    }

    /**
     * Establishes (aContainer) as the container for this scope.
     *
     * @param aContainer the container scope.
     */
    public void container(Container aContainer) {
        container = aContainer;
    }

    /**
     * Returns the nest level for this scope.
     *
     * @return the nest level for this scope.
     */
    public int nestLevel() {
        if (container == null) {
            return 0;
        }
        return container.nestLevel() + 1;
    }

    /**
     * Returns the tab count for this scope.
     *
     * @return the tab count for this scope.
     */
    public int tabCount() {
        return nestLevel() - 1;
    }

    /**
     * Returns a revised identifier derived from the supplied (identifier). Certain derived classes override this
     * method.
     *
     * @param identifier identifies a named entity.
     * @return a revised identifier derived from the supplied (identifier).
     */
    public String revised(String identifier) {
        return identifier;
    }

    /**
     * Returns the type of the variable to which a (reference) resolves.
     *
     * @param reference refers to a variable in some compiler scope.
     * @return the type of the variable to which a (reference) resolves.
     */
    public Class resolveType(Reference reference) {
        return (container == null) ? null : container.resolveType(reference);
    }

    /**
     * Returns the type name of the variable to which a (reference) resolves.
     *
     * @param reference refers to a variable in some compiler scope.
     * @return the type name of the variable to which a (reference) resolves.
     */
    public String resolveTypeName(Reference reference) {
        return (container == null) ? null : container.resolveTypeName(reference);
    }

    /**
     * Returns whether the container can resolve a symbolic (reference). Derived classes may override this method.
     *
     * @param reference a symbolic reference to be resolved.
     * @return whether the container can resolve a symbolic (reference).
     */
//    public boolean resolves(Reference reference) {
//        return false;
//    }

    /**
     * Returns the container that resolves a symbolic (reference). Derived classes may override this method.
     *
     * @param reference a symbolic reference to be resolved.
     * @return the container that resolves a symbolic (reference).
     */
//    public Container scopeResolving(Reference reference) {
//        return (this.resolves(reference) ? this
//                : container.scopeResolving(reference));
//    }

    /**
     * Resolves an undefined (reference) if appropriate. This implementation does nothing. Some derived class should
     * override this method.
     *
     * @param reference a symbolic reference to be resolved.
     */
    public Variable resolveUndefined(Reference reference) {
        return null; // does nothing
    }

    /**
     * Releases the container of this object. Derived classes may override this method.
     */
    public void release() {
        if (container != null) {
            container.release();
        }
        container = null;
    }

    /**
     * Returns the name of a (component) as known by this container.
     *
     * @param component a class or interface.
     * @return the name of a (component) as known by this container.
     */
    public String nameOf(Container component) {
        return component.name();
    }

    /**
     * Returns the name of this container.
     *
     * @return the name of this container.
     */
    public String name() {
        return null;
    }

    /**
     * Returns whether this face is a metaface.
     *
     * @return whether this face is a metaface.
     */
    public boolean isMetaface() {
        return false;
    }

    /**
     * Returns whether this face has a metaface.
     *
     * @return whether this face has a metaface.
     */
    public boolean hasMetaface() {
        return false;
    }

    /**
     * Returns whether this container has facial characteristics.
     *
     * @return whether this container has facial characteristics.
     */
    public boolean isFacial() {
        return false;
    }

    /**
     * Returns whether this container is a method scope.
     *
     * @return whether this container is a method scope.
     */
    public boolean isMethod() {
        return false;
    }

    public boolean isBlock() {
        return false;
    }

    /**
     * Returns whether this container has primitive available.
     *
     * @return whether this container has primitive available.
     */
    public boolean hasPrimitiveFactory() {
        return false;
    }

    /**
     * Returns whether this container needs magnitudes defined.
     *
     * @return whether this container needs magnitudes defined.
     */
    public boolean needsMagnitudes() {
        return false;
    }

    /**
     * Returns whether this container needs collections defined.
     *
     * @return whether this container needs collections defined.
     */
    public boolean needsCollections() {
        return false;
    }

    /**
     * Converts this container to a scope type.
     * @param <ScopeType> a scope type
     * @param scopeType a scope type
     * @return a ScopeType
     */
    public <ScopeType extends Container> ScopeType asScope(Class<ScopeType> scopeType) {
        return (ScopeType)this;
    }

    /**
     * Returns the facial scope that contains this object.
     *
     * @return the facial scope that contains this object.
     */
    public Container facialScope() {
        return (container == null) ? null : container.facialScope();
    }

    /**
     * Returns the file scope that contains this object.
     *
     * @return the file scope that contains this object.
     */
    public Container fileScope() {
        return (container == null) ? null : container.fileScope();
    }

    public TokenStream tokenStream() {
        return (fileScope() == null) ? null : fileScope().tokenStream();
    }

    /**
     * Accepts a visitor for inspection of the receiver.
     *
     * @param aVisitor visits the receiver for its information.
     */
    public void acceptVisitor(Visitor aVisitor) {
        aVisitor.visit(this);
    }

    public String commentFrom(CommonTree node) {
        Token hidden = hiddenToken((CommonTree) node);
        return (hidden == null ? null : hidden.getText());
    }

    public Token hiddenToken(CommonTree node) {
        if (node == null) {
            return null;
        }
        return hiddenToken(node.getToken());
    }

    public Token hiddenToken(Token token) {
        if (token == null || tokenStream() == null) {
            return null;
        }
        Token candidate = tokenStream().get(token.getTokenIndex() - 1);
        return (candidate.getChannel() == Token.HIDDEN_CHANNEL ? candidate : null);
    }


    // emissions
    public Emission emitEmpty() {
        return emit("Empty");
    }

    public Emission emitNull() {
        return emit("Null");
    }

    public Emission emitNil() {
        return emit("Nil");
    }

    public Emission emitModifiers() {
        return emitEmpty(); // override this!
    }

    public Emission emitOptimized() {
        return emitEmpty(); // override this!
    }

    public Emission emitPrimitive() {
        return emitEmpty(); // override this!
    }

    public Emission emitItem() {
        return emitEmpty(); //override this!
    }

    public Emission emitItem(String value) {
        return emit("Item").item(value == null ? EmptyString : value);
    }

    public Emission emitTerm(Operand operand) {
        return emitTerm(emitOperand(operand));
    }

    public Emission emitTerm(Emission e) {
        return emit("Term").value(e.result());
    }

    public Emission emitTerm(String value) {
        return emit("Term").value(value);
    }

    public Emission emitComposite(Emission base, Emission path) {
        return emit("Composite").with("base", base).with("path", path);
    }

    public Emission emitSequence(List<String> items) {
        return emit("Sequence").with(Items, items);
    }

    public Emission emitList(List<Emission> items) {
        return emit("List").items(items);
    }

    public Emission emitLines(List<Emission> items) {
        return emit("Lines").items(items);
    }

    public <T extends Operand> Emission emitOperand(T operand) {
        // should be resolved by (operand) type refinement.
        return operand.emitOperand();
    }

    public Emission emitCast(String typeName, Emission value) {
        return emit("Cast").type(typeName).value(value);
    }

    public Emission emitTrueGuard(Emission value) {
        return emit("TrueGuard").value(value);
    }

    public Emission emitFalseGuard(Emission value) {
        return emit("FalseGuard").value(value);
    }

    public Emission emitTypeName(String typeName) {
        // simplify the root class name whenever possible
        return emitItem(RootClass.equals(typeName) ? SimpleRoot : typeName);
    }

    public Emission emitStatement(Emission value) {
        return emit("Statement").value(value);
    }

    public Emission emitClosureValue(Emission aBlock) {
        return emit("ClosureValue").with("closure", aBlock);
    }

    public Emission emitResult(Emission value) {
        return emit("Result").value(value);
    }
}
