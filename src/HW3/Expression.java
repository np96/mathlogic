package HW3;

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
        setGetinRep();
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
        return setGetinRep().getinRep;

    }

    String getinRep = "";

    Expression setGetinRep() {
        if (getinRep.equals("")) {
            getinRep+='(';
            if (variable != null) {
                getinRep = variable;
                return this;
            }
            if (getType() == '!') {
                getinRep += getType();
                getLeft().setGetinRep();
                getinRep += getLeft().getinRep;
                getinRep+=')';
                return this;
            }

            if (getLeft() != null) {
                getLeft().setGetinRep();
                getinRep += getLeft().getinRep;
            }
            getinRep += getType();
            if (getRight() != null) {
                getRight().setGetinRep();
                getinRep += getRight().getinRep;
                getinRep+=")";
            }
        }
        return this;
    }

    boolean eval(Map<String, Boolean> variables) {
        if (type == '0') {
            return variables.get(variable);
        }
        if (type == '|') {
            return this.getRight().eval(variables) | this.getLeft().eval(variables);
        }
        if (type == '&') {
            return this.getRight().eval(variables) & this.getLeft().eval(variables);
        }
        if (type == '-') {
            return !(this.getLeft().eval(variables)) | this.getRight().eval(variables);
        }
        return type == '!' & !this.getLeft().eval(variables);

    }


}
