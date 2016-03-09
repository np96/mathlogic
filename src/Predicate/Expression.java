package Predicate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class Expression {

    public abstract boolean eval(Map<String, Boolean> variables);

    protected Expression left;
    protected Expression right;
    protected Set<String> chainedVariables;

    abstract int getPriority();

    protected boolean brackets;

    public boolean ifBrackets() {
        return brackets;

    }

    public Expression setBrackets() {
        brackets = true;
        return this;
    }

    public Expression newChainedVariable(String var) {
        if (chainedVariables == null) chainedVariables = new HashSet<>();
        if (left != null) left.newChainedVariable(var);
        if (right != null) right.newChainedVariable(var);
        chainedVariables.add(var);

        return this;
    }

    abstract public ops getType();

    public boolean freeVariable(String var) {
        if (this instanceof Quantifier) {
            return !var.equals(((Quantifier) this).var) && getLeft().freeVariable(var);
        }
        if (this instanceof Predicate) {
            return ((Predicate) this).terms != null && ((Predicate) this).terms.toString().contains(var);
        }
        boolean lAns = left == null || left.freeVariable(var);
        return (lAns && right == null) || (lAns || right!=null&&right.freeVariable(var));
    }

    @Override
    public String toString() {
        return "(" + this.getLeft() + ")" + getType().getChar() + "(" + getRight() + ")";
    }

    public Expression getLeft() {
        return left;
    }

    public Expression getRight() {
        return right;
    }

    public Expression(Expression left, Expression right) {
        brackets = false;

        if (chainedVariables == null) {
            chainedVariables = new HashSet<>();
        }
        this.left = left;
        this.right = right;
    }

    public Expression() {
        brackets = false;
    }

    public Expression(Expression left) {
        brackets = false;
        this.left = left;
        this.left.chainedVariables = chainedVariables;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Expression && toString().equals(obj.toString());
    }
}
