package Predicate;

import java.util.Map;

public class Exists extends Quantifier {

    @Override
    public boolean eval(Map<String, Boolean> variables) {
        return false;
    }

    public Exists(String var, Expression expr) {
        super(var, expr);
    }

    @Override
    public ops getType() {
        return ops.EXISTS;
    }
}
