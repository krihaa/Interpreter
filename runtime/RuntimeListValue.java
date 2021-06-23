package runtime;

import main.*;
import parser.AspSyntax;
import runtime.RuntimeIntValue;
import runtime.RuntimeNoneValue;
import runtime.RuntimeValue;
import java.util.ArrayList;
public class RuntimeListValue extends RuntimeValue {
    public ArrayList<RuntimeValue> exprs;

    public RuntimeListValue(ArrayList<RuntimeValue> exprs) {
        this.exprs = exprs;
    }


    @Override
    public String typeName() {
	    return "List";
    }


    @Override 
    public String toString() { //Here i get every element in the list and add them to a string
        String a = "";
        for(int x = 0; x < exprs.size(); x++) {
            a = a +exprs.get(x).showInfo(); // Adding , sign to separate elements
            if(x < exprs.size()-1) {
                a = a +",";
            }
        }
	    return "["+a+"]"; // Adding [] signs to make it look like a list
    }

    @Override
    public boolean getBoolValue(String what, AspSyntax where) { //False if list is empty, true otherwise
        if(exprs.size() <= 0) {
            return false;
        }
	    return true;
    }

    @Override
    public RuntimeValue evalLen(AspSyntax where) { // Length of the list
        return new RuntimeIntValue(exprs.size());
    }

    @Override
    public RuntimeValue evalSubscription(RuntimeValue v, AspSyntax where) {
        if(v instanceof RuntimeIntValue) {
            int i = (int)v.getIntValue("Subscription index", where);
            if(i >= 0 && i < exprs.size()) {
                return exprs.get(i);
            }
            runtimeError("List index out of bounds!", where);
        }
        runtimeError("List index must be a integrer!", where);
        return null;  
    }

    public void evalAssignElem(RuntimeValue inx, RuntimeValue val, AspSyntax where) {
        if(inx instanceof RuntimeIntValue) {
            int i = (int)inx.getIntValue("eval assingn element index", where);
            if(i >= 0 && i < exprs.size()) {
                exprs.set(i, val);
                return;
            }
            runtimeError("List index out of bounds!", where);
        }
        runtimeError("List index must be a integrer!", where);
    }

    @Override
    public RuntimeValue evalMultiply(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeIntValue) {
            ArrayList<RuntimeValue> nValues = new ArrayList<>();
            
            for(int x = 0; x < v.getIntValue("", where); x++) { // add in x times whats in this list into a new list
                for(RuntimeValue e : exprs) {
                    nValues.add(e);
                }
            }
            return new RuntimeListValue(nValues); // return the new list
        }
        runtimeError("'*' undefined for "+typeName()+" and "+v.typeName()+"!", where);
        return null;  
    }

    @Override
    public RuntimeValue evalEqual(RuntimeValue v, AspSyntax where) {
        if(v instanceof RuntimeNoneValue) {
            return new RuntimeBoolValue(false);
        }
        runtimeError("'==' undefined for "+typeName()+"!", where);
        return null;  
    }

    @Override
    public RuntimeValue evalNot(AspSyntax where) {
	    return new RuntimeBoolValue(getBoolValue("", where)).evalNot(where);
    }

    @Override
    public RuntimeValue evalNotEqual(RuntimeValue v, AspSyntax where) {
        return evalEqual(v, where).evalNot(where);
    }
}