package eu.ailao.hub.dialogue;

import eu.ailao.hub.questions.Question;

import java.util.*;

/**
 * Created by Petr Marek on 24.02.2016.
 */
public class DialogueMemorizer {
	private Random idgen;

	public DialogueMemorizer() {
		this.idgen = new Random();
	}

	private HashMap<Integer,Dialogue> dialogs = new HashMap<>();

	public void addDialogue(Dialogue dialogue){
			dialogs.put(dialogue.getId(),dialogue);
	}

	public Dialogue getDialog(int dialogID){
		return dialogs.get(dialogID);
	}

	public ArrayList<Dialogue> getDialogs(){
		return new ArrayList<Dialogue>(dialogs.values());
	}

	public int createNewDialogue(Question firstQuestion){
		int newDialogueID = idgen.nextInt(Integer.MAX_VALUE);
		this.addDialogue(new Dialogue(newDialogueID));
		this.getDialog(newDialogueID).addQuestion(firstQuestion);
		return newDialogueID;
	}

}
