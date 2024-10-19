package horn.sat.solver.util;

import horn.sat.solver.atoms.IAtom;

import java.util.Map;
import java.util.Optional;

public class SolverResult {

    private final SatResult satResult;
    private final Optional<Map<IAtom, Boolean>> satisfiableConfiguration;

    private SolverResult(final SatResult satResult) {
        this.satResult = satResult;
        this.satisfiableConfiguration = Optional.empty();
    }

    private SolverResult(final SatResult satResult, final Map<IAtom, Boolean> satisfiableConfiguration) {
        this.satResult = satResult;
        this.satisfiableConfiguration = Optional.of(satisfiableConfiguration);
    }

    public static SolverResult unsat() {
        return new SolverResult(SatResult.UNSAT);
    }

    public static SolverResult sat(final Map<IAtom, Boolean> satisfiableConfiguration) {
        return new SolverResult(SatResult.SAT, satisfiableConfiguration);
    }

    public String getVerdict() {
        final StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("Verdict: ").append(this.satResult).append("\n");

        if (this.satResult == SatResult.UNSAT) {
            stringBuilder.append("There exists no configuration that satisfies the given Horn Formula.");
            return stringBuilder.toString();
        }

        stringBuilder.append("e.g. (");

        for (final Map.Entry<IAtom, Boolean> entry : satisfiableConfiguration.get().entrySet()) {
            stringBuilder
                    .append(entry.getKey())
                    .append(": ")
                    .append(Boolean.toString(entry.getValue()))
                    .append(", ");
        }
        stringBuilder.delete(stringBuilder.length()-2, stringBuilder.length());
        stringBuilder.append(")");

        return stringBuilder.toString();
    }

    public enum SatResult {
        SAT,
        UNSAT
    }
}
