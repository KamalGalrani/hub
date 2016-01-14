package eu.ailao.hub.users;

import eu.ailao.hub.concepts.ConceptMemorizer;

/**
 * Created by Petr Marek on 14.01.2016.
 */
public class User {
	String id;
	ConceptMemorizer conceptMemorizer;

	public User(String id, ConceptMemorizer conceptMemorizer) {
		this.id = id;
		this.conceptMemorizer = conceptMemorizer;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ConceptMemorizer getConceptMemorizer() {
		return conceptMemorizer;
	}

	public void setConceptMemorizer(ConceptMemorizer conceptMemorizer) {
		this.conceptMemorizer = conceptMemorizer;
	}
}
