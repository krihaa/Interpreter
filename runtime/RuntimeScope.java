package runtime;

// For part 4:

import java.util.HashMap;

import main.*;
import parser.AspSyntax;

public class RuntimeScope {
    private RuntimeScope outer;
    private HashMap<String,RuntimeValue> decls = new HashMap<>();

    public RuntimeScope() {
	outer = null;
    }


    public RuntimeScope(RuntimeScope oScope) {
	outer = oScope;
    }


    public void assign(String id, RuntimeValue val) {
	decls.put(id, val);
    }

    public void print() {
        decls.forEach((k,v) -> System.out.println("Variable: "+k+" = "+v));
    }

    public RuntimeValue find(String id, AspSyntax where) {
        RuntimeValue v = decls.get(id);
        if (v != null)
            return v;
        if (outer != null)
            return outer.find(id, where);

        RuntimeValue.runtimeError("Name " + id + " not defined!", where);
    
	    return null;  // Required by the compiler.
    }

    /**
     * Finds the scope for a variable, (I needed this to not assign the variable again in a inner scope)
     * @param id
     * @param where
     * @return The scope the variable is in, or null if it does not exist
     */
    public RuntimeScope findScope(String id, AspSyntax where) {
        RuntimeValue v = decls.get(id);
        if (v != null)
            return this;
        if (outer != null)
            return outer.findScope(id, where);
	    return null;
    }
}
