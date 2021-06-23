package parser;

import java.util.ArrayList;
import parser.AspSyntax;
import runtime.*;
import scanner.*;
import static scanner.TokenKind.*;

/**
 * Suite, the body contained in other statements, for example a for statement or function statement
 */
public class AspSuit extends AspSyntax {
    ArrayList<AspSyntax> stmts = new ArrayList<AspSyntax>();
    AspSuit(int n) {
	    super(n);
    }
    public static AspSuit parse(Scanner s) {
        enterParser("suite");
        
        AspSuit t = new AspSuit(s.curLineNum());
        if(s.curToken().kind != newLineToken) {
            t.stmts.add(AspSmallList.parse(s));
        }else if(s.curToken().kind == newLineToken) {
            skip(s, newLineToken);
            skip(s, indentToken);
            while(s.curToken().kind != dedentToken) {
                t.stmts.add(AspStmt.parse(s));
            }
            skip(s, dedentToken);
        }else {
            s.printParserError();
        }
        leaveParser("suite");

        return t;
	    
    }


    @Override
    public void prettyPrint() {
        for(AspSyntax s : stmts) {
            if(s instanceof AspStmt) {
                    prettyIndent();
                    s.prettyPrint();
                    prettyDedent();
            }
            
        }
    }


    @Override
    public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
	for(AspSyntax s : stmts) {
        s.eval(curScope);
    }
	return null;
    }
}