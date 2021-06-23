package parser;

import java.util.ArrayList;
import main.*;
import parser.AspExpr;
import parser.AspName;
import runtime.*;
import scanner.*;
import static scanner.TokenKind.*;

/**
 * Integrer number (as long)
 */
class AspInteg extends AspAtom {
    long nr;
    AspInteg(int n) {
	    super(n);
    }

    public static AspInteg parse(Scanner s) {
	    enterParser("integrer literal");
        AspInteg t = new AspInteg(s.curLineNum());
        t.nr = s.curToken().integerLit;
        skip(s, integerToken);
	    leaveParser("integrer literal");
	    return t;
    }


    @Override
    public void prettyPrint() {
        prettyWrite(Long.toString(nr));
    }


    @Override
    public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        return new RuntimeIntValue(nr);
    }
}
/**
 * Floating numbers (double)
 */
class AspFloat extends AspAtom {
    double nr;
    AspFloat(int n) {
	    super(n);
    }

    public static AspFloat parse(Scanner s) {
	    enterParser("float literal");
        AspFloat t = new AspFloat(s.curLineNum());
        t.nr = s.curToken().floatLit;
        skip(s, floatToken);
	    leaveParser("float literal");
	    return t;
    }


    @Override
    public void prettyPrint() {
        prettyWrite(Double.toString(nr));
    }


    @Override
    public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        return new RuntimeFloatValue(nr);
    }
}
/**
 * String
 */
class AspString extends AspAtom {
    String txt;
    AspString(int n) {
	    super(n);
    }

    public static AspString parse(Scanner s) {
	    enterParser("string literal");
        AspString t = new AspString(s.curLineNum());
        t.txt = s.curToken().stringLit;
        if(s.curToken().kind != stringToken) {
            s.printParserError();
        }
        skip(s, stringToken);
	    leaveParser("string literal");
	    return t;
    }


    @Override
    public void prettyPrint() {
        prettyWrite("\"");
        prettyWrite(txt);
        prettyWrite("\"");
    }


    @Override
    public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        return new RuntimeStringValue(txt);
    }
}

/**
 * Boolean value (true,false)
 */
class AspBoolean extends AspAtom {
    Boolean bool;
    AspBoolean(int n) {
	    super(n);
    }
    public static AspBoolean parse(Scanner s) {
	    enterParser("boolean literal");
        AspBoolean t = new AspBoolean(s.curLineNum());
        if(s.curToken().kind == falseToken) {
            t.bool = false;
            skip(s, falseToken);
        }else if(s.curToken().kind == trueToken) {
            t.bool = true;
            skip(s, trueToken);
        }else {
            parserError("Expected 'True' or 'False' but found " + 
			s.curToken().kind + "!", s.curLineNum());
        }
	    leaveParser("boolean literal");
	    return t;
    }


    @Override
    public void prettyPrint() {
        if(bool) {
            prettyWrite("True");
        }
        else {
            prettyWrite("False");
        }
    }


    @Override
    public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        return new RuntimeBoolValue(bool);
    }
}

/**
 * Variable name
 */
class AspName extends AspAtom {
    String name;
    AspName(int n) {
	    super(n);
    }

    public static AspName parse(Scanner s) {
	    enterParser("name");
        AspName t = new AspName(s.curLineNum());
        t.name = s.curToken().name;
        skip(s, nameToken);
	    leaveParser("name");
	    return t;
    }


    @Override
    public void prettyPrint() {
        prettyWrite(name);
    }


    @Override
    public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
	    return new RuntimeStringValue(name);
    }
}
/**
 * None value
 */
class AspNone extends AspAtom {
    AspNone(int n) {
	    super(n);
    }

    public static AspNone parse(Scanner s) {
	    enterParser("none literal");
        AspNone t = new AspNone(s.curLineNum());
        skip(s, noneToken);
	    leaveParser("none literal");
	    return t;
    }


    @Override
    public void prettyPrint() {
        prettyWrite("none");
    }


    @Override
    public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        return new RuntimeNoneValue();
    }
}