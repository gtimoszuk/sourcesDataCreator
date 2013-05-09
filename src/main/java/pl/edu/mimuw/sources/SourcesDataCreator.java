package pl.edu.mimuw.sources;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.neo4j.Neo4jGraph;

/**
 * not working to be removed soon
 * 
 * @author gtimoszuk
 * 
 */
public class SourcesDataCreator {

	private static final String KEY = "KEY";

	private static final String CLASS = "class";

	private static final String TYPE_PROPERTY = "TYPE_PROPERTY";

	static final Logger LOGGER = LoggerFactory.getLogger(SourcesDataCreator.class);

	static final Set<String> EDGES_TO_REMOVE = new HashSet<String>();

	static final Set<String> EDGES_TO_UNIFY = new HashSet<String>();

	static final String CALLS_EDGE = "CALLS";

	static final String CONTAINS_EDGE = "CONTAINS";

	static final String VARIABLE = "variable";

	static final String METHOD = "method";

	private final Set<Edge> edgesToRemove = new HashSet<Edge>();

	static {
		EDGES_TO_REMOVE.add("IMPLEMENTS");
		EDGES_TO_REMOVE.add("EXTENDS");
		EDGES_TO_UNIFY.add("RETURNS");
		EDGES_TO_UNIFY.add("HAS_TYPE");
		EDGES_TO_UNIFY.add("TAKES");
	}

	public void createData(String path) {
		Graph graph = new Neo4jGraph(path);

		// removeVertices(graph);
		// removeEdges(graph);

		shrinkData(graph);

		printData(graph);

		graph.shutdown();
	}

	private void shrinkData(Graph graph) {
		for (Vertex v : graph.getVertices()) {
			if (METHOD.equals(v.getProperty(TYPE_PROPERTY)) || VARIABLE.equals(v.getProperty(TYPE_PROPERTY))) {
				for (Edge edge : v.getEdges(Direction.BOTH, new String[] {})) {
					LOGGER.info("EDGE: " + edge.getVertex(Direction.OUT).getProperty(KEY) + " -- " + edge.getLabel()
							+ " -> " + edge.getVertex(Direction.IN).getProperty(KEY));
				}

			}
		}
	}

	private void shrinkData2(Graph graph) {
		for (Edge e : graph.getEdges()) {
			if (CONTAINS_EDGE.equals(e.getLabel())) {
				Vertex from = e.getVertex(Direction.IN);
				Vertex to = e.getVertex(Direction.OUT);
				for (Edge toEdge : to.getEdges(Direction.IN, new String[] {})) {
					if (!CONTAINS_EDGE.equals(toEdge.getLabel())) {
						graph.addEdge(getId(), toEdge.getVertex(Direction.OUT), from, toEdge.getLabel());
					}
				}
				for (Edge toEdge : to.getEdges(Direction.IN, new String[] {})) {
					graph.removeEdge(toEdge);
				}
				for (Edge fromEgde : to.getEdges(Direction.OUT, new String[] {})) {
					if (!CONTAINS_EDGE.equals(fromEgde.getLabel())) {
						graph.addEdge(getId(), from, fromEgde.getVertex(Direction.OUT), fromEgde.getLabel());
					}
				}
				for (Edge fromEgde : to.getEdges(Direction.OUT, new String[] {})) {
					graph.removeEdge(fromEgde);

				}

			}
		}
	}

	private Long id = 1000000L;

	private Long getId() {
		return id++;
	}

	private void removeEdges(Graph graph) {
		for (Edge e : graph.getEdges()) {
			if (EDGES_TO_REMOVE.contains(e.getLabel())) {
				edgesToRemove.add(e);
			}
		}

	}

	private void printData(Graph graph) {
		int edgeNumber = 0;
		Set<String> labelSet = new HashSet<String>();
		for (Edge e : graph.getEdges()) {
			labelSet.add(e.getLabel());
			edgeNumber++;
		}
		LOGGER.info("egde number: {}", edgeNumber);

		LOGGER.info("\n\n edge labels");
		for (String s : labelSet) {
			LOGGER.info(s);
		}

		Set<String> propertySet = new HashSet<String>();
		Set<String> typesSet = new HashSet<String>();
		int classCount = 0;
		for (Vertex v : graph.getVertices()) {
			int edgeCount = 0;
			for (Edge e : v.getEdges(Direction.BOTH, new String[] {})) {
				edgeCount++;
			}
			LOGGER.info("node: {} edge count {}", v.getProperty(KEY), edgeCount);
			propertySet.addAll(v.getPropertyKeys());
			if (v.getProperty(TYPE_PROPERTY) != null) {
				LOGGER.trace("TYPE_PROPERTY: {}", v.getProperty(TYPE_PROPERTY));
				typesSet.add(v.getProperty(TYPE_PROPERTY).toString());
				if (v.getProperty(TYPE_PROPERTY).equals(CLASS)) {
					classCount++;
				}
			}
		}

		LOGGER.info("\n\ntype set");
		for (String s : typesSet) {
			LOGGER.info(s);
		}
		LOGGER.info("class count: {}", classCount);

		LOGGER.info("\n\nproperties");
		for (String s : propertySet) {
			LOGGER.info(s);
		}
	}

	private void removeVertices(Graph graph) {
		int allNodesNumber = 0;
		int removedNodesNumber = 0;

		for (Vertex vertex : graph.getVertices()) {
			allNodesNumber++;
			if (vertex.getProperty(KEY) == null) {
				removeVertex(vertex, graph);
				removedNodesNumber++;
			} else if (vertex.getProperty("stubNode") != null) {
				Boolean stubNode = new Boolean(vertex.getProperty("stubNode").toString());
				if (stubNode) {
					removeVertex(vertex, graph);
					removedNodesNumber++;
				}
			}
		}
		LOGGER.info("\n\n All nodes: {} removed nodes: {}", allNodesNumber, removedNodesNumber);
	}

	private void removeVertex(Vertex vertex, Graph graph) {
		if (vertex.getProperty(KEY) == null) {
			LOGGER.trace("removing vertex with null key");
		} else {
			LOGGER.trace("removing vertex with key: {}", vertex.getProperty(KEY));
		}
		for (Edge e : vertex.getEdges(Direction.BOTH, new String[] {})) {
			LOGGER.trace("edge: {}", e.getLabel());
			edgesToRemove.add(e);
		}
		graph.removeVertex(vertex);

	}
}
