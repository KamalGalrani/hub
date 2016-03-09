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

	private LinkedHashMap<Integer, Dialog> dialogs = new LinkedHashMap<>();

	/**
	 * Add dialog to dialog memorizer
	 * @param dialog dialog to add
	 */
	public void addDialog(Dialog dialog) {
		dialogs.put(dialog.getId(), dialog);
	}

	/**
	 * Returns dialog with id
	 * @param dialogID id in form d_id
	 * @return dialog
	 */
	public Dialog getDialog(String dialogID) {
		int Id = Integer.parseInt(dialogID.replace("d_", ""));
		Dialog dialog = dialogs.get(Id);
		if (dialog == null) {
			return createNewDialog();
		}
		return dialogs.get(Id);
	}

	/**
	 * Returns all dialogs form dialog memorizer
	 * @return all dialogs
	 */
	public ArrayList<Dialog> getDialogs() {
		return new ArrayList<Dialog>(dialogs.values());
	}

	/**
	 * Creates new dialog and adds it to dialog memorizer
	 * @return new dialog
	 */
	public Dialog createNewDialog() {
		int newDialogueID = idgen.nextInt(Integer.MAX_VALUE);
		Dialog dialog = new Dialog(newDialogueID);
		this.addDialog(dialog);
		return dialog;
	}

	/**
	 * Creates new dialog with first question and adds it to dialog memorizer
	 * @param firstQuestion first question of dialog to add
	 * @return new dialog
	 */
	public Dialog createNewDialog(Question firstQuestion) {
		int newDialogueID = idgen.nextInt(Integer.MAX_VALUE);
		Dialog dialog = new Dialog(newDialogueID);
		dialog.addQuestion(firstQuestion);
		this.addDialog(dialog);
		return dialog;
	}
}
