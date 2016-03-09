package HW3;

public class Solver {

    public static void main(String[] args)  {


        ProofMaker prf = new ProofMaker(args[0],args[1],args[2],args[3]);//"rules", "Axioms.in", "src/input", "src/output");
        try {
            prf.execute();

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
