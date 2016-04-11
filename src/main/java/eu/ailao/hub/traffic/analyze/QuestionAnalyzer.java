package eu.ailao.hub.traffic.analyze;

import eu.ailao.hub.Statics;
import eu.ailao.hub.traffic.analyze.dataclases.LoadedDataset;
import eu.ailao.hub.traffic.analyze.dataclases.StreetCandidate;
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
	public TrafficQuestionInfo analyzeTrafficQuestion(String question, LoadedDataset loadedDataset) {
		TopicAnalyzer topicAnalyzer = new TopicAnalyzer();
		TrafficTopic topic = topicAnalyzer.analyzeTrafficTopic(question,loadedDataset);
		//TrafficTopic topic = analyzeQuestionTopic(question);
		if (!topic.equals(TrafficTopic.FASTEST_ROUTE)) {
			String street = analyzeStreetName(question);
			return new TrafficQuestionInfo(topic, street);
		} else {
			try {
				String streetOne = analyzeStreetName(question);
				String streetTwo = analyzeStreetName(question.replace(streetOne, ""));
				String[] fromTo = findOriginDestination(question, streetOne, streetTwo);
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
		String[] tokens = tokenization(question);
		StreetCandidate streetCandidate = null;
		for (int i = 1; i < MAXIMUM_STREET_NAME_WORDS; i++) {
			streetCandidate = findStreetCandidate(tokens, i, streetCandidate);
			if (streetCandidate!=null && streetCandidate.getDistance()==0) {
				return streetCandidate.getStreetName();
			}
		}
		return streetCandidate.getStreetName();
	}

	/**
	 * Finds street name with length of "number of Words"
	 * @param words Words of question
	 * @param numberOfWords length of street name
	 * @return name of street if it was founded, null otherwise
	 */
	private StreetCandidate findStreetCandidate(String[] words, int numberOfWords, StreetCandidate bestStreetCandidate) {
		for (int i = 0; i < words.length - numberOfWords + 1; i++) {
			String searchTerm = "";
			for (int j = 0; j < numberOfWords; j++) {
				if (j == numberOfWords - 1) {
					searchTerm += words[i + j];
				} else {
					searchTerm += words[i + j] + " ";
				}
			}
			StreetCandidate streetCandidate = sendSearchTermToLabelLookup(searchTerm);
			if (streetCandidate==null){
				continue;
			}
			if (streetCandidate.getDistance()==0){
				streetCandidate= searchSurroundings(i, numberOfWords, words, streetCandidate);
				return streetCandidate;
			}else{
				if (bestStreetCandidate==null || bestStreetCandidate.getDistance() > streetCandidate.getDistance() ){
					bestStreetCandidate=streetCandidate;
				}else if(bestStreetCandidate.getDistance() == streetCandidate.getDistance() && bestStreetCandidate.getStreetName().length()<streetCandidate.getStreetName().length()){
					bestStreetCandidate=streetCandidate;
				}
			}
		}
		return bestStreetCandidate;
	}

	/**
	 * Tries to search for street name in the surrounding of founded name so far
	 * The motivation is to find name of street "Na staré cestě", when there is street named "Na staré"
	 * "Na staré" is founded as candidate for street name and this method tries to find if this name continues or not
	 * @param index index of beginning of street name in question
	 * @param numberOfWords number of words, from which the name of street consists
	 * @param words question divided to words
	 * @param foundedStreet founded street name
	 * @return name of street
	 */
	private StreetCandidate searchSurroundings(int index, int numberOfWords, String[] words, StreetCandidate foundedStreet) {
		//try words after
		StreetCandidate longestStreet = foundedStreet;
		String searchTerm = foundedStreet.getStreetName();
		for (int i = index - 1; i >= 0; i--) {
			searchTerm = words[i] + " " + searchTerm;
			StreetCandidate newCandidate = sendSearchTermToLabelLookup(searchTerm);
			if (newCandidate != null && newCandidate.getDistance()==0) {
				longestStreet = newCandidate;
			} else {
				break;
			}
		}
		searchTerm = longestStreet.getStreetName();
		for (int i = index + numberOfWords; i < words.length; i++) {
			searchTerm += " " + words[i];
			StreetCandidate newCandidate = sendSearchTermToLabelLookup(searchTerm);
			if (newCandidate != null && newCandidate.getDistance()==0) {
				longestStreet = newCandidate;
			} else {
				break;
			}
		}
		return longestStreet;
	}

	/**
	 * Searches for street name in label lookup
	 * @param searchTerm search term to send, usually name of street
	 * @return name of street which was founded, or null
	 */
	private StreetCandidate sendSearchTermToLabelLookup(String searchTerm) {
		TrafficConnector trafficConnector = new TrafficConnector();
		searchTerm = searchTerm.replace(" ", "%20");
		String url = LABEL_LOOKUP_ADDRESS + "search/" + searchTerm;
		try {
			JSONObject labelLookup = trafficConnector.GETRequest(url);
			StreetCandidate streetCandidate = getStreetCandidateFromLabelLookup(labelLookup);
			if (streetCandidate != null) {
				return streetCandidate;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Splits sentence to words
	 * @param sentence sentence to split
	 * @return array of words
	 */
	private String[] tokenization(String sentence) {
		String[] words = sentence.toLowerCase().replaceAll("[.,?;]", "").split("\\s+");
		return words;
	}

	/**
	 * Checks what results match name of some street
	 * @param labelLookup labelLookup result
	 * @return street candidate
	 */
	StreetCandidate getStreetCandidateFromLabelLookup(JSONObject labelLookup) {
		JSONArray results = labelLookup.getJSONArray("results");
		if (results.length() > 0) {
			String streetName = ((JSONObject) results.get(0)).getString("matchedLabel");
			float distance = (float) ((JSONObject) results.get(0)).getDouble("dist");
			StreetCandidate streetCandidate = new StreetCandidate(streetName, distance);
			return streetCandidate;
		}
		return null;
	}

	/**
	 * Finds which street is considered as origin and which as goal
	 * @param question Question
	 * @param streetOne name of the street
	 * @param streetTwo name of the street
	 * @return String array[2] in format [name of from street, name of to street]
	 */
	String[] findOriginDestination(String question, String streetOne, String streetTwo) {
		String newQuestion = question.toLowerCase();
		String[] dividedQuestionOne = newQuestion.split(streetOne.toLowerCase());
		String[] dividedQuestionTwo = newQuestion.split(streetTwo.toLowerCase());

		String[] wordsOne = tokenization(dividedQuestionOne[0]);
		String[] wordsTwo = tokenization(dividedQuestionTwo[0]);

		String wordBeforeStreetOne = wordsOne[wordsOne.length - 1];
		String wordBeforeStreetTwo = wordsTwo[wordsTwo.length - 1];

		if (wordBeforeStreetOne.equals("from")) {
			return new String[]{streetOne, streetTwo};
		} else if (wordBeforeStreetOne.equals("to")) {
			return new String[]{streetTwo, streetOne};
		} else if (wordBeforeStreetTwo.equals("from")) {
			return new String[]{streetTwo, streetOne};
		} else if (wordBeforeStreetTwo.equals("to")) {
			return new String[]{streetOne, streetTwo};
		} else {
			return new String[]{streetOne, streetTwo};
		}
	}
}
