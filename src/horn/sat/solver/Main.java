package horn.sat.solver;

import horn.sat.solver.exceptions.InvalidInputException;
import horn.sat.solver.parsers.Parser;
import horn.sat.solver.parsers.ParserFactory;
import horn.sat.solver.util.CliParser;
import horn.sat.solver.util.SolverResult;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Linear SAT solver for Horn Formulas.
 */
public class Main {

    public static void main(String[] args) {

        final Map<String, String> options = new HashMap<>();
        try {
            options.putAll(CliParser.parseOptions(args));
        } catch (InvalidInputException e) {
            System.err.println("Usage: Solver [-i INPUT_FILE] [-o OUTPUT_FILE]");
            System.err.println(e.getMessage());
            System.exit(1);
        }

        final Optional<Path> inputFilePath = Optional.ofNullable(options.get("inputFile")).map(Main::toPath);
        final Optional<Path> outputFilePath = Optional.ofNullable(options.get("outputFile")).map(Main::toPath);

        final Parser parser = ParserFactory.getParser(inputFilePath);
        final HornFormula hornFormula = parser.parse();
        final SolverResult result = Solver.solve(hornFormula);

        try(final BufferedWriter writer = getOutputWriter(outputFilePath)) {
            writer.write(result.getVerdict());
        } catch (IOException e) {
            System.err.printf("Error writing SAT result: %s", e.getMessage());
            System.exit(1);
        }
    }

    private static BufferedWriter getOutputWriter(final Optional<Path> outputFilePath) throws IOException {
        if (outputFilePath.isPresent()) {
            return new BufferedWriter(new FileWriter(outputFilePath.get().toFile()));
        }
        return new BufferedWriter(new PrintWriter(System.out));
    }

    private static Path toPath(final String stringPath) {
        try {
            if (stringPath.startsWith("./")) {
                final String[] pathComponents = stringPath.substring(2).split(System.getProperty("file.separator"));
                return Path.of(System.getProperty("user.dir"), pathComponents);
            }
            return Path.of(stringPath);
        } catch (InvalidPathException e) {
            System.err.printf("Path '%s' is not a valid path: %s%n", stringPath, e.getMessage());
            System.exit(1);
        }
        throw new RuntimeException(); // unreachable
    }
}
