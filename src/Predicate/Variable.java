package Predicate;


import java.util.ArrayList;

public class Variable extends Term {
    String var;




    public Variable(String var) {
        super();
        this.var = var;
    }

    @Override
    public String toString() {
        return var;
    }

    @Override
    public Variable putTerm(String x, String y) {
        if (var.equals(x)) {
            return new Variable(y);
        } else return this;
    }
}
