![Bistro][logo]

### Bistro Smalltalk ###

Bistro is a variation of Smalltalk that integrates the best features of [Smalltalk][smalltalk] and [Java][java].
Bistro Smalltalk is an experimental programming language that runs on the Java Virtual Machine, or [JVM][jvm].

Bistro Smalltalk preserves much of the simplicity, expressiveness and readability of standard Smalltalk. 
Apart from a few minor differences in punctuation, the method syntax of Bistro Smalltalk is almost identical to 
that of standard Smalltalk. 
Thus, Bistro Smalltalk offers software developers the ability to design and build software models with a syntax 
that approaches the expressiveness and readability of natural language.
However, Bistro Smalltalk incorporates several language features from Java to better support their integration.

Bistro Smalltalk provides class libraries that implement the standard Smalltalk protocols for code portability, 
along with optional types, and type erasure, to ease integration with existing host VMs and Java class libraries. 

Unlike many [Smalltalks][stimps], Bistro does not provide its own virtual machine VM. 
Instead, Bistro takes advantage of already existing virtual machines that have matured over the past few decades. 

Also, Bistro does not use image-based persistence for storing its code. 
Rather, like many other programming languages, it uses simple text files. 
This allows developers to use popular tools for editing and source code version control.

#### Platform Requirements ####

Bistro has been upgraded to use (and now requires at least) Java SE 8 [JDK][jdk8].
Bistro 3.8 updates its usage to the more modern collection classes and streams that became available in Java 8.
Bistro 3.8 also migrates from Ant to [Maven 3][maven] for its build process.

#### Project Installation ####

Clone this repository and perform the following Maven command from the cloned work folder.

```
mvn -U clean install
```

This will build the compiler, then compile the Bistro library sources using the Bistro and Java compilers
(combined), and then run the various tests of the library code using the library test fixtures.

#### The Bistro Compiler and Class Library ####

This repository contains the Bistro compiler, class libraries, and design documentation.
Overall, the Bistro compiler performs source-to-source translation, from Bistro code to [Java][java]. 
Then, the Bistro compiler uses a standard Java compiler to translate the intermediate Java source code 
into Java class files. 
The generated Java class files can then be packaged as JARs, deployed with and run on any compliant [JVM][jvm].

The Bistro compiler is built with [ANTLR 3.5.2][antlr]. 
The parser (generated by [ANTLR][antlr]) recognizes the code in Bistro source files 
and builds an abstract syntax tree AST that captures the structure of the Bistro code.
The compiler then generates Java code from the nodes of the AST.

This repository contains the following modules:

| Module           | Contents   |
|------------------|------------|
| bistro-compiler  | the Bistro compiler sources (Java code) |
| bistro-library   | the library classes (Bistro + Java code) |
| libs-smalltalk   | the library target (Java code) and test fixtures |

#### Features ####

The following table summarizes the important features of Bistro. 
For more details, see my last [paper][paper] regarding Bistro.
Each of the links below provides access to discussions of the specific design details.
Note that the compiler and library code are still in transition, undergoing upgrades from the older 
Java libraries, and therefore subject to change.

| Feature     | Description   |
|-------------|---------------|
| [Language Model][model]    | Bistro has a _declarative_ language model. |
| [Name Spaces][spaces]      | Bistro packages and name spaces are assigned based on folders. |
| [Classes][classes]         | Bistro classes have a hybrid structure based primarily on Smalltalk. |
| [Metaclasses][classes]     | Bistro supports metaclasses like those found in Smalltalk. |
| [Types][types]             | Bistro supports first-class interfaces (as types). |
| [Metatypes][types]         | Bistro types can have associated metatypes. |
| [Library][lib]             | Bistro includes types that implement ANSI standard Smalltalk. |
| [Access Control][access]   | Bistro code may use access controls: **public**, **protected**, **private**. |
| [Decorations][decor]       | Bistro also supports: **abstract**, **final**,   **static**. |
| [Optional Types][optional] | Bistro variable and argument type specifications are optional. |
| [Methods][methods]         | Bistro methods resemble those of Smalltalk. |
| [Interoperability][xop]    | Bistro method names become compatible host method names. |
| [Primitives][prims]        | Bistro supports **primitive** methods. |
| [Comments][comments]       | Bistro comments are copied over to Java. |
| [Blocks][blocks]           | Bistro blocks are implemented with Lambdas. |
| [Threads][threads]         | Bistro blocks support the **fork** protocol for spawning Java threads. |
| [Exceptions][except]       | Bistro supports both Smalltalk and Java exception handling. |

Copyright 1999,2016 Nikolas S. Boyd. All rights reserved.

[smalltalk]: http://en.wikipedia.org/wiki/Smalltalk "Smalltalk"
[stimps]: http://en.wikipedia.org/wiki/Smalltalk#List_of_implementations "Smalltalk Implementations"
[jdk8]: http://www.oracle.com/technetwork/java/javase/downloads/index.html
[java]: http://en.wikipedia.org/wiki/Java_%28programming_language%29 "Java"
[jvm]: http://en.wikipedia.org/wiki/Java_virtual_machine "Java Virtual Machine"
[csharp]: http://en.wikipedia.org/wiki/C_Sharp_%28programming_language%29 "C#"
[clr]: http://en.wikipedia.org/wiki/Common_Language_Runtime "Common Language Runtime"
[st]: http://www.stringtemplate.org/ "StringTemplate"
[antlr]: http://www.antlr.org/ "ANTLR"
[maven]: https://maven.apache.org/ "Maven"
[paper]: http://www.drdobbs.com/web-development/the-bistro-programming-language/184405578 "DDJ"

[logo]: https://bitbucket.org/nik_boyd/bistro-smalltalk/raw/master/bistro-design/bistro-logo.svg "Bistro"

[model]: ../master/bistro-design/model.md#markdown-header-language-model "Language Model"
[spaces]: ../master/bistro-design/model.md#markdown-header-name-spaces "Name Spaces"
[classes]: ../master/bistro-design/model.md#markdown-header-classes-and-metaclasses "Classes"
[types]: ../master/bistro-design/model.md#markdown-header-types-and-metatypes "Types"
[access]: ../master/bistro-design/model.md#markdown-header-access-controls-and-decorations "Access Controls"
[decor]: ../master/bistro-design/model.md#markdown-header-access-controls-and-decorations "Decorations"
[optional]: ../master/bistro-design/model.md#markdown-header-variables "Optional Types"
[methods]: ../master/bistro-design/methods.md "Methods"
[prims]: ../master/bistro-design/methods.md "Primitives"
[xop]: ../master/bistro-design/methods.md#markdown-header-interoperability "Interoperability"
[lib]: ../master/bistro-design/model.md#markdown-header-library-types-and-classes "Library"
[blocks]: ../master/bistro-design/blocks.md "Blocks"
[threads]: ../master/bistro-design/blocks.md#markdown-header-bistro-threads "Threads"
[except]: ../master/bistro-design/exceptions.md "Exceptions"
[comments]: ../master/bistro-design/model.md#markdown-header-comments "Comments"
