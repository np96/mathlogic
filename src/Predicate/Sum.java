package Predicate;

import java.util.ArrayList;

public class Sum extends Term {

    public Sum(Term l, Term r) {
        subterms.add(l);
        subterms.add(r);
    }

    @Override
    public String toString() {
        /*String answ = "";
        for (int i = 0; i <= 1; ++i) {
            if (this.subterms.get(i) instanceof Sum) {
                answ += "(" + this.subterms.get(i).toString() + ")";
            } else answ += this.subterms.get(i).toString();
            if (i== 1) break;
            answ+="+";
        }
        return answ;*/
        return this.subterms.get(0) + "+" + (this.subterms.get(1) instanceof Sum?"("+this.subterms.get(1)+")":this.subterms.get(1));

    }
}
