# Tutorial on our new language

## Introduction
Our language is designed with the purpose of minimalizing symbols as much as possible to improve code readability for the ones who are not familiar with programming. 

Some key highlights from our language:
* Besides alphabetical and digit characters, only allowed symbols in non strings and non comments are " (for string), # (for comment), space, tab, and new line
(for usual code writing). 
* Basic comparators and operations such as `+`, `-`, `>`, `<`, etc are replaced with keywords. More details below.
* Each line of code in our language needs to be on a seperate line (no need for indentation like Python, but indetation improves code readability).

## Grammar
For more references, you can look at the grammar [here](https://github.com/anhnguyenphung/csc372-project2/blob/master/CSC%20372%20Project%202%20Grammar.pdf) to understand the syntax better.

### Run the program in our language
Suppose you write a program `Program.txt` in our language. To compile and run, you should run the following commands in the terminal:
* `javac Translator.java` to compile the translator program.
* `java Translator Program.txt` to convert the program from our language into Java. If there are any parsing errors, the error message would be printed out to the terminal.
* `javac Program.java` to compile the program in Java.
* `java Program` to run the program with any command line arguments. For example, `java Program 3 4 5`.


## Basic syntax
### Comment
In our language, comments start with `#`. After `#` can be any ASCII characters except for `"`.

### Integer expression
Our language supports `add` (+), `sub` (-), `mult` (*), `div` (/), `mod` (%), `negate` (-). There should be a space before and after each keyword, except for `negate` that
only needs a space after. 

All operations are left associative, with order of highest to lowest precedence (from left to right) and any operations that have equal precedence are placed in { }.

`negate`, {`mult`, `div`, `mod`}, {`add`, `sub`}

Examples of valid integer expression:
```shell
2 add 3 mod 2
3 sub 2 mult 4
negate 4 div 2
```

### Boolean expression
Our language supports `and` (&&), `or` (||), `not` (!). For comparators, our language supports `gt` (>), `lt` (<), `gte` (>=), `lte` (<=), `equal` (==), `diff` (!=).
There should be a space before and after each keyword, except for `not` that only needs a space after. 

All operations are left associative, with order of highest to lowest precedence (from left to right) and any operations that have equal precedence are placed in { }.

{`<int_expr> <comparator> <int_expr>`, `not`}, `and`, `or`

##### Note: Our language does not comparison between boolean values. So something like `2 gt 3 equal true` is invalid in our language.
##### Note: Something like `not 2 gt 3` is invalid in our language.

Examples of valid boolean expression:
```shell
2 lt 3 and 3 mod 1 equal 0
not x                       # with 'boolean x assign true'
2 diff 3 or true
```

### Declaring variable
In our language, you can declare variables of type integer and boolean. The name of a variable (non-command line argument variable) should only contains
alphabetical characters (both uppercase and lowercase)
```shell
integer a assign 5
integer B assign 2 add 3
boolean X assign true
boolean y assign 2 gte 3
```
You can not re-declare a variable. For example, the codes below are considered invalid:
```shell
integer a assign 5
integer a assign 4
```
```shell
boolean a assign true
integer a assign 4
```

### Reassign variable
In our language, you can reassign the value of a variable if that variable is already declared. If reassigned, the value must be the same type as the declared type.
Moreover, you can not reassign the value of a variable if that variable is not declared first.

Example of valid variable reassignment:
```shell
integer x assign 3      # int x = 3
x assign 4              # x = 4
x assign x add 1        # x = x + 1
```

### String
In our language, strings are wrapped inside `"`, such as `"Hello World!"`.

### Print
In our language, you can print either boolean/integer expressions (including variables) and strings. `print` for printing without new line and `printout` for printing with new line
```shell
printout 2
print true
printout "Hello World!"
printout 2 add 3 sub 5
print 2 lt 3
```

### If/else conditionals
Examples of valid if/else conditionals:
```shell
if 2 lt 3
  print "2 less than 3"
end
```
```shell
if 2 lt 3
  print "2 less than 3"
else 
  if 2 equal 2
    printout "Hello!"
  end
end
```

### Loops
Examples of valid loops:
```shell
integer i assign 0
during i lt 2         # while i < 2
  printout i          
  i assign i add 1    # i = i + 1
end
```
```shell
integer i assign 0
during i lt 2         # while i < 2
  if i equal 1
    printout "World!"
  else
    printout "Hello"
  end
  i assign i add 1    # i = i + 1
end
```

### Command line arguments
In our language, to use command line arguments, you have to specify how many command line arguments you want to use at the beginning of the file. For example, if you want to
use 3 command line arguments, at the beginning of the file, you should have this line of code:
```shell
use 3 cmd args
```
##### Note: You have to follow the exact syntax above with a single space between keywords and the number, otherwise there would be a parsing error.
Then you can use 3 command line arguments `arg0`, `arg1`, `arg2` as variables directly in your code. The name of a command line argument variable starts with `arg` followed by
a number indicating the index.

For example, suppose you want to read in one command line argument and print out the square of that command line argument, then the desired program should be:
```shell
use 1 cmd args
printout arg0 mult arg0
```
Another example is that if you want to read in two command line arguments and print out the sum of those two command line arguments, then the desired program should be:
```shell
use 2 cmd args
printout arg0 add arg1
```

## Example programs
You can find example programs in the GitHub repo, with `Program1.txt` and `Program2.txt` are valid programs in our language and `ProgramX.txt` where `X` from `3` to `10` inclusive
are programs that produce parsing errors.


