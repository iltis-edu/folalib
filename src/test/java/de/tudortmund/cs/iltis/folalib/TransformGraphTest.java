package de.tudortmund.cs.iltis.folalib;

import static org.junit.Assert.*;

import de.tudortmund.cs.iltis.folalib.transform.Label;
import de.tudortmund.cs.iltis.folalib.transform.TransformGraph;
import de.tudortmund.cs.iltis.utils.function.SerializableFunction;
import org.junit.Test;

public class TransformGraphTest {
    @Test
    public void testTransformGraph() {
        final class StringLabel extends Label<String> {}
        final class IntegerLabel extends Label<Integer> {}
        final class DoubleLabel extends Label<Double> {}

        SerializableFunction<String, Integer> sToI = Integer::valueOf;
        SerializableFunction<Integer, Double> iToD = i -> (double) i;
        TransformGraph graph = new TransformGraph(new StringLabel(), "5");
        graph.registerTransform(new StringLabel(), new IntegerLabel(), sToI);
        graph.registerTransform(new IntegerLabel(), new DoubleLabel(), iToD);

        assertEquals(5.0, graph.get(new DoubleLabel()), 0.0);
        assertEquals(5, (int) graph.get(new IntegerLabel()));

        graph = new TransformGraph(new IntegerLabel(), 5);
        graph.registerTransform(new StringLabel(), new IntegerLabel(), sToI);
        graph.registerTransform(new IntegerLabel(), new DoubleLabel(), iToD);

        assertEquals(5.0, graph.get(new DoubleLabel()), 0.0);
        assertEquals(5, (int) graph.get(new IntegerLabel()));

        try {
            graph.get(new StringLabel());
            fail();
        } catch (RuntimeException e) {
            // expected, no way to get from integer to string
        }
    }
}
