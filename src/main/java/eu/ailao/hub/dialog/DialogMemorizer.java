package eu.ailao.hub.dialog;

import eu.ailao.hub.questions.Question;

import java.util.*;

/**
 * Created by Petr Marek on 24.02.2016.
 * Class handling memorizing of dialogs
 */
public class DialogMemorizer {
	private Random idgen;

	public DialogMemorizer() {
		this.idgen = new Random();
	}

	private HashMap<Integer,Dialog> dialogs = new HashMap<>();

	/**
	 * Add dialog to dialog memorizer
	 * @param dialog dialog to add
	 */
	public void addDialogue(Dialog dialog){
			dialogs.put(dialog.getId(), dialog);
	}

	public Dialog getDialog(int dialogID){
		return dialogs.get(dialogID);
	}

	/**
	 * Returns all dialogs form dialog memorizer
	 * @return all dialogs
	 */
	public ArrayList<Dialog> getDialogs(){
		return new ArrayList<Dialog>(dialogs.values());
	}

	/**
	 * Creates new dialog with first question and adds it to dialog memorizer
	 * @param firstQuestion first question of dialog to add
	 * @return id of dialog
	 */
	public int createNewDialog(Question firstQuestion){
		int newDialogueID = idgen.nextInt(Integer.MAX_VALUE);
		this.addDialogue(new Dialog(newDialogueID));
		this.getDialog(newDialogueID).addQuestion(firstQuestion);
		return newDialogueID;
	}

}
