package HW2;

import java.util.Map;

public class Expression {

    private Expression l;
    private Expression r;

    private char type = '0';
    private String variable = null;
    private String str;


    public Expression getLeft() {
        return this.l;

    }

    public Expression getRight() {
        return this.r;

    }


    public Expression(Expression l, Expression r) {
        this.l = l;
        this.r = r;
    }

    public Expression(Expression l) {
        this(l, null);
    }

    Expression setVariable(String _variable) {
        this.variable = _variable;
        return this;
    }


    Expression setString(String _str) {
        this.str = _str;
        return this;
    }

    Expression setType(char _type) {
        this.type = _type;
        return this;
    }


    char getType() {
        return type;
    }

    String getVariable() {
        return variable;
    }
    @Override
    public String toString() {
        return str;

    }

    boolean eval(Map<String, Boolean> variables) {
        if (type == '0') {
            return variables.get(variable);
        }
        if (type == '|') {
            return this.getRight().eval(variables)||this.getLeft().eval(variables);
        } if (type == '&') {
            return this.getRight().eval(variables)&&this.getLeft().eval(variables);
        } if (type == '-') {
            return !this.getRight().eval(variables)||this.getLeft().eval(variables);
        } if (type =='!') {
            return !variables.get(variable);
        }
        System.out.println("Bad Expression type " + this.toString());
        return false;
    }



}
