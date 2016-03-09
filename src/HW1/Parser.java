package HW1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

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

    static final char CONST = '0';


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
                axioms.add(binary(0, pre.length(), pre, ops.IMPL));
            }
        }
    }

    public void getResult() {
        int i = 1;
        try {
            while (sc.hasNextLine()) {
                String temp = sc.nextLine();
                expression = temp.replace(">", "");
                wr.write("(" + i + ") " + temp + " (" + checkExpression(binary(0, expression.length(), expression, ops.IMPL)) + ')' + '\n');
                i++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (sc != null)
                sc.close();
            if (wr != null)
                wr.close();
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
                axioms.add(binary(0, temp.length(), temp, ops.IMPL));
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
                statements.add(e);
                return "акс. " + q;
            }
            if (q > axSize && checkStatementEquality(e, i)) {
                statements.add(e);
                return "предп. " + (q - axSize);
            }
            q++;
        }
        for (int i = statements.size() - 1; i >= 0; --i) {

            if (statements.get(i).getRight() != null && checkStatementEquality(e, statements.get(i).getRight()) && statements.get(i).getType() == '-') {
                for (int j = statements.size() - 1; j >= 0; --j) {
                    if (checkStatementEquality(statements.get(i).getLeft(), statements.get(j))) {
                        statements.add(e);
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

        int i = type == ops.IMPL ? l : r - 1;
        int brCount = 0;

        while (i < r && i >= l) {
            if (string.charAt(i) == '(') {
                brCount++;
            }
            if (string.charAt(i) == ')') {
                brCount--;
            }
            if (string.charAt(i) == type.getChar() && brCount == 0) {
                Expression res;
                if (type == ops.IMPL) {
                    res = new Expression(binary(l, i, string, type.next()), binary(i + 1, r, string, type));
                } else {
                    res = new Expression(binary(l, i, string, type), binary(i + 1, r, string, type.next()));
                }
                return res.setType(type.getChar()).
                        setString(res.getLeft().toString() + type.getChar() + res.getRight().toString());

            }
            if (type == ops.IMPL) {
                i++;
            } else i--;
        }
        return binary(l, r, string, type.next());
    }


    private Expression negation(int l, int r, String string) {
        if (string.charAt(l) == '(' && string.charAt(r - 1) == ')') {
            return binary(l + 1, r - 1, string, ops.IMPL);
        }
        if (string.charAt(l) == ops.NEG.getChar()) {
            Expression res = binary(l + 1, r, string, ops.IMPL);
            return new Expression(res).setString("!" + res.toString()).setType('!');
        }
        return new Expression(null)
                .setType(CONST)
                .setString(string.substring(l, r))
                .setVariable(string.substring(l, r));
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
                    return e.toString().equals(axMap.get(ax.getVariable()).toString());
                }
            }
        }
        return false;
    }

    public Expression buildExpression(String s) {
        return binary(0, s.length(), s, ops.IMPL);
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
