package Predicate;

import java.util.Map;

public class Implication extends Expression  {
    @Override
    int getPriority() {
        return 3;
    }
    @Override
    public boolean eval(Map<String, Boolean> variables) {
        return !getLeft().eval(variables)|getRight().eval(variables);
    }

    public Implication(Expression left , Expression right) {
        super(left,right);
    }

    @Override
    public ops getType() {
        return ops.IMPL;
    }
}
