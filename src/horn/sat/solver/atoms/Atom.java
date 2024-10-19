package horn.sat.solver.atoms;

/**
 * Propositional logic atom
 */
public class Atom implements IAtom {

    private final String symbol;

    public Atom(final String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return this.symbol;
    }
}
