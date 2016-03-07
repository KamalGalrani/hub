package eu.ailao.hub.dialog;

import eu.ailao.hub.corefresol.answers.ClueMemorizer;
import eu.ailao.hub.corefresol.concepts.ConceptMemorizer;
import eu.ailao.hub.questions.Question;

import java.util.ArrayList;

/**
 * Created by Petr Marek on 24.02.2016.
 * Dialog class represents dialog. It contains id of dialog and ids of questions in this dialog.
 */
public class Dialog {
	private int id;
	private ArrayList<Question> questionsOfDialogue;
	private ConceptMemorizer conceptMemorizer;
	private ClueMemorizer clueMemorizer;

	public Dialog(int id) {
		this.id = id;
		this.questionsOfDialogue = new ArrayList<>();
		this.conceptMemorizer = new ConceptMemorizer();
		this.clueMemorizer = new ClueMemorizer();
	}

	/**
	 * Adds question to dialog
	 * @param questionID id of question
	 */
	public void addQuestion(Question questionID) {
		questionsOfDialogue.add(questionID);
	}

	public ArrayList<Question> getQuestions() {
		return questionsOfDialogue;
	}

	/**
	 * Gets all question's ids of dialog
	 * @return array list of question's ids in dialog
	 */
	public ArrayList<Integer> getQuestionsIDs() {
		ArrayList<Integer> questionIDs = new ArrayList<Integer>();
		for (int i = 0; i < questionsOfDialogue.size(); i++) {
			questionIDs.add(questionsOfDialogue.get(i).getYodaQuestionID());
		}
		return questionIDs;
	}

	public int getId() {
		return id;
	}

	public ConceptMemorizer getConceptMemorizer() {
		return conceptMemorizer;
	}

	public ClueMemorizer getClueMemorizer() {
		return clueMemorizer;
	}

	public boolean hasQuestionWithId(int id) {
		for (Question question : questionsOfDialogue) {
			if (question.getYodaQuestionID() == id) {
				return true;
			}
		}
		return false;
	}
}
