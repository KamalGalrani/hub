package eu.ailao.hub.dialogue;

import java.util.ArrayList;

/**
 * Created by Petr Marek on 24.02.2016.
 */
public class Dialogue {
	private int id;
	private ArrayList<Integer> questionsOfDialogue;

	public Dialogue(int id, ArrayList<Integer> questionsOfDialogue) {
		this.id = id;
		this.questionsOfDialogue = questionsOfDialogue;
	}

	public void addQuestion(int questionID) {
		questionsOfDialogue.add(questionID);
	}

	public ArrayList getQuestion(){
		return questionsOfDialogue;
	}
}
