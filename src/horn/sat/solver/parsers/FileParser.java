package horn.sat.solver.parsers;

import horn.sat.solver.HornClause;
import horn.sat.solver.HornFormula;
import horn.sat.solver.exceptions.InvalidFormulaException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

/**
 * Read horn formula from a specified input file.
 */
public class FileParser extends LinearParser {

    private final Path path;

    FileParser(final Path path) {
        this.path = path;
    }

    @Override
    public HornFormula parse() {
        final Set<HornClause> clauses = new HashSet<>();

        try(final BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
            String line;

            while ((line = reader.readLine()) != null) {
                clauses.add(parseHornClause(line));
            }
        } catch (InvalidFormulaException e) {
            System.err.printf("Given formula is not a valid horn formula: %s", e.getMessage());
            System.exit(1);
        } catch (FileNotFoundException e) {
            System.err.printf("File at path '%s' not found", path.toString());
            System.exit(1);
        } catch (IOException e) {
            System.err.printf("Error reading from the input file: %s%n", e.getMessage());
            System.exit(1);
        }

        return new HornFormula(clauses);
    }
}
