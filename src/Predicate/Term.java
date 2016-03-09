package Predicate;

import java.util.ArrayList;


public class Term {
    final ArrayList<Term> subterms;
    String name = "";


    //for 0
    public Term(int i) {
        this.name = "0";
        subterms = new ArrayList<>();
    }


    public Term(String t, ArrayList<Term> terms) {
        name = t;
        subterms = terms;
    }




    protected Term() {
        subterms = new ArrayList<>();
    }

    @Override
    public String toString() {
        if (subterms == null) {
            return name;
        }
        return name.equals("0") ? name : name + "(" + subterms.toString().replaceAll("[\\]\\[ ]", "") + ")";
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof Term && another.toString().equals(toString());

    }
}
