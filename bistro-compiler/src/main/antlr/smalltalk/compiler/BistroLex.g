//==================================================================================================
// BistroLex.g - Bistro Lexical Grammar
//==================================================================================================

lexer grammar BistroLex;

options {
filter = true;
}

@header {
package smalltalk.compiler;
}

//==================================================================================================
// tokens
//==================================================================================================

Package    : 'package:'    ;
Import     : 'import:'     ;
Subclass   : 'subclass:'   ;
Implements : 'implements:' ;
Metaclass  : 'metaclass:'  ;
Class      : 'class:'      ;
Subtype    : 'subtype:'    ;
Metatype   : 'metatype:'   ;
Type       : 'type:'       ;
Throws     : 'throws:'     ;
Inner      : 'asNew:'      ;

Public       : ( options {greedy=true;} : 'public' WhiteSpace ) ;
Protected    : ( options {greedy=true;} : 'protected' WhiteSpace ) ;
Private      : ( options {greedy=true;} : 'private' WhiteSpace ) ;
Abstract     : ( options {greedy=true;} : 'abstract' WhiteSpace ) ;
Final        : ( options {greedy=true;} : 'final' WhiteSpace ) ;
Native       : ( options {greedy=true;} : 'native' WhiteSpace ) ;
Static       : ( options {greedy=true;} : 'static' WhiteSpace ) ;
Wrapped      : ( options {greedy=true;} : 'wrapped' WhiteSpace ) ;
Synchronized : ( options {greedy=true;} : 'synchronized' WhiteSpace ) ;

//==================================================================================================
// names + identifiers
//==================================================================================================

Symbol 	: ( options {greedy=true;} : Pound SymbolText ) ;
fragment SymbolText : ( Keyword ) => Keyword+ | Identifier | ConstantString | BinarySelector | Comma ;

PartType   : NewTerm NamedPart ( EmptyBlock )? EndTerm ;
NamedType  : NewTerm Identifier ( EmptyBlock )* EndTerm ;

PartName   : ( options {greedy=true;} : 'self' Part ) ;
Keyword    : ( options {greedy=true;} : Name ':' ) ;

NamedPackage : ( options {greedy=true;} : Name ( Part )* '.*' ) ;
NamedPart    : ( options {greedy=true;} : Name ( Part )+ ) ;
Identifier   : ( options {greedy=true;} : Name ) ;

fragment Part  : ( options {greedy=true;} : '.' ( ( ~WhiteSpace ) => Name ) ) ;
fragment Name  : ( options {greedy=true;} : Letter ( Letter | Digit )* ) ;

fragment Letter : Upper | Lower ;
fragment Upper : 'A'..'Z' ;
fragment Lower : 'a'..'z' ;
fragment Digit : '0'..'9' ;

//==================================================================================================
// blocks
//==================================================================================================

PrimitiveBlock : NewPrim PrimitiveText EndPrim ;
fragment PrimitiveText : ( ( NewPrim ) => PrimitiveBlock | ( ~'}' ) => . )* ;
fragment NewPrim : '{' ;
fragment EndPrim : '}' ;

EmptyBlock : NewBlock EndBlock ;
NewBlock : '[' ;
EndBlock : ']' ;

NewTerm : '(' ;
EndTerm : ')' ;

//==================================================================================================
// literals
//==================================================================================================

ConstantString :
( SingleString SingleString ) => StringSequence |
  SingleString ;

fragment StringSequence : ( SingleString )+ ;
fragment SingleString : ( Quote .* Quote ) ;
fragment Quote : '\'' ;

Comment : ( Quoted .* Quoted ) {$channel=HIDDEN;} ;
fragment Quoted : '\"' ;

ConstantCharacter : '$' . ;

//==================================================================================================
// literal numbers
//==================================================================================================

ScaledFloat     : ( options {greedy=true;} : ( Digit )+ Period ( Digit )+ PowerMark ( Minus )? ( Digit )+ );
ScaledDecimal   : ( options {greedy=true;} : ( Digit )+ Period ( Digit )+ ScaleMark ( Digit )+ );
ConstantFloat   : ( options {greedy=true;} : ( Digit )+ Period ( Digit )+ ) ;

RadixedInteger  : ( options {greedy=true;} : ( Digit )+ RadixMark ( Minus )? ( Digit | Upper )+ );
ScaledInteger   : ( options {greedy=true;} : ( Digit )+ ScaleMark ( Digit )+ );
ConstantInteger : ( options {greedy=true;} : ( Digit )+ );

fragment RadixMark : 'r' ;
fragment PowerMark : 'e' ;
fragment ScaleMark : 's' ;

//==================================================================================================
// operators
//==================================================================================================

Assign : ':=' ;

BinarySelector :
( SingleOperator SingleOperator ) => OperatorSequence |
( Minus SingleOperator ) => Minus OperatorSequence |
  SingleOperator ;

fragment OperatorSequence : ( SingleOperator )+ ;
fragment SingleOperator :
  Not | And | Star | Div | Mod | Plus | Equal | More | Less | At | Percent
;

fragment Not   : '~' ;
fragment And   : '&' ;
fragment Star  : '*' ;
fragment Div   : '/' ;
fragment Mod   : '\\' ;
fragment Plus  : '+' ;
fragment Equal : '=' ;
fragment More  : '>' ;
fragment Less  : '<' ;
fragment At    : '@' ;
fragment Percent : '%' ;

//==================================================================================================
// punctuators
//==================================================================================================

Colon   : ':' ;
Cascade : ';' ;
Comma   : ',' ;
Period  : '.' ;
Bang    : '!' ;
Pound   : '#' ;
Exit    : '^' ;
Or      : '|' ;
Minus   : '-' ;

//==================================================================================================
// white space
//==================================================================================================

WhiteSpaces : ( options {greedy=true;} : WhiteSpace+ ) ;
fragment WhiteSpace : ' ' | '\t' | '\r' | '\n' | '\f' ;

//==================================================================================================
