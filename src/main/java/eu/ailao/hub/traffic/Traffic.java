package eu.ailao.hub.traffic;

import eu.ailao.hub.questions.Question;
import eu.ailao.hub.traffic.analyze.QuestionAnalyzer;
import eu.ailao.hub.traffic.analyze.TrafficQuestionInfo;
import eu.ailao.hub.traffic.analyze.TrafficTopic;
import eu.ailao.hub.traffic.hereapi.*;
import eu.ailao.hub.traffic.hereapi.dataclasses.BoundingBox;
import eu.ailao.hub.traffic.hereapi.dataclasses.StreetFlowInfo;
import eu.ailao.hub.traffic.hereapi.dataclasses.StreetIncidentInfo;
import eu.ailao.hub.traffic.output.AnswerTextGenerator;
import eu.ailao.hub.traffic.output.TrafficAnswerMemorizer;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Petr Marek on 16.03.2016.
 * Class handling analyze of traffic questions and asking
 */
public class Traffic {

	final Logger logger = LoggerFactory.getLogger(Traffic.class);
	private Random idgen = new Random();
	private TrafficAnswerMemorizer trafficAnswerMemorizer = new TrafficAnswerMemorizer();
	private QuestionAnalyzer questionAnalyzer = new QuestionAnalyzer();

	/**
	 * Analyzes and start answering process for client
	 * @param question text of question
	 * @return service id
	 */
	public int askQuestion(String question) {
		int id = idgen.nextInt(Integer.MAX_VALUE);


		TrafficQuestionInfo trafficQuestionInfo = questionAnalyzer.analyzeTrafficQuestion(question);
		String streetName = trafficQuestionInfo.getStreetName();
		TrafficTopic topic = trafficQuestionInfo.getTrafficTopic();

		logger.info("Analyzing traffic question| Topic: {} Street name: {}", topic, streetName);

		String answerText="I don't know what you ask.";

		if (streetName==null) {
			answerText="I don't know this street.";
		}

		if (!topic.equals(TrafficTopic.UNKNOWN) && streetName!=null){
			logger.info("Analyzing traffic question| This is traffic question");
			TrafficInformationGetter trafficInformationGetter = new TrafficInformationGetter();
			ArrayList<BoundingBox> boundingBoxes = (ArrayList<BoundingBox>) trafficInformationGetter.getStreetBoundingBoxes(streetName);
			//ArrayList<BoundingBox> boundingBoxes = null;
			switch (topic){
				case TRAFFIC_SITUATION:
					StreetFlowInfo streetFlowInfo = trafficInformationGetter.getStreetFlowInfo(streetName, boundingBoxes);
					answerText = new AnswerTextGenerator().generateAnswerText(streetFlowInfo);
					break;
				case INCIDENT:
					StreetIncidentInfo incidentInfo = trafficInformationGetter.getStreetIncidentInfo(streetName, boundingBoxes);
					answerText = new AnswerTextGenerator().generateAnswerText(incidentInfo);
					break;
			}
		}else{
			logger.info("Analyzing traffic question| Question is not about traffic");
			return -1;
		}

		trafficAnswerMemorizer.addToAnswerMap(id, answerText);
		return id;
	}

	/**
	 * Returns text of answer
	 * @param id
	 * @return
	 */
	public String getAnswerString(int id) {
		return trafficAnswerMemorizer.getAnswer(id);
	}

	/**
	 * Crates answer for traffic
	 * @param id clientID
	 * @param question question for which answer is
	 * @return JSONObject for client
	 */
	public JSONObject getAnswer(int id, Question question) {
		JSONObject answer = new JSONObject();
		answer.put("sources", new JSONObject());
		answer.put("gen_answers", 1);
		answer.put("snippets", new JSONObject());

		JSONArray answers = new JSONArray();
		answers.put(createAnswerJSONObject(question.getServiceQuestionID()));

		answer.put("answers", answers);
		answer.put("gen_sources", 0);
		answer.put("finished", true);

		answer.put("id", id);
		answer.put("text", question.getOriginalQuestionText());
		answer.put("hasOnlyArtificialConcept", false);
		answer.put("artificialConcepts", new JSONArray());
		return answer;
	}

	/**
	 * Crates one answer
	 * @param id clientID
	 * @return JSONObject with answer and confidence
	 */
	private JSONObject createAnswerJSONObject(int id) {
		JSONObject answer = new JSONObject();
		answer.put("snippetIDs", new JSONArray());
		answer.put("confidence", 0.99);
		answer.put("text", getAnswerString(id));
		answer.put("ID", 0);
		return answer;
	}
}
