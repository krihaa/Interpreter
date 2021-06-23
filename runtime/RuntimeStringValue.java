package runtime;

import main.*;
import parser.AspSyntax;
import runtime.RuntimeValue;

public class RuntimeStringValue extends RuntimeValue {
    String strValue;

    public RuntimeStringValue(String v) {
        strValue = v;
    }


    @Override
    public String typeName() {
	    return "String";
    }

    public String showInfo() {
        return "\""+toString()+"\"";
    }

    @Override 
    public String toString() {
	    return strValue;
    }

    @Override
    public String getStringValue(String what, AspSyntax where) {
        return strValue;
    }

    @Override
    public boolean getBoolValue(String what, AspSyntax where) {
        if(strValue.equals("")) {
            return false;
        }
	    return true;
    }

    @Override
    public RuntimeValue evalLen(AspSyntax where) {
        return new RuntimeIntValue(strValue.length());
    }

    @Override
    public RuntimeValue evalAdd(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeStringValue) { 
            return new RuntimeStringValue(strValue + v.getStringValue("", where));
        }
        runtimeError("'+' undefined for "+typeName()+" and "+v.typeName()+"!", where);
        return null;  
    }

    @Override
    public RuntimeValue evalMultiply(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeIntValue) {
            String nString = "";
            for(int x = 0; x < v.getIntValue("", where); x++) {
                nString = nString + strValue;
            }
            return new RuntimeStringValue(nString);
        }
        runtimeError("'*' undefined for "+typeName()+" and "+v.typeName()+"!", where);
        return null;  
    }


    @Override
    public RuntimeValue evalGreater(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeStringValue) { 
            if(strValue.compareTo(v.getStringValue("", where)) > 0) {
                return new RuntimeBoolValue(true);
            }
            return new RuntimeBoolValue(false);
        }
	    runtimeError("Type error for >.", where);
	    return null;  // Required by the compiler
    }

    @Override
    public RuntimeValue evalGreaterEqual(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeStringValue) { 
            if(strValue.compareTo(v.getStringValue("", where)) >= 0) {
                return new RuntimeBoolValue(true);
            }
            return new RuntimeBoolValue(false);
        }
	    runtimeError("Type error for >=.", where);
	    return null;  // Required by the compiler
    }

    @Override
    public RuntimeValue evalLess(RuntimeValue v, AspSyntax where) {
        RuntimeValue v2 = v.evalGreaterEqual(this, where);
        if(v2 == null) {
            runtimeError("Type error for <.", where);
            return null;  // Required by the compiler
        }
        return v2;
    }

    @Override
    public RuntimeValue evalLessEqual(RuntimeValue v, AspSyntax where) {
        RuntimeValue v2 = v.evalGreater(this, where);
        if(v2 == null) {
            runtimeError("Type error for <=.", where);
            return null;  // Required by the compiler
        }
        return v2;
    }


    @Override
    public RuntimeValue evalEqual(RuntimeValue v, AspSyntax where) {
        if(v instanceof RuntimeNoneValue) {
            return new RuntimeBoolValue(false);
        }
        if (v instanceof RuntimeStringValue) { 
            if(strValue.compareTo(v.getStringValue("", where)) == 0) {
                return new RuntimeBoolValue(true);
            }
            return new RuntimeBoolValue(false);
        }
	    runtimeError("Type error for ==.", where);
	    return null;  // Required by the compiler
    }


    @Override
    public RuntimeValue evalNot(AspSyntax where) {
	    return new RuntimeBoolValue(getBoolValue("", where)).evalNot(where);
    }


    @Override
    public RuntimeValue evalNotEqual(RuntimeValue v, AspSyntax where) {
        RuntimeValue v2 = evalEqual(v, where);
        if(v2 != null) {
            return v2.evalNot(where);
        }
	    runtimeError("Type error for !=.", where);
	    return null;  // Required by the compiler
    }
}