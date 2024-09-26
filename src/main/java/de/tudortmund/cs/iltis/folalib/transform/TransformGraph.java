package de.tudortmund.cs.iltis.folalib.transform;

import de.tudortmund.cs.iltis.utils.collections.Pair;
import de.tudortmund.cs.iltis.utils.collections.SerializablePair;
import de.tudortmund.cs.iltis.utils.function.SerializableFunction;
import de.tudortmund.cs.iltis.utils.graph.Graph;
import de.tudortmund.cs.iltis.utils.graph.hashgraph.HashGraph;
import java.io.Serializable;
import java.util.*;

public class TransformGraph implements Serializable {
    private Map<Label<? extends Serializable>, List<Serializable>> cache;
    private Graph<
                    Label<? extends Serializable>,
                    SerializableFunction<? extends Serializable, ? extends Serializable>>
            transformGraph;

    // For serialization
    private TransformGraph() {}

    public <T extends Serializable> TransformGraph(Label<T> initial, T initialObject) {
        transformGraph = new HashGraph<>();
        cache = new HashMap<>();

        addRepresentation(initial, initialObject);
    }

    public <T extends Serializable> boolean hasCached(Label<T> kind) {
        return cache.containsKey(kind) && !cache.get(kind).isEmpty();
    }

    public <T extends Serializable> void addRepresentation(Label<T> label, T object) {
        addRepresentation_(label, object);
    }

    private void addRepresentation_(Label<? extends Serializable> label, Serializable object) {
        cache.computeIfAbsent(label, k -> new ArrayList<>()).add(object);
    }

    public <From extends Serializable, To extends Serializable> void registerTransform(
            Label<From> from, Label<To> to, SerializableFunction<From, To> transform) {
        if (!transformGraph.hasVertex(from)) transformGraph.addVertex(from);
        if (!transformGraph.hasVertex(to)) transformGraph.addVertex(to);
        transformGraph.addEdge(from, to, transform);
    }

    public <To extends Serializable> To get(Label<To> to) {
        List<Serializable> existing = cache.get(to);
        if (existing != null && !existing.isEmpty())
            return (To) existing.get(0); // TODO: strategy for picking?

        List<
                        SerializablePair<
                                SerializableFunction<
                                        ? extends Serializable, ? extends Serializable>,
                                Label<? extends Serializable>>>
                optimalPath = null;
        Label<? extends Serializable> startingPoint = null;

        for (Label<? extends Serializable> from : cache.keySet()) {
            if (from.equals(to) || cache.get(from).isEmpty()) continue;

            List<
                            SerializablePair<
                                    SerializableFunction<
                                            ? extends Serializable, ? extends Serializable>,
                                    Label<? extends Serializable>>>
                    transformPath = shortestPath(from, to);

            // TODO: Better optimality criterion
            if (transformPath != null
                    && (optimalPath == null || transformPath.size() < optimalPath.size())) {
                optimalPath = transformPath;
                startingPoint = from;
            }
        }

        if (optimalPath == null)
            throw new RuntimeException(
                    "No transformation found"); // FIXME: proper handling (maybe via Result?)

        Serializable current = cache.get(startingPoint).get(0); // TODO: strategy, see above

        for (SerializablePair<
                        SerializableFunction<? extends Serializable, ? extends Serializable>,
                        Label<? extends Serializable>>
                nextStep : optimalPath) {
            SerializableFunction<? extends Serializable, ? extends Serializable> transform =
                    nextStep.first();
            Label<? extends Serializable> resultLabel = nextStep.second();

            current = ((SerializableFunction<Serializable, Serializable>) transform).apply(current);

            addRepresentation_(resultLabel, current);
        }

        return (To) current;
    }

    // TODO: an algorithm more sophisticated than BFS should probably the used here in the future
    // (for example one that takes into account time-complexity of the transformations!)
    private List<
                    SerializablePair<
                            SerializableFunction<? extends Serializable, ? extends Serializable>,
                            Label<? extends Serializable>>>
            shortestPath(Label<? extends Serializable> from, Label<? extends Serializable> to) {
        Map<
                        Label<? extends Serializable>,
                        Pair<
                                Label<? extends Serializable>,
                                SerializableFunction<
                                        ? extends Serializable, ? extends Serializable>>>
                bfsPredecessors = transformGraph.breadthFirstTraversal(from);

        if (!bfsPredecessors.containsKey(to)) return null;

        LinkedList<
                        SerializablePair<
                                SerializableFunction<
                                        ? extends Serializable, ? extends Serializable>,
                                Label<? extends Serializable>>>
                transforms = new LinkedList<>();
        Label<? extends Serializable> current = to;

        while (!current.equals(from)) {
            // assumption: from =/= to
            Label<? extends Serializable> pred = bfsPredecessors.get(current).first();
            transforms.offerFirst(
                    new SerializablePair<>(transformGraph.getEdge(pred, current).get(), current));
            current = pred;
        }

        return transforms;
    }
}
