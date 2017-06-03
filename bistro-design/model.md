### Language Model ###

Traditionally, [Smalltalk][stimps] systems were built in the context of an object memory _image_.
Smalltalk class definitions loaded from a source file were _interpreted_ in the context of the image. 
While an image-based development environment contributes significantly to the agility of Smalltalk's
integrated suite of tools, it introduces some difficulties for code, component, and system configuration 
management in large development projects. 

A declarative, file based programming model makes it much easier to use existing commercial version 
control and configuration management tools. 
The Java language is file based, both for its source code **.java** files and its executable binary **.class** files. 
The Java platform also supports the deployment of class libraries as files in archival form (as **.zip, .jar**, etc.).
In order to leverage these features of the Java platform, Bistro utilizes a _declarative_ language model 
for its source code **.bist** files. 
The current Bistro compiler performs source-to-source translation, from Bistro to Java. 
Then, a Java compiler can be used to compile the intermediate Java source code into Java class files.

The overall source code structure of each Bistro class resembles that found in Java, 
including the location of classes with respect to their packages. 
However, the specific syntax used to declare a Bistro class resembles that found in Smalltalk. 
The following code example provides a general template for Bistro class definitions.

```smalltalk
"Identify the package for this class."
package: smalltalk.example;

"Make all the standard collection classes visible."
import: smalltalk.collection.*;

"Define a new class and its metaclass."
Superclass subclass: Subclass 
metaclass: [ "..." ]
class: [ "..." ]
```

The example above shows only one of several possible kinds of Bistro definitions.
Here are some more example templates.

**Type Definitions**

```smalltalk
nil subtype: BaseType 
metatype: [ "..." ]
type: [ "..." ]
```

```smalltalk
SuperType subtype: Subtype 
metatype: [ "..." ]
type: [ "..." ]
```

**Root Class Definitions**

```smalltalk
nil subclass: BaseClass 
metaclass: [ "..." ]
class: [ "..." ]
```

```smalltalk
nil subclass: BaseClass 
implements: Type
metaclass: [ "..." ]
class: [ "..." ]
```

**Subclass Definitions**

```smalltalk
Superclass subclass: Subclass 
metaclass: [ "..." ]
class: [ "..." ]
```

```smalltalk
Superclass subclass: Subclass 
implements: Type
metaclass: [ "..." ]
class: [ "..." ]
```

#### Name Spaces ####

The absence of a name space mechanism is one of the major deficiencies of most commercial Smalltalk environments. 
The absence of name spaces allows class naming conflicts to arise, especially when integrating 
large class libraries from independent software development organizations, e.g., 3rd party vendors. 
While some name space models have been proposed for Smalltalk, none has yet been widely adopted, 
and no standard exists.

Luckily, the Java language model supports the name space concept with packages. 
Packages organize Java classes into logical partitions that serve as name spaces for classes. 
This helps system designers prevent potential class naming conflicts. 
Java class files are physically organized into package directories in a file system. 
So in Java, there's a direct correspondence between these logical and physical organizations.

#### Classes and Metaclasses ####

Bistro classes are translated into Java classes. 
Bistro class member variables and methods become Java class variables and methods. 
However, Smalltalk has first-class metaclasses, while Java does not. 
Bistro implements its metaclasses by splitting each class definition into two parts - one Java class for the 
Bistro class methods and variables, and a separate Java class for the Bistro metaclass methods and variables. 
Putting Bistro metaclass methods and variables in another Java class allows the metaclass hierarchy to 
parallel the class hierarchy. 
This provides inheritance and polymorphism for metaclasses like that found in Smalltalk.

Each Bistro metaclass is implemented as a **public static** class nested within an outer Java class. 
The nested class metaclass name is always **mClass**. 
Then, each class has a **public static** member **$class** that refers to the sole instance of its singleton metaclass. 
Note however, that because metaclasses are singletons, they do not support the definition of **abstract** methods. 
The following list shows the parallels between a few selected classes and their metaclasses.

```
smalltalk.behavior.Object
smalltalk.behavior.Object.mClass

smalltalk.collection.OrderedCollection
smalltalk.collection.OrderedCollection.mClass

smalltalk.geometry.Point
smalltalk.geometry.Point.mClass
```

The following diagram shows the full inheritance hierarchy for some of the essential behavior classes. 

![Classes and Metaclasses][diagram]

Note each class in the above diagram has a **static** link to its metaclass **$class**. 
However, these links must be resolved (by instantiation) after the inheritance links have been established 
during compilation. 
Making each metaclass a **public static** nested class enables this. 
Also note, the metaclass **$class** links of all metaclasses refer a _singleton_ **public static** instance of the 
class **smalltalk.behavior.Metaclass**. 

All root classes, those derived from **nil**, have inheritance and metaclass structures like that of 
**smalltalk.behavior.Object**. 
A Bistro class can also be derived from a Java class. 
In this case, the inheritance and metaclass structures of the generated classes also look like that of 
**smalltalk.behavior.Object**.

#### Types and Metatypes ####

Support for defining interfaces is one of the more powerful and innovative features introduced by Java. 
Interfaces provide a language mechanism for defining polymorphic behavior independent of inheritance. 
Bistro supports the definition and use of interfaces as types. 
However, because Smalltalk supports first-class metaclasses and full metaprogramming, 
Bistro takes a further step by coupling each type with a metatype. 
This gives software designers the ability to coordinate polymorphic design and meta-design.

Each Bistro type (and its metatype) is translated into a pair of Java interface definitions. 
Each metatype interface is defined as a nested interface of its corresponding type interface, 
similar to the relationship between a class and its metaclass. 
As with Java interfaces, Bistro supports type inheritance. 
As with classes and metaclasses, the metatype inheritance structure parallels the type inheritance structure. 
When a Bistro class implements a type, the metaclass also implements the corresponding metatype. 
Also, when a Bistro class implements a Java interface, a metatype does not exist. 
So then, the metaclass does _not_ implement a metatype.

#### Library Types and Classes ####

[ANSI Smalltalk][ansi] defines the message protocols for the standard Smalltalk types. 
These types are supported by the built-in Bistro library classes. 
The library classes have been organized as follows:

| Name Space    | Contents  |
|---------------|-----------|
| Smalltalk Core         | Root ANSI Types  |
| Smalltalk Blocks       | ANSI Valuables and Block Types |
| Smalltalk Magnitudes   | ANSI Values and Magnitudes |
| Smalltalk Collections  | ANSI Collection Types |
| Smalltalk Streams      | ANSI Stream Types |
| Smalltalk Exceptions   | ANSI Exception Types |
| Bistro Behaviors       | Root Classes |
| Bistro Magnitudes      | Value and Magnitude Classes |
| Bistro Geometry        | Geometry Classes |
| Bistro Collections     | Collection Classes |
| Bistro Streams         | Stream Classes |
| Bistro Tests           | ANSI Standard API Tests |

#### Access Controls and Decorations ####

Access controls play an important role in defining contracts in object-oriented designs. 
Like Java, Bistro provides access controls on classes, types (interfaces), methods and variables. 
Bistro supports the Java reserved words that specify access control, including **public, protected, private**. 
While each class, method and variable may be declared with any of these three access controls, 
Bistro uses the common Smalltalk conventions for default access when no explicit control has been supplied. 
By default, Bistro classes and types are **public**, methods are **public**, and variables are **protected**. 
Also, Bistro metaclasses and metatypes are always **public**. 
All access controls are enforced at runtime by the JVM.

```
"Define a static constant."
protected static final Zero := 0.

"Define a private synchronized method."
private synchronized syncMethod: argument [ "..." ]

"Define a protected final method."
protected final finalMethod: argument [ "..." ]

"Define a public abstract method."
abstract abstractMethod []

"Define a public native method."
native nativeMethod: argument []

"Define a public static main method."
static (void) main: arguments (String[]) [ "..." ]
```

Notice that both **abstract** and **native** method declarations must have empty blocks because their 
implementations cannot be specified. 
An **abstract** method must be implemented in the subclasses of the class in which it is defined. 
A **final** method cannot be implemented (overridden) in subclasses. 
A **native** method must be implemented using a Java Native Interface (JNI). 
A **static** method is not polymorphic like the methods defined in a metaclass. 
However, a **static** method can be used to define the main entry point for a console application.

#### Scopes ####

Smalltalk methods and blocks is very similar. 
Both contain a series of statements, and possibly a final result. 
The primary difference is that blocks are delimited with square brackets **[ ]**, while Smalltalk methods are not. 
In order to normalize its syntax, Bistro uses square brackets as scope delimiters throughout. 
Thus, the declaration of Bistro classes, types, methods, and blocks are all delimited with square brackets **[ ]**.

#### Comments ####

The Bistro compiler translates quoted class and method comments into Java class and method comments. 
This way, standard Java tooling can be used to generate documentation from the resulting Java source files.

#### Variables ####

Bistro mixes some language features from both Smalltalk and Java. 
Bistro's design attempts to achieve a balance between the features and limitations of both languages. 
The declaration of variables is an area that presents such a set of design trade-offs. 

Smalltalk has a special syntax for declaring all variables before the statements of a method or a block scope. 
Java allows variables to be declared and initialized directly within the body of a scope. 
In balance, the later feature is preferable because it makes code maintenance easier. 
To simplify its structure, Bistro eliminates the need to declare local variables in a separate 
section at the beginning of each block or method. 

Java requires that every variable declaration include a type specification, while Smalltalk has no type 
specifications. 
Both are important options, so Bistro allows both. 
Bistro variable and argument type specifications are optional. 
Making types optional imposes the need to forbid variable re-declaration. 

Some of these choices have implications for other design choices, such as whether a variable from an 
outer scope can be overridden by an inner scope. 
The following table compares these features and limitations with respect to variable declarations, 
and indicates what Bistro supports.

| Feature  | Smalltalk | Bistro | Java |
|----------|-----------|--------|------|
| Local variables must be declared in a special section | yes | no | no |
| Variables must be declared before usage | yes | yes | yes |
| Inner scopes may reference variables in outer scopes | yes | yes | yes |
| Inner scopes may override variables in outer scopes  | no  | no  | yes |
| Variable declarations support initializations        | no  | yes | yes |
| Variable type declarations are ... | unsupported | optional | required |

As with Java, Bistro variables can be declared anywhere in a scope, so long as they are declared before they are used, 
typically with an initial value. 

Given that some types are declared or discoverable, the Bistro compiler optimizes messages sent to typed variables, 
including sends to **self** and **super**. 
For untyped variables and expression results, the compiler automatically generates dynamic message sends 
using an appropriate **perform:** message.

Copyright 1999,2017 Nikolas S. Boyd. All rights reserved.

[diagram]: diagram.gif "Classes and Metaclasses"
[stimps]: http://en.wikipedia.org/wiki/Smalltalk#List_of_implementations "Smalltalk Implementations"
[ansi]: https://github.com/nikboyd/owl-labs/raw/master/design/ANSI-X3J20-1.9.pdf

