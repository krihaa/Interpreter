package parser;

import java.util.ArrayList;

import main.*;
import runtime.*;
import scanner.*;
import static scanner.TokenKind.*;

/**
 * The Asp Program
 */
public class AspProgram extends AspSyntax {
    ArrayList<AspStmt> stmts = new ArrayList<>();

    AspProgram(int n) {
	    super(n);
    }
    
    public static AspProgram parse(Scanner s) {
	    enterParser("program");

        AspProgram ap = new AspProgram(s.curLineNum());
        while (s.curToken().kind != eofToken) {
            ap.stmts.add(AspStmt.parse(s));
        }

        leaveParser("program");
        return ap;
    }


    @Override
    public void prettyPrint() {
        for(AspStmt s : stmts) {
            s.prettyPrint();
        }
    }


    @Override
    public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        for(AspStmt s : stmts) {
            s.eval(curScope);
        }
	    return null;
    }
}
