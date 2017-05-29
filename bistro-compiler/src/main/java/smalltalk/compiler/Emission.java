//====================================================================
// Emission.java
//====================================================================
package smalltalk.compiler;

import java.io.IOException;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;
import org.stringtemplate.v4.STWriter;

/**
 * Emits generated Java code using StringTemplate v4.
 * @author Copyright 1999,2017 Nikolas S. Boyd. All rights reserved.
 */
public class Emission {

    public static final String Items = "items";
    public static final String Item = "item";
    public static final String Values = "values";
    public static final String Value = "value";
    public static final String Notes = "notes";

    private static final String Empty = "";
    private static final String Emit = "emit";

    private static final ST None = null;
    private static final String CodeFile = "CodeTemplates.stg";
    private static final STGroupFile CodeGroup = new STGroupFile(CodeFile);

    private ST builder;

    private Logger getLogger() {
        return LoggerFactory.getLogger(getClass());
    }

    /**
     * Indicates whether a list is empty.
     * @param list a list
     * @return whether the list is empty
     */
    public static <ItemType> boolean isEmpty(List<ItemType> list) {
        return (list == null || list.isEmpty());
    }

    /**
     * Capitalizes a name.
     * @param name a name
     * @return a capitalized name
     */
    public static String capitalize(String name) {
        if (name == null || name.length() == 0) return Empty;
        StringBuilder buffer = new StringBuilder(name.trim());
        buffer.setCharAt(0, Character.toUpperCase(buffer.charAt(0)));
        return buffer.toString();
    }

    public static Emission emit(String name) {
        return named(Emit + name);
    }


    /**
     * Returns a new Emission.
     * @param groupName a group name
     * @return a new Emission
     */
    public static Emission named(String groupName) {
        Emission result = new Emission();
        result.builder = CodeGroup.getInstanceOf(groupName);
        return result;
    }


    public Emission notice(String notice) {
        return this.with("notice", notice);
    }

    public Emission comment(String comment) {
        return this.with("comment", comment);
    }

    public Emission packageName(String packageName) {
        return this.with("packageName", packageName);
    }

    public Emission signature(ST signature) {
        return this.with("signature", signature);
    }

    public Emission type(String type) {
        return this.with("type", type);
    }

    public Emission base(ST base) {
        return this.with("base", base);
    }

    public Emission details(ST details) {
        return this.with("details", details);
    }

    public Emission notes(List<ST> notes) {
        return this.with(Notes, notes);
    }

    public Emission notes(ST notes) {
        return this.with(Notes, notes);
    }

    public Emission names(List<String> names) {
        if (isEmpty(names)) return this;
        return this.with("names", names);
    }

    public Emission name(String name) {
        return this.with("name", name);
    }

    public Emission name(Emission value) {
        return this.with("name", value == null ? None : value.result());
    }

    public Emission values(List<ST> values) {
        return this.with(Values, values);
    }

    public Emission values(ST values) {
        return this.with(Values, values);
    }

    public Emission value(Emission value) {
        return this.value(value == null ? None : value.result());
    }

    public Emission value(ST value) {
        return this.with(Value, value);
    }

    public Emission value(String value) {
        return this.with(Value, value);
    }

    public Emission items(List<Emission> items) {
        return this.with(Items, items);
    }

    public Emission items(ST items) {
        return this.with(Items, items);
    }

    public Emission item(ST item) {
        return this.with(Item, item);
    }

    public Emission item(String item) {
        return this.with(Item, item);
    }

    public <ItemType> Emission with(String name, List<ItemType> items) {
        if (isEmpty(items)) {
            builder.add(name, new ArrayList());
            return this;
        }

        if (items.get(0) instanceof Emission) {
            builder.add(name, new ArrayList());
            items.stream().forEach(item -> this.with(name, (Emission)item));
        }
        else {
            builder.add(name, items);
        }

        return this;
    }

    public Emission with(String name, Emission e) {
        return this.with(name, e == null ? None : e.result());
    }

    public Emission with(String name, ST item) {
        builder.add(name, item);
        return this;
    }

    public Emission with(String name, String item) {
        builder.add(name, item);
        return this;
    }

    public ST result() {
        return this.builder;
    }

    public void write(STWriter writer) {
        try {
            result().write(writer);
        }
        catch (IOException ex) {
            getLogger().error(ex.getMessage(), ex);
        }
    }
}
