package pl.edu.mimuw.sources;

import static com.tinkerpop.blueprints.Direction.IN;
import static com.tinkerpop.blueprints.Direction.OUT;

import java.io.BufferedWriter;
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
import com.tinkerpop.blueprints.Vertex;

public class AdditiveTreeSourceDataCreator extends AbstractSourceDataCreator {

	static final Logger LOGGER = LoggerFactory.getLogger(AdditiveTreeSourceDataCreator.class);

	private final Map<String, Vertex> packagesToBeOmmited = new HashMap<String, Vertex>();

	@Override
	protected void writePackages(String outPath) throws FileNotFoundException, IOException {

		findTreeRoots();
		// for (Vertex v : packagesToBeOmmited) {
		// LOGGER.info(v.getProperty(KEY));
		// }
		//
		String packagesPath = outPath + PACKAGES_SUFFIX;
		Writer packagesWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(packagesPath)));
		Map<String, Vertex> packagesNames = new HashMap<String, Vertex>();
		for (Vertex v : packages) {
			packagesNames.put((String) v.getProperty(KEY), v);
		}

		for (Entry<String, Vertex> entry : packagesNames.entrySet()) {
			if (!packagesToBeOmmited.containsKey(entry.getKey())) {
				packagesWriter.append(entry.getKey());
				packagesWriter.append(" ");
				packagesWriter.append(findTreeName(entry.getValue()));
				packagesWriter.append("\n");
			}

		}
		packagesWriter.close();
	}

	private String findTreeName(Vertex value) {
		Vertex currVertex = value;
		Vertex parent = getParent(currVertex);
		while (parent != null && !packagesToBeOmmited.containsKey(parent.getProperty(KEY))) {
			currVertex = parent;
			parent = getParent(parent);
		}
		return (String) currVertex.getProperty(KEY);
	}

	private Vertex getParent(Vertex value) {
		for (Edge inEgde : value.getEdges(IN, new String[] { CONTAINS_EDGE })) {
			if (PACKAGE.equals(inEgde.getVertex(OUT).getProperty(TYPE_PROPERTY))) {
				return inEgde.getVertex(OUT);
			}
		}
		LOGGER.error("for vertex {} there is no parent", value.getProperty(KEY));
		return null;
	}

	/*
	 * version that takes only children
	 * 
	 * private void findChildPackagesContainingOnlyPackages() { Set<Vertex>
	 * superSetOfTrees = new HashSet<Vertex>(); for (Vertex v : packages) { if
	 * (constainsOnlyPackages(v)) { superSetOfTrees.add(v); } } for (Vertex v :
	 * superSetOfTrees) { if (isChildPackage(v, superSetOfTrees)) {
	 * packagesContainingOnlyPackages.add(v); } } }
	 */

	private void findTreeRoots() {
		Set<Vertex> superSetOfTrees = new HashSet<Vertex>();
		for (Vertex v : packages) {
			if (constainsOnlyPackages(v)) {
				superSetOfTrees.add(v);
			}
		}
		for (Vertex v : superSetOfTrees) {
			if (superSetOfTrees.contains(getParent(v)) || getParent(v) == null) {
				packagesToBeOmmited.put((String) v.getProperty(KEY), v);
			}
		}
	}

	private boolean containsOnlyTreeRoots(Vertex v, Set<Vertex> superSetOfTrees) {
		for (Edge outEgde : v.getEdges(OUT, new String[] { CONTAINS_EDGE })) {
			if (!superSetOfTrees.contains(outEgde.getVertex(IN))) {
				LOGGER.info("package {} contains elt not from super set {}", v.getProperty(KEY), outEgde.getVertex(IN)
						.getProperty(KEY));
				return false;
			}
		}
		return true;
	}

	private boolean isChildPackage(Vertex v, Set<Vertex> superSetOfTrees) {
		for (Edge outEgde : v.getEdges(OUT, new String[] { CONTAINS_EDGE })) {
			if (superSetOfTrees.contains(outEgde.getVertex(IN))) {
				return false;
			}
		}
		return true;
	}

	private boolean constainsOnlyPackages(Vertex v) {
		for (Edge outEgde : v.getEdges(OUT, new String[] { CONTAINS_EDGE })) {
			if (!PACKAGE.equals(outEgde.getVertex(IN).getProperty(TYPE_PROPERTY))) {
				return false;
			}
		}
		return true;
	}
}
