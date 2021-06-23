package runtime;

import main.*;
import parser.AspSyntax;
import runtime.RuntimeIntValue;
import runtime.RuntimeNoneValue;
import runtime.RuntimeStringValue;
import runtime.RuntimeValue;
import java.util.ArrayList;
public class RuntimeDictValue extends RuntimeValue {
    ArrayList<RuntimeValue> keys;
    ArrayList<RuntimeValue> values;
    public RuntimeDictValue(ArrayList<RuntimeValue> keys, ArrayList<RuntimeValue> values) {
        this.keys = keys;
        this.values = values;
    }

    @Override
    public String typeName() {
	    return "Dictionary";
    }


    @Override 
    public String toString() { //Here i get every element in the dict and add them to a string
        String a = "";
        for(int x = 0; x < keys.size(); x++) {
            a = a + keys.get(x).showInfo();
            a = a + ":";
            a = a +values.get(x).showInfo();
            
            if(x < keys.size()-1) {
                a = a +", ";
            }
        }
	    return "{"+a+"}"; // Adding [] signs to make it look like a dict
    }

    @Override
    public boolean getBoolValue(String what, AspSyntax where) { //False if dict is empty, true otherwise
        if(keys.size() <= 0) {
            return false;
        }
	    return true;
    }

    @Override
    public RuntimeValue evalLen(AspSyntax where) { // Length of the dict
        return new RuntimeIntValue(keys.size());
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
    public RuntimeValue evalSubscription(RuntimeValue v, AspSyntax where) {
        
        if(v instanceof RuntimeStringValue) {
            for(int x = 0; x < keys.size(); x++) {
                if(keys.get(x).evalEqual(v, where).getBoolValue("dict subscription", where)) {
                    return values.get(x);
                }
            }
            runtimeError("Key not found in dictionary!", where);
        }
        runtimeError("Dictionary key must be a string!", where);
        return null;  
    }

    public void evalAssignElem(RuntimeValue inx, RuntimeValue val, AspSyntax where) {
        if(inx instanceof RuntimeStringValue) {
            for(int x = 0; x < keys.size(); x++) {
                if(keys.get(x).evalEqual(inx, where).getBoolValue("dict subscription", where)) {
                    values.set(x, val);
                    return;
                }
            }
            keys.add(inx);
            values.add(val);
            return;
        }
        runtimeError("Dictionary key must be a string!", where);
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