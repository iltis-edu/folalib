package de.tudortmund.cs.iltis.folalib.io;

import static org.junit.Assert.fail;

import de.tudortmund.cs.iltis.folalib.io.alphabet.inference.AlphabetInferenceFaultCollection;
import de.tudortmund.cs.iltis.utils.collections.Fault;
import de.tudortmund.cs.iltis.utils.collections.Pair;
import de.tudortmund.cs.iltis.utils.io.parser.error.IncorrectParseInputException;
import de.tudortmund.cs.iltis.utils.io.parser.fault.ParsingFaultCollection;
import de.tudortmund.cs.iltis.utils.io.parser.fault.ParsingFaultTypeMapping;
import de.tudortmund.cs.iltis.utils.io.reader.general.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.junit.Test;

/**
 * Test rig for automated testing of readers
 *
 * @param <ParserOutput> output type of parser
 */
public abstract class ParserTestRig<ParserOutput, ConvertedOutput> {
    // There are different readers for each entry because the properties can differ

    // elements should be: new Object[] {reader: Reader,
    //                                   input: String,
    //                                   convertOutput: Function<ParserOutput, ConvertedOutput>, //
    // A function which transforms the parser output into something else to allow better testing
    //                                   targetObject: ?}
    protected static List<Object[]> positives;

    // elements should be: new Object[] {reader: Reader,
    //                                   input: String,
    //                                   convertOutput: Function<ParserOutput, ConvertedOutput>, //
    // A function which transforms the parser output into something else to allow better testing
    //                                   targetObject: ?,
    //                                   faultReasons: List<Pair<Enum, Integer>>} // The amount
    // every fault occurs
    protected static List<Object[]> negatives;

    protected static boolean verbose = true;

    @SuppressWarnings("unchecked")
    @Test
    public void testPositives() {
        if (positives == null) return;
        int no = 0;
        List<Integer> errorList = new ArrayList<>();
        for (Object[] entry : positives) {
            Reader<ParserOutput> reader = (Reader<ParserOutput>) entry[0];
            String testee = (String) entry[1];
            Function<ParserOutput, ConvertedOutput> convertOutput =
                    (Function<ParserOutput, ConvertedOutput>) entry[2];
            ParserOutput targetResult = (ParserOutput) entry[3];

            no++;
            if (verbose) System.out.println("Positive testee " + no + ": " + testee);

            try {
                ParserOutput actualResult;
                actualResult = reader.read(testee);
                if (verbose) System.out.println("Parsing result: " + actualResult);

                ConvertedOutput convertedOutput = convertOutput.apply(actualResult);
                if (verbose) System.out.println("Converted output: " + convertedOutput);

                if (!targetResult.equals(convertedOutput)) {
                    if (verbose)
                        System.out.println(
                                ">>> Result not equal to expected result: " + targetResult);
                    errorList.add(no);
                }
            } catch (IncorrectParseInputException e) {
                ParsingFaultTypeMapping<?> mapping = e.getFaultMapping();
                if (verbose) System.out.println(">>> IncorrectParseInputException thrown");
                if (verbose) System.out.println(">>> Thrown output: " + mapping.getOutput());
                if (verbose) System.out.println(">>> Thrown mapping: " + mapping);
                errorList.add(no);
            } catch (Exception e) {
                if (verbose) e.printStackTrace(System.out);
                errorList.add(no);
            } finally {
                if (verbose) System.out.println();
            }
        }
        if (!errorList.isEmpty())
            fail("Errors occurred for the following positive test cases: " + errorList);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testNegatives() {
        if (negatives == null) return;
        int no = 0;
        List<Integer> errorList = new ArrayList<>();
        for (Object[] entry : negatives) {
            Reader<ParserOutput> reader = (Reader<ParserOutput>) entry[0];
            String testee = (String) entry[1];
            Function<ParserOutput, ConvertedOutput> convertOutput =
                    (Function<ParserOutput, ConvertedOutput>) entry[2];
            ParserOutput target = (ParserOutput) entry[3];
            List<Pair<Enum<?>, Integer>> expectedFaults = (List<Pair<Enum<?>, Integer>>) entry[4];

            no++;
            if (verbose) System.out.println("Negative testee " + no + ": " + testee);
            try {
                ParserOutput actualResult;
                actualResult = reader.read(testee);

                if (verbose) System.out.println(">>> No exception thrown for " + testee);
                if (verbose) System.out.println(">>> Instead output: " + actualResult);
                errorList.add(no);
            } catch (IncorrectParseInputException e) {
                ParsingFaultTypeMapping<?> mapping = e.getFaultMapping();
                if (verbose) System.out.println("Thrown output: " + mapping.getOutput());
                if (verbose) System.out.println("Thrown mapping: " + mapping);
                if (verbose) System.out.println("Expected output: " + target);

                ConvertedOutput convertedOutput;
                if (mapping.getOutput() == null) convertedOutput = null;
                else convertedOutput = convertOutput.apply((ParserOutput) mapping.getOutput());

                if (verbose) System.out.println("Converted output: " + convertedOutput);

                if (target != convertedOutput) {
                    if (target == null || convertedOutput == null) {
                        if (verbose)
                            System.out.println(
                                    ">>> Thrown output or expected output is null, but the other is not.");
                        errorList.add(no);
                        continue;
                    } else if (!target.equals(convertedOutput)) {
                        if (verbose)
                            System.out.println(
                                    ">>> Thrown output unequal to expected output: " + target);
                        errorList.add(no);
                        continue;
                    }
                }

                ParsingFaultCollection parsingFaultCollection =
                        (ParsingFaultCollection)
                                e.getFaultMapping().get(ParsingFaultCollection.class);
                List<Fault<?>> actualFaults = new ArrayList<>();
                if (parsingFaultCollection != null)
                    actualFaults.addAll(parsingFaultCollection.getFaults());

                AlphabetInferenceFaultCollection alphabetInferenceFaultCollection =
                        (AlphabetInferenceFaultCollection)
                                e.getFaultMapping().get(AlphabetInferenceFaultCollection.class);
                if (alphabetInferenceFaultCollection != null)
                    actualFaults.addAll(alphabetInferenceFaultCollection.getFaults());

                int totalExpectedFaults = 0;
                for (Pair<Enum<?>, Integer> fault : expectedFaults) {
                    totalExpectedFaults += fault.second();
                    long actualAmount =
                            actualFaults.stream()
                                    .filter(o -> o.getReason() == fault.first())
                                    .count();
                    if (actualAmount != fault.second()) {
                        if (verbose)
                            System.out.println(
                                    ">>> Fault '"
                                            + fault.first()
                                            + "' expected "
                                            + fault.second()
                                            + " times, but actually occurred "
                                            + actualAmount
                                            + " times.");
                        errorList.add(no);
                    }
                }

                if (totalExpectedFaults != actualFaults.size()) {
                    if (verbose)
                        System.out.println(
                                ">>> There are "
                                        + actualFaults.size()
                                        + " faults although "
                                        + totalExpectedFaults
                                        + " were expected.");
                    errorList.add(no);
                }
            } catch (Exception e) {
                if (verbose) e.printStackTrace(System.out);
                errorList.add(no);
            } finally {
                if (verbose) System.out.println();
            }
        }
        if (!errorList.isEmpty())
            fail("Errors occurred for the following negative test cases: " + errorList);
    }
}
