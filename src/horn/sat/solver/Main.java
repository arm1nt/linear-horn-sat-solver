package horn.sat.solver;

import horn.sat.solver.exceptions.InvalidInputException;
import horn.sat.solver.parsers.Parser;
import horn.sat.solver.parsers.ParserFactory;
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

        if (args.length > 2) {
            usage("Unexpected number of arguments");
        }

        final Map<String, String> options = new HashMap<>();
        try {
            options.putAll(parseOptions(args));
        } catch (InvalidInputException e) {
            usage(e.getMessage());
        }

        final Optional<Path> opInputFilePath = Optional.ofNullable(options.get("inputFile")).map(Main::toPath);
        final Optional<Path> opOutputFilePath = Optional.ofNullable(options.get("outputFile")).map(Main::toPath);

        final Parser parser = ParserFactory.getParser(opInputFilePath);

        final HornFormula hornFormula = parser.parse();
        final SolverResult result = Solver.solve(hornFormula);

        try(final BufferedWriter writer = getOutputWriter(opOutputFilePath)) {
            writer.write(result.getVerdict());
        } catch (IOException e) {
            System.err.printf("Error writing SAT result: %s", e.getMessage());
            System.exit(1);
        }
    }

    private static Path toPath(final String stringPath) {
        try {
            return (stringPath.startsWith("./"))
                    ? getAbsolutePath(stringPath)
                    : Path.of(stringPath);
        } catch (InvalidPathException e) {
            usage(String.format("Path '%s' is not a valid path: %s", stringPath, e.getMessage()));
        }
        //unreachable
        throw new RuntimeException();
    }

    private static Path getAbsolutePath(final String relativePath) {
        final String[] pathComponents = relativePath.substring(2).split(System.getProperty("file.separator"));
        return Path.of(System.getProperty("user.dir"), pathComponents);
    }

    private static BufferedWriter getOutputWriter(final Optional<Path> outputFilePath) throws IOException {
        if (outputFilePath.isPresent()) {
            return new BufferedWriter(new FileWriter(outputFilePath.get().toFile()));
        }
        return new BufferedWriter(new PrintWriter(System.out));
    }

    private static Map<String, String> parseOptions(final String[] args) throws InvalidInputException {
        final Map<String, String> options = new HashMap<>();

        for (int i = 0; i < args.length; i++) {

            switch (args[i]) {
                case "-i" -> {
                    if (options.containsKey("inputFile")) {
                        throw new InvalidInputException(String.format("Option '%s' must not be specified more than once!", "-i"));
                    }
                    options.put("inputFile", getValue(i, "-i", args));
                    i++;
                }
                case "-o" -> {
                    if (options.containsKey("outputFile")) {
                        throw new InvalidInputException(String.format("Option '%s' must not be specified more than once!", "-o"));
                    }
                    options.put("outputFile", getValue(i, "-o", args));
                    i++;
                }
                default -> throw new InvalidInputException(String.format("'%s' is not recognized a supported option!", args[i]));
            }
        }
        return options;
    }

    private static String getValue(final int index, final String option, final String[] args) throws InvalidInputException {
        if (index+1 >= args.length) {
            throw new InvalidInputException(String.format("Option '%s' is missing its argument", option));
        }
        return args[index+1].trim();
    }

    private static void usage(final String message) {
        System.err.println("Usage: Solver [-o OUTPUT_FILE] [-i INPUT_FILE]");
        System.err.println(message);
        System.exit(1);
    }
}
