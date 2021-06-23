package runtime;

import java.util.ArrayList;
import java.util.Scanner;

import main.*;
import parser.AspSyntax;

public class RuntimeLibrary extends RuntimeScope {
    private Scanner keyboard = new Scanner(System.in);

    public RuntimeLibrary() {
    assign("len", new RuntimeFunc(null, null, null) {
            @Override
            public RuntimeValue evalFuncCall(ArrayList<RuntimeValue> actualParams, AspSyntax where) {
                checkNumParams(actualParams, 1, "len", where);
                return actualParams.get(0).evalLen(where);
            }
        }
    );

    assign("print", new RuntimeFunc(null, null, null) {
        @Override
        public RuntimeValue evalFuncCall(ArrayList<RuntimeValue> actualParams, AspSyntax where) {
            for(RuntimeValue v : actualParams) {
                System.out.print(v.toString() + " ");
            }
            System.out.println();
            return new RuntimeNoneValue();
        }
    }
    );

    assign("input", new RuntimeFunc(null, null, null) {
        @Override
        public RuntimeValue evalFuncCall(ArrayList<RuntimeValue> actualParams, AspSyntax where) {
            checkNumParams(actualParams, 1, "input", where);
            System.out.println(actualParams.get(0).toString());
            Scanner in = new Scanner(System.in);
            String i = in.nextLine();
            //in.close();
            
            return new RuntimeStringValue(i);
        }
    }
    );

    assign("range", new RuntimeFunc(null, null, null) {
        @Override
        public RuntimeValue evalFuncCall(ArrayList<RuntimeValue> actualParams, AspSyntax where) {
            checkNumParams(actualParams, 2, "range", where);
            if(actualParams.get(0) instanceof RuntimeIntValue && actualParams.get(1) instanceof RuntimeIntValue) {

                ArrayList<RuntimeValue> v = new ArrayList<>();
                for(long x = actualParams.get(0).getIntValue("range", where); x < actualParams.get(1).getIntValue("range", where); x++) {
                    v.add(new RuntimeIntValue(x));
                }

                RuntimeListValue l = new RuntimeListValue(v);
                return l;
            }
            RuntimeValue.runtimeError("range must take integrer parameters!",where);
            return null;
        }
    }
    );


    assign("float", new RuntimeFunc(null, null, null) {
        @Override
        public RuntimeValue evalFuncCall(ArrayList<RuntimeValue> actualParams, AspSyntax where) {
            checkNumParams(actualParams, 1, "float", where);
            if(actualParams.get(0) instanceof RuntimeIntValue) {
                double l = Double.longBitsToDouble(actualParams.get(0).getIntValue("float",where));
                return new RuntimeFloatValue(l);
            } else if(actualParams.get(0) instanceof RuntimeStringValue) {
                double l = Double.parseDouble(actualParams.get(0).getStringValue("float",where));
                return new RuntimeFloatValue(l);
            }else if(actualParams.get(0) instanceof RuntimeFloatValue) {
                return actualParams.get(0);
            }
            RuntimeValue.runtimeError("Unsupported conversion!",where);
            return null;
        }
    }
    );

    assign("int", new RuntimeFunc(null, null, null) {
        @Override
        public RuntimeValue evalFuncCall(ArrayList<RuntimeValue> actualParams, AspSyntax where) {
            checkNumParams(actualParams, 1, "int", where);
            if(actualParams.get(0) instanceof RuntimeFloatValue) {
                long l = (long)actualParams.get(0).getFloatValue("int",where);
                return new RuntimeIntValue(l);
            } else if(actualParams.get(0) instanceof RuntimeStringValue) {
                long l = Long.parseLong(actualParams.get(0).getStringValue("int",where));
                return new RuntimeIntValue(l);
            }else if(actualParams.get(0) instanceof RuntimeIntValue) {
                return actualParams.get(0);
            }
            RuntimeValue.runtimeError("Unsupported conversion!",where);
            return null;
        }
    }
    );

    assign("str", new RuntimeFunc(null, null, null) {
        @Override
        public RuntimeValue evalFuncCall(ArrayList<RuntimeValue> actualParams, AspSyntax where) {
            checkNumParams(actualParams, 1, "str", where);
            return new RuntimeStringValue(actualParams.get(0).toString());
        }
    }
    );

    }

    private void checkNumParams(ArrayList<RuntimeValue> actArgs, int nCorrect, String id, AspSyntax where) {
        if (actArgs.size() != nCorrect)
            RuntimeValue.runtimeError("Wrong number of parameters to "+id+"!",where);
    }
}
