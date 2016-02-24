package eu.ailao.hub.dialogue;

import eu.ailao.hub.concepts.Concept;

import java.util.ArrayDeque;
import java.util.List;

/**
 * Created by Petr Marek on 24.02.2016.
 */
public class DialogueMemorizer {
	private final int NUMBER_OF_DIALOGS_TO_REMEMBER=5;
	private ArrayDeque<Dialogue> dialogs = new ArrayDeque<>();

	public void addDialogue(Dialogue dialogue){
		if (dialogs.size()>=NUMBER_OF_DIALOGS_TO_REMEMBER){
			dialogs.poll();
			dialogs.add(dialogue);
		}
	}

	public Dialogue[] getDialogs(){
		return (Dialogue[])dialogs.toArray();
	}

}
