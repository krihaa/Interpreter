package parser;

import java.util.ArrayList;
import parser.AspExpr;
import parser.AspSyntax;
import runtime.*;
import scanner.*;
import static scanner.TokenKind.*;

/**
 * A small list of statements, on a single line. for example
 * a=5; b=2;
 */
class AspSmallList extends AspStmt {
    ArrayList<AspSyntax> smalls = new ArrayList<>();
    AspSmallList(int n) {
        super(n);
    }

    public static AspSmallList parse(Scanner s) {
        enterParser("small stmt list");
        AspSmallList t = new AspSmallList(s.curLineNum());
        while(true) {
            if(s.curToken().kind == newLineToken) break;
            t.smalls.add(AspSmallStmt.parse(s));
            if(s.curToken().kind != semicolonToken) break;
            skip(s, semicolonToken);
        }
        skip(s, newLineToken);
        leaveParser("small stmt list");
        return t;
    }
    @Override
    public void prettyPrint() {
        int i = 0;
        for (AspSyntax s : smalls) {
            s.prettyPrint();
            if(smalls.size() > 0) {
                if(i < smalls.size() - 1)
                    prettyWrite("; ");
                else
                    prettyWrite(";");
            }
            i++;
        }
        prettyWriteLn();
    }
    @Override
    public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
	for (AspSyntax s : smalls) {
        s.eval(curScope);
    }
	return null;
    }
}
/**
 *  Small statement
 *  can contain either pass, return, variable assignment or variable expression statement
 */
abstract class AspSmallStmt extends AspSyntax {
    AspSmallStmt(int n) {
        super(n);
    }

    public static AspSyntax parse(Scanner s) {
        enterParser("small stmt");
        AspSmallStmt t = null;
        switch (s.curToken().kind) {
            case passToken:
                t = AspPass.parse(s);
                break;
            case returnToken:
                t = AspReturn.parse(s);
                break;
            case nameToken:
                if(s.anyEqualToken()) {
                    t = AspAmt.parse(s);
                }else {
                    t = AspExprStmt.parse(s);
                }
                break;
            default:
                s.printScannerError("Invalid small statement"+ s.curToken().kind);
        }
        leaveParser("small stmt");
        return t;
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
 * Wrapper class for expressions inside a small statement
 */
class AspExprStmt extends AspSmallStmt {
    AspExpr expr;
    AspExprStmt(int n) {
	    super(n);
    }
    public static AspExprStmt parse(Scanner s) {
	    enterParser("expr stmt");
        AspExprStmt t = new AspExprStmt(s.curLineNum());
        t.expr = AspExpr.parse(s);
	    leaveParser("expr stmt");
	    return t;
    }
    @Override
    public void prettyPrint() {
        expr.prettyPrint();
    }
    @Override
    public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
	    return expr.eval(curScope);
    }
}
/**
 * a return statement with its expression
 * example: return 5
 */
class AspReturn extends AspSmallStmt {
    AspExpr expr;
    AspReturn(int n) {
	    super(n);
    }
    public static AspReturn parse(Scanner s) {
	    enterParser("return stmt");
        AspReturn t = new AspReturn(s.curLineNum());
        skip(s, returnToken);
        t.expr = AspExpr.parse(s);
	    leaveParser("return stmt");
	    return t;
    }
    @Override
    public void prettyPrint() {
        prettyWrite("return ");
        expr.prettyPrint();
    }
    @Override
    public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
	    throw new RuntimeReturnValue(expr.eval(curScope), lineNum);
    }
}
/**
 * A simple pass statement
 */
class AspPass extends AspSmallStmt {
    AspPass(int n) {
	    super(n);
    }
    public static AspPass parse(Scanner s) {
	    enterParser("pass stmt");
        AspPass t = new AspPass(s.curLineNum());
        skip(s, passToken);
	    leaveParser("pass stmt");
	    return t;
    }
    @Override
    public void prettyPrint() {
        prettyWrite("pass");
        prettyWriteLn();
    }
    @Override
    public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
	return null;
    }
}
/**
 * Assigment statement, contains the variable name and the expression linked to it
 */
class AspAmt extends AspSmallStmt { //Assignment class
    ArrayList<AspSub> subs = new ArrayList<>();
    AspName name;
    AspExpr expr;

    AspAmt(int n) {
	    super(n);
    }


    public static AspAmt parse(Scanner s) {
	    enterParser("assignment");
        AspAmt t = new AspAmt(s.curLineNum());
        t.name = AspName.parse(s);
        while(s.curToken().kind == leftBracketToken) {
            t.subs.add(AspSub.parse(s));
        }
        skip(s, equalToken);
        t.expr = AspExpr.parse(s);

	    leaveParser("assignment");
	    return t;
    }


    @Override
    public void prettyPrint() {
        name.prettyPrint();
        for(AspSub s : subs) {
            s.prettyPrint();
        }
        prettyWrite(" = ");
        expr.prettyPrint();
    }


    @Override
    public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        String n = name.eval(curScope).toString();
        if(subs.size() <= 0) {
            // If we have a variable that exists in a outer scope, we need to update it in that scope and not add it again in this scope
            RuntimeValue v = expr.eval(curScope);
            RuntimeScope s = curScope.findScope(n, this);
            if(s == null) {
                curScope.assign(n, v);
            }
            else {
                s.assign(n, v);
            }
            trace(n +" = " +v.toString());
        }else {
            RuntimeValue v = curScope.find(n, this); // find list/dictionary
            for(int x = 0; x < subs.size() - 1; x++) { // Go trought all exept last
                v = v.evalSubscription(subs.get(x).eval(curScope), this);
            }
            v.evalAssignElem(subs.get(subs.size() - 1).eval(curScope), expr.eval(curScope), this); // Assign the last element in the list
        }
	    return null;
    }
}