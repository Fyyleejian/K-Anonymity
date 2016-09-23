package App.Model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Keinan.Gilad on 9/18/2016.
 */
public class DataSetModel {

    private Set<Edge> edges;
    private Set<Vertex> vertices;
    private String title;
    private Map<Vertex, Set<Vertex>> vertexToNeighbors;

    public DataSetModel() {
        edges = new HashSet<>();
        vertices = new HashSet<>();
        vertexToNeighbors = new HashMap<>();
    }

    public void addRow(String[] valueRowSplits) {
        Vertex v0 = new Vertex(valueRowSplits[0]);
        Vertex v1 = new Vertex(valueRowSplits[1]);

        vertices.add(v0);
        vertices.add(v1);
        edges.add(new Edge(v0, v1));

        // update degrees:
        updateNeightbors(v0, v1);
    }

    private void updateNeightbors(Vertex v0, Vertex v1) {
        updateNeighbor(v0, v1);
        updateNeighbor(v1, v0);
    }

    private void updateNeighbor(Vertex v0, Vertex v1) {
        Set<Vertex> vertices = vertexToNeighbors.get(v0);
        if (vertices == null) {
            vertices = new HashSet<Vertex>();
        }
        vertices.add(v1);
        vertexToNeighbors.put(v0, vertices);
    }

    public void addEdge(Vertex v0, Vertex v1) {
        edges.add(new Edge(v0, v1));
        updateNeightbors(v0, v1);
    }

    public Map<Vertex, Set<Vertex>> getVertexToNeighbors() {
        return vertexToNeighbors;
    }

    public void setVertexToNeighbors(Map<Vertex, Set<Vertex>> vertexToNeighbors) {
        this.vertexToNeighbors = vertexToNeighbors;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Set<Edge> getEdges() {
        return edges;
    }

    public void setEdges(Set<Edge> edges) {
        this.edges = edges;
    }

    public Set<Vertex> getVertices() {
        return vertices;
    }

    public void setVertices(Set<Vertex> vertices) {
        this.vertices = vertices;
    }
}