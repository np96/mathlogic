package HW1;

import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

public class DeductionTheorem {
    String arrow = "->";

    public DeductionTheorem(String axs, String inFile, String outFile) {
        Parser parser = new Parser(axs, inFile, outFile);
        try {
            Scanner sc = new Scanner(new File(inFile));
            PrintWriter pw = new PrintWriter(new File(outFile));
            String[] input = parser.cond.split(",");
            String delim = "";
            for (int i = 0; i < input.length - 1; ++i) {
                pw.write(delim.concat(input[i]));
                delim = ",";
            }
            int a = input[input.length - 1].indexOf("|-");
            pw.write("|-".concat(input[input.length - 1].substring(0, a)
                    .concat(arrow)
                    .concat(input[input.length - 1].substring(a + 2))).concat("\n"));
            sc.nextLine();
            System.out.println(parser.alpha);
            while (sc.hasNextLine()) {
                String ans = sc.nextLine();
                String s = ans.replace(">", "");
                Expression expr = parser.binary(0, s.length(), s, ops.IMPL);
                String answ = parser.checkExpression(expr);
                if (s.replace("-","->").equals(parser.alpha)) {
                    pw.write(aalpha(parser.alpha));
                    continue;
                }
                if (answ.contains("акс. ") ||
                        answ.contains("предп. ")) {
                    pw.write(eq(ans, parser.alpha));
                    continue;
                }
                if (answ.contains("M.P.")) {
                    System.out.println(ans);
                    pw.write(modus(parser.statements.get(Integer.parseInt((answ.split(" "))[1]
                            .split(",")[0]) - 1).toString().replace("-","->"),ans, parser.alpha));
                }

            }
            pw.close();
            sc.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String eq(String s, String alpha) {
        return ("b\n" +
                "(b)->((a)->(b))\n" +
                "(a)->(b)\n").replace("a", alpha).replace("b", s);
    }

    String modus(String mp, String s, String alpha) {

        return ("((a)->(b1))->(((a)->((b1)->(b2)))->((a)->(b2)))\n" +
                "(((a)->((b1)->(b2)))->((a)->(b2)))\n" +
                "(a)->(b2)\n").replace("a", alpha).replace("b1", mp).replace("b2", s);


    }

    String aalpha(String a) {
        return ("(a)->((a)->(a))->(a)\n" +
                "(a)->(a)->(a)\n" +
                "((a)->((a)->(a)))->((a)->((a)->(a))->(a))->((a)->(a))\n" +
                "((a)->((a)->(a))->(a))->((a)->(a))\n" +
                "(a)->(a)\n").replace("a", a);


    }
}
