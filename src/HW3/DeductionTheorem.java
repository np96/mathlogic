package HW3;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class DeductionTheorem {
    Parser parser;

    public DeductionTheorem(Parser prsr) {
        parser = prsr;
    }
    
    String reformat(Expression expr,Expression alpha) throws Exception {

        String answ = parser.checkExpression(expr);
        if (parser.checkStatementEquality(expr,alpha)) {
            return aalpha((alpha).toString());
        }

        if (answ.contains("акс. ") ||
                answ.contains("предп. ")) {
            return eq(expr.toString(), (alpha).toString());
        }

        if (answ.contains("M.P.")) {
            Expression mp = parser.statements.get(Integer.parseInt((answ.split(" "))[2])-1);
            return (modus(mp.getLeft().getinRep, mp.getRight().getinRep, alpha.toString()));
        }
        throw new Exception("Hello "+ expr.toString().replace("-","->") + "\n" +
                expr.getLeft().getinRep.replace("-","->") + "\n"
        + expr.getRight().getinRep.replace("-","->"));
    }

    String eq(String s, String alpha) {
        return ("(b)\n" +
                "((b)-((a)-(b)))\n" +
                "((a)-(b))").replace("a", alpha).replace("b", s);
    }

    String modus(String mp, String s, String alpha) {

        return ("((a)-(b1))-((a)-(b1)-(b2))-((a)-(b2))\n" +
                "((a)-((b1)-(b2)))-((a)-(b2))\n" +
                "(a)-(b2)").replace("a", alpha).replace("b1", mp).replace("b2", s);


    }

    String aalpha(String a) {
        return (
                "(a)-((a)-(a))\n" +
                "((a)-((a)-(a)))-((a)-((a)-(a))-(a))-((a)-(a))\n" +
                        "(a)-((a)-(a))-(a)\n" +
                "((a)-((a)-(a))-(a))-((a)-(a))\n" +
                "(a)-(a)").replace("a", a);
    }
}
