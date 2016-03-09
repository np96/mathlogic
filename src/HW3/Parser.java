package HW3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

public class Parser {
    private Map<String, Expression> axMap;

    private Scanner sc;
    private PrintWriter wr;
    ArrayList<Character> operations;
    ArrayList<Expression> axioms;
    ArrayList<Expression> presumptions;
    ArrayList<Expression> statements;
    String cond;
    String expression;
    String alpha;
    int axSize;
    ArrayList<String> condit;
    ArrayList<String> list;
    ArrayList<String> res;

    static final char CONST = '0';

    public Parser(String axs, String firstLine) {
        axMap = new HashMap<>();
        axioms = new ArrayList<>();
        operations = new ArrayList<>();
        statements = new ArrayList<>();
        presumptions = new ArrayList<>();
        operations.add('&');
        operations.add('|');
        operations.add('-');
        operations.add('!');
        String[] tokens = firstLine.split("\\|\\-");
        initializeAxioms(axs);
        condit = new ArrayList<>();
        condit.addAll(Arrays.asList(tokens[0].split(",")));
        alpha = condit.get(condit.size() - 1);
        cond = tokens[1];
        for (String s : condit) {
            axioms.add(buildExpression(s.replace(">", "")));
        }
    }


    public Parser(String axs, String file, String outFile) {
        axMap = new HashMap<>();
        axioms = new ArrayList<>();
        operations = new ArrayList<>();
        statements = new ArrayList<>();
        presumptions = new ArrayList<>();
        operations.add('&');
        operations.add('|');
        operations.add('-');
        operations.add('!');
        initializeAxioms(axs);
        if (file != null && outFile != null) {
            try {
                sc = new Scanner(new File(file));
                wr = new PrintWriter(new File(outFile));
            } catch (Exception e) {
                e.printStackTrace();
            }
            cond = sc.nextLine();
            wr.write(cond + '\n');
            if (cond.charAt(0) != '|') {
                String[] pres = cond.split("\\|-")[0].split(",");
                alpha = pres[pres.length - 1];
                for (String pre : pres) {
                    pre = pre.replace(">", "");
                    axioms.add(buildExpression(pre).setGetinRep());
                }
            }
        }


    }

    public void getResult() {
        int i = 1;
        list = new ArrayList<>();
        res = new ArrayList<>();
        while (sc.hasNextLine()) {
            String temp = sc.nextLine();
            expression = temp.replace(">", "");
            list.add(temp);
            res.add(checkExpression(buildExpression(expression)));
            i++;
        }

    }

    private void initializeAxioms(String path) {
        try {
            Scanner sc = new Scanner(new File(path));
            while (sc.hasNextLine()) {
                String temp = sc.nextLine().replace(" ", "").replace(">", "");
                if (temp.length() == 0) {
                    continue;
                }
                axioms.add(buildExpression(temp));

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        axSize = axioms.size();
    }


    public String checkExpression(Expression e) {
        int q = 1;
        for (Expression i : axioms) {
            axMap.clear();
            if (q <= axSize && checkAxiomEquality(e, i)) {
                statements.add(e.setGetinRep());
                return "акс. " + q;
            }
            if (q > axSize && checkStatementEquality(e, i)) {
                statements.add(e.setGetinRep());
                return "предп. " + (q - axSize);
            }
            q++;
        }
        for (int i = statements.size() - 1; i >= 0; --i) {

            if (checkStatementEquality(e, statements.get(i).getRight()) && statements.get(i).getType() == '-') {
                for (int j = 0; j < statements.size(); ++j) {
                    if (checkStatementEquality(statements.get(i).getLeft(), statements.get(j))) {
                        statements.add(e.setGetinRep());
                        return "M.P. " + (j + 1) + ", " + (i + 1);
                    }
                }
            }
        }

        return "Не доказано";
    }

    Expression binary(int l, int r, String string, ops type) {
        if (type == ops.NEG) {
            return negation(l, r, string);
        }

        int i = type == ops.IMPL?l :r - 1;
        int brCount = 0;

        while (i < r && i >=l ) {
            if (string.charAt(i) == '(') {
                brCount++;
            }
            if (string.charAt(i) == ')') {
                brCount--;
            }
            if (string.charAt(i) == type.getChar() && brCount == 0) {
                Expression res;
                if (type == ops.IMPL) {
                    res = new Expression(binary(l, i, string, type.next()).setGetinRep(), binary(i + 1, r, string, type).setGetinRep());
                } else {
                    res = new Expression(binary(l, i, string, type).setGetinRep(), binary(i + 1, r, string, type.next()).setGetinRep());
                }
                return res.setType(type.getChar()).
                        setString(res.getLeft().toString() + type.getChar() + res.getRight().toString()).setGetinRep();

            }
            i = type == ops.IMPL?i+1:i-1;
        }
        return binary(l, r, string, type.next());
    }

    private Expression negation(int l, int r, String string) {
        if (string.charAt(l) == '(' && string.charAt(r - 1) == ')') {
            return binary(l + 1, r - 1, string, ops.IMPL).setGetinRep();
        }

        if (string.charAt(l) == ops.NEG.getChar()) {
            Expression res = binary(l + 1, r, string, ops.IMPL);
            return new Expression(res).setString("!" + res.toString()).setType('!').setGetinRep();
        }
        return new Expression(null)
                .setType(CONST)
                .setString(string.substring(l, r))
                .setVariable(string.substring(l, r)).setGetinRep();
    }

    private boolean checkAxiomEquality(Expression e, Expression ax) {

        if ((e == null && ax != null) || (ax == null && e != null)) {
            return false;
        }

        if (e == null) {
            return true;
        }

        if (e.getType() == CONST && ax.getType() != CONST) {
            return false;
        }

        if (ax.getType() == CONST || e.getType() == ax.getType()) {
            if (ax.getType() != CONST) {
                return (checkAxiomEquality(e.getLeft(), ax.getLeft()) & checkAxiomEquality(e.getRight(), ax.getRight()));
            } else {
                if (!axMap.containsKey(ax.getVariable())) {
                    axMap.put(ax.getVariable(), e);
                    return true;
                } else {
                    return checkStatementEquality(e, axMap.get(ax.getVariable()));
                }
            }
        }
        return false;
    }

    public Expression buildExpression(String s) {
        return binary(0, s.length(), s, ops.IMPL).setGetinRep();
    }

    boolean checkStatementEquality(Expression e, Expression st) {
        if (e == null || st == null) {
            return (e == null) == (st == null);
        }
        if (e.getVariable() != null || st.getVariable() != null) {
            return ((e.getVariable() != null && st.getVariable() != null) && e.getVariable().equals(st.getVariable()));
        }
        return checkStatementEquality(e.getLeft(), st.getLeft()) &
                checkStatementEquality(e.getRight(), st.getRight()) && e.getType() == st.getType();
    }
}
