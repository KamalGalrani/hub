package eu.ailao.hub.users;

import eu.ailao.hub.concepts.ConceptMemorizer;
import eu.ailao.hub.dialogue.DialogueMemorizer;

/**
 * Created by Petr Marek on 14.01.2016.
 * Class storing id of user and his concept memorizer
 */
public class User {
	ConceptMemorizer conceptMemorizer;
	int userID;

	public User(ConceptMemorizer conceptMemorizer, int userID) {
		this.conceptMemorizer = conceptMemorizer;
		this.userID = userID;
	}

	public ConceptMemorizer getConceptMemorizer() {
		return conceptMemorizer;
	}

	public int getUserID() {
		return userID;
	}
}
