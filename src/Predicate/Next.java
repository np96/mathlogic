package Predicate;

import java.util.ArrayList;

public class Next extends Term {
    Term prec;
    public Next(Term term) {
        super();
        prec = term;
        subterms.add(prec);

    }
    public String toString() {
        if (prec instanceof Variable || prec instanceof Next || prec.name.equals("0")) {
            return prec.toString() + '\'';
        }
        return "(" +  prec.toString() + ")" + '\'';
        //((A(f(a+0''),b+d'',c*4,d))&(B))-(C(fs(0+(0'')*(a+b,f(a+(0)*b)))) — лишняя скобка

    }
}
