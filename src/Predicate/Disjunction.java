package Predicate;

import java.util.Map;

public class Disjunction extends Expression {
    @Override
    int getPriority() {
        return 2;
    }
    @Override
    public boolean eval(Map<String, Boolean> variables) {
        return getLeft().eval(variables) | getRight().eval(variables);
    }

    public Disjunction(Expression left , Expression right) {
        super(left,right);
    }

    @Override
    public ops getType() {
        return ops.DIS;
    }
}
