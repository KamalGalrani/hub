package eu.ailao.hub.traffic.analyze;

import eu.ailao.hub.Statics;
import eu.ailao.hub.traffic.hereapi.TrafficConnector;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Petr Marek on 21.03.2016.
 * Class for analyze traffic question
 */
public class QuestionAnalyzer {

	private String LABEL_LOOKUP_ADDRESS = Statics.labelLookupURL;
	private int MAXIMUM_STREET_NAME_WORDS = 4;

	/**
	 * This method recognize topic of answer and street name
	 * @param question traffic question
	 * @return topic and street name
	 */
	public TrafficQuestionInfo analyzeTrafficQuestion(String question) {
		TrafficTopic topic = analyzeQuestionTopic(question);
		String street = analyzeStreetName(question);
		return new TrafficQuestionInfo(topic, street);
	}

	/**
	 * Finds topic of question by keywords
	 * @param question traffic question
	 * @return topic of question
	 */
	private TrafficTopic analyzeQuestionTopic(String question) {
		String[] flowKeywords = {"flow", "traffic flow", "traffic"};
		String[] incidentKeywords = {"incident", "incidents", "traffic incident", "traffic incidents", "crash", "crashes", "accident", "accidents", "problem", "happen"};
		String[] fastestRoadKeywords = {"how long", "how to", "fastest route", "take me"};
		String[] constructionKeywords = {"construction","constructions"};
		String[] closedKeywords = {"closed","closure"};
		String[] restrictionEndKeywords = {"end","again","passable"};

		//Incidents
		for (String incidentKeyword : incidentKeywords) {
			if (Statics.isContain(question.toLowerCase(), incidentKeyword)) {
				return TrafficTopic.INCIDENT;
			}
		}

		//Traffic flow
		for (String flowKeyword : flowKeywords) {
			if (Statics.isContain(question.toLowerCase(), flowKeyword)) {
				return TrafficTopic.TRAFFIC_SITUATION;
			}
		}

		//Fastest route
		for (String fastestRoadKeyword : fastestRoadKeywords) {
			if (Statics.isContain(question.toLowerCase(), fastestRoadKeyword)) {
				return TrafficTopic.FASTEST_ROUTE;
			}
		}

		//Constructions
		for (String constructionKeyword : constructionKeywords) {
			if (Statics.isContain(question.toLowerCase(), constructionKeyword)) {
				return TrafficTopic.CONSTRUCTION;
			}
		}

		//Closure
		for (String closedKeyword : closedKeywords) {
			if (Statics.isContain(question.toLowerCase(), closedKeyword)) {
				return TrafficTopic.CLOSURE;
			}
		}

		//Restriction end
		for (String restrictionEndKeyword : restrictionEndKeywords) {
			if (Statics.isContain(question.toLowerCase(), restrictionEndKeyword)) {
				return TrafficTopic.RESTRICTION_END;
			}
		}

		return TrafficTopic.UNKNOWN;
	}

	/**
	 * Finds name of street contained in question by ask to label-lookup
	 * @param question traffic question
	 * @return name of street, null if it was not founded
	 */
	private String analyzeStreetName(String question) {
		String[] words = sentenceToWords(question);

		for (int i = 1; i < MAXIMUM_STREET_NAME_WORDS; i++) {
			String name = findStreetName(words, i);
			if (name != null) {
				return name;
			}
		}
		return null;
	}

	/**
	 * Finds street name with length of "number of Words"
	 * @param words Words of question
	 * @param numberOfWords length of street name
	 * @return name of street if it was founded, null otherwise
	 */
	private String findStreetName(String[] words, int numberOfWords) {
		TrafficConnector trafficConnector = new TrafficConnector();
		for (int i = 0; i < words.length - numberOfWords + 1; i++) {
			String searchTerm = "";
			for (int j = 0; j < numberOfWords; j++) {
				if (j == numberOfWords - 1) {
					searchTerm += words[i + j];
				} else {
					searchTerm += words[i + j] + " ";
				}
			}
			searchTerm = searchTerm.replace(" ", "%20");
			searchTerm = searchTerm.replace("?", "");
			String url = LABEL_LOOKUP_ADDRESS + "search/" + searchTerm;
			JSONObject labelLookup = trafficConnector.GETRequest(url);
			String streetName = getStreetNameFromLabelLookup(labelLookup);
			if (streetName != null) {
				return streetName;
			}
		}
		return null;
	}

	/**
	 * Splits sentence to words
	 * @param sentence sentence to split
	 * @return array of words
	 */
	private String[] sentenceToWords(String sentence) {
		String[] words = sentence.split("\\s+");
		return words;
	}

	/**
	 * Checks what results match name of some street
	 * @param labelLookup labelLookup result
	 * @return name of street
	 */
	String getStreetNameFromLabelLookup(JSONObject labelLookup) {
		JSONArray results = labelLookup.getJSONArray("results");
		for (Object result : results) {
			int distance = ((JSONObject) result).getInt("dist");
			if (distance == 0) {
				return ((JSONObject) result).getString("matchedLabel");
			}
		}
		return null;
	}
}
