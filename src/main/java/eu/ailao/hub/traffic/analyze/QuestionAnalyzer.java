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

	String LABEL_LOOKUP_ADDRESS = "http://[::1]:5000/";

	/**
	 * This method recognize topic of answer and street name
	 * @param question traffic question
	 * @return topic and street name
	 */
	public TrafficQuestionInfo analyzeTrafficQuestion(String question){
		TrafficTopic topic = analyzeQuestionTopic(question);
		String street = analyzeStreetName(question);
		return new TrafficQuestionInfo(topic,street);
	}

	/**
	 * Finds topic of question by keywords
	 * @param question traffic question
	 * @return topic of question
	 */
	private TrafficTopic analyzeQuestionTopic(String question){
		String[] flowKeywords = {"flow", "traffic flow", "traffic"};
		String[] incidentKeywords = {"incident", "incidents", "traffic incident", "traffic incidents"};

		//Incidents
		for (String incidentKeyword : incidentKeywords) {
			if (Statics.isContain(question, incidentKeyword)) {
				return  TrafficTopic.INCIDENT;
			}
		}

		//Traffic flow
		for (String flowKeyword : flowKeywords) {
			if (Statics.isContain(question, flowKeyword)) {
				return TrafficTopic.TRAFFIC_SITUATION;
			}
		}

		return TrafficTopic.UNKNOWN;
	}

	/**
	 * Finds name of street contained in question by ask to label-lookup
	 * @param question traffic question
	 * @return name of street
	 */
	private String analyzeStreetName(String question){
		TrafficConnector trafficConnector = new TrafficConnector();

		String[] words= sentenceToWords(question);
		for (String word: words){
			String url= LABEL_LOOKUP_ADDRESS +"search/"+word;
			JSONObject labelLookup=trafficConnector.GETRequest(url);
			String streetName=getStreetNameFromLabelLookup(labelLookup);
			if (streetName!=null){
				return  streetName;
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
		return  words;
	}

	/**
	 * Checks what results match name of some street
	 * @param labelLookup labelLookup result
	 * @return name of street
	 */
	String getStreetNameFromLabelLookup(JSONObject labelLookup){
		JSONArray results=labelLookup.getJSONArray("results");
		for (Object result: results) {
			int distance=  ((JSONObject)result).getInt("dist");
			if (distance==0){
				return ((JSONObject)result).getString("matchedLabel");
			}
		}
		return null;
	}
}
