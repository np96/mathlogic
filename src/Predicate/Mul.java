package Predicate;

import java.util.ArrayList;

public class Mul extends Term {
    public Mul(Term l, Term r) {
        subterms.add(l);
        subterms.add(r);
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (subterms.get(0) instanceof  Sum) {
            sb.append("(").append(subterms.get(0).toString()).append(")");
        } else sb.append(subterms.get(0).toString());
        sb.append("*");
        if (subterms.get(1) instanceof  Sum) {
            sb.append("(").append(subterms.get(1).toString()).append(")");
        } else sb.append(subterms.get(1).toString());
        return sb.toString();
    }

}
