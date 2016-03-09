package Sum;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;


public class Sum {

    final private static String lemma3 =
            "a+0=a->(0=0->0=0->0=0)->a+0=a\n" +
                    "a+0=a\n" +
                    "(0=0->0=0->0=0)->a+0=a\n" +
                    "(0=0->0=0->0=0)->@a(a+0=a)\n" +
                    "0=0->0=0->0=0\n" +
                    "@a(a+0=a)\n" +
                    "@a(a+0=a)->0+0=0\n" +
                    "0+0=0\n" +
                    "(a=b->a=c->b=c)\n" +
                    "(a=b->a=c->b=c)->(0=0->0=0->0=0)->(a=b->a=c->b=c)\n" +
                    "(0=0->0=0->0=0)->(a=b->a=c->b=c)\n" +
                    "(0=0->0=0->0=0)->@c(a=b->a=c->b=c)\n" +
                    "(0=0->0=0->0=0)->@b@c(a=b->a=c->b=c)\n" +
                    "(0=0->0=0->0=0)->@a@b@c(a=b->a=c->b=c)\n" +
                    "@a@b@c(a=b->a=c->b=c)\n" +
                    "@a@b@c(a=b->a=c->b=c)->@b@c(a+0=b->a+0=c->b=c)\n" +
                    "@b@c(a+0=b->a+0=c->b=c)\n" +
                    "@b@c(a+0=b->a+0=c->b=c)->@c(a+0=a->a+0=c->a=c)\n" +
                    "@c(a+0=a->a+0=c->a=c)\n" +
                    "@c(a+0=a->a+0=c->a=c)->(a+0=a->a+0=a->a=a)\n" +
                    "a+0=a->a+0=a->a=a\n" +
                    "a+0=a\n" +
                    "a+0=a->a=a\n" +
                    "a=a\n" +
                    "(a=a)->(0=0->0=0->0=0)->a=a\n" +
                    "(0=0->0=0->0=0)->a=a\n" +
                    "(0=0->0=0->0=0)->@a(a=a)\n" +
                    "@a(a=a)\n" +
                    "@a(a=a)->(0+a)'=(0+a)'\n" +
                    "(0+a)'=(0+a)'\n" +
                    "a+b'=(a+b)'\n" +
                    "(a+b'=(a+b)')->(0=0->0=0->0=0)->(a+b'=(a+b)')\n" +
                    "(0=0->0=0->0=0)->(a+b'=(a+b)')\n" +
                    "(0=0->0=0->0=0)->@b(a+b'=(a+b)')\n" +
                    "(0=0->0=0->0=0)->@a@b(a+b'=(a+b)')\n" +
                    "@a@b(a+b'=(a+b)')\n" +
                    "@a@b(a+b'=(a+b)')->@b(0+b'=(0+b)')\n" +
                    "@b(0+b'=(0+b)')\n" +
                    "@b(0+b'=(0+b)')->(0+a'=(0+a)')\n" +
                    "0+a'=(0+a)'\n" +
                    "a=b->a'=b'\n" +
                    "(a=b->a'=b')->(0=0->0=0->0=0)->(a=b->a'=b')\n" +
                    "(0=0->0=0->0=0)->(a=b->a'=b')\n" +
                    "(0=0->0=0->0=0)->@b(a=b->a'=b')\n" +
                    "(0=0->0=0->0=0)->@a(@b(a=b->a'=b'))\n" +
                    "@a(@b(a=b->a'=b'))\n";

    private final static String lemma2 =
            "(s)+0=(s)\n" +
                    "@a(@b(a=b->a'=b'))->@b(0+(s)=b->(0+(s))'=b')\n" +
                    "@b(0+(s)=b->(0+(s))'=b')\n" +
                    "@b(0+(s)=b->(0+(s))'=b')->(0+(s)=(s)->(0+(s))'=(s)')\n" +
                    "0+(s)=(s)->(0+(s))'=(s)'\n" +
                    "@a@b@c(a=b->a=c->b=c)->@b@c((s)=b->(s)=c->b=c)\n" +
                    "@b@c((s)=b->(s)=c->b=c)\n" +
                    "@b@c((s)=b->(s)=c->b=c)->@c((s)=q->(s)=c->q=c)\n" +
                    "@c((s)=q->(s)=c->q=c)\n" +
                    "@c((s)=q->(s)=c->q=c)->((s)=q->(s)=(s)->q=(s))\n" +
                    "(s)=q->(s)=(s)->q=(s)\n" +
                    "(s)=(s)->(s)=q->(s)=(s)\n" +
                    "@a(a=a)->(s)=(s)\n" +
                    "(s)=(s)\n" +
                    "(s)=q->(s)=(s)\n" +
                    "((s)=q->(s)=(s))->((s)=q->(s)=(s)->q=(s))->((s)=q->q=(s))\n" +
                    "((s)=q->(s)=(s)->q=(s))->((s)=q->q=(s))\n" +
                    "(s)=q->q=(s)\n";
    

    public static void main(String[] args) {
        String a = "0''''''''''";
        String b = "0'''''''''''''''";
        try (Scanner sc = new Scanner(new File(args[0]))) {
            String q = sc.nextLine();
            a = q.split(" ")[0];
            b = q.split(" ")[1];
            if (!a.contains("\'")) {
                a = 0 + new String(new char[Integer.valueOf(a)]).replace("\0", "\'");
            }
            if (!b.contains("\'")) {
                b = 0 + new String(new char[Integer.valueOf(b)]).replace("\0", "\'");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<String> proof = new ArrayList<>();
        int count = 0;
        String t = b;
        while (!t.equals("0")) {
            count++;
            proof.add(a + "+" + t + "=(" + a + "+" + (t = t.substring(0, t.length() - 1)) + ")'" + '\n');
        }
        proof.add(lemma3);
        String s = a;
        while (count > 0) {
            proof.add(a + "+" + t + "=" + s + '\n');
            proof.add(a + "+" + t + "=" + s + "->" + "(" + a + "+" + t + ")'" + "=" + s + "'" + '\n');
            proof.add("(" + a + "+" + t + ")'" + "=" + s + "'" + '\n');
            proof.add(lemma2.replace("s", a + "+" + t + "'").replace("q", "(" + a + "+" + t + ")'"));
            proof.add("(" + a + "+" + t + ")'=" + a + "+" + t + "'" + '\n');
            proof.add("(" + a + "+" + t + ")'=" + a + "+" + t + "'->(" + a + "+" + t + ")'=" + s + "'->" + a + "+" + t + "'=" + s + "'" + '\n');
            proof.add("(" + a + "+" + t + ")'=" + s + "'->" + a + "+" + t + "'=" + s + "'" + '\n');
            s += '\'';
            t += '\'';
            count--;
        }
        proof.add(a + "+" + t + "=" + s + '\n');

        try (PrintWriter pw = new PrintWriter(new File(args[1]))) {
            for (String subproof : proof) {
                pw.write(subproof);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
