package eu.ailao.hub.concepts;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayDeque;
import java.util.Map;

/**
 * Created by Petr Marek on 02.12.2015.
 * Class which memorize concepts from last questions.
 * How much it memorize is determined by constant MAX_QUESTIONS_TO_REMEMBER_CONCEPT
 * Example: If MAX_QUESTIONS_TO_REMEMBER_CONCEPT = 3, than it memorize all concepts of last three questions
 */
public class ConceptMemorizer {
	private ArrayDeque<Concept> concepts = new ArrayDeque<>();
	private final int MAX_QUESTIONS_TO_REMEMBER_CONCEPT = 5;

	/***
	 * Returns concepts in memory
	 * @return concepts
	 */
	public ArrayDeque<Concept> getConcepts(){
		return concepts;
	}

	/***
	 * Updates memory of concepts. Adds new concepts and deletes the old ones
	 * @param queryParamsMap concepts in map
	 * @param questionCount Number of asked questions until now
	 */
	public void updateConcepts(Map<String, String[]> queryParamsMap, int questionCount) {
		removeOldConcepts(questionCount);
		int numberOfConcepts = Integer.parseInt(queryParamsMap.get("numberOfConcepts")[0]);
		if (numberOfConcepts > 1) {
			for (int i = 1; i < numberOfConcepts; i++) {
				Concept concept = new Concept(Integer.parseInt(queryParamsMap.get("pageID" + String.valueOf(i))[0]),
						queryParamsMap.get("fullLabel" + String.valueOf(i))[0], questionCount);
				concepts.add(concept);
			}
		}
	}

	/***
	 * Updates memory of concepts. Adds new concepts and deletes the old ones
	 * @param json concepts in json
	 * @param questionCount Number of asked questions until now
	 */
	public void updateConcepts(JSONObject json, int questionCount) {
		if (((Boolean)json.get("finished"))) {
			removeOldConcepts(questionCount);
			JSONObject summary = json.getJSONObject("summary");
			JSONArray conceptsJSON = summary.getJSONArray("concepts");
			if (conceptsJSON.length() > 0) {
				for (int i = 0; i < conceptsJSON.length(); i++) {
					JSONObject JSONConcept=conceptsJSON.getJSONObject(i);
					Concept concept = new Concept((int)JSONConcept.get("pageId"),(String)JSONConcept.get("title"), questionCount);
					concepts.add(concept);
				}
			}
		}
	}

	/***
	 * Removes old concepts from memory
	 * @param questionCount Number of asked questions until now
	 */
	private void removeOldConcepts(int questionCount) {
		while (concepts.size() > 0 && questionCount - MAX_QUESTIONS_TO_REMEMBER_CONCEPT > concepts.peek().getQuestionNumber()) {
			concepts.poll();
		}
	}
}
