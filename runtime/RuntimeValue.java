package runtime;

import java.util.ArrayList;

import main.*;
import parser.AspSyntax;

public abstract class RuntimeValue {
    abstract public String typeName();

    public String showInfo() {
	return toString();
    }

    public boolean getBoolValue(String what, AspSyntax where) {
	runtimeError("Type error: "+what+" is not a Boolean!", where);
	return false;  
    }

    public double getFloatValue(String what, AspSyntax where) {
	runtimeError("Type error: "+what+" is not a float!", where);
	return 0.0;  
    }

    public long getIntValue(String what, AspSyntax where) {
	runtimeError("Type error: "+what+" is not an integer!", where);
	return 0;  
    }

    public String getStringValue(String what, AspSyntax where) {
	runtimeError("Type error: "+what+" is not a text string!", where);
	return null;  
    }

    // For part 3:

    public RuntimeValue evalAdd(RuntimeValue v, AspSyntax where) {
	runtimeError("'+' undefined for "+typeName()+"!", where);
	return null;  
    }

    public RuntimeValue evalDivide(RuntimeValue v, AspSyntax where) {
	runtimeError("'/' undefined for "+typeName()+"!", where);
	return null;  
    }

    public RuntimeValue evalEqual(RuntimeValue v, AspSyntax where) {
	runtimeError("'==' undefined for "+typeName()+"!", where);
	return null;  
    }

    public RuntimeValue evalGreater(RuntimeValue v, AspSyntax where) {
	runtimeError("'>' undefined for "+typeName()+"!", where);
	return null;  
    }

    public RuntimeValue evalGreaterEqual(RuntimeValue v, AspSyntax where) {
	runtimeError("'>=' undefined for "+typeName()+"!", where);
	return null;  
    }

    public RuntimeValue evalIntDivide(RuntimeValue v, AspSyntax where) {
	runtimeError("'//' undefined for "+typeName()+"!", where);
	return null;  
    }

    public RuntimeValue evalLen(AspSyntax where) {
	runtimeError("'len' undefined for "+typeName()+"!", where);
	return null;  
    }

    public RuntimeValue evalLess(RuntimeValue v, AspSyntax where) {
	runtimeError("'<' undefined for "+typeName()+"!", where);
	return null;  
    }

    public RuntimeValue evalLessEqual(RuntimeValue v, AspSyntax where) {
	runtimeError("'<=' undefined for "+typeName()+"!", where);
	return null;  
    }

    public RuntimeValue evalModulo(RuntimeValue v, AspSyntax where) {
	runtimeError("'%' undefined for "+typeName()+"!", where);
	return null;  
    }

    public RuntimeValue evalMultiply(RuntimeValue v, AspSyntax where) {
	runtimeError("'*' undefined for "+typeName()+"!", where);
	return null;  
    }

    public RuntimeValue evalNegate(AspSyntax where) {
	runtimeError("Unary '-' undefined for "+typeName()+"!", where);
	return null;  
    }

    public RuntimeValue evalNot(AspSyntax where) {
	runtimeError("'not' undefined for "+typeName()+"!", where);
	return null;  
    }

    public RuntimeValue evalNotEqual(RuntimeValue v, AspSyntax where) {
	runtimeError("'!=' undefined for "+typeName()+"!", where);
	return null;  
    }

    public RuntimeValue evalPositive(AspSyntax where) {
	runtimeError("Unary '+' undefined for "+typeName()+"!", where);
	return null;  
    }

    public RuntimeValue evalSubscription(RuntimeValue v, AspSyntax where) {
	runtimeError("Subscription '[...]' undefined for "+typeName()+"!", where);
	return null;  
    }

    public RuntimeValue evalSubtract(RuntimeValue v, AspSyntax where) {
	runtimeError("'-' undefined for "+typeName()+"!", where);
	return null;  
    }

    // General:

    public static void runtimeError(String message, int lNum) {
	Main.error("Asp runtime error on line " + lNum + ": " + message);
    }

    public static void runtimeError(String message, AspSyntax where) {
	runtimeError(message, where.lineNum);
    }

    // For part 4:

    public void evalAssignElem(RuntimeValue inx, RuntimeValue val, AspSyntax where) {
	runtimeError("Assigning to an element not allowed for "+typeName()+"!", where);
    }

    public RuntimeValue evalFuncCall(ArrayList<RuntimeValue> actualParams, 
				     AspSyntax where) {
	runtimeError("'Function call (...)' undefined for "+typeName()+"!", where);
	return null;  
    }
}
