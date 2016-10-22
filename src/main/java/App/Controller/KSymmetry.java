package App.Controller;

import App.Common.IAlgorithm;
import App.Common.Utils.DemoDataCreator;
import App.Model.Graph;
import App.Model.Vertex;
import App.lib.jNauty.McKayGraphLabelingAlgorithm;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Keinan.Gilad on 10/20/2016.
 */
public class KSymmetry implements IAlgorithm {
    private static Logger logger = Logger.getLogger(KSymmetry.class);

    @Autowired
    private McKayGraphLabelingAlgorithm jNauty;

    @Override
    public Graph anonymize(Graph graph, Integer k) {
        // 1. fetch orbits from the graph by jNauty algorithm (McKay).
        logger.debug("Start to findAutomorphisms");
        List<List<Integer>> orbits = jNauty.getCyclicRepresenatation(graph);
        if (orbits == null) {
            logger.debug("No orbits found");
            return graph;
        }
        logger.debug(String.format("found %s orbits", orbits.size()));

        // 2. for each orbit -> call ocp until size at least k.
        for (int i = 0; i < orbits.size(); i++) {
            logger.debug(String.format("Iteration for orbit %s", i));

            List<Integer> orbit = orbits.get(i);
            if (orbit.size() >= k) {
                logger.debug(String.format("orbit %s size above k", i));
                continue;
            }
            // orbit size is below k, calling ocp procedure.
            int copyCounter = 1;
            while (orbit.size() < k) {
                logger.debug(String.format("Start orbitCopying for orbit %s", i));
                orbit = orbitCopying(graph, orbit, copyCounter);
                copyCounter++;
                logger.debug(String.format("Done orbitCopying for orbit %s", i));
            }
        }

        // 3. return the anonymized graph
        logger.debug("return the anonymized graph");
        return graph;
    }

    /**
     * Orbit copying
     *
     * @param graph - the graph
     * @param orbit - the orbit that will be copied.
     */
    private List<Integer> orbitCopying(Graph graph, List<Integer> orbit, int copyCounter) {
        Map<Vertex, Set<Vertex>> vertexToNeighbors = graph.getVertexToNeighbors();
        List<Integer> orbitTemp = new ArrayList<>(orbit);
        // 1. for each vertex in orbit
        for (Integer idx : orbit) {
            // 1.1. introduce new vertex into the graph and add to orbit
            List<Vertex> vertices = graph.getVertices();

            Vertex v = vertices.get(idx);
            String name = v.getName();
            if (name.startsWith("-")) {
                continue; // not copying tag vertices, only originals.
            }

            String vertexTagName = createVertexTagName(name, copyCounter);
            Vertex vTag = new Vertex(vertexTagName);
            // add vertex into the graph
            graph.addVertex(vTag);

            // add vertex into the orbit
            orbitTemp.add(vertices.indexOf(vTag));

            // 1.2. connect new edges according to orbits.
            Set<Vertex> vertexNeighbors = vertexToNeighbors.get(v);
            for (Vertex neighbor : vertexNeighbors) {
                if (isInSameOrbit(neighbor, vertices, orbit)) {
                    // in same orbit connecting them by tag
                    graph.addEdge(vTag, new Vertex(createVertexTagName(neighbor.getName(), copyCounter)));
                } else {
                    // in same orbit connecting them regularly
                    graph.addEdge(vTag, neighbor);
                }
            }
        }

        return orbitTemp;
    }

    /**
     * @param neighbor - a neighbor we want to check if he part of the orbit
     * @param vertices - all graph's vertices
     * @param orbit    - an orbit to check in
     * @return true if neighbor is part of the orbit, false otherwise.
     */
    private boolean isInSameOrbit(Vertex neighbor, List<Vertex> vertices, List<Integer> orbit) {
        int neighborIdx = vertices.indexOf(neighbor);
        if (neighborIdx < 0) {
            return false;
        }
        boolean isContains = orbit.contains(neighborIdx);
        return isContains;
    }

    /**
     * calculating the tag name
     *
     * @param name - vertex name
     * @return the name with tag (* -1)
     */
    private String createVertexTagName(String name, int copyCounter) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < copyCounter; i++) {
            sb.append("-");
        }

        return sb.append(name).toString();
    }

    public static void main(String[] args) {
        BasicConfigurator.configure();

        KSymmetry algo = new KSymmetry();
        algo.jNauty = new McKayGraphLabelingAlgorithm();

        // k=2
        //Graph anonymize = algo.anonymize(DemoDataCreator.generateGraphSymmetry(), 2);
        //System.out.println(anonymize.getVertices().size());
        //System.out.println(anonymize.getVertices());

        // k=3
        Graph anonymize = algo.anonymize(DemoDataCreator.generateGraphSymmetry(), 3);
        System.out.println(anonymize.getVertices().size());
        System.out.println(anonymize.getVertices());
    }
}