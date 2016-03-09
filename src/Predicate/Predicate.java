package Predicate;

import java.util.ArrayList;
import java.util.Map;

public class Predicate extends Expression {
    @Override
    int getPriority() {
        return 0;
    }

    String name = "";
    ArrayList<Term> terms;


    public Predicate(String name, ArrayList<Term> terms) {
        this.name = name;
        this.terms = new ArrayList<>(terms);
    }


    public Predicate(Term left, Term right) {
        terms = new ArrayList<>();
        name = "=";
        terms.add(left);
        terms.add(right);
    }

    public Predicate(String name, Term term) {
        this.name = name;
        terms = new ArrayList<>();
        terms.add(term);
    }

    @Override
    public String toString() {
        if (name.equals("=")) {
            return "(" + terms.get(0).toString() + ")" + '=' + "(" + terms.get(1).toString() + ")";
        }
        String toS = name;
        if (terms.size() > 0) {
            toS += "(";
            toS += String.join(",", terms.toString().replaceAll("[\\]\\[ ]", ""));
            toS += ")";
        }
        return toS;
    }

    Predicate() {

    }

    @Override
    public boolean eval(Map<String, Boolean> variables) {
        return false;
    }

    public Predicate(String s) {
        super(null);
    }

    @Override
    public ops getType() {
        return ops.PRED;
    }

}
