package eu.ailao.hub.traffic;

import eu.ailao.hub.questions.Question;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Random;

/**
 * Created by Petr Marek on 16.03.2016.
 * Class handling analyzing of traffic questions and asking
 */
public class Traffic {

	private Random idgen=new Random();
	private TrafficAnswerMemorizer trafficAnswerMemorizer = new TrafficAnswerMemorizer();

	/**
	 * Analyzes and start answering process
	 * @param question text of question
	 * @return service id
	 */
	public int askQuestion(String question){
		int id= idgen.nextInt(Integer.MAX_VALUE);
		trafficAnswerMemorizer.addToAnswerMap(id, "Not implemented yet.");
		return id;
	}

	/**
	 * Returns text of answer
	 * @param id
	 * @return
	 */
	public String getAnswerString(int id){
		return trafficAnswerMemorizer.getAnswer(id);
	}

	/**
	 * Crates answer for traffic
	 * @param id clientID
	 * @param question question for which answer is
	 * @return JSONObject for client
	 */
	public JSONObject getAnswer(int id, Question question){
		JSONObject answer = new JSONObject();
		answer.put("sources",new JSONObject());
		answer.put("gen_answers",1);
		answer.put("snippets",new JSONObject());

		JSONArray answers=new JSONArray();
		answers.put(createAnswerJSONObject(question.getServiceQuestionID()));

		answer.put("answers", answers);
		answer.put("gen_sources",0);
		answer.put("finished", true);

		answer.put("id", id);
		answer.put("text",question.getOriginalQuestionText());
		answer.put("hasOnlyArtificialConcept",false);
		answer.put("artificialConcepts",new JSONArray());
		return answer;
	}

	/**
	 * Crates one answer
	 * @param id clientID
	 * @return JSONObject with answer and confidence
	 */
	private JSONObject createAnswerJSONObject(int id){
		JSONObject answer=new JSONObject();
		answer.put("snippetIDs", new JSONArray());
		answer.put("confidence", 0.99);
		answer.put("text",getAnswerString(id));
		answer.put("ID",0);
		return answer;
	}
}
