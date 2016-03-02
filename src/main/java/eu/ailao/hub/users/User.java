package eu.ailao.hub.users;

import eu.ailao.hub.corefresol.answers.BestAnswerMemorizer;
import eu.ailao.hub.corefresol.concepts.ConceptMemorizer;

/**
 * Created by Petr Marek on 14.01.2016.
 * Class storing id of user and his concept memorizer
 */
public class User {
	ConceptMemorizer conceptMemorizer;
	BestAnswerMemorizer bestAnswerMemorizer;
	int userID;

	public User(ConceptMemorizer conceptMemorizer, BestAnswerMemorizer bestAnswerMemorizer, int userID) {
		this.conceptMemorizer = conceptMemorizer;
		this.bestAnswerMemorizer = bestAnswerMemorizer;
		this.userID = userID;
	}

	public ConceptMemorizer getConceptMemorizer() {
		return conceptMemorizer;
	}

	public BestAnswerMemorizer getBestAnswerMemorizer() {
		return bestAnswerMemorizer;
	}

	public int getUserID() {
		return userID;
	}
}
