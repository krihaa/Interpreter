package runtime;

import main.*;
import parser.*;
import parser.AspSyntax;
import runtime.RuntimeValue;

import java.util.ArrayList;

public class RuntimeFunc extends RuntimeValue {
    ArrayList<String> paramNames;
    AspSuit suit;
    RuntimeScope outer;

    public RuntimeFunc(ArrayList<String> paramNames, AspSuit suit, RuntimeScope scope) {
        this.paramNames = paramNames;
        this.suit = suit;
        this.outer = scope;
    }

    @Override
    public String typeName() {
        return "function";
    }

    public RuntimeValue evalFuncCall(ArrayList<RuntimeValue> actualParams, AspSyntax where) {
        if (paramNames.size() != actualParams.size()) {
            runtimeError("Function called with wrong number of arguments!", where);
            return null;
        }
        RuntimeScope scope = new RuntimeScope(outer);
        for (int x = 0; x < actualParams.size(); x++) {
            scope.assign(paramNames.get(x), actualParams.get(x));
        }
        try {
            suit.eval(scope);
            return new RuntimeNoneValue();
        } catch (RuntimeReturnValue e) {
            return e.value;
        }
    }

}