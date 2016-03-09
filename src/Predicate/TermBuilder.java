package Predicate;

import java.util.ArrayList;
import java.util.Arrays;

public class TermBuilder {

    public static Term build(String in) {
        return term(in);
    }

    private static Term term(String s) {
        int brCount = 0;
        for (int i = s.length() - 1; i >= 0; --i) {
            if (s.charAt(i) == '(') {
                brCount++;
            }
            if (s.charAt(i) == ')') {
                brCount--;
            }
            if (brCount == 0 && s.charAt(i) == '+') {
                return new Sum(term(s.substring(0, i)), mul(s.substring(i + 1, s.length())));
            }
        }
        return mul(s);
    }


    private static Term mul(String s) {
        int brCount = 0;
        for (int i = s.length() - 1; i >= 0; --i) {
            if (s.charAt(i) == '(') {
                brCount++;
            }
            if (s.charAt(i) == ')') {
                brCount--;
            }
            if (brCount == 0 && s.charAt(i) == '*') {
                return new Mul(mul(s.substring(0, i)), exp(s.substring(i + 1, s.length())));
            }
        }
        return exp(s);
    }

    private static Term exp(String s) {

        if ( s.charAt(s.length() - 1) == '\'') {
            return new Next(exp(s.substring(0, s.length() - 1)));
        }
        if (s.charAt(0) == '(' && s.charAt(s.length() - 1) == ')') {
            return term(s.substring(1, s.length() - 1));
        }
        if (s.charAt(0) == '0') {
            return new Term(0);
        }
        String t = "";
        int i = 0;
        while (i < s.length() && Character.isLetterOrDigit(s.charAt(i))) {
            t += s.charAt(i);
            i++;
        }
        if (i == s.length()) {
            return new Variable(t);
        }
        int brc = 0;
        int q = ++i;
        ArrayList<Term> ar = new ArrayList<>();
        for (; i < s.length() - 1; ++i) {
            if (s.charAt(i) == '(') brc++;
            if (s.charAt(i) == ')') brc--;
            if (brc == 0 && s.charAt(i) == ',') {
                ar.add(build(s.substring(q, i)));
                q = i + 1;
            }
        }
        if (q < i) {
            ar.add(build(s.substring(q, i)));
        }
        return new Term(t, ar);


    }

}
