package Predicate;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Solver {

    public static void main(String args[])  {
        if (args.length != 4)
            for (int i = 1; i <= 12; ++i) {
                try {
                    new ProofChecker(new ArrayList<>(Arrays.asList("Axioms.in", "Arithm.in")), "tests/HW4/incorrect" + i + ".in", "output").checkProof();
                    try (Scanner sc = new Scanner(new File("output"))) {
                        System.out.println(i + "\t" + sc.nextLine());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    continue;
                }
            }
        else try {
            new ProofChecker(new ArrayList<>(Arrays.asList(args[0], args[1])), args[2], args[3]).checkProof();
        } catch (IOException e) {
            e.printStackTrace();
        }}
}