package eu.ailao.hub.traffic.analyze;

import eu.ailao.hub.traffic.analyze.dataclases.TrafficQuestionInfo;
import eu.ailao.hub.traffic.analyze.dataclases.TrafficTopic;

/**
 * Created by Petr Marek on 21.03.2016.
 * Class for analyze traffic question
 */
public class QuestionAnalyzer {

	private final int FROM = 0;
	private final int TO = 1;

	/**
	 * This method recognize topic of answer and street name
	 * @param question traffic question
	 * @return topic and street name
	 */
	public TrafficQuestionInfo analyzeTrafficQuestion(String question) {
		StreetAnalyzer streetAnalyzer = new StreetAnalyzer();
		TopicAnalyzer topicAnalyzer = new TopicAnalyzer();
		TrafficTopic topic = topicAnalyzer.analyzeTrafficTopic(question);
		if (!topic.equals(TrafficTopic.FASTEST_ROUTE)) {
			String street = streetAnalyzer.analyzeStreetName(question);
			return new TrafficQuestionInfo(topic, street);
		} else {
			try {
				String streetOne = streetAnalyzer.analyzeStreetName(question);
				String streetTwo = streetAnalyzer.analyzeStreetName(question.replace(streetOne, ""));
				String[] fromTo = streetAnalyzer.findOriginDestination(question, streetOne, streetTwo);
				return new TrafficQuestionInfo(topic, fromTo[FROM], fromTo[TO]);
			} catch (Exception e) {
				return new TrafficQuestionInfo(topic, null);
			}
		}
	}
}
