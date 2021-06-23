package parser;

import java.util.ArrayList;
import main.*;
import parser.AspSyntax;
import runtime.*;
import scanner.*;
import static scanner.TokenKind.*;

/**
 * Abstract class for statements
 */
public abstract class AspStmt extends AspSyntax {
    AspStmt(int n) {
	    super(n);
    }
    public static AspStmt parse(Scanner s) {
	    enterParser("stmt");
        AspStmt ret = null;
        switch (s.curToken().kind) {
            case forToken:
            case ifToken:
            case whileToken:
            case defToken:
                ret = AspCompound.parse(s);
                break;
            default:
                ret = AspSmallList.parse(s);
        }
        leaveParser("stmt");
        return ret;
	    
    }


    @Override
    public void prettyPrint() {
	//abstrakt
    }


    @Override
    public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
	//abstrakt
	return null;
    }
}



/**
 * Abstract class for compound statements (a statement with a body, for example 'for' statement)
 */
abstract class AspCompound extends AspStmt {
    AspCompound(int n) {
        super(n);
    }

    public static AspStmt parse(Scanner s) {

        enterParser("compound stmt");
        AspCompound ret = null;
        switch (s.curToken().kind) {
            case forToken:
                ret = AspFor.parse(s);
                break;
            case ifToken:
                ret = AspIf.parse(s);
                break;
            case whileToken:
                ret = AspWhile.parse(s);
                break;
            case defToken:
                ret = AspDef.parse(s);
                break;
            default:
                s.printParserError();
        }
        leaveParser("compund stmt");
        return ret;
    }
    
    @Override
    public void prettyPrint() {
	//abstrakt
    }
    @Override
    public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
	//abstrakt
	return null;
    }
}
/**
 * For statement, contains a loop variable, expression and the body (suite)
 */
class AspFor extends AspCompound {
    AspName name;
    AspExpr expr;
    AspSuit suit;

    AspFor(int n) {
        super(n);
    }

    public static AspFor parse(Scanner s) {
        enterParser("for stmt");
        AspFor t = new AspFor(s.curLineNum());
        skip(s, forToken);
        t.name = AspName.parse(s);
        skip(s, inToken);
        t.expr = AspExpr.parse(s);
        skip(s, colonToken);
        t.suit = AspSuit.parse(s);
        leaveParser("for stmt");
        return t;
    }
    @Override
    public void prettyPrint() {
        prettyWrite("for ");
        name.prettyPrint();
        prettyWrite(" in ");
        expr.prettyPrint();
        prettyWrite(":");
        prettyWriteLn();
        suit.prettyPrint();
    }
    @Override
    public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {

        RuntimeListValue v = (RuntimeListValue)expr.eval(curScope);
        long st = 0;
        if(v.exprs.size() > 0) { // Range(1,1) will give a empty list
            st = v.exprs.get(0).getIntValue("", this);
        }
        long i = v.evalLen(this).getIntValue("For loop", this);

        trace("For loop with length: "+i);
        //RuntimeScope s = new RuntimeScope(curScope);
        for(long x = st; x < i+st; x++) {
            curScope.assign(name.name, new RuntimeIntValue(x));
            suit.eval(curScope);
        }
        trace("End For loop");
	    return null;
    }
}
/**
 * If/elif/else statements
 * Contains the expressions and the bodies (suite)
 */
class AspIf extends AspCompound {
    
    ArrayList<AspExpr> exprs = new ArrayList<AspExpr>();
    ArrayList<AspSuit> suits = new ArrayList<AspSuit>();
    AspIf(int n) {
        super(n);
    }

    public static AspIf parse(Scanner s) {

        enterParser("if stmt");
        
        AspIf t = new AspIf(s.curLineNum());
        
        skip(s, ifToken);
        t.exprs.add(AspExpr.parse(s)); 
        skip(s, colonToken);
        t.suits.add(AspSuit.parse(s));
        
        while(true){
            if(s.curToken().kind == elifToken) {
                skip(s, elifToken);
                t.exprs.add(AspExpr.parse(s)); 
                skip(s, colonToken);
                t.suits.add(AspSuit.parse(s));
            }
            else if(s.curToken().kind == elseToken){
                skip(s, elseToken);
                skip(s, colonToken);
                t.suits.add(AspSuit.parse(s));
            }
            else {
                break;
            }
        }
        leaveParser("if stmt");  
        return t;
    }

    @Override
    public void prettyPrint() {
        for(int i = 0; i < suits.size(); i++) {
            if(i == 0) {
                if(exprs.size() > i) {
                    prettyWrite("if ");
                    exprs.get(i).prettyPrint();
                    prettyWrite(":");
                    prettyWriteLn();
                    suits.get(i).prettyPrint();
                }
            }else {
                if(exprs.size() > i) {
                    prettyWrite("elif ");
                    exprs.get(i).prettyPrint();
                    prettyWrite(":");
                    prettyWriteLn();
                    suits.get(i).prettyPrint();
                }else if(suits.size() > i) {
                    prettyWrite("else");
                    prettyWrite(":");
                    prettyWriteLn();
                    suits.get(i).prettyPrint();
                }
            }
        }
        
    }
    @Override
    public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        for(int x = 0; x < suits.size(); x++) {
            if(x >= exprs.size()) { // Else
                trace("Else");
                return suits.get(x).eval(curScope);
            }
            if(exprs.get(x).eval(curScope).getBoolValue("If/else/elif", this)) { //Evaluate first one thats true
                if(x == 0)
                    trace("If");
                else
                    trace("Elif");
                return suits.get(x).eval(curScope);
            }
        }
	    return null;
    }
}
/**
 * While statement
 * Contains the expression and the body
 */
class AspWhile extends AspCompound {
    AspExpr ap;
    AspSuit as;

    AspWhile(int n) {
        super(n);
    }

    public static AspWhile parse(Scanner s) {
        enterParser("while stmt");
        
        AspWhile t = new AspWhile(s.curLineNum());
        skip(s, whileToken);
        t.ap = AspExpr.parse(s);
        skip(s, colonToken);
        t.as = AspSuit.parse(s);

        leaveParser("while stmt");

        return t;        
    }
    @Override
    public void prettyPrint() {
        prettyWrite(" while ");
        ap.prettyPrint();
        prettyWrite(":");
        prettyWriteLn();
        as.prettyPrint();
    }
    @Override
    public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {

        //RuntimeScope s = new RuntimeScope(curScope);
        trace("While");
        while(ap.eval(curScope).getBoolValue("While Loop", this)) {
            as.eval(curScope);
        }
        trace("End While");
	    return null;
    }
}
/**
 * Function statement
 * contains function name, list of parameters and body
 */
class AspDef extends AspCompound {
    AspName funcName;
    ArrayList<AspName> paramNames = new ArrayList<AspName>();
    AspSuit as;
    
    
    AspDef(int n) {
        super(n);
    }

    public static AspDef parse(Scanner s) {

        enterParser("func def");
        
        AspDef t = new AspDef(s.curLineNum());
        skip(s, defToken);
        t.funcName = AspName.parse(s);
        skip(s, leftParToken);

        while(true) {
            if(s.curToken().kind == nameToken) {
                t.paramNames.add(AspName.parse(s));
                if(s.curToken().kind == commaToken) {
                    skip(s, commaToken);
                }
                else{
                    break;
                }
            }else {
                break;
            }
        }
        skip(s, rightParToken);
        skip(s, colonToken);
        t.as = AspSuit.parse(s);

        leaveParser("func def");

        return t;
        
    }
    @Override
    public void prettyPrint() {
        prettyWrite("def ");
        funcName.prettyPrint();
        prettyWrite("(");
        for(int x = 0; x < paramNames.size(); x++) {
            paramNames.get(x).prettyPrint();
            if(x < paramNames.size() - 1) {
                prettyWrite(", ");
            }
        }
        prettyWrite(")");
        prettyWrite(":");
        prettyWriteLn();
        as.prettyPrint();
        prettyWriteLn();
    }
    @Override
    public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        ArrayList<String> pNames = new ArrayList<>();
        for(AspName n : paramNames) {
            pNames.add(n.name);
        } 
        RuntimeFunc n = new RuntimeFunc(pNames, as, curScope);
        trace("def "+ funcName.name);
        curScope.assign(funcName.name, n);
	    return null;
    }
}
