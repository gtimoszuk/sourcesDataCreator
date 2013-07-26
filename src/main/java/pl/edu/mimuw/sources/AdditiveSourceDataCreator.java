package pl.edu.mimuw.sources;

import static com.tinkerpop.blueprints.Direction.IN;
import static com.tinkerpop.blueprints.Direction.OUT;

import java.io.BufferedWriter;
import java.io.File;
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

public class AdditiveSourceDataCreator {

	private static final Logger LOGGER = LoggerFactory.getLogger(AdditiveSourceDataCreator.class);

	private static final String CALLS_EDGE = "CALLS";

	private static final String CONTAINS_EDGE = "CONTAINS";

	private static final String TYPE_PROPERTY = "TYPE_PROPERTY";

	private static final String PACKAGE = "package";

	private static final String PACKAGES_SUFFIX = "packages.txt";
	private static final String ENTITIES_SUFFIX = "entities.txt";
	private static final String EDGES_SUFFIX = "edges.txt";

	private static final String KEY = "KEY";

	private final Map<ClassPair, Integer> callCount = new HashMap<ClassPair, Integer>();

	private final Set<Vertex> packages = new HashSet<Vertex>();

	private final Map<Vertex, Vertex> childAndParent = new HashMap<Vertex, Vertex>();

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
		String packagesPath = outPath + PACKAGES_SUFFIX;
		String entitiesPath = outPath + ENTITIES_SUFFIX;
		String edgesPath = outPath + EDGES_SUFFIX;
		Writer packagesWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(packagesPath)));
		Writer entitiesWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(entitiesPath)));
		Writer edgesWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(edgesPath)));

		Set<String> packagesNames = new HashSet<String>();
		for (Vertex v : packages) {
			packagesNames.add((String) v.getProperty(KEY));
		}
		for (String s : packagesNames) {
			packagesWriter.append(s);
			packagesWriter.append("\n");
		}
		packagesWriter.close();

		for (Entry<Vertex, Vertex> e : childAndParent.entrySet()) {
			entitiesWriter.append((String) e.getKey().getProperty(KEY));
			entitiesWriter.append(" ");
			entitiesWriter.append((String) e.getValue().getProperty(KEY));
			entitiesWriter.append("\n");
		}
		entitiesWriter.close();

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
					childAndParent.put(inVertex, result);
					addPackageParent(result);
				}
			}
		}
	}
}
