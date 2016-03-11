package Predicate;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;


public class ProofChecker {
    ArrayList<Expression> axioms;
    ArrayList<Expression> statements;
    ArrayList<String> answers;
    int axiomsSize;
    String fname;
    String fl;
    String outFname;
    Map<String, String> axMap;
    Map<String, String> termMap;
    HashMap<String, Integer> axVariables;
    HashMap<String, Integer> rulesVariable;
    Expression alpha;
    boolean checkAll;
    boolean checkExists;

    public ProofChecker(ArrayList<String> AxiomsPath, String inputFile, String outputFile) throws IOException {
        answers = new ArrayList<>();
        axVariables = new HashMap<>();
        rulesVariable = new HashMap<>();
        statements = new ArrayList<>();
        initAxs(AxiomsPath);
        try (Scanner sc = new Scanner(new File(inputFile))) {
            this.fname = inputFile;
            this.outFname = outputFile;
            fl = sc.nextLine();

            if (fl.contains("|-")) {
                String s = fl.split("\\|-")[0];
                int brc = 0;
                int q = 0;
                for (int i = 0; i < s.length(); ++i) {
                    if (s.charAt(i) == '(') brc++;
                    if (s.charAt(i) == ')') brc--;
                    if (brc == 0 && s.charAt(i) == ',') {
                        axioms.add(ExpressionBuilder.buildExpression(s.substring(q, i)));
                        q = i + 1;
                    }
                }
                if (s.length() > 0) {
                    axioms.add(ExpressionBuilder.buildExpression(s.substring(q, s.length())));
                    alpha = axioms.get(axioms.size() - 1);
                }
            }
        }
    }

    public void checkProof() throws IOException {
        try (Scanner sc = new Scanner(new File(fname));
             PrintWriter wr = new PrintWriter(new File(outFname))) {
            int i = 1;
            if (fl.contains("|-")) {
                sc.nextLine();
            }
            while (sc.hasNextLine()) {
                Expression expr = ExpressionBuilder.buildExpression(sc.nextLine());
                if (!checkStatement(expr, i)) {
                    wr.write("Вывод некорректен начиная с формулы номер " + i);
                    if (checkAll && expr.getRight() instanceof Any) {
                        wr.write((": переменная " + ((Any) expr.getRight()).var + " входит свободно в формулу " + expr.getLeft()).replace("-", "->"));
                        return;
                    }
                    if (checkExists && expr.getLeft() instanceof Exists) {
                        wr.write((": переменная " + ((Exists) expr.getLeft()).var + " входит свободно в формулу " + expr.getRight()).replace("-", "->"));
                        return;
                    }
                    wr.write(checkError(expr).replace("-", "->"));
                    return;
                }
                if (i % 1000 == 0) {
                    System.out.println(i);
                }
                i++;
            }

            if (alpha != null) {
                for (String var : axVariables.keySet()) {
                    if (alpha.freeVariable(var) && Arrays.asList(alpha.toString().split("\\W")).contains(var)) {
                        wr.write(("Вывод некорректен начиная с формулы номер " + axVariables.get(var) + ": используется схема аксиом" +
                                " с квантором по переменной " + var + ", входящей свободно в допущение " + alpha).replace("-", "->"));
                        return;
                    }
                }
                for (String var : rulesVariable.keySet()) {
                    if (alpha.freeVariable(var) && Arrays.asList(alpha.toString().split("\\W")).contains(var)) {
                        wr.write(("Вывод некорректен начиная с формулы номер " + rulesVariable.get(var) + ": используется правило" +
                                " с квантором по переменной " + var + ", входящей свободно в допущение " + alpha).replace("-", "->"));
                        return;
                    }
                }
            }
            if (alpha != null) {
                String toW = " ";
                for (int q = axiomsSize; q < axioms.size() - 1; ++q) {
                    toW += (axioms.get(q).toString().replace("-", "->") + ',');
                }
                toW = (toW.substring(0, toW.length() - 1) + "|-" + alpha + "->" + fl.split("\\|-")[1]).replace(" ", "");
                wr.write(toW + "\n");
                for (int j = 0; j < answers.size(); ++j) {
                    if (statements.get(j).toString().equals(alpha.toString())) {
                        wr.write(DeductionTheorem.aalpha(alpha.toString()).replace("-", "->"));
                        continue;
                    }
                    if (answers.get(j).contains("mp")) {
                        String mp = answers.get(j).split(" ")[1];
                        wr.write(DeductionTheorem.modus(statements.get(Integer.valueOf(mp)).toString(), statements.get(j).toString(), alpha.toString()).replace("-", "->"));
                    }
                    if (answers.get(j).contains("ax")) {
                        wr.write(DeductionTheorem.eq(statements.get(j).toString(), alpha.toString()).replace("-", "->"));
                    }
                    if (answers.get(j).contains("exists")) {
                        wr.write(DeductionTheorem.existsRule(alpha.toString()
                                , statements.get(j).getLeft().getLeft().toString()
                                , statements.get(j).getRight().toString()
                                , ((Quantifier) statements.get(j).getLeft()).var).replace("-", "->"));
                    }
                    if (answers.get(j).contains("all")) {
                        wr.write(DeductionTheorem.allRule(alpha.toString()
                                , statements.get(j).getLeft().toString()
                                , statements.get(j).getRight().getLeft().toString()
                                , ((Quantifier) statements.get(j).getRight()).var).replace("-", "->"));
                    }
                }
                return;
            }
            for (Expression st :
                    statements) {
                wr.write(st.toString().replace("-", "->") + '\n');
            }
        }
    }

    private boolean checkStatement(Expression e, int num) {

        if (specialRulesCheck(e, num)) {
            answers.add("ax");
            statements.add(e);
            return true;
        }
        for (int i = 0; i < 13; ++i) {
            axMap = new HashMap<>();
            termMap = new HashMap<>();
            if (axiomCheck(e, axioms.get(i))) {
                answers.add("ax");
                statements.add(e);
                return true;
            }
        }
        for (int i = 13; i < axioms.size(); ++i) {
            if (e.equals(axioms.get(i))) {
                answers.add("ax");
                statements.add(e);
                return true;
            }
        }
        checkAll = false;
        checkExists = false;

        for (int i = statements.size() - 1; i >= 0; --i) {
            if (statements.get(i) instanceof Implication) {
                if (e instanceof Implication) {
                    if (e.getLeft() instanceof Exists) {
                        boolean check = new TreeTraveler().traverseExpressionTree(e.getLeft().getLeft(), statements.get(i).getLeft()
                                , ((Exists) e.getLeft()).var, null)
                                && e.getRight().equals(statements.get(i).getRight());
                        if (check) {
                            checkExists = true;
                        }
                        if ((!Arrays.asList(e.getRight().toString().split("\\W")).contains(((Exists) e.getLeft()).var)
                                || !(e.getRight().freeVariable(((Exists) e.getLeft()).var))) && check) {
                            rulesVariable.putIfAbsent(((Exists) e.getLeft()).var, num);
                            statements.add(e);
                            answers.add("exists");
                            return true;
                        }
                    }
                    if (e.getRight() instanceof Any) {
                        boolean check = new TreeTraveler().traverseExpressionTree(e.getRight().getLeft(), statements.get(i).getRight()
                                , ((Any) e.getRight()).var, null)
                                && e.getLeft().equals(statements.get(i).getLeft());
                        if (check) checkAll = true;
                        if (check && ((!Arrays.asList(e.getLeft().toString().split("\\W")).contains(((Any) e.getRight()).var)
                                || !(e.getLeft().freeVariable(((Any) e.getRight()).var))))) {
                            rulesVariable.putIfAbsent(((Any) e.getRight()).var, num);
                            statements.add(e);
                            answers.add("all");
                            return true;
                        }
                    }
                }
                if (e.equals(statements.get(i).getRight())) {
                    for (int j = statements.size() - 1; j >= 0; --j) {
                        if (statements.get(j).equals(statements.get(i).getLeft())) {
                            statements.add(e);
                            answers.add("mp " + j);
                            return true;
                        }
                    }
                }
            }

        }
        return false;
    }

    public boolean specialRulesCheck(Expression e, int num) {
        if (e instanceof Implication) {
            if (e.getLeft() instanceof Conjuction
                    &&
                    e.getLeft().getRight() instanceof Any
                    && e.getLeft().getRight().getLeft().getLeft() != null
                    && new TreeTraveler().traverseExpressionTree(e.getLeft().getRight().getLeft().getLeft(), e.getRight(),
                    ((Quantifier) e.getLeft().getRight()).var, null)) {
                Expression expr = ExpressionBuilder.buildExpression(e.getLeft().getRight().getLeft().getLeft().toString());
                if (new TreeTraveler().traverseExpressionTree(
                        expr,
                        e.getRight(), ((Any) e.getLeft().getRight()).var, ((Any) e.getLeft().getRight()).var) &&
                        new TreeTraveler().traverseExpressionTree(
                                expr,
                                e.getLeft().getLeft(), ((Any) e.getLeft().getRight()).var, "0") &&
                        new TreeTraveler().traverseExpressionTree(
                                expr,
                                e.getLeft().getRight().getLeft().getRight(), ((Any) e.getLeft().getRight()).var, ((Any) e.getLeft().getRight()).var + "'"))
                    return true;
            }
            if (e.getLeft() instanceof Any) {
                Expression newEx = ExpressionBuilder.buildExpression(e.getLeft().getLeft().toString());
                TreeTraveler tt = new TreeTraveler();
                if (tt.traverseExpressionTree(newEx, e.getRight(), ((Any) e.getLeft()).var, null)) {
                    return true;
                }
            }
            if (e.getRight() instanceof Exists) {
                Expression newEx = ExpressionBuilder.buildExpression(e.getRight().getLeft().toString());
                TreeTraveler tt = new TreeTraveler();
                if (tt.traverseExpressionTree(newEx, e.getLeft(), ((Exists) e.getRight()).var, null)) {
                    return true;
                }
            }
        }
        return false;
    }


    public boolean axiomCheck(Expression e, Expression ax) {
        if (e == null || ax == null) {
            return e == null && ax == null;
        }
        if (ax instanceof Predicate) {
            if (((Predicate) ax).terms == null || ((Predicate) ax).terms.size() == 0) {
                if (axMap.containsKey(ax.toString())) {
                    return e.toString().equals(axMap.get(ax.toString()));
                }
                axMap.put(ax.toString(), e.toString());
                return true;
            } else if (e instanceof Predicate && ((Predicate) e).name.equals(((Predicate) ax).name)) {
                ArrayList<Term> ar1 = ((Predicate) e).terms;
                ArrayList<Term> axTerms = ((Predicate) ax).terms;
                return checkTermEquality(ar1, axTerms);
            }
        }
        return ax.getType() == e.getType() && axiomCheck(e.getLeft(), ax.getLeft()) && axiomCheck(e.getRight(), ax.getRight());
    }


    private boolean checkTermEquality(ArrayList<Term> ar1, ArrayList<Term> axTerms) {
        if (ar1.size() != axTerms.size()) return false;
        for (int i = 0; i < ar1.size(); ++i) {
            if (axTerms.get(i) instanceof Variable) {
                if (termMap.containsKey(axTerms.get(i).toString())) {
                    if (!ar1.get(i).toString().equals(termMap.get(axTerms.get(i).toString()))) {
                        return false;
                    }
                    continue;
                } else {
                    termMap.put(axTerms.get(i).toString(), ar1.get(i).toString());
                    continue;
                }
            }
            if (axTerms.get(i).name.equals(ar1.get(i).name)
                    && axTerms.get(i).subterms.size() == ar1.get(i).subterms.size()) {
                if (!checkTermEquality(ar1.get(i).subterms, axTerms.get(i).subterms)) {
                    return false;
                }
            } else return false;
        }
        return true;
    }

    private void initAxs(ArrayList<String> AxiomsPath) throws IOException {
        axioms = new ArrayList<>();
        for (String Axioms : AxiomsPath) {
            try (Scanner sc = new Scanner(new File(Axioms))) {

                while (sc.hasNextLine()) {
                    axioms.add(ExpressionBuilder.buildExpression(sc.nextLine()));
                }
                axiomsSize = axioms.size();
            }
        }
    }

    private String checkError(Expression e) {
        if (e instanceof Implication) {
            if (e.getLeft() instanceof Any) {
                ErrorAnsw errorAnsw = new ErrorAnsw(e.getLeft().getLeft(), e.getRight(), ((Any) e.getLeft()).var, null);
                if (errorAnsw.traverseExpressionTree()) {
                    return ": терм " + errorAnsw.b + " не свободен для подстановки в формулу "
                            + e.getLeft().getLeft() + " вместо переменной " + errorAnsw.a;
                }
            }
            if (e.getRight() instanceof Exists) {
                ErrorAnsw errorAnsw = new ErrorAnsw(e.getRight().getLeft(), e.getLeft(), ((Exists) e.getRight()).var, null);
                if (errorAnsw.traverseExpressionTree()) {
                    return ": терм " + errorAnsw.b + " не свободен для подстановки в формулу "
                            + e.getRight().getLeft() + " вместо переменной " + errorAnsw.a;
                }
            }
        }
        return "";

    }
}