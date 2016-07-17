### Bistro Blocks ###

Blocks are a very powerful part of the Smalltalk language. 
They are so important, Bistro not only retains the block concepts of Smalltalk, but also extends 
them in thoughtful ways to improve integration with Java. 
Blocks are so flexible, Bistro (like Smalltalk) uses them for a wide variety of language features, 
including decision structures, collection iteration, exception handling, multithreading, and event handling. 
The following table lists the various block idioms supported by Bistro.

| Idiom         | Examples     |
|---------------|--------------|
| Decisions     | result := boolValue **or:** [ aBoolean ].   |
|               | result := boolValue **and:** [ aBoolean ].  |
| Alternatives  | result := boolValue **ifTrue:** [ "..." ].  |
|               | result := boolValue **ifTrue:** [ "..." ] **ifFalse:** [ "..." ].  |
|               | result := boolValue **ifFalse:** [ "..." ].  |
|               | result := boolValue **ifFalse:** [ "..." ] **ifTrue:** [ "..." ].  |
| Loops         | [ boolValue ] **whileTrue:** [ "..." ]. |
|               | [ boolValue ] **whileFalse:** [ "..." ]. |
|               | [ "..." boolValue ] **whileTrue**. |
|               | [ "..." boolValue ] **whileFalse**. |
| Intervals     | start **to:** end **do:** [ :index ┃ "..." ]. |
|               | start **to:** end **by:** delta **do:** [ :index ┃ "..." ]. |
| Collections   | elements **do:** [ :element ┃ "..." ]. |
|               | results := elements **collect:** [ :element ┃ "..." ]. |
|               | results := elements **select:** [ :element ┃ "..." ]. |
|               | results := elements **reject:** [ :element ┃ "..." ]. |
|               | results := elements **collect:** [ :element ┃ "..." ]. |
|               | result := elements **detect:** [ :element ┃ "..." ]. |
|               | result := elements **detect:** [ :element ┃ "..." ] **ifNone:** [ "..." ]. |
|               | result := elements **inject:** initialValue **into:** [ :aValue :element ┃ "..." ]. |
| Evaluations   | result := [ :a :b ┃ "..." ] **value:** x value: y. |
|               | result := [ :a ┃ "..." ] **value:** x. |
|               | result := [ "..." ] **value**. |
| Exceptions    | [ "..." ] **ifCurtailed:** [ "... catch any exception ..." ]. |
|               | [ "..." ] **ensure:** [ "... evaluated finally ..." ]. |
|               | [ "..." ] **catch:** [ : ExceptionType! ex ┃ "..." ] **ensure:** [ "... evaluated finally ..." ]. |
| Threads       | [ "..." ] **fork**. |
|               | [ "..." ] **forkAt:** aPriority. |
| Synchronizing | subject **lockDuring:** [ "..." ]. |
|               | subject **notifyOneWaitingThread**. |
|               | subject **notifyAllWaitingThreads**. |
|               | subject **wait:** durationMsecs **ifInterrupted:** [ "..." ]. |
| Adapters      | InterfaceType **asNew:** [ "..." ] |


#### Decision Structures ####

There are no reserved words for decision structures in Bistro like there are in languages such as Java and C++. 
Instead, decision structures use message idioms that combine Boolean expressions with Blocks. 
The idioms table (above) lists some of the most commonly used decision structures as they are expressed in Bistro. 
Notice that the decision idioms do not include a switch or a case statement. 
There are a number of ways to mimic such a control structure in Bistro. 
However, it is generally discouraged in favor of object-oriented designs that make use of classes and 
polymorphism to distinguish and handle the separation of cases.

Many of the decision structures identified in idioms table can be optimized during translation to Java. 
Often, they can be translated directly into equivalent Java decision structures. 
Similar optimizations are often performed by commercial Smalltalk compilers. 
However, under certain circumstances, these control structures and other custom blocks are best implemented 
using Java inner classes.

#### Implementing Blocks with Inner Classes ####

Java inner classes make duplicating the semantics of Smalltalk blocks rather easy. 
Blocks can be implemented by translation into appropriate anonymous inner classes. 
By defining a few abstract base classes, Bistro provides the foundations for deriving these anonymous inner classes. 

Some Smalltalk implementations support an arbitrary number of block arguments. 
To keep the translation mapping simple, Bistro currently limits support for blocks to those that 
take 0, 1, or 2 arguments. 
The following table lists the abstract base classes for blocks included in Bistro 
and a representative example of the kind of block each supports.

```
"ZeroArgumentBlock" [ "..." ] value

"OneArgumentBlock"  [ :a ┃ ".." ] value: x

"TwoArgumentBlock"  [ :a :b ┃ "..." ] value: x value: y
```

The following method provides a simple example of a block commonly used for sorting the elements in a 
**SortedCollection**. 
The Bistro compiler translates the block returned by this method into an instance of an anonymous Java 
inner class derived from **TwoArgumentBlock**.

```
"a sort block, which orders a pair of text elements, indicating whether (a <= b)"
sortBlock [  
    ^[ (Boolean) :a (String) :b (String) ┃ a <= b ]  
]
```

#### Method Returns from Blocks ####


Like Smalltalk, Bistro supports the ability to return method results directly from inside nested 
blocks using a message expression that begins with a caret **^**. 
Method returns exit all enclosing block scopes, including the enclosing method scope. 

The following **search:** method provides an example of this feature. 
If the method finds an element from **aCollection** in the **targets**, it returns the element as the 
result of the method. 
Note that in this case, the method result is returned by an exit from a nested block scope.


```
"returns the 1st element from targets found in aCollection"
search: aCollection for: targets
[  
    aCollection do: [ :element ┃  
        (targets includes: element)  
            ifTrue: [ ^element ] "<-- method exit"  
    ].  
    ^nil  
]
```

When the Bistro compiler can't optimize a block scope inside a method scope, it uses an inner class to implement it.
Then, special consideration must be given to the method returns. 
Bistro implements such method returns using the Java exception facility. 

Bistro uses an instance of **MethodResult** to carry a block return value across the scopes of the intervening 
inner classes. 
**MethodResult** is a subclass of **RuntimeException**. 
So, **MethodResult** does not impact the method signatures of the inner classes used to implement the block scopes. 

Bistro wraps the block result value in an instance of **MethodResult**, throws the **MethodResult** from the 
inner block scope and catches the **MethodResult** once it reaches the enclosing method scope. 
Then, the **MethodResult** is caught and unwrapped to return the block result value as the method result.

#### Listeners and Adapters ####

The Java event model uses interfaces and inner classes to provide a flexible general mechanism for handling events. 
In order to integrate easily with the event model, the Bistro compiler recognizes a special message idiom to 
support the definition of event handlers as anonymous inner classes. 

The following code fragment provides an example of how this special idiom **asNew:** can be used to 
define an **ActionListener** that handles a button click.


```
"attach an action listener to the closeButton"
closeButton addActionListener: 
(
    "the listener interface is implemented by a handler instance"
    java.awt.event.ActionListener asNew: 
    [
        "Define the event handler for the closeButton click event."
        (void) actionPerformed: event (java.awt.event.ActionEvent)
        [ "... handle the click event from the closeButton ..."
        ]
    ]
).
```

#### Bistro Threads ####

Bistro supports the common Smalltalk block **fork** idiom for spawning threads, but implements these threads 
using primitive Java threads. 
In Bistro, block **fork** expressions return instances of **smalltalk.behavior.ZeroArgumentBlock**.

```
aBlock (ZeroArgumentBlock) := [ "... block expressions ..." ] fork.
```

Once a new thread has been created, you can access the primitive thread by sending the message 
**primitiveThread** to the block instance.

```
primitiveThread (Thread) := aBlock primitiveThread.
```

Blocks that have not been forked return primitive **null** in response to **primitiveThread**.

#### Thread Synchronization ####

Java supports thread synchronization on methods and within methods. 
Bistro supports the declaration of **synchronized** methods and also supports object synchronization within methods. 
The base class, **smalltalk.behavior.Object**, provides a method that acquires a Java monitor. 
Thus, any Bistro method may synchronize threads on an object by using a statement similar to the following one.


```
"aquires a monitor on sampleObject"
sampleObject acquireMonitorDuring: [ "... critical section ..." ].
```

Bistro methods can also wait on an object monitor using the following idioms.

```
"current thread waits on sampleObject until notified or interrupted."
sampleObject waitForChangeIfInterrupted: [ "..." ].

"current thread waits on sampleObject until notified, interrupted, or a millisecondDuration expires."
sampleObject waitForChange: millisecondDuration ifInterrupted: [ "..." ].
```

After a thread has been suspended using one these wait idioms, another thread can awaken the sleeping 
thread using one of the following methods.

```
"current thread awakens one thread waiting on sampleObject."
sampleObject awakenWaitingThread.

"current thread awakens all threads waiting on sampleObject."
sampleObject awakenAllWaitingThreads.
```

Copyright 1999,2016 Nikolas S. Boyd. All rights reserved.
