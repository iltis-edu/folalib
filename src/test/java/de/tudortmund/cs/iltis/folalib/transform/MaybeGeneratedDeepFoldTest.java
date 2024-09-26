package de.tudortmund.cs.iltis.folalib.transform;

import static org.junit.Assert.*;

import org.junit.Test;

public class MaybeGeneratedDeepFoldTest {

    @Test
    public void testReplicateBehaviour() {
        MaybeGenerated<Integer, String> testMG1 = new MaybeGenerated.Input<>(1);
        MaybeGenerated<MaybeGenerated<Integer, String>, String> testMG2 =
                new MaybeGenerated.Input<>(testMG1);
        MaybeGenerated<MaybeGenerated<MaybeGenerated<Integer, String>, String>, String> testMG3 =
                new MaybeGenerated.Generated<>("Test1");
        MaybeGenerated<
                        MaybeGenerated<
                                MaybeGenerated<MaybeGenerated<Integer, String>, String>, String>,
                        String>
                testMG4 = new MaybeGenerated.Input<>(testMG3);
        MaybeGenerated<
                        MaybeGenerated<
                                MaybeGenerated<
                                        MaybeGenerated<MaybeGenerated<Integer, String>, String>,
                                        String>,
                                String>,
                        String>
                testMG5 = new MaybeGenerated.Input<>(testMG4);
        MaybeGenerated<
                        MaybeGenerated<
                                MaybeGenerated<
                                        MaybeGenerated<
                                                MaybeGenerated<
                                                        MaybeGenerated<Integer, String>, String>,
                                                String>,
                                        String>,
                                String>,
                        String>
                testMG6 = new MaybeGenerated.Input<>(testMG5);

        MaybeGenerated<Integer, String> testMGReduced2 =
                MaybeGeneratedDeepFold.deepFold(testMG2, this::helper);
        MaybeGenerated<Integer, String> testMGReduced3 =
                MaybeGeneratedDeepFold.deepFold(testMG3, this::helper);
        MaybeGenerated<Integer, String> testMGReduced4 =
                MaybeGeneratedDeepFold.deepFold(testMG4, this::helper);
        MaybeGenerated<Integer, String> testMGReduced5 =
                MaybeGeneratedDeepFold.deepFold(testMG5, this::helper);
        MaybeGenerated<Integer, String> testMGReduced6 =
                MaybeGeneratedDeepFold.deepFold(testMG6, this::helper);

        assertEquals(new MaybeGenerated.Input<>(1), testMGReduced2);
        assertEquals(new MaybeGenerated.Generated<>("MGLayer0_Test1"), testMGReduced3);
        assertEquals(new MaybeGenerated.Generated<>("MGLayer1_Test1"), testMGReduced4);
        assertEquals(new MaybeGenerated.Generated<>("MGLayer2_Test1"), testMGReduced5);
        assertEquals(new MaybeGenerated.Generated<>("MGLayer3_Test1"), testMGReduced6);
    }

    private MaybeGenerated<Integer, String> helper(String path, Object result) {
        if (path.matches(
                "0+")) { // path of only zero's indicates result was found at In{In{In{... x ...}}}
            return new MaybeGenerated.Input<>((Integer) result);
        } else {
            return new MaybeGenerated.Generated<>(
                    "MGLayer" + (path.length() - 1) + "_" + result.toString());
        }
    }
}
