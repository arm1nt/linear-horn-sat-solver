package horn.sat.solver.atoms;

/**
 * Truth value constant for 'true'
 */
public class Verum implements IAtom {

    private final static Verum VERUM = new Verum();

    private Verum() {};

    public static Verum getInstance() {
        return VERUM;
    }

    @Override
    public String toString() {
        return "⊤";
    }
}
