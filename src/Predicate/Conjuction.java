package Predicate;

import java.util.Map;


public class Conjuction extends Expression {
    @Override
    int getPriority() {
        return 1;
    }
    @Override
    public boolean eval(Map<String, Boolean> variables) {
        return getLeft().eval(variables) & getRight().eval(variables);
    }

    public Conjuction(Expression left , Expression right) {
        super(left,right);
    }

    @Override
    public ops getType() {
        return ops.CONJ;
    }
}
