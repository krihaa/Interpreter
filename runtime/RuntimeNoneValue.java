package runtime;

import main.*;
import parser.AspSyntax;

public class RuntimeNoneValue extends RuntimeValue {
    @Override
    public String typeName() {
	return "None";
    }


    @Override 
    public String toString() {
	return "None";
    }


    @Override
    public boolean getBoolValue(String what, AspSyntax where) {
	return false;
    }


    @Override
    public RuntimeValue evalEqual(RuntimeValue v, AspSyntax where) {
	return new RuntimeBoolValue(v instanceof RuntimeNoneValue);
    }


    @Override
    public RuntimeValue evalNot(AspSyntax where) {
	return new RuntimeBoolValue(true);
    }


    @Override
    public RuntimeValue evalNotEqual(RuntimeValue v, AspSyntax where) {
	return new RuntimeBoolValue(!(v instanceof RuntimeNoneValue));
    }
}
