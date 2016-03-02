package eu.ailao.hub.corefresol.answers;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Petr Marek on 02.03.2016.
 */
public class BestAnswerMemorizer {
	private String bestAnswer = "";

	public String getBestAnswer() {
		return bestAnswer;
	}

	public void setBestAnswer(JSONObject answer) {
		if (((Boolean) answer.get("finished"))) {
			JSONArray answers = answer.getJSONArray("answers");
			this.bestAnswer = ((JSONObject) answers.get(0)).getString("text");
		}
	}
}
