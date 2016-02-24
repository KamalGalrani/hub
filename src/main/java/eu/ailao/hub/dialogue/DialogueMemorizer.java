package eu.ailao.hub.dialogue;

import eu.ailao.hub.concepts.Concept;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Petr Marek on 24.02.2016.
 */
public class DialogueMemorizer {
	private HashMap<Integer,Dialogue> dialogs = new HashMap<>();

	public void addDialogue(Dialogue dialogue){
			dialogs.put(dialogue.getId(),dialogue);
	}

	public Dialogue getDialog(int dialogID){
		return dialogs.get(dialogID);
	}

}
