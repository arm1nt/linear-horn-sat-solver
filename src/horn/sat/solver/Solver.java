package horn.sat.solver;

import horn.sat.solver.atoms.Falsum;
import horn.sat.solver.atoms.IAtom;
import horn.sat.solver.atoms.Verum;
import horn.sat.solver.util.SolverResult;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Takes a Horn Formula as input and determines whether the formula is satisfiable or not.
 * If the formula is satisfiable, a satisfying configuration is returned.
 */
public class Solver {
    private static final IAtom VERUM = Verum.getInstance();
    private static final IAtom FALSUM = Falsum.getInstance();

    public static SolverResult solve(final HornFormula hornFormula) {
        // All atoms contained in this set are considered to be 'marked' and are assigned the truth value 'true'.
        //  All un-marked atoms that are contained in the formula are implicitly assigned the truth value 'false'.
        final Set<IAtom> marked = new HashSet<>();
        final Set<IAtom> atoms = hornFormula.getAllContainedAtoms();

        if (atoms.contains(VERUM)) {
            marked.add(VERUM);
        }

        while (true) {
            //Get a horn clause that under the current assignment evaluates to false.
            final Optional<HornClause> unsatClause = hornFormula.getUnsatClause(marked);

            if (unsatClause.isEmpty()) { // The current configuration satisfies all of the formulas clauses
                break;
            }

            marked.add(unsatClause.get().getImpliedAtom());
        }

        if (marked.contains(FALSUM)) {
            // Falsum must assume the value 'true' in order for the input formula to be satisfiable. However, falsum
            // must always have the value 'false', therefore, the formula is not satisfiable.
            return SolverResult.unsat();
        }

        final Map<IAtom, Boolean> satisfyingConfiguration = new HashMap<>();

        for (final IAtom atom : atoms) {
            satisfyingConfiguration.put(atom, marked.contains(atom));
        }

        return SolverResult.sat(satisfyingConfiguration);
    }
}
