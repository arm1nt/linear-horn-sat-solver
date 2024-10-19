package horn.sat.solver.parsers;

import horn.sat.solver.HornClause;
import horn.sat.solver.atoms.IAtom;
import horn.sat.solver.exceptions.InvalidAtomException;
import horn.sat.solver.exceptions.InvalidClauseException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Parses a line representing a horn clause and build the corresponding {@link HornClause} object.
 */
public abstract class LinearParser implements Parser {

    /**
     * Takes the string representation of a horn clause and returns its corresponding {@link HornClause} object.
     *
     * @param line string representation of the Horn Clause to be parsed
     * @return parsed horn clause
     * @throws InvalidAtomException thrown if an atom's identifier is blank
     * @throws InvalidClauseException thrown if the format of the given clause is invalid, e.g. missing parenthesis, etc.
     */
    public HornClause parseHornClause(final String line) throws InvalidAtomException, InvalidClauseException {

        if (line.isBlank()) {
            throw new InvalidClauseException("Clause must not be blank");
        }

        final String[] splitClause = line.split("->");

        if (splitClause.length != 2) {
            throw new InvalidClauseException(String.format("Clause '%s' does not follow the required format 'conjunction -> atom'", line));
        }

        final List<IAtom> conjunctionAtoms = getConjunctionAtoms(splitClause[0].trim());
        final IAtom impliedAtom = toAtom(splitClause[1].trim());

        return new HornClause(conjunctionAtoms, impliedAtom);
    }

    /**
     * Returns a list with all the atoms contained in the input clause's conjunction.
     *
     * @param conjunction conjunction part of the inputted horn clause's string representation
     * @return list of all atoms contained in this given conjunction string
     * @throws InvalidClauseException thrown if the format of the given clause is invalid, e.g. missing parenthesis, etc.
     */
    private List<IAtom> getConjunctionAtoms(final String conjunction) throws InvalidClauseException {
        return getConjunctionAtoms(conjunction, 0, conjunction.length()-1);
    }

    /**
     * Returns a list with all the atoms contained in the input clause's conjunction
     *
     * @param conjunction conjunction part of the horn clause's string representation
     * @param startIndex position where in the given string the conjunction to be parsed starts
     * @param endIndex position where in the given string the conjunction to be parsed ends
     * @return list of all atoms contained in the conjunction between the start and end index
     * @throws InvalidClauseException thrown if the format of the given clause is invalid, e.g. missing parenthesis, etc
     */
    private List<IAtom> getConjunctionAtoms(final String conjunction, final int startIndex, final int endIndex) throws InvalidClauseException {
        final List<IAtom> atoms = new ArrayList<>();

        int index = startIndex;

        while (index <= endIndex) {
            final char character = conjunction.charAt(index);

            if (Character.isWhitespace(character)) { // ignoring superfluous whitespaces
                index++;
                continue;
            }

            if (character == '&' || character == ')') {
                throw new InvalidClauseException(String.format("Found unexpected '%s' in conjunction '%s'", character, conjunction));
            }

            if (character == '(') { // Get all atoms in the sub-conjunction
                final int subConjunctionStart = index + 1; // remove leading parenthesis
                final int subConjunctionEnd = findSubConjunctionEnd(subConjunctionStart, endIndex, conjunction); //this index is incl. trailing parenthesis
                atoms.addAll(getConjunctionAtoms(conjunction, subConjunctionStart, subConjunctionEnd - 1));

                // commence the atom search after the sub-conjunction's closing parenthesis
                index = findStartOfNextConjunct(subConjunctionEnd + 1, endIndex, conjunction);
                continue;
            }

            // All other characters are considered to be valid conjunct starters
            final int conjunctEnd = findEndOfConjunct(index, endIndex, conjunction);
            atoms.add(toAtom(conjunction.substring(index, conjunctEnd).trim()));

            index = findStartOfNextConjunct(conjunctEnd, endIndex, conjunction);
        }
        return atoms;
    }

    /**
     * Starting from the given start index, this method searches the starting position of the next conjunct.
     *
     * @param start position where the search for a new conjunct starts
     * @param end end of the search scope
     * @param conjunction scope on which the search is performed
     * @return the start position of the next conjunct, otherwise a value greater than end is returned
     * @throws InvalidClauseException if the conjunction string indicates that another conjunct should exist, but that
     *  conjunct cannot be found.
     */
    private int findStartOfNextConjunct(final int start, final int end, final String conjunction) throws InvalidClauseException {
        int index = start;

        while (index <= end) {
            final char character = conjunction.charAt(index);

            if (character == '&') {
                final String trailingSubstring = conjunction.substring(index+1);

                final long trailingValidCharacters = IntStream
                        .range(0, trailingSubstring.length())
                        .mapToObj(trailingSubstring::charAt)
                        .distinct()
                        .filter(val -> !val.equals('&') && !val.equals(')') & !Character.isWhitespace(val))
                        .count();

                if (trailingValidCharacters == 0) {
                    throw new InvalidClauseException(String.format("Conjunction '%s' is missing a conjunct", conjunction));
                }

                return index+1;
            }

            if (Character.isWhitespace(character)) {
                index++;
                continue;
            }

            throw new InvalidClauseException(String.format("Invalid conjunction '%s' provided", conjunction));
        }
        return index;
    }

    /**
     * Starting from the given start index, this method searches the end of the conjunct's identifier.
     *
     * @param start position where the conjunct's identifier name starts
     * @param end end of the search scope in the given conjunction
     * @param conjunction scope on which the search is performed
     * @return the index in the given conjunction where the conjunct's identifier ends, if no end is found, a value
     *  greater than the search scopes end index is returned
     */
    private int findEndOfConjunct(final int start, final int end, final String conjunction) {
        int index = start;

        while (index <= end) {
            final char character = conjunction.charAt(index);

            if (Character.isWhitespace(character) || character == '&') {
                return index;
            }
            index++;
        }
        return index;
    }

    /**
     * This method searches the position in the conjunction where the sub-conjunction starting from index 'start' ends.
     *
     * @param start position where the sub-conjunction starts in the given conjunction
     * @param end end of the search scope in the given conjunction
     * @param conjunction search scope
     * @return position representing the end of the sub-conjunction
     * @throws InvalidClauseException thrown if no end can be found for the started sub-conjunction
     */
    private int findSubConjunctionEnd(final int start, final int end, final String conjunction) throws InvalidClauseException {
        int parenthesisDepth = 0;

        for (int i = start; i <= end; i++) {

            if (conjunction.charAt(i) == '(') {
                parenthesisDepth++;
                continue;
            }

            if (conjunction.charAt(i) == ')') {
                if (parenthesisDepth == 0) {
                    return i;
                }
                parenthesisDepth--;
            }
        }
        throw new InvalidClauseException(String.format("Closing parenthesis is missing in conjunction '%s'", conjunction));
    }
}
