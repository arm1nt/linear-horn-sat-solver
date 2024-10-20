package horn.sat.solver.util;

import horn.sat.solver.exceptions.InvalidInputException;

import java.util.HashMap;
import java.util.Map;

public class CliParser {

    /**
     * Parse the specified cli arguments
     *
     * @param args commandline arguments
     * @return map associating the accepted options with their arguments
     * @throws InvalidInputException is thrown if an unsupported option is specified or if an option is specified multiple
     *  times
     */
    public static Map<String, String> parseOptions(final String[] args) throws InvalidInputException {
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
}
