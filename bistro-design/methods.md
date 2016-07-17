### Bistro Methods ###

Bistro methods are very similar to those of Smalltalk, although those of Bistro are delimited with square brackets **[ ]**. 

```
"a sample normalized Bistro method"
normalMethod: argument
[ "... statements ..."
]
```

Bistro also allows primitive methods to be written directly in Java using curly brace delimiters **{ }**. 

```
"A sample primitive Java method"
primitiveMethod: argument
{ // ... statements ...
}
```

Both kinds of methods use signatures similar to those found in Smalltalk. 
During translation, the Bistro compiler copies the Java statements inside primitive methods over to the 
generated Java verbatim.

#### Interoperability ####

To achieve seamless integration between Bistro and Java, their methods must be interoperable. 
Bistro code must be able to invoke methods in existing Java classes, and Java code must be able to 
invoke methods in Bistro classes. 
To achieve such interoperability, the Bistro compiler renames methods using some conventions. 
Special consideration must be given to both binary operators and keyword selectors.

Bistro supports the standard binary operators provided by Smalltalk. 
These are renamed using the conventions shown in the table below. 
The Bistro compiler includes a prefix **$** in the translated method name to prevent confusion 
with existing Java protocols. 

The table below also shows the renaming conventions for Smalltalk keyword selectors. 
The colon in each keyword is replaced by an underscore. 
The final colon(s) (if any) is dropped. 

Bistro also supports the use of colons as argument separators, serving as anonymous keywords 
like those found in block signatures. 
These are dropped during translation, which allows Bistro to support the definition and invocation of Java 
methods that take more than one argument (see the map **put:** example below).

| Bistro  | Java   |
|---------|--------|
| bool & aBoolean          | bool.$and(aBoolean)  |
| bool ┃ aBoolean          | bool.$or(aBoolean)  |
|  |  |
| number **+** aNumber         | number.$plus(aNumber)  |
| number **-** aNumber         | number.$minus(aNumber)  |
| number * aNumber             | number.$times(aNumber)  |
| number **/** aNumber         | number.$divided(aNumber)  |
| number **//** aNumber        | number.$quotient(aNumber)  |
| number **\\** aNumber        | number.$remnant(aNumber)  |
| number **\\\\** aNumber      | number.$remainder(aNumber)  |
| number **%** aNumber         | number.$modulo(aNumber)  |
|  |  |
| value **=** aValue           | value.$equal(aValue)  |
| value **~=** aValue          | value.$notEqual(aValue)  |
| value **==** aValue          | value.$same(aValue)  |
| value **~~** aValue          | value.$notSame(aValue)  |
| value **<** aValue           | value.$lessThan(aValue)  |
| value **<=** aValue          | value.$lessEqual(aValue)  |
| value **>** aValue           | value.$moreThan(aValue)  |
| value **>=** aValue          | value.$moreEqual(aValue)  |
|  |  |
| map **get:** key             | map.get(key)  |
| map **put:** key **:** value | map.put(key, value)  |
| dictionary **at:** key **put:** value | dictionary.at_put(key, value)  |
|  |  |
| list **,** element           | list.$append(element)  |
| x **@** y                    | x.$at(y)  |


#### Dynamic Method Resolution ####

The lack of pervasive type declarations in [Smalltalk][smalltalk] contributes to its simplicity and agility. 
Software prototyping is much easier without type specifications littered throughout the code. 
Specifying types pervasively in code during development requires much more time. 
Also, static type information simply makes the resolution of method implementations safer and more efficient.

[Bistro][bistro] uses [reflection][reflect] to resolve unknown method implementations at runtime.
The Bistro runtime caches the resolved method references to speed up dynamic method invocations. 
While this allows Bistro to mimic Smalltalk, and makes its dynamic method invocations work,
this approach also has both space (memory) and time (performance) costs.

With the advent of support in Java for dynamic method invocation in both the VM and the runtime library,
it's now feasible to use those capabilities in Hoot. 
The Hoot compiler and runtime cooperate and use dynamic method invocation of the underlying host platform, 
be it Java or C#. 
Hoot also uses this mechanism to implement the standard Smalltalk **perform:** operations.
Unimplemented methods discovered at runtime produce **MessageNotUnderstood** exceptions, just like Smalltalk.

As mentioned [elsewhere][optional], Hoot supports optional typing.
The Hoot compiler attempts to resolve the type of each message receiver. 
For message primaries such as constants, variables and arguments, the compiler resolves 
an inferred type (for constants) or a declared type (for variables and arguments). 
For nested message expressions, the compiler attempts to resolve the type of each nested message result. 
Where such type resolution is possible, the compiler determines whether the receiver implements 
the message selector using the Java reflection facility. 

If the compiler can locate an appropriate method during translation, the compiler generates a 
direct method invocation against the receiver rather than a dynamic method resolution using 
one of the **perform:** selectors. 
Whenever the compiler can generate a direct method invocation, it also remembers the result 
type of the invoked method. 
This gives it the opportunity to resolve a further method invoked on the message result in the 
case of nested message expressions.


#### Native and Abstract Methods ####

Bistro also provides the ability to indicate that a method is **native** or **abstract**. 
Both kinds of methods must have an empty method body **[]**, as their implementations are provided elsewhere.
**abstract** methods must be implemented in a derived class.
**native** methods must be implemented as external functions, and made available through Java Native Interface (JNI)
bindings.

Copyright 1999,2016 Nikolas S. Boyd. All rights reserved.

[smalltalk]: http://en.wikipedia.org/wiki/Smalltalk "Smalltalk"
[reflect]: http://docs.oracle.com/javase/7/docs/api/java/lang/reflect/package-summary.html "Java Reflection"
