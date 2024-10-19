package horn.sat.solver;

import horn.sat.solver.atoms.IAtom;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Class representing a Horn Clause (e.g. P1 ∧ P2 ∧ ... ∧ Pn => P*)
 */
public class HornClause {

    private final Collection<IAtom> conjunction;
    private final IAtom impliedAtom;

    public HornClause(final Collection<IAtom> conjunction, final IAtom impliedAtom) {
        this.conjunction = conjunction;
        this.impliedAtom = impliedAtom;
    }

    public IAtom getImpliedAtom() {
        return this.impliedAtom;
    }

    /**
     * Returns a set with all atoms that are part of this clause (i.e. the atoms in the conjunction and the implied atom)
     *
     * @return set with the clause's atoms.
     */
    public Set<IAtom> getAllAtoms() {
        final Set<IAtom> allAtoms = new HashSet<>(this.conjunction);
        allAtoms.add(this.impliedAtom);
        return allAtoms;
    }

    /**
     * Evaluates whether the clause is satisfied under the given configuration.
     *
     * @param configuration all atoms contained in the given configuration are assigned the value 'true'. All other
     *                      atoms are implicitly assigned the truth value 'false'.
     * @return 'true' if the clause is satisfied, 'false' otherwise.
     */
    public boolean satisfiedByGivenConfiguration(final Set<IAtom> configuration) {
        // If at least one of the conjunction's atoms is assigned the value 'false', the clause evaluates to true.
        for (final IAtom atom : this.conjunction) {
            if (!configuration.contains(atom)) {
                return true;
            }
        }

        return configuration.contains(this.impliedAtom);
    }

    @Override
    public boolean equals(Object o) {
        //Two horn clauses are equivalent if their conjuncts contain the same set of atoms (atom cardinality does not
        // matter). Furthermore, their implied atoms must match as well.
        if (o == this) return true;
        if (o == null || o.getClass() != this.getClass()) return false;

        final HornClause otherClause = (HornClause) o;

        if (!this.impliedAtom.equals(otherClause.impliedAtom)) return false;

        final Set<IAtom> conjunctionAtoms = new HashSet<>(this.conjunction);
        final Set<IAtom> otherConjunctionAtoms = new HashSet<>(otherClause.conjunction);

        return conjunctionAtoms.equals(otherConjunctionAtoms);
    }

    @Override
    public String toString() {
        final String formattedConjunction = conjunction
                .stream()
                .map(Objects::toString)
                .collect(Collectors.joining("∧"));

        return String.format("(%s -> %s)", formattedConjunction, this.impliedAtom.toString());
    }
}
