package eu.ailao.hub.users;

import eu.ailao.hub.corefresol.answers.ClueMemorizer;
import eu.ailao.hub.corefresol.concepts.ConceptMemorizer;

/**
 * Created by Petr Marek on 14.01.2016.
 * Class storing id of user and his concept memorizer
 */
public class User {
	ConceptMemorizer conceptMemorizer;
	ClueMemorizer clueMemorizer;
	int userID;

	public User(ConceptMemorizer conceptMemorizer, ClueMemorizer clueMemorizer, int userID) {
		this.conceptMemorizer = conceptMemorizer;
		this.clueMemorizer = clueMemorizer;
		this.userID = userID;
	}

	public ConceptMemorizer getConceptMemorizer() {
		return conceptMemorizer;
	}

	public ClueMemorizer getClueMemorizer() {
		return clueMemorizer;
	}

	public int getUserID() {
		return userID;
	}
}
