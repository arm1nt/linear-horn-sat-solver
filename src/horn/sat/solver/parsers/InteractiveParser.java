package horn.sat.solver.parsers;

import horn.sat.solver.HornClause;
import horn.sat.solver.HornFormula;
import horn.sat.solver.exceptions.InvalidFormulaException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Get user input from stdin
 */
public class InteractiveParser extends LinearParser {

    @Override
    public HornFormula parse() throws InvalidFormulaException {
        final Set<HornClause> clauses = new LinkedHashSet<>();

        System.out.println("Enter each clause making up your horn formula line by line!");
        System.out.println("Press '<STRG> + D' to stop entering new clauses");

        try(final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {

            while (true) {
                System.out.printf("Current formula: %s", stringifiedFormulaState(clauses));
                System.out.print("> ");

                final String line = reader.readLine();

                if (line == null) {
                    break;
                }

                clauses.add(parseHornClause(line.trim()));
            }
            System.out.printf("Formula whose satisfiability will be determined: %s", stringifiedFormulaState(clauses));
        } catch (InvalidFormulaException e) {
            System.err.printf("Given formula is not a valid horn formula: %s", e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.printf("Error parsing the formula: '%s'%n", e.getMessage());
            System.exit(1);
        }

        return new HornFormula(clauses);
    }

    /**
     * Returns a stringified version of the current Horn Formula state.
     *
     * @param clauses all clauses specified by the user
     * @return stringified version of the formula resulting of these clauses.
     */
    private static String stringifiedFormulaState(final Set<HornClause> clauses) {
        return clauses
                .stream()
                .map(HornClause::toString)
                .collect(Collectors.joining(" ∧ "));
    }
}
