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
	private final int MAXIMUM_STREET_NAME_WORDS = 4;
	private final int FROM = 0;
	private final int TO = 1;

	/**
	 * This method recognize topic of answer and street name
	 * @param question traffic question
	 * @return topic and street name
	 */
	public TrafficQuestionInfo analyzeTrafficQuestion(String question) {
		TrafficTopic topic = analyzeQuestionTopic(question);
		if (!topic.equals(TrafficTopic.FASTEST_ROUTE)) {
			String street = analyzeStreetName(question);
			return new TrafficQuestionInfo(topic, street);
		} else {
			try {
				String streetOne = analyzeStreetName(question);
				String streetTwo = analyzeStreetName(question.replace(streetOne, ""));
				String[] fromTo = findFromAndTo(question, streetOne, streetTwo);
				return new TrafficQuestionInfo(topic, fromTo[FROM], fromTo[TO]);
			} catch (Exception e) {
				return new TrafficQuestionInfo(topic, null);
			}
		}
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
		String[] constructionKeywords = {"construction", "constructions"};
		String[] closedKeywords = {"closed", "closure", "passable"};
		String[] restrictionEndKeywords = {"end", "again", "passable", "will"};

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
			try {
				JSONObject labelLookup = trafficConnector.GETRequest(url);
				String streetName = getStreetNameFromLabelLookup(labelLookup);
				if (streetName != null) {
					return streetName;
				}
			} catch (Exception e) {
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
		String[] words = sentence.toLowerCase().split("\\s+");
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

	//TODO finding multiword street names
	String[] findFromAndTo(String question, String streetOne, String streetTwo) {
		String[] words = sentenceToWords(question.toLowerCase());
		String streetOneLowerCase = streetOne.toLowerCase();
		String streetTwoLowerCase = streetTwo.toLowerCase();
		int streetOneIndex = -1;
		int streetTwoIndex = -1;
		for (int i = 0; i < words.length; i++) {
			if (streetOneIndex != -1 && streetTwoIndex != -1) {
				break;
			}
			if (words[i].equals(streetOneLowerCase)) {
				streetOneIndex = i;
			}
			if (words[i].equals(streetTwoLowerCase)) {
				streetTwoIndex = i;
			}
		}
		if ((streetOneIndex - 1) >= 0 && words[streetOneIndex - 1].equals("from")) {
			return new String[]{streetOne, streetTwo};
		} else if ((streetOneIndex - 1) >= 0 && words[streetOneIndex - 1].equals("to")) {
			return new String[]{streetTwo, streetOne};
		} else if ((streetTwoIndex - 1) >= 0 && words[streetTwoIndex - 1].equals("from")) {
			return new String[]{streetTwo, streetOne};
		} else if ((streetTwoIndex - 1) >= 0 && words[streetTwoIndex - 1].equals("to")) {
			return new String[]{streetOne, streetTwo};
		} else {
			return new String[]{streetOne, streetTwo};
		}
	}
}
