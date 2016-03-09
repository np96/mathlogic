package Predicate;


import java.util.Map;

public abstract class Quantifier extends Expression {
    final String var;

    @Override
    int getPriority() {
        return 0;
    }

    @Override
    public boolean eval(Map<String, Boolean> variables) {
        return false;
    }

    public Quantifier(String var, Expression expr) {
        super(expr);
        this.var = var;

    }

    @Override
    public String toString() {
        return getType().getChar() + var + "(" + getLeft() + ")";
    }
}
