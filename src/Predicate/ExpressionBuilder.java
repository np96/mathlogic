package Predicate;

import java.util.ArrayList;

public class ExpressionBuilder {
    public static Expression buildExpression(String s) {
        s = s.replace(">", "");
        return bin(s, ops.IMPL);
    }

    private static Expression bin(String s, ops type) {
        if (type == ops.NEG) {
            return un(s);
        }
        int i = type ==ops.IMPL?0:s.length()-1;
        int brCount = 0;
        while (i < s.length() && i >= 0) {
            if (s.charAt(i) == '(') {
                brCount++;
            }
            if (s.charAt(i) == ')') {
                brCount--;
            }

            if (s.charAt(i) == type.getChar() && brCount == 0) {
                if (type == ops.IMPL) {
                    return build(type, bin(s.substring(0, i), type.next()), bin(s.substring(i + 1), type));
                }
                return build(type, bin(s.substring(0, i), type), bin(s.substring(i + 1), type.next()));
            }
            i=type==ops.IMPL?i+1:i-1;
        }

        return bin(s, type.next());
    }

    private static Expression un(String s) {
        if (s.charAt(0) == '(' && s.charAt(s.length() - 1) == ')' && bracketsCheck(s.substring(1,s.length()-1))) {
            return bin(s.substring(1, s.length() - 1), ops.IMPL);
        }
        if (s.charAt(0) == '!') {
            return new Negation(un(s.substring(1)));
        }
        if (s.charAt(0) == '@' || s.charAt(0) == '?') {
            String var = s.substring(1).split("[^a-z\\d]")[0];
            if (var.length() > 1 && var.charAt(1) <='z' && var.charAt(1) >='a') var = var.substring(0,1);
            return build(ops.fromChar(s.charAt(0)), bin(s.substring(1 + var.length()), ops.IMPL), null, var);
        }
        String pred;

        if ((pred = s.split("\\W")[0]).matches("^[A-Z]+\\d*")) {
            int brc = 0, q = pred.length() + 1;
            ArrayList<Term> terms = new ArrayList<>();
            for (int i = pred.length() + 1; i < s.length() - 1; ++i) {
                if (s.charAt(i) == '(') brc++;
                if (s.charAt(i) == ')') brc--;
                if (brc == 0 && s.charAt(i) == ',') {
                    terms.add(TermBuilder.build(s.substring(q, i)));
                    q = i + 1;
                }
            }
            if (q < s.length() - 1) {
                terms.add(TermBuilder.build(s.substring(q, s.length() - 1)));
            }
            return new Predicate(pred, terms);
        }
        return new Predicate(TermBuilder.build(s.substring(0, s.indexOf('='))),
                (TermBuilder.build(s.substring(s.indexOf('=') + 1))));
    }

    static private Expression build(ops type, Expression l, Expression r) {
        return build(type, l, r, null);
    }


    static private Expression build(ops type, Expression l, Expression r, String var) {
        switch (type) {
            case IMPL:
                return new Implication(l, r);
            case CONJ:
                return new Conjuction(l, r);
            case DIS:
                return new Disjunction(l, r);
            case NEG:
                return new Negation(l);
            case EXISTS:
                return new Exists(var, l).newChainedVariable(var);
            case ANY:
                return new Any(var, l).newChainedVariable(var);
            default:
                assert false : l.toString();
                return null;
        }
    }
    public static boolean bracketsCheck(String s) {
        int brc = 0;
        for (int i = 0; i < s.length(); ++i) {
            if (s.charAt(i) == ')') brc--;
            if (s.charAt(i) == '(') brc++;
            if (brc < 0) return false;
        }
        return true;
    }
}

