package scanner;

import main.*;

class TokenIndex {
    public static final int keyword_start = 4;
    public static final int symbol_start = 19;
    public static final int double_symbol_start = 36;
    public static final int length = 40;
}

public enum TokenKind {
    // Names and literals:
    nameToken("name"),
    integerToken("integer literal"),
    floatToken("float literal"),
    stringToken("string literal"),

    // Keywords:
    andToken("and"),
    defToken("def"),
    elifToken("elif"),
    elseToken("else"),
    falseToken("False"),
    ifToken("if"),
    inToken("in"),
    noneToken("None"),
    forToken("for"),
    notToken("not"),
    orToken("or"),
    passToken("pass"),
    returnToken("return"),
    trueToken("True"),
    whileToken("while"),

    //Symbols:
    greaterToken(">"),
    astToken("*"),
    lessToken("<"),
    minusToken("-"),
    percentToken("%"),
    plusToken("+"),
    slashToken("/"),
    colonToken(":"),
    commaToken(","),
    equalToken("="),
    leftBraceToken("{"),
    leftBracketToken("["),
    leftParToken("("),
    rightBraceToken("}"),
    rightBracketToken("]"),
    rightParToken(")"),
    semicolonToken(";"),
    doubleEqualToken("=="),
    doubleSlashToken("//"),
    greaterEqualToken(">="),
    lessEqualToken("<="),
    notEqualToken("!="),


    asToken("as"),              
    assertToken("assert"),      
    breakToken("break"),        
    classToken("class"),        
    continueToken("continue"),  
    delToken("del"),            
    exceptToken("except"),      
    finallyToken("finally"),    
    fromToken("from"),          
    globalToken("global"),      
    importToken("import"),      
    isToken("is"),              
    lambdaToken("lambda"),      
    nonlocalToken("nonlocal"),  
    raiseToken("raise"),        
    tryToken("try"),            
    withToken("with"),          
    yieldToken("yield"),        


   


    // Format tokens:
    indentToken("INDENT"),
    dedentToken("DEDENT"),
    newLineToken("NEWLINE"),
    eofToken("E-o-f");

    String image;

    TokenKind(String s) {
	image = s;
    }

    public String toString() {
	return image;
    }
}