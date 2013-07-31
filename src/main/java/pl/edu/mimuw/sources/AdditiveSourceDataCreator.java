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
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

public class AdditiveSourceDataCreator extends AbstractSourceDataCreator {

	static final Logger LOGGER = LoggerFactory.getLogger(AdditiveSourceDataCreator.class);

	@Override
	public void writePackages(String outPath) throws FileNotFoundException, IOException {
		String packagesPath = outPath + PACKAGES_SUFFIX;
		Writer packagesWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(packagesPath)));
		Map<String, Vertex> packagesNames = new HashMap<String, Vertex>();
		for (Vertex v : packages) {
			packagesNames.put((String) v.getProperty(KEY), v);
		}

		for (Entry<String, Vertex> packageNamesToVertices : packagesNames.entrySet()) {
			packagesWriter.append(packageNamesToVertices.getKey());
			packagesWriter.append(" ");
			packagesWriter.append(findFirstParent(packageNamesToVertices.getValue()));
			packagesWriter.append("\n");
		}

		packagesWriter.close();
	}

	private String findFirstParent(Vertex inVertex) {
		String name = (String) inVertex.getProperty(KEY);
		Vertex result = inVertex;

		while (result != null) {
			Vertex tempResult = null;
			for (Edge inEgde : result.getEdges(IN, new String[] { CONTAINS_EDGE })) {
				if (PACKAGE.equals(inEgde.getVertex(OUT).getProperty(TYPE_PROPERTY))) {
					tempResult = inEgde.getVertex(OUT);
					name = (String) tempResult.getProperty(KEY);
				}
			}
			result = tempResult;
		}
		return name;
	}
}
