package Predicate;

import java.util.Map;


public class Any extends Quantifier {

    @Override
    public boolean eval(Map<String, Boolean> variables) {
        return false;
    }

    public Any(String var, Expression expr) {
        super(var,expr);
    }
    @Override
    public ops getType() {
        return ops.ANY;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
