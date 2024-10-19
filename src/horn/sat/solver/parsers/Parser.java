package horn.sat.solver.parsers;

import horn.sat.solver.HornFormula;
import horn.sat.solver.atoms.Atom;
import horn.sat.solver.atoms.Falsum;
import horn.sat.solver.atoms.IAtom;
import horn.sat.solver.atoms.Verum;
import horn.sat.solver.exceptions.InvalidAtomException;

public interface Parser {

    HornFormula parse();

    default IAtom toAtom(final String symbol) throws InvalidAtomException {
        if (symbol.isBlank()) {
            throw new InvalidAtomException("Symbol must not be empty");
        }

        return switch (symbol) {
            case "0" -> Falsum.getInstance();
            case "1" -> Verum.getInstance();
            default -> new Atom(symbol);
        };
    }
}
