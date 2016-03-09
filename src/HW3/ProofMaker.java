package HW3;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class ProofMaker {
    Expression expr;
    Parser parser;
    String axioms;
    String rules;
    String inFile;
    String outFile;
    PrintWriter pw;
    Scanner sc;
    ArrayList<Expression> proof;
    ArrayList<ArrayList<String>> patterns;
    ArrayList<String> variables;
    ArrayList<Pair> map;
    ArrayList<String> exclMid;

    class Pair {
        Map<String, Boolean> mapping;
        ArrayList<Expression> expressions;

        Pair(Map<String, Boolean> m, ArrayList<Expression> pf) {
            mapping = m;
            expressions = pf;
        }
    }

    public ProofMaker(String rules, String axs, String file, String outfile) {
        this.rules = rules + "/";
        axioms = axs;
        inFile = file;
        outFile = outfile;
        parser = new Parser(axioms, null, null);
    }

    private boolean recursion(Expression ex, Map<String, Boolean> vars) {
        if (ex.getType() == Parser.CONST) {
            return ex.eval(vars);
        }
        ex.setGetinRep();
        Expression left = ex.getLeft();
        Expression right = ex.getRight();
        boolean leftRes = recursion(left, vars);
        boolean rightRes = false;
        if (right != null) {
            rightRes = recursion(right, vars);
        }

        if (ex.getType() == ops.CONJ.getChar()) {
            if (leftRes & rightRes) {
                adder(0, left, right);
                return true;
            } else if (leftRes) {
                adder(1, left, right);
            } else if (rightRes) {
                adder(2, left, right);
            } else {
                adder(3, left, right);
            }
            return false;
        }

        if (ex.getType() == ops.IMPL.getChar()) {
            if (leftRes & rightRes) {
                adder(4, left, right);
                return true;
            } else if (leftRes) {
                adder(5, left, right);
                return false;
            } else if (rightRes) {
                adder(6, left, right);
                return true;
            } else {
                adder(7, left, right);
                return true;
            }
        }
        if (ex.getType() == ops.DIS.getChar()) {
            if (leftRes & rightRes) {
                adder(8, left, right);
                return true;
            } else if (leftRes) {
                adder(9, left, right);
                return true;
            } else if (rightRes) {
                adder(10, left, right);
                return true;
            } else {
                adder(11, left, right);
                return false;
            }
        }
        if (ex.getType() == ops.NEG.getChar()) {
            if (leftRes) {
                adder(12, left, left);
                return false;
            }
            adder(13, left, left);
            return true;
        }
        return false;
    }

    public void execute() throws Exception {
        String s = new Scanner(new File(inFile)).nextLine();
        pw = new PrintWriter(outFile);
        expr = parser.buildExpression(s.replace(">", ""));
        map = new ArrayList<>();
        expr.setGetinRep();
        sc = new Scanner(new File("rules/excluded_middle"));
        exclMid = new ArrayList<>();
        while (sc.hasNextLine()) {
            exclMid.add(sc.nextLine());
        }
        getVariables(expr.toString());
        patterns = new ArrayList<>();
        for (int i = 0; i < 15; ++i) {
            sc = new Scanner(new File(rules + "rule" + (i + 1)));
            patterns.add(new ArrayList<>());
            while (sc.hasNextLine()) {
                String temp = sc.nextLine().replace(">", "");
                patterns.get(i).add(temp);
            }
        }
        if (!proof(0, new TreeMap<>())) {
            return;

        }
        ArrayList<Expression> ans = answer();
        pw.write("|-" + expr.getinRep + '\n');
        for (Expression q : ans) {
                pw.write(q.getinRep.replace("-", "->") + '\n');
        }
        pw.close();


    }

    boolean proof(int i, Map<String, Boolean> vars) throws Exception{
        if (i == variables.size()) {
            proof = new ArrayList<>();
            if (!recursion(expr, vars)) {
                try (PrintWriter pw =new PrintWriter(new File(outFile))) {
                    String ans = "Высказывание ложно при ";
                    for (String q : variables) {
                        ans+=q+"="+(vars.get(q)?"И":"Л")+", ";
                    }
                    pw.write(ans.substring(0,ans.length()-2));
                }
                return false;
            }
            Pair t = new Pair(vars, proof);
            map.add(t);
            return true;
        }
        Map<String, Boolean> pos = new TreeMap<>(vars);
        pos.put(variables.get(i), true);
        boolean q = proof(i + 1, pos);
        pos.put(variables.get(i), false);
        return proof(i + 1, pos)&q;
    }

    public ArrayList<Expression> answer() throws Exception {

        while (map.size() > 1) {
            Pair first = map.get(0);
            Pair second = map.get(1);
            String al = variables.get(first.mapping.size() - 1);
            Map<String, Boolean> mapping = first.mapping;
            ArrayList<Expression> proof = new ArrayList<>();
            mapping.remove(al);
            Expression alpha = parser.buildExpression(al);
            String str = "";
            al = parser.buildExpression(al).setGetinRep().getinRep;
            for (String elem : variables) {
                if (mapping.containsKey(elem))
                    if (mapping.get(elem)) {
                        str += elem + ",";
                    } else str += "!" + elem + ",";
            }
            Parser prs = new Parser("Axioms.in", str + al + "|-" + expr.getinRep);
            DeductionTheorem dt = new DeductionTheorem(prs);
            for (int i = 0; i < first.expressions.size(); ++i) {
                String[] ded = (dt.reformat(first.expressions.get(i).setGetinRep(), alpha).split("\n"));
                for (String t : ded) {
                    proof.add(parser.buildExpression(t));
                }
            }
            prs = new Parser("Axioms.in", str + "!" + al + "|-" + expr.getinRep);
            dt = new DeductionTheorem(prs);
            alpha = parser.buildExpression("!"+alpha);
            for (int i = 0; i < second.expressions.size(); ++i) {
                String[] ded = (dt.reformat(second.expressions.get(i).setGetinRep(), alpha).split("\n"));
                for (String t: ded) {
                    proof.add(parser.buildExpression(t));
                }
            }
            alpha = parser.buildExpression(al);
            for (String st: exclMid) {
                proof.add(prs.buildExpression(st.replace("a", alpha.getinRep).replace(">", "")));
            }
            proof.add(parser.buildExpression("(a-b)-(!a-b)-(a|!a)-(b)".replace("a", alpha.getinRep).replace("b", expr.getinRep)));
            proof.add(parser.buildExpression("(!a-b)-(a|!a-b)".replace("a", alpha.getinRep).replace("b", expr.getinRep)));
            proof.add(parser.buildExpression("a|!(a)-b".replace("a", alpha.getinRep).replace("b", expr.getinRep)));
            proof.add(parser.buildExpression(expr.getinRep));
            map.add(new Pair(mapping, proof));
            map.remove(0);
            map.remove(0);
        }
        sc.close();
        return map.get(0).expressions;
    }


    void adder(int i, Expression left, Expression right) {
        for (int j = 1; j < patterns.get(i).size(); j++) {
            proof.add(parser.buildExpression(patterns.get(i).get(j).replace("a", left.setGetinRep().getinRep).replace("b", right.setGetinRep().getinRep)));
        }
    }

    void getVariables(String str) {
        String[] s = str.split("\\W");
        variables = new ArrayList<>();
        for (String q : s) {
            if (!variables.contains(q) && q.length() > 0) {
                variables.add(q);
            }
        }
    }


}
