package parser;

import java.util.ArrayList;

import main.*;
import runtime.*;
import scanner.*;
import static scanner.TokenKind.*;

/**
 * A expression
 * Can contain mathematical/logical expressions or a atom (string,int, etc)
 */
public class AspExpr extends AspSyntax {
    ArrayList<AspAndTest> andTests = new ArrayList<>();

    AspExpr(int n) {
	    super(n);
    }
    public static AspExpr parse(Scanner s) {
        enterParser("expr");
        AspExpr ae = new AspExpr(s.curLineNum());

        while(true) {
            ae.andTests.add(AspAndTest.parse(s));
            if(s.curToken().kind != orToken) break;
            skip(s, orToken);
        }
        leaveParser("expr");
        return ae;
    }


    @Override
    public void prettyPrint() {
        int j = 0;
        for(AspAndTest s : andTests) {
            if(j > 0) {
                prettyWrite(" or ");
            }
            s.prettyPrint();
            j++;
        }
    }


    @Override
    public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        RuntimeValue v = andTests.get(0).eval(curScope);
        for (int i = 1; i < andTests.size(); ++i) {
            if(v.getBoolValue("or operator", this))
                return v;
            v = andTests.get(i).eval(curScope);
        }
        return v;
    }
}

/**
 * And expressions
 */
class AspAndTest extends AspSyntax {
    ArrayList<AspNotTest> notTest = new ArrayList<>(); //Assignment

    AspAndTest(int n) {
	    super(n);
    }

    static AspAndTest parse(Scanner s) {
	    enterParser("and test");
        
        AspAndTest test = new AspAndTest(s.curLineNum());
        while(true) {
            test.notTest.add(AspNotTest.parse(s));
            if(s.curToken().kind != andToken) break;
            skip(s, andToken);
        }
	    leaveParser("and test");
	    return test;
    }

    @Override
    public void prettyPrint() {
        int j = 0;
        for(AspNotTest s : notTest) {
            if(j > 0) {
                prettyWrite(" and ");
            }
            s.prettyPrint();
            j++;
        }
    }

    @Override
    public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        RuntimeValue v = notTest.get(0).eval(curScope);
        for (int i = 1; i < notTest.size(); ++i) {
            if(!v.getBoolValue("and operator", this))
                return v;
            v = notTest.get(i).eval(curScope);
        }
	    return v;
    }
}
/**
 * Not Expressions
 */
class AspNotTest extends AspSyntax {
    AspComp comp;
    boolean not = false;

    AspNotTest(int n) {
	    super(n);
    }
    static AspNotTest parse(Scanner s) {
	    enterParser("not test");
        
        AspNotTest t = new AspNotTest(s.curLineNum());

        if(s.curToken().kind == notToken) {
            skip(s, notToken);
            t.not = true;
        }

        t.comp = AspComp.parse(s);

	    leaveParser("not test");
	    return t;
    }

    @Override
    public void prettyPrint() {
        if(not) {
            prettyWrite(" not ");
        }
        comp.prettyPrint();
    }

    @Override
    public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        RuntimeValue v = comp.eval(curScope);
        if(not) {
            return v.evalNot(this);
        }
	    return v;
    }
}
/**
 * Comparative expression
 * Contains comparative operators and the terms
 */
class AspComp extends AspSyntax {
    ArrayList<AspTerm> terms = new ArrayList<>();
    ArrayList<AspCompOpr> oprs = new ArrayList<>();

    AspComp(int n) {
	    super(n);
    }
    static AspComp parse(Scanner s) {
	    enterParser("comparison");
        
        AspComp t = new AspComp(s.curLineNum());

        while(true) {
            t.terms.add(AspTerm.parse(s));
            if(!s.isCompOpr()) break;
            t.oprs.add(AspCompOpr.parse(s));
        }

	    leaveParser("comparison");
	    return t;
    }

    @Override
    public void prettyPrint() {
        int i = terms.size() > oprs.size() ? terms.size() : oprs.size();
        for(int x = 0; x < i; x++) {
            if(x < terms.size()) {
                terms.get(x).prettyPrint();
            }
            if(x < oprs.size()) {
                oprs.get(x).prettyPrint();
            }
        }
    }

    // 1 < 0
    // 1 <= 2 <= 3

    @Override
    public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        RuntimeValue t = terms.get(0).eval(curScope); 
        for(int x = 0; x < oprs.size(); x++) {
            TokenKind k = oprs.get(x).opr;
            RuntimeValue t2 = terms.get(x+1).eval(curScope);
            switch(k) {
                case lessToken:
                    t = t.evalLess(t2, this);
                    break;
                case lessEqualToken:
                    t = t.evalLessEqual(t2, this);
                    break;
                case greaterToken:
                    t = t.evalGreater(t2, this);
                    break;
                case greaterEqualToken:
                    t = t.evalGreaterEqual(t2, this);
                    break;
                case doubleEqualToken:
                    t = t.evalEqual(t2, this);
                    break;
                case notEqualToken:
                    t = t.evalNotEqual(t2, this);
                    break;
                default:
                    return null;
            }
            if(!t.getBoolValue("comp", this) || x+1 >= oprs.size()) {
                return t;
            }
            t = t2;
        }
        return t;
    }
}
/**
 * Comparison operator
 */
class AspCompOpr extends AspSyntax {
    TokenKind opr;
    AspCompOpr(int n) {
	    super(n);
    }
    static AspCompOpr parse(Scanner s) {
	    enterParser("comp opr");
        AspCompOpr t = new AspCompOpr(s.curLineNum());
        t.opr = s.curToken().kind;
        s.readNextToken();
	    leaveParser("comp opr");
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

class AspTerm extends AspSyntax {
    ArrayList<AspFact> facts = new ArrayList<>();
    ArrayList<AspTermOpr> oprs = new ArrayList<>();
    AspTerm(int n) {
	    super(n);
    }
    static AspTerm parse(Scanner s) {
	    enterParser("term");
        
        AspTerm t = new AspTerm(s.curLineNum());

        while(true) {
            t.facts.add(AspFact.parse(s));
            if(!s.isTermOpr()) break;
            t.oprs.add(AspTermOpr.parse(s));
        }

	    leaveParser("term");
	    return t;
    }

    @Override
    public void prettyPrint() {
        int i = facts.size() > oprs.size() ? facts.size() : oprs.size();
        for(int x = 0; x < i; x++) {
            if(x < facts.size()) {
                facts.get(x).prettyPrint();
            }
            if(x < oprs.size()) {
                oprs.get(x).prettyPrint();
            }
        }
    }

    @Override
    public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        RuntimeValue t = facts.get(0).eval(curScope);
        for(int x = 0; x < oprs.size(); x++) {
            if(oprs.get(x).opr == plusToken) {
                t = t.evalAdd(facts.get(x+1).eval(curScope), this); // Evaluate expr first then add them together
            }
            if(oprs.get(x).opr == minusToken) {
                t = t.evalSubtract(facts.get(x+1).eval(curScope), this);
            }
        }
        return t;
    }
}
// adding or substracting
class AspTermOpr extends AspSyntax {
    TokenKind opr;
    AspTermOpr(int n) {
	    super(n);
    }
    static AspTermOpr parse(Scanner s) {
	    enterParser("term opr");
        AspTermOpr t = new AspTermOpr(s.curLineNum());
        t.opr = s.curToken().kind;
        s.readNextToken();
	    leaveParser("term opr");
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
 * Indexering i primitiver, f.eks i lister
 */
class AspPrimary extends AspSyntax {
    AspAtom atom;
    ArrayList<AspSyntax> suffs = new ArrayList<>();

    AspPrimary(int n) {
	    super(n);
    }
    static AspPrimary parse(Scanner s) {
	    enterParser("primary");
        AspPrimary t = new AspPrimary(s.curLineNum());
        t.atom = AspAtom.parse(s);
        while(s.curToken().kind == leftBracketToken || s.curToken().kind == leftParToken) {
            t.suffs.add(AspPrimarySuffix.parse(s));
        }

	    leaveParser("primary");
	    return t;
    }

    @Override
    public void prettyPrint() {
        atom.prettyPrint();
        for(AspSyntax s : suffs) {
            s.prettyPrint();
        }
    }

    @Override
    public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        RuntimeValue v = atom.eval(curScope);
        if(atom instanceof AspName) { // Variable name
            v = curScope.find(v.toString(), this); // Find the variable
        }
        if(suffs.size() > 0) { // If it has arguments
            if(suffs.get(0) instanceof AspSub) {
                for(int x = 0; x < suffs.size(); x++) {
                    v = v.evalSubscription(suffs.get(x).eval(curScope), this);
                }
            } else {
                ArrayList<RuntimeValue> args = new ArrayList<>();
                for(AspSyntax s : suffs) {
                    AspArg a = (AspArg)s;
                    for( AspExpr e : a.exprs) {
                        args.add(e.eval(curScope));
                    }
                }
                trace("Called function: "+ ((AspName)atom).name+ " with params: " + args.toString());
                v = v.evalFuncCall(args, this); // eval function and set v to the value the function returns
                trace("Return: "+ v.toString());
            }
        }
        return v;
    }
}
/**
 * Check if primary is a subscription or argument
 */
abstract class AspPrimarySuffix extends AspSyntax {
    AspPrimarySuffix(int n) {
	    super(n);
    }
    static AspSyntax parse(Scanner s) {
        enterParser("primary suffix");
        AspSyntax ret = null;
        if(s.curToken().kind == leftBracketToken) {
            ret = AspSub.parse(s);
        }else if(s.curToken().kind == leftParToken) {
            ret = AspArg.parse(s);
        }else {
            parserError("Expected '[' or '(' but found " + 
			s.curToken().kind + "!", s.curLineNum());
        }
	    leaveParser("primary suffix");
	    return ret;
    }

    @Override
    public void prettyPrint() {
	//abstract
    }

    @Override
    public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
	//abstract
	return null;
    }
}
/**
 * Abstract class to create primary types
 * variables, integrers, floats, strings, boolean, etc..
 */
abstract class AspAtom extends AspSyntax {
    AspAtom(int n) {
	    super(n);
    }
    static AspAtom parse(Scanner s) {
	    enterParser("atom");
        AspAtom t = null;
        
        switch (s.curToken().kind) {
            case nameToken:
                t = AspName.parse(s);
                break;
            case integerToken:
                t = AspInteg.parse(s);
                break;
            case floatToken:
                t = AspFloat.parse(s);
                break;
            case stringToken:
                t = AspString.parse(s);
                break;
            case trueToken:
            case falseToken:
                t = AspBoolean.parse(s);
                break;
            case noneToken:
                t = AspNone.parse(s);
                break;
            case leftParToken:
                t = AspInnerExpr.parse(s);
                break;
            case leftBracketToken:
                t = AspListDisplay.parse(s);
                break;
            case leftBraceToken:
                t = AspDictDisplay.parse(s);
                break;
            default:
                s.printParserError();

        }

	    leaveParser("atom");
	    return t;
    }

    @Override
    public void prettyPrint() {
	//abstrakt
    }

    @Override
    public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
	//abstract
	return null;
    }
}
/**
 * Inner expression
 */
class AspInnerExpr extends AspAtom {
    AspExpr expr;

    AspInnerExpr(int n) {
	    super(n);
    }
    static AspInnerExpr parse(Scanner s) {
	    enterParser("inner expr");
        AspInnerExpr t = new AspInnerExpr(s.curLineNum());
        skip(s,leftParToken);
        t.expr = AspExpr.parse(s);
        skip(s, rightParToken);
	    leaveParser("inner expr");
	    return t;
    }

    @Override
    public void prettyPrint() {
        prettyWrite("(");
        expr.prettyPrint();
        prettyWrite(")");
    }

    @Override
    public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        return expr.eval(curScope);
    }
}
/**
 * A List of expressions
 */
class AspListDisplay extends AspAtom {
    ArrayList<AspExpr> exprs = new ArrayList<AspExpr>();
    AspListDisplay(int n) {
	    super(n);
    }
    static AspListDisplay parse(Scanner s) {
	    enterParser("list display");
        AspListDisplay t = new AspListDisplay(s.curLineNum());
        skip(s,leftBracketToken);
        while(s.curToken().kind != rightBracketToken) {
            t.exprs.add(AspExpr.parse(s));
            if(s.curToken().kind == commaToken) {
                skip(s, commaToken);
            }else{
                break;
            }
        }
        
        skip(s, rightBracketToken);
	    leaveParser("list display");
	    return t;
    }

    @Override
    public void prettyPrint() {
        prettyWrite("[");

        for(int x = 0; x < exprs.size(); x++) {
            exprs.get(x).prettyPrint();
            if(x < exprs.size()-1) {
                prettyWrite(", ");
            }
        }
        prettyWrite("]");
    }

    @Override
    public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        ArrayList<RuntimeValue> v = new ArrayList<>();
        for (AspExpr e : exprs) {
            v.add(e.eval(curScope)); //Evaluates the expressions first then add them to the list
        }
        RuntimeListValue v2 = new RuntimeListValue(v);
	    return v2;
    }
}
/**
 * A Dictionary
 * Contains expressions and the corresponding string
 */
class AspDictDisplay extends AspAtom {
    ArrayList<AspExpr> exprs = new ArrayList<AspExpr>();
    ArrayList<AspString> stirngLit = new ArrayList<AspString>();    
    AspDictDisplay(int n) {
	    super(n);
    }
    static AspDictDisplay parse(Scanner s) {
	    enterParser("dict display");
        AspDictDisplay t = new AspDictDisplay(s.curLineNum());
        skip(s,leftBraceToken);
        while(s.curToken().kind != rightBraceToken) {
            t.stirngLit.add(AspString.parse(s));
            skip(s,colonToken);
            t.exprs.add(AspExpr.parse(s));
            if(s.curToken().kind == commaToken) {
                skip(s, commaToken);
            }else{
                break;
            }
        }
        skip(s, rightBraceToken);
	    leaveParser("dict display");
	    return t;
    }

    @Override
    public void prettyPrint() {
        prettyWrite("{");

        for(int x = 0; x < stirngLit.size(); x++) {
            stirngLit.get(x).prettyPrint();
            prettyWrite(":");
            exprs.get(x).prettyPrint();
            if(x < stirngLit.size()-1) {
                prettyWrite(", ");
            }
        }
        prettyWrite("}");
    }

    @Override
    public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        ArrayList<RuntimeValue> values = new ArrayList<>();
        ArrayList<RuntimeValue> keys = new ArrayList<>();
        for (AspExpr e : exprs) {
            values.add(e.eval(curScope)); //Evaluates the expressions first then add them to the list
        }
        for(AspString e : stirngLit) {
            keys.add(e.eval(curScope));
        }
        RuntimeDictValue v2 = new RuntimeDictValue(keys,values);
	    return v2;
    }
}
/**
 * Arguments
 */
class AspArg extends AspSyntax {
    ArrayList<AspExpr> exprs = new ArrayList<AspExpr>();
    AspArg(int n) {
	    super(n);
    }
    static AspArg parse(Scanner s) {
	    enterParser("arguments");
        AspArg t = new AspArg(s.curLineNum());
        skip(s,leftParToken);
        if(s.curToken().kind != rightParToken) {
            while(true) {
                t.exprs.add(AspExpr.parse(s));
                if(s.curToken().kind == commaToken) {
                    skip(s, commaToken);
                }else{
                    break;
                }
            }
        }
        skip(s, rightParToken);
	    leaveParser("arguments");
	    return t;
    }

    @Override
    public void prettyPrint() {
        prettyWrite("(");
        for(int x = 0; x < exprs.size(); x++) {
            exprs.get(x).prettyPrint();
            if(x < exprs.size()-1) {
                prettyWrite(", ");
            }
        }
        prettyWrite(")");
    }

    @Override
    public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
	return null;
    }
}

/**
 * A subscription into a variable
 */
class AspSub extends AspSyntax {
    AspExpr exprs; 

    AspSub(int n) {
	    super(n);
    }

    static AspSub parse(Scanner s) {
	    enterParser("subscription");
        AspSub t = new AspSub(s.curLineNum());
        skip(s, leftBracketToken);
        t.exprs = AspExpr.parse(s);
        skip(s, rightBracketToken);
	    leaveParser("subscription");
	    return t;
    }

    @Override
    public void prettyPrint() {
        prettyWrite("[");
        exprs.prettyPrint();
        prettyWrite("]");
    }

    @Override
    public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        return exprs.eval(curScope);
    }
}