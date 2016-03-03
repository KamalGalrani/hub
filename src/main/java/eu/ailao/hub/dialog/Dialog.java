package eu.ailao.hub.dialog;

import eu.ailao.hub.questions.Question;

import java.util.ArrayList;

/**
 * Created by Petr Marek on 24.02.2016.
 * Dialog class represents dialog. It contains id of dialog and ids of questions in this dialog.
 */
public class Dialog {
	private int id;
	private ArrayList<Question> questionsOfDialogue;

	public Dialog(int id) {
		this.id = id;
		this.questionsOfDialogue = new ArrayList<>();
	}

	/**
	 * Adds question to dialog
	 * @param questionID id of question
	 */
	public void addQuestion(Question questionID) {
		questionsOfDialogue.add(questionID);
	}

	public ArrayList<Question> getQuestions(){
		return questionsOfDialogue;
	}

	/**
	 * Gets all question's ids of dialog
	 * @return array list of question's ids in dialog
	 */
	public ArrayList<Integer> getQuestionsIDs(){
		ArrayList<Integer> questionIDs = new ArrayList<Integer>();
		for (int i = 0; i < questionsOfDialogue.size(); i++) {
			questionIDs.add(questionsOfDialogue.get(i).getYodaQuestionID());
		}
		return questionIDs;
	}

	public int getId() {
		return id;
	}
}
