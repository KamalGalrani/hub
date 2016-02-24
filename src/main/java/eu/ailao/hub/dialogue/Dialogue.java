package eu.ailao.hub.dialogue;

import java.util.ArrayList;

/**
 * Created by Petr Marek on 24.02.2016.
 */
public class Dialogue {
	private int id;
	private ArrayList<Integer> questionsOfDialogue;

	public Dialogue(int id) {
		this.id = id;
		this.questionsOfDialogue = new ArrayList<>();
	}

	public void addQuestion(int questionID) {
		questionsOfDialogue.add(questionID);
	}

	public ArrayList<Integer> getQuestions(){
		return questionsOfDialogue;
	}

	public int getId() {
		return id;
	}
}
