package horn.sat.solver;

import horn.sat.solver.atoms.IAtom;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Class representing the Horn Formula whose satisfiability should be determined.
 *
 * Horn formulas are a special subset of propositional logic formulas that have the following form:
 *  (P1 ∧ P2 ∧ ... ∧ Pn => P*) ∧ (Q1 ∧ Q2 ∧ ... ∧ Qn => Q*) ∧ .... ∧ (Z1 ∧ Z2 ∧ ... ∧ Zn => Z*)
 */
public class HornFormula {

    private final Set<HornClause> clauses; // All clauses are implicitly connected via a logical conjunction

    public HornFormula(final Set<HornClause> clauses) {
        this.clauses = clauses;
    }

    /**
     * Computes a set with all the atoms that are contained in the clauses that make up this formula.
     *
     * @return Set with all atoms
     */
    public Set<IAtom> getAllContainedAtoms() {
        final Set<IAtom> allAtoms = new HashSet<>();

        for (final HornClause clause : this.clauses) {
            allAtoms.addAll(clause.getAllAtoms());
        }

        return allAtoms;
    }

    /**
     * Return a clause that evaluates to false under the specified configuration.
     *
     * @param configuration all atoms contained in the configuration are assigned the truth value 'true'. All other
     *                      atoms implicitly assume the value 'false'.
     * @return An unsat clause if it exists, Optional.empty() otherwise.
     */
    public Optional<HornClause> getUnsatClause(final Set<IAtom> configuration) {
        return this.clauses
                .stream()
                .filter(clause -> !clause.satisfiedByGivenConfiguration(configuration))
                .findAny();
    }
}
