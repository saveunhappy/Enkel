//header
//这个语言叫做Enkel,因为要根据这个文件生成Java代码，所以，这个就是大部分的类的前缀名称
grammar Enkel;
//表明要去哪里找这些包。
@header {
package com.bendcap.enkel.antlr;
}

//RULES
//最根本的规则：这个文件必须是在一个类声明的里面
compilationUnit : classDeclaration EOF ; //root rule - our code consist consist only of variables and prints (see definition below)
//className：类声明，比如得有一个名字，名字是所有的英文字符和数字，superClassName* 多了个冒号，就相当于Java的继承然后就是方括号的body

classDeclaration : className superClassName* '{' classBody '}' ;
className : ID ;
superClassName : ':' ID ;
//class的块儿中必须全部都是方法
classBody :  function* ;
//方法声明可以有八大基本类型或者是void，或者不写也行，也可以是对象类型的，但是必须要加上全限定名，然后就是方法体了，
//方法体也是一个块儿，可以有，也可以没有，也可以是一个函数。
//这个block就是你在一个语句块儿中的东西了，比如 var a = 1  var b = 2 var c = a  var a = string(a) 可以是变量，可以是值，可以是函数
//或者  print a  print 1  print string(a)
//或者是print string(print(getMessage(a)))    getmessage(int a){var a = 1  var b = 2 var c = a  var a = string(a)},可以一直嵌套
function : functionDeclaration '{' (blockStatement)* '}' ;
// (type)？ 就是返回类型可以写也可以不写。
functionDeclaration : (type)? functionName '('  (functionArgument)* (',' functionArgument)* ')' ;
functionName : ID ;
//但是传参数就必须带上类型了，而且，可以有默认值，默认值可以有，也可以没有，多个参数用逗号隔开。
//比如原来的Java中使用的是，(int a,int b)  这个就可以(int a = 1,int b = 2)
functionArgument : type ID functionParamdefaultValue? ;
//这里面等于号后面的表达式可以是一个变量名，可以是一个值，也可以是一个函数调用
functionParamdefaultValue : '=' expression ;
type : primitiveType
     | classType ;
//这个('[' ']')* 的括号是一个整体
primitiveType :     'boolean' ('[' ']')*
                |   'string' ('[' ']')*
                |   'char' ('[' ']')*
                |   'byte' ('[' ']')*
                |   'short' ('[' ']')*
                |   'int' ('[' ']')*
                |   'long' ('[' ']')*
                |   'float' ('[' ']')*
                |   'double' ('[' ']')*
                |   'void' ('[' ']')* ;
classType : QUALIFIED_NAME ('[' ']')* ;

blockStatement : variableDeclaration
               | printStatement
               | functionCall ;
variableDeclaration : VARIABLE name EQUALS expression;
printStatement : PRINT expression ;
functionCall : functionName '('expressionList ')';
name : ID ;
expressionList : (expression)* (',' expression)* ;
expression : varReference
           | value
           | functionCall ;
varReference : ID ;
value : NUMBER
      | STRING ;

//TOKENS
VARIABLE : 'var' ;
PRINT : 'print' ;
EQUALS : '=' ;
NUMBER : [0-9]+ ;
STRING : '"'.*'"' ;
ID : [a-zA-Z0-9]+ ;
//org.antlr.v4.runtime注意，这里是加号，代表必须要有一个，所以你直接写个类名是不行的，具体能不能import，暂时未知
QUALIFIED_NAME : ID ('.' ID)+;
WS: [ \t\n\r]+ -> skip ;