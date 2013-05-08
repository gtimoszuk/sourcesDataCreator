package pl.edu.mimuw.sources;

import com.tinkerpop.blueprints.Vertex;

public class ClassPair {

	Vertex inVertex;

	Vertex outVertex;

	public ClassPair(Vertex inVertex, Vertex outVertex) {
		super();
		this.inVertex = inVertex;
		this.outVertex = outVertex;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((inVertex == null) ? 0 : inVertex.hashCode());
		result = prime * result + ((outVertex == null) ? 0 : outVertex.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ClassPair other = (ClassPair) obj;
		if (inVertex == null) {
			if (other.inVertex != null)
				return false;
		} else if (!inVertex.equals(other.inVertex))
			return false;
		if (outVertex == null) {
			if (other.outVertex != null)
				return false;
		} else if (!outVertex.equals(other.outVertex))
			return false;
		return true;
	}

}
