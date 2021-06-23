package parser;

import java.util.ArrayList;
import main.*;
import parser.AspExpr;
import parser.AspName;
import parser.AspSyntax;
import runtime.*;
import scanner.*;
import static scanner.TokenKind.*;

/**
 * Mathematical operations on primaries
 */
class AspFact extends AspSyntax {
    ArrayList<AspSyntax> factories = new ArrayList<>();

    AspFact(int n) {
	    super(n);
    }
    static AspFact parse(Scanner s) {
	    enterParser("factor");
        AspFact t = new AspFact(s.curLineNum());
        while(true) {
            if(s.isFactorPrefix()) {
                t.factories.add(AspFactPre.parse(s));
            }
            t.factories.add(AspPrimary.parse(s));
            if(!s.isFactorOpr()) break;
            t.factories.add(AspFactOpr.parse(s));
        }

	    leaveParser("factor");
	    return t;
    }

    @Override
    public void prettyPrint() {
        for(AspSyntax s : factories) {
            s.prettyPrint();
        }
    }


    @Override
    public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        RuntimeValue res = null;
        int s = 0;
        if(factories.get(s) instanceof AspFactPre) { // Check if we have a prefix before the primary
                AspFactPre fp = (AspFactPre)factories.get(s);
                s += 1;
                AspPrimary p = (AspPrimary)factories.get(s);
                if(fp.opr == plusToken) {
                    res = p.eval(curScope).evalPositive(this);
                }else {
                    res = p.eval(curScope).evalNegate(this);
                }
        }else {
            AspPrimary p = (AspPrimary)factories.get(s);
            res = p.eval(curScope);
        }
        s += 1;

        //Apply operator to result using current  => 2*3*4 => [res = 2 , opr = *, cur = 3] => [res = 6, opr = * , cur = 4] => [res = 24]
        for(int x = s; x < factories.size(); x++) {

            AspFactOpr opr = (AspFactOpr)factories.get(x); //Get the operation
            x+=1;
            TokenKind k = opr.opr;

            RuntimeValue cur = null;
            if(factories.get(x) instanceof AspFactPre) { // Check if we have a prefix or not
                AspFactPre fp = (AspFactPre)factories.get(x);
                x += 1;
                AspPrimary p = (AspPrimary)factories.get(x);
                if(fp.opr == plusToken) {
                    cur = p.eval(curScope).evalPositive(this);
                }else {
                    cur = p.eval(curScope).evalNegate(this);
                }
            }else {
                AspPrimary p = (AspPrimary)factories.get(x);
                cur = p.eval(curScope);
            }
		    switch(k) { 
                case astToken:
                    res = res.evalMultiply(cur, this);
                    break;
                case slashToken:
                    res = res.evalDivide(cur, this);
                    break;
                case percentToken:
                    res = res.evalModulo(cur, this);
                    break;
                case doubleSlashToken:
                    res = res.evalIntDivide(cur, this);
                    break;
			default: // This shouldnt happen as parser will detect this error earlier.
				return null;
            }
        }
        return res;
    }
}
/**
 * Factory prefix
 * negate or positive
 */
class AspFactPre extends AspSyntax {
    TokenKind opr;
    AspFactPre(int n) {
	    super(n);
    }
    static AspFactPre parse(Scanner s) {
	    enterParser("factor prefix");
        AspFactPre t = new AspFactPre(s.curLineNum());
        t.opr = s.curToken().kind;
        s.readNextToken();
	    leaveParser("factor prefix");
	    return t;
    }

    @Override
    public void prettyPrint() {
        prettyWrite(opr.toString());
    }

    @Override
    public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
	//Unused (done in other class)
	return null;
    }
}
/**
 * Factory operator
 */
class AspFactOpr extends AspSyntax {
    TokenKind opr;
    AspFactOpr(int n) {
	    super(n);
    }
    static AspFactOpr parse(Scanner s) {
	    enterParser("factor opr");
        AspFactOpr t = new AspFactOpr(s.curLineNum());
        t.opr = s.curToken().kind;
        s.readNextToken();
	    leaveParser("factor opr");
	    return t;
    }

    @Override
    public void prettyPrint() {
        prettyWrite(opr.toString());
    }

    @Override
    public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
	//Unused (done in other class)
	return null;
    }
}