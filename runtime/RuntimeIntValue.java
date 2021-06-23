package runtime;

import main.*;
import parser.AspSyntax;
import runtime.RuntimeValue;

public class RuntimeIntValue extends RuntimeValue {
    long intValue;

    public RuntimeIntValue(long v) {
        intValue = v;
    }


    @Override
    public String typeName() {
	    return "integrer";
    }


    @Override 
    public String toString() {
	    return Long.toString(intValue);
    }

    @Override
    public long getIntValue(String what, AspSyntax where) {
        return intValue;
    }

    @Override
    public boolean getBoolValue(String what, AspSyntax where) {
        if(intValue == 0) {
            return false;
        }
	    return true;
    }

    @Override
    public RuntimeValue evalNegate(AspSyntax where) {
        return new RuntimeIntValue(0-intValue);
    }

    @Override
    public RuntimeValue evalPositive(AspSyntax where) {
        return this; // This dosnt do anything?
    }

    @Override
    public RuntimeValue evalAdd(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeIntValue) { 
            return new RuntimeIntValue(intValue + v.getIntValue("", where));
        }else if(v instanceof RuntimeFloatValue) {
            return new RuntimeFloatValue(intValue + v.getFloatValue("", where));
        }
        runtimeError("'+' undefined for "+typeName()+" and "+v.typeName()+"!", where);
        return null;  
    }

    @Override
    public RuntimeValue evalSubtract(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeIntValue) { 
            return new RuntimeIntValue(intValue - v.getIntValue("", where));
        }else if(v instanceof RuntimeFloatValue) {
            return new RuntimeFloatValue(intValue- v.getFloatValue("", where));
        }
        runtimeError("'-' undefined for "+typeName()+" and "+v.typeName()+"!", where);
        return null;  
    }

    @Override
    public RuntimeValue evalDivide(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeIntValue) { 
            return new RuntimeFloatValue(intValue / (double) v.getIntValue("", where));
        }else if(v instanceof RuntimeFloatValue) {
            return new RuntimeFloatValue(intValue / (double) v.getFloatValue("", where) );
        }
        runtimeError("'/' undefined for "+typeName()+" and "+v.typeName()+"!", where);
        return null;  
    }

    @Override
    public RuntimeValue evalIntDivide(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeIntValue) { 
            return new RuntimeIntValue(Math.floorDiv(intValue, v.getIntValue("", where)));
        }else if(v instanceof RuntimeFloatValue) {
            return new RuntimeFloatValue(Math.floor((double) intValue / v.getFloatValue("", where)));
        }
        runtimeError("'//' undefined for "+typeName()+" and "+v.typeName()+"!", where);
        return null;  
    }

    @Override
    public RuntimeValue evalModulo(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeIntValue) { 
            return new RuntimeIntValue(Math.floorMod(intValue, v.getIntValue("", where)));
        }else if(v instanceof RuntimeFloatValue) {
            return new RuntimeFloatValue(intValue - v.getFloatValue("", where) *  Math.floor( (double) intValue / v.getFloatValue("", where) ));
        }
        runtimeError("'%' undefined for "+typeName()+" and "+v.typeName()+"!", where);
        return null;  
    }

    @Override
    public RuntimeValue evalMultiply(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeIntValue) { 
            return new RuntimeIntValue(intValue * v.getIntValue("", where));
        }else if(v instanceof RuntimeFloatValue) {
            return new RuntimeFloatValue(intValue * v.getFloatValue("", where));
        }
        runtimeError("'*' undefined for "+typeName()+" and "+v.typeName()+"!", where);
        return null;  
    }


    @Override
    public RuntimeValue evalGreater(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeIntValue) {
            if(intValue > v.getIntValue("", where)) {
                return new RuntimeBoolValue(true);
            }
            return new RuntimeBoolValue(false);
        } else if(v instanceof RuntimeFloatValue) {
            if(intValue > v.getFloatValue("", where)) {
                return new RuntimeBoolValue(true);
            }
            return new RuntimeBoolValue(false);
        }
	    runtimeError("Type error for >.", where);
	    return null;  // Required by the compiler
    }

    @Override
    public RuntimeValue evalGreaterEqual(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeIntValue) {
            if(intValue >= v.getIntValue("", where)) {
                return new RuntimeBoolValue(true);
            }
            return new RuntimeBoolValue(false);
        } else if(v instanceof RuntimeFloatValue) {
            if(intValue >= v.getFloatValue("", where)) {
                return new RuntimeBoolValue(true);
            }
            return new RuntimeBoolValue(false);
        }
	    runtimeError("Type error for >=.", where);
	    return null;  // Required by the compiler
    }

    @Override
    public RuntimeValue evalLess(RuntimeValue v, AspSyntax where) {
        RuntimeValue v2 = v.evalGreater(this, where);
        if(v2 == null) {
            runtimeError("Type error for <.", where);
            return null;  // Required by the compiler
        }
        return v2;
    }

    @Override
    public RuntimeValue evalLessEqual(RuntimeValue v, AspSyntax where) {
        RuntimeValue v2 = v.evalGreaterEqual(this, where);
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
        if (v instanceof RuntimeIntValue) {
            if(intValue == v.getIntValue("", where)) {
                return new RuntimeBoolValue(true);
            }
            return new RuntimeBoolValue(false);
        }else if(v instanceof RuntimeFloatValue) {
            if(intValue == v.getFloatValue("", where)) {
                return new RuntimeBoolValue(true);
            }
            return new RuntimeBoolValue(false);
        }
	    runtimeError("Type error for ==.", where);
	    return null;  // Required by the compiler
    }


    @Override
    public RuntimeValue evalNot(AspSyntax where) {
	    return new RuntimeBoolValue(!getBoolValue("", where));
    }


    @Override
    public RuntimeValue evalNotEqual(RuntimeValue v, AspSyntax where) {
        return evalEqual(v, where).evalNot(where);
    }
}