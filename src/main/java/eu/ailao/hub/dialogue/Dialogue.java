package eu.ailao.hub.dialogue;

import eu.ailao.hub.questions.Question;

import java.util.ArrayList;

/**
 * Created by Petr Marek on 24.02.2016.
 */
public class Dialogue {
	private int id;
	private ArrayList<Question> questionsOfDialogue;

	public Dialogue(int id) {
		this.id = id;
		this.questionsOfDialogue = new ArrayList<>();
	}

	public void addQuestion(Question questionID) {
		questionsOfDialogue.add(questionID);
	}

	public ArrayList<Question> getQuestions(){
		return questionsOfDialogue;
	}

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
