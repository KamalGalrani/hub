package eu.ailao.hub.corefresol.answers;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Petr Marek on 02.03.2016.
 * This class handles memorizing of clues for coreference resolution, clue is best answer to last question
 */
public class ClueMemorizer {
	private String clue = "";

	public String getClue() {
		return clue;
	}

	/**
	 * Gets highest scored answer and saves it as clue
	 * @param answer answer to question
	 */
	public void setClue(JSONObject answer) {
		if (((Boolean) answer.get("finished"))) {
			JSONArray answers = answer.getJSONArray("answers");
			this.clue = ((JSONObject) answers.get(0)).getString("text");
		}
	}
}
