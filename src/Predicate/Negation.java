package Predicate;

import java.util.Map;

public class Negation extends Expression {
    @Override
    int getPriority() {
        return 0;
    }

    @Override
    public boolean eval(Map<String, Boolean> variables) {
        return !getLeft().eval(variables);
    }

    @Override
    public ops getType() {
        return ops.NEG;
    }

    public Negation(Expression left, Expression right) {
        super(left);
    }

    public Negation(Expression left) {
        super(left);
    }

    @Override
    public String toString() {
        return "!" + "(" + getLeft().toString() + ")";
    }

}
