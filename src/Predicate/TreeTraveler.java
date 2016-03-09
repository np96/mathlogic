package Predicate;

import java.util.ArrayList;
import java.util.HashSet;


public class TreeTraveler {
    Expression ex1;
    Expression ex2;
    String a, b;
    HashSet<String> vars;
    public boolean traverseExpressionTree(Expression ex1, Expression ex2, String a, String b) {
        return traverseExpressionTree(ex1,ex2,a,b,new HashSet<>());
    }
    public boolean traverseExpressionTree(Expression ex1, Expression ex2, String a, String b, HashSet<String> vars) {
        this.vars = vars;
        this.ex1 = ex1;
        this.ex2 = ex2;
        this.a = a;
        this.b = b;
        return traverseExpressionTree(ex1, ex2);
    }

    private boolean traverseExpressionTree(Expression ex1, Expression ex2) {

        this.ex1 = ex1;
        this.ex2 = ex2;
        if (ex1 == null || ex2 == null)
            return ex1 == null && ex2 == null;
        if (!ex1.getType().equals(ex2.getType())) {
            return false;
        }
        if (ex1 instanceof Quantifier) {
            if (((Quantifier) ex1).var.equals(a))
                return (((Quantifier) ex2).var.equals(a))
                        && traverseExpressionTree(ex1.getLeft(), ex2.getLeft(), a, a, vars);
            else {
                HashSet<String> add = vars;
                add.add(((Quantifier) ex1).var);
                return (((Quantifier) ex2).var.equals(((Quantifier) ex1).var) && traverseExpressionTree(ex1.getLeft(), ex2.getLeft(), a, b, add));
            }
        }
        if (ex1 instanceof Predicate) {
            if (((Predicate) ex1).terms.size() != (((Predicate) ex2).terms.size()) && !((Predicate) ex1).name.equals(((Predicate) ex2).name)) {
                return false;
            }
            ArrayList<Term> terms1 = ((Predicate) ex1).terms;
            ArrayList<Term> terms2 = ((Predicate) ex2).terms;
            for (int i = 0; i < terms1.size(); ++i) {
                if (!traverseTermTree(terms1.get(i), terms2.get(i))) {
                    return false;
                }
            }
        }
        return (traverseExpressionTree(ex1.getLeft(), ex2.getLeft()) && traverseExpressionTree(ex1.getRight(), ex2.getRight()));
    }

    private boolean traverseTermTree(Term term1, Term term2) {
        if (term1.toString().equals(a)) {
            if (b == null) {
                for (String ch : check(term2.toString())) {
                    if (vars.contains(ch)) {
                        return false;
                    }
                }
                b = term2.toString();
                return true;
            }
            return term2.toString().equals(b);
        } else {
            if (term1.subterms.size() != term2.subterms.size()) return false;
            if (term1.subterms.size() == 0) return term1.toString().equals(term2.toString());
            for (int i = 0; i < term1.subterms.size(); ++i) {
                if (!traverseTermTree(term1.subterms.get(i), term2.subterms.get(i)))
                    return false;
            }
            return true;
        }
    }

    ArrayList<String> check(String s) {
        ArrayList<String> answ = new ArrayList<>();
        for (int i = 0; i < s.length(); ++i) {
            String toAdd = "";
            while (i < s.length() && Character.isLetterOrDigit(s.charAt(i))) {
                toAdd += s.substring(i, i + 1);
                i++;
            }
            if (i < s.length() &&  s.charAt(i) == '(') continue;
            answ.add(toAdd);
        }
        return answ;
    }

}