package horn.sat.solver.parsers;

import java.nio.file.Path;
import java.util.Optional;

public class ParserFactory {

    /**
     * Returns a situation appropriate parser
     *
     * @param inputFilePath path to the file containing the Horn Formula to be tested for satisfiability
     * @return  if an input file path is specified a {@link FileParser} is used, otherwise a
     *  {@link InteractiveParser} is returned.
     */
    public static Parser getParser(final Optional<Path> inputFilePath) {
        if (inputFilePath.isEmpty()) {
            return new InteractiveParser();
        }
        return new FileParser(inputFilePath.get());
    }
}
