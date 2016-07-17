### Bistro Exceptions ###

While some superficial similarities exist between Smalltalk and Java exception handling mechanisms, 
there are also some fundamental differences. 
So, deciding whether and how to integrate these mechanisms presented one of the more challenging 
problems for the design of Bistro. 
The following table compares the salient aspects of the Smalltalk and Java exception handling mechanisms.

| Language  | Exception Model |
|-----------|-----------------|
| Smalltalk | models exceptions as instances of exception classes: **Exception** and its subclasses |
| Java      | models exceptions as instances of exception classes: **Throwable** and its subclasses |
| | |
| Smalltalk | has no special syntax for dealing with exceptions, only standard message idioms |
| Java      | has syntax for dealing with exceptions: **try { } catch { } finally { }, throw** and **throws** |
| | |
| Smalltalk | provides fine grained control over exception handling |
| Java      | exception handling is strictly stack oriented |
| | |
| Smalltalk | exceptions never impact method signatures |
| Java      | exceptions thrown by a method must be declared in a **throws** clause |

Like Smalltalk, Bistro provides fine grained control over exception handling, including the ability to decide
whether and when stack frames are unwound, and the ability to resume execution at the point of origin, 
where the exception was raised.
In contrast, Java exception handling is strictly stack oriented: once unwound, the stack frames between the point 
of origin and the handler are unavailable.
Given their differences, finding a common ground between Smalltalk and Java exceptions is straightforward, 
but merging the mechanisms completely doesn't really make sense. 

#### Exception Idioms ####

Some of the Smalltalk idioms can easily be mapped to Java. 
For example, the **ZeroArgumentBlock** class provides the standard **ifCurtailed:**, **ensure:**, 
and **on:do:** methods for handling exceptions. 
However, Bistro also provides a new idiom that the compiler maps directly to the Java exception handling mechanism.

```
"evaluate a block and catch any exception throw thereby"
[ "..." ] ifCurtailed:
    [ "... catch any exception ..." ]
```

```
"evaluate a block and always the final block"
[ "..." ] ensure:
    [ "... evaluate finally ..." ]
```

```
"evaluate a block and catch a specific kind of exception"
[ "..." ] on: ExceptionType
    do: [ "... evaluate when exception thrown ..." ]
```

```
"evaluate a block and catch a specific kind of exception"
[ "..." ] on: ExceptionType
    do: [ :ex | "... evaluate when exception thrown ..." ]
```

```
"evaluate a block and catch specific kind(s) of exceptions and ensure final block"
[ "..." ]
    catch: [ :ex (ExceptionType) | "... evaluate when exception thrown ..." ]
    ensure: [ "... evaluate finally ..." ]
```

#### Scopes with Exceptions ####

Finally, Bistro supports the declaration of exceptions thrown by methods and blocks. 
Both method and block signatures can include a **throws:** clause. 
If a Bistro method signature includes a **throws:** clause, the compiler translates it directly into the 
equivalent Java method signature - i.e., the Java method will also include a **throws** clause with the 
same list of exception classes. 
However, block exceptions require special handling by the compiler.
Note that when a block throws an exception, it will be caught and rethrown as an 
**UnhandledJavaException**.

The signatures of the value methods in the block classes (e.g., **ZeroArgumentBlock**) do not include 
exception declarations. 
For this reason, the Bistro compiler automatically generates wrapper methods for blocks that are 
declared to throw exceptions. 
Each generated wrapper method catches any exception thrown by a block and rethrows it as an 
**UnhandledJavaException**. 
Because **UnhandledJavaException** is derived from **RuntimeException**, the Java compiler will not 
complain about the exception erasure. 
Exception erasure is a special kind of type erasure that makes the Java exceptions transparent. 
The following table provides examples of the syntax used to declare method and block exceptions.

```
"a unary method that can throw any kind of exception"
unary ; throws: java.lang.Throwable [ "..." ]

"a binary method that can throw any kind of exception"
binary: argument ; throws: java.lang.Throwable [ "..." ]

"a keyword method that can throw an IOException"
keyword: a1 : a2 ; throws: IOException [ "..." ]

"a block that can throw any exception"
[ throws: java.lang.Throwable | "..." ]

"a block that can throw an IOException"
[ :argument ; throws: IOException | "..." ]
```

Copyright 1999,2016 Nikolas S. Boyd. All rights reserved.

[smalltalk]: http://en.wikipedia.org/wiki/Smalltalk "Smalltalk"
[reflect]: http://docs.oracle.com/javase/7/docs/api/java/lang/reflect/package-summary.html "Java Reflection"
