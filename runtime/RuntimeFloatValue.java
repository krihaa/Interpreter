package runtime;

import main.*;
import parser.AspSyntax;
import runtime.RuntimeValue;

public class RuntimeFloatValue extends RuntimeValue {
    double floatValue;

    public RuntimeFloatValue(double v) {
        floatValue = v;
    }


    @Override
    public String typeName() {
	    return "float";
    }


    @Override 
    public String toString() {
	    return Double.toString(floatValue);
    }

    @Override
    public double getFloatValue(String what, AspSyntax where) {
        return floatValue;
    }

    @Override
    public boolean getBoolValue(String what, AspSyntax where) {
        if(floatValue == 0) {
            return false;
        }
	    return true;
    }

    @Override
    public RuntimeValue evalNegate(AspSyntax where) {
        return new RuntimeFloatValue(0.0-floatValue);
    }

    @Override
    public RuntimeValue evalPositive(AspSyntax where) {
        return this;
    }

    @Override
    public RuntimeValue evalAdd(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeIntValue) { 
            return new RuntimeFloatValue(floatValue + v.getIntValue("", where));
        }else if(v instanceof RuntimeFloatValue) {
            return new RuntimeFloatValue(floatValue + v.getFloatValue("", where));
        }
        runtimeError("'+' undefined for "+typeName()+" and "+v.typeName()+"!", where);
        return null;  
    }

    @Override
    public RuntimeValue evalSubtract(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeIntValue) { 
            return new RuntimeFloatValue(floatValue - v.getIntValue("", where));
        }else if(v instanceof RuntimeFloatValue) {
            return new RuntimeFloatValue(floatValue - v.getFloatValue("", where));
        }
        runtimeError("'-' undefined for "+typeName()+" and "+v.typeName()+"!", where);
        return null;
    }

    @Override
    public RuntimeValue evalDivide(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeIntValue) { 
            return new RuntimeFloatValue(floatValue / (double) v.getIntValue("", where));
        }else if(v instanceof RuntimeFloatValue) {
            return new RuntimeFloatValue(floatValue / (double)  v.getFloatValue("", where));
        }
        runtimeError("'/' undefined for "+typeName()+" and "+v.typeName()+"!", where);
        return null;
    }

    @Override
    public RuntimeValue evalIntDivide(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeIntValue) { 
            return new RuntimeFloatValue(Math.floor(floatValue / (double) v.getIntValue("", where)));
        }else if(v instanceof RuntimeFloatValue) {
            return new RuntimeFloatValue(Math.floor(floatValue / (double)  v.getFloatValue("", where)));
        }
        runtimeError("'//' undefined for "+typeName()+" and "+v.typeName()+"!", where);
        return null;
    }

    @Override
    public RuntimeValue evalModulo(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeIntValue) {
            return new RuntimeFloatValue(floatValue - v.getIntValue("", where) *  Math.floor( floatValue / (double) v.getIntValue("", where)));
        }else if(v instanceof RuntimeFloatValue) {
            return new RuntimeFloatValue(floatValue - v.getFloatValue("", where) *  Math.floor(floatValue / (double) v.getFloatValue("", where)));
        }
        runtimeError("'%' undefined for "+typeName()+" and "+v.typeName()+"!", where);
        return null;
    }

    @Override
    public RuntimeValue evalMultiply(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeIntValue) { 
            return new RuntimeFloatValue(floatValue * v.getIntValue("", where));
        }else if(v instanceof RuntimeFloatValue) {
            return new RuntimeFloatValue(floatValue * v.getFloatValue("", where));
        }
        runtimeError("'*' undefined for "+typeName()+" and "+v.typeName()+"!", where);
        return null; 
    }


    @Override
    public RuntimeValue evalGreater(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeIntValue) { 
            if(floatValue > v.getIntValue("", where)) {
                return new RuntimeBoolValue(true);
            }
            return new RuntimeBoolValue(false);
        } else if(v instanceof RuntimeFloatValue) {
            if(floatValue > v.getFloatValue("", where)) {
                return new RuntimeBoolValue(true);
            }
            return new RuntimeBoolValue(false);
        }
	    runtimeError("Type error for >.", where);
	    return null;
    }

    @Override
    public RuntimeValue evalGreaterEqual(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeIntValue) { 
            if(floatValue >= v.getIntValue("", where)) {
                return new RuntimeBoolValue(true);
            }
            return new RuntimeBoolValue(false);
        } else if(v instanceof RuntimeFloatValue) {
            if(floatValue >= v.getFloatValue("", where)) {
                return new RuntimeBoolValue(true);
            }
            return new RuntimeBoolValue(false);
        }
	    runtimeError("Type error for >=.", where);
	    return null;
    }

    @Override
    public RuntimeValue evalLess(RuntimeValue v, AspSyntax where) {
        RuntimeValue v2 = v.evalGreater(this, where);
        if(v2 == null) {
            runtimeError("Type error for <.", where);
            return null;
        }
        return v2;
    }

    @Override
    public RuntimeValue evalLessEqual(RuntimeValue v, AspSyntax where) {
        RuntimeValue v2 = v.evalGreaterEqual(this, where);
        if(v2 == null) {
            runtimeError("Type error for <=.", where);
            return null;
        }
        return v2;
    }


    @Override
    public RuntimeValue evalEqual(RuntimeValue v, AspSyntax where) {
        if(v instanceof RuntimeNoneValue) {
            return new RuntimeBoolValue(false);
        }
        if (v instanceof RuntimeIntValue) {
            if(floatValue == v.getIntValue("", where)) {
                return new RuntimeBoolValue(true);
            }
            return new RuntimeBoolValue(false);
        }else if(v instanceof RuntimeFloatValue) {
            if(floatValue == v.getFloatValue("", where)) {
                return new RuntimeBoolValue(true);
            }
            return new RuntimeBoolValue(false);
        }
	    runtimeError("Type error for ==.", where);
	    return null;
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