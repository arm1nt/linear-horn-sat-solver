package horn.sat.solver.atoms;

/**
 * Truth value constant for 'false'.
 */
public class Falsum implements IAtom {

    private final static Falsum FALSUM = new Falsum();

    private Falsum() {}

    public static Falsum getInstance() {
        return FALSUM;
    }

    @Override
    public String toString() {
        return "⊥";
    }
}
