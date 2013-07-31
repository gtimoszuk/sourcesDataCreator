package pl.edu.mimuw.sources;

import static com.tinkerpop.blueprints.Direction.IN;
import static com.tinkerpop.blueprints.Direction.OUT;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.neo4j.Neo4jGraph;

public abstract class AbstractSourceDataCreator {

	static final Logger LOGGER = LoggerFactory.getLogger(AbstractSourceDataCreator.class);

	protected static final String CALLS_EDGE = "CALLS";
	protected static final String CONTAINS_EDGE = "CONTAINS";
	protected static final String TYPE_PROPERTY = "TYPE_PROPERTY";
	protected static final String PACKAGE = "package";
	protected static final String PACKAGES_SUFFIX = "packages.txt";
	protected static final String ENTITIES_SUFFIX = "entities.txt";
	protected static final String EDGES_SUFFIX = "edges.txt";
	protected static final String KEY = "KEY";
	protected final Map<ClassPair, Integer> callCount = new HashMap<ClassPair, Integer>();
	protected final Set<Vertex> packages = new HashSet<Vertex>();
	protected final Map<Vertex, Vertex> childAndParent = new HashMap<Vertex, Vertex>();

	public AbstractSourceDataCreator() {
		super();
	}

	public void createData(String path, String outPath) throws IOException {
		Map<String, String> configuration = new HashMap<String, String>();
		configuration.put("allow_store_upgrade", "true");
		Graph graph = new Neo4jGraph(path, configuration);

		getData(graph);

		writeData(outPath);

		graph.shutdown();

	}

	private void writeData(String outPath) throws IOException {
		(new File(outPath)).mkdirs();

		writePackages(outPath);
		writeEntities(outPath);
		writeEdges(outPath);

	}

	protected void writeEdges(String outPath) throws FileNotFoundException, IOException {
		String edgesPath = outPath + EDGES_SUFFIX;
		Writer edgesWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(edgesPath)));

		for (Entry<ClassPair, Integer> e : callCount.entrySet()) {
			edgesWriter.append((String) e.getKey().inVertex.getProperty(KEY));
			edgesWriter.append(" ");
			edgesWriter.append((String) e.getKey().outVertex.getProperty(KEY));
			edgesWriter.append(" ");
			edgesWriter.append(e.getValue().toString());
			edgesWriter.append("\n");

		}
		edgesWriter.close();
	}

	protected void writeEntities(String outPath) throws FileNotFoundException, IOException {
		String entitiesPath = outPath + ENTITIES_SUFFIX;
		Writer entitiesWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(entitiesPath)));
		for (Entry<Vertex, Vertex> e : childAndParent.entrySet()) {
			entitiesWriter.append((String) e.getKey().getProperty(KEY));
			entitiesWriter.append(" ");
			entitiesWriter.append((String) e.getValue().getProperty(KEY));
			entitiesWriter.append("\n");
		}
		entitiesWriter.close();
	}

	protected abstract void writePackages(String outPath) throws FileNotFoundException, IOException;

	private void getData(Graph graph) {
		int count = 0;
		for (Edge e : graph.getEdges()) {
			boolean hasIN = false, hasOUT = false;
			Vertex inVertex = null, outVertex = null;
			if (CALLS_EDGE.equals(e.getLabel())) {
				for (Edge inEgde : e.getVertex(IN).getEdges(IN, new String[] {})) {
					if (CONTAINS_EDGE.equals(inEgde.getLabel())) {
						LOGGER.trace("INEGDE : in {} --contains-> out {}", inEgde.getVertex(IN).getProperty(KEY),
								inEgde.getVertex(OUT).getProperty(KEY));
						hasIN = true;
						inVertex = inEgde.getVertex(OUT);
					}
				}

				for (Edge inEgde : e.getVertex(OUT).getEdges(IN, new String[] {})) {
					if (CONTAINS_EDGE.equals(inEgde.getLabel())) {
						LOGGER.trace("OUTEGDE: in {} --contains-> out {}", inEgde.getVertex(IN).getProperty(KEY),
								inEgde.getVertex(OUT).getProperty(KEY));
						hasOUT = true;
						outVertex = inEgde.getVertex(OUT);
					}
				}

				if (hasIN && hasOUT && !inVertex.equals(outVertex)) {
					LOGGER.trace("in {} ---> out {}", e.getVertex(IN).getProperty(KEY),
							e.getVertex(OUT).getProperty(KEY));
					LOGGER.trace("classes: in {} <--- out {}", inVertex.getProperty(KEY), outVertex.getProperty(KEY));
					if (findPackage(inVertex) != null && findPackage(outVertex) != null) {
						count++;
						ClassPair clp = new ClassPair(outVertex, inVertex);
						if (callCount.containsKey(clp)) {
							int cc = callCount.get(clp);
							cc++;
							callCount.put(clp, cc);
						} else {
							callCount.put(clp, 1);

						}
					}

				}
			}
		}

		for (Entry<Vertex, Vertex> e : childAndParent.entrySet()) {
			LOGGER.trace("class {} package {}", e.getKey().getProperty(KEY), e.getValue().getProperty(KEY));
		}

		for (Entry<ClassPair, Integer> entry : callCount.entrySet()) {
			LOGGER.trace(entry.getKey().inVertex.getProperty(KEY) + " " + entry.getKey().outVertex.getProperty(KEY)
					+ " " + entry.getValue());
		}

		LOGGER.info("call count: {}", count);
		LOGGER.info("map size: {}", callCount.size());
		LOGGER.info("packages count: {}", packages.size());
		LOGGER.info("class count: {}", childAndParent.size());

	}

	private Vertex findPackage(Vertex inVertex) {
		Vertex result = null;
		LOGGER.trace("finding package for {}", inVertex.getProperty(KEY));
		for (Edge inEgde : inVertex.getEdges(IN, new String[] { CONTAINS_EDGE })) {
			if (PACKAGE.equals(inEgde.getVertex(OUT).getProperty(TYPE_PROPERTY))) {
				result = inEgde.getVertex(OUT);
				LOGGER.trace("Package: {}", inEgde.getVertex(OUT).getProperty(KEY));
				packages.add(result);
				addPackageParent(result);
				childAndParent.put(inVertex, result);
			}
		}
		if (result == null) {
			LOGGER.info("finding package for {}", inVertex.getProperty(KEY));
			LOGGER.info("NO PACKAGE FOUND!!!!!!");
		}
		return result;
	}

	private void addPackageParent(Vertex inVertex) {

		Vertex result = null;
		for (Edge inEgde : inVertex.getEdges(IN, new String[] { CONTAINS_EDGE })) {
			if (PACKAGE.equals(inEgde.getVertex(OUT).getProperty(TYPE_PROPERTY))) {
				result = inEgde.getVertex(OUT);
				if (!packages.contains(result)) {
					packages.add(result);

				}
				if (result != null) {
					childAndParent.put(inVertex, result);
					addPackageParent(result);
				}
			}
		}
	}

}