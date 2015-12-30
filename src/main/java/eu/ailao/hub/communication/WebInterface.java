package eu.ailao.hub.communication;
/**
 * Created by Petr Marek on 26.11.2015.
 * Class which handles connection between web interface and yodaQA
 */

import eu.ailao.hub.concepts.Concept;
import eu.ailao.hub.concepts.ConceptMemorizer;
import eu.ailao.hub.questions.Question;
import eu.ailao.hub.questions.QuestionMapper;
import eu.ailao.hub.transformations.Transformation;
import eu.ailao.hub.transformations.TransformationArray;
import org.json.JSONObject;
import spark.Request;
import spark.Response;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static eu.ailao.hub.Statics.isContain;
import static spark.Spark.*;
import static spark.Spark.get;


public class WebInterface implements Runnable {

	private int port;
	private String yodaQAURL;
	private ConceptMemorizer conceptMemorizer;
	private QuestionMapper questionMapper;

	public WebInterface(int port, String yodaQAURL) {
		this.port = port;
		this.yodaQAURL = yodaQAURL;
		conceptMemorizer=new ConceptMemorizer();
		questionMapper =new QuestionMapper();
	}

	/***
	 * Starts getting requests from web interface and sending it to yodaQA
	 */
	public void run() {
		port(port);
		post("/q", (request, response) -> handleGettingID(request, response));
		get("/q/:id", ((request, response) -> handleGettingAnswer(request, response)));
		get("/q/", ((request, response) -> handleGettingInformation(request, response)));
	}

	/***
	 * Reaction to POST request to /q
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	private Object handleGettingID(Request request, Response response) throws IOException, ExecutionException, InterruptedException {
		response.header("Access-Control-Allow-Origin", "*");
		response.status(201);

		Map<String, String[]> queryParamsMap = request.queryMap().toMap();
		conceptMemorizer.updateConcepts(queryParamsMap);

		String questionText = queryParamsMap.get("text")[0];
		Question question=new Question(questionText);
		question=transformQuestion(question);

		String answerID= askQuestion(question, request);
		questionMapper.addQuestion(getQuestionID(answerID),question);

		return answerID;
	}


	/***
	 * Reaction to GET request to /q/:id
	 * @param request
	 * @param response
	 * @return
	 */
	private Object handleGettingAnswer(Request request, Response response) {
		response.type("application/json");
		response.header("Access-Control-Allow-Origin", "*");
		response.status(201);
		int id = Integer.parseInt(request.params("id"));
		CommunicationHandler communicationHandler = new CommunicationHandler();
		String GETResponse = communicationHandler.getGETResponse(yodaQAURL + "q/" + id);
		JSONObject answer = new JSONObject(GETResponse);
		answer=transformBack(id,answer);
		conceptMemorizer.updateConcepts(answer);
		return answer.toString();
	}

	/***
	 * Reaction to GET request to /q/
	 * @param request
	 * @param response
	 * @return
	 */
	private Object handleGettingInformation(Request request, Response response) {
		String result=null;
		response.type("application/json");
		response.header("Access-Control-Allow-Origin", "*");
		response.status(201);
		CommunicationHandler communicationHandler = new CommunicationHandler();
		if (request.queryParams("toAnswer") != null) {
			result = communicationHandler.getGETResponse(yodaQAURL + "q/?toAnswer");
		} else if (request.queryParams("inProgress") != null) {
			result = communicationHandler.getGETResponse(yodaQAURL + "q/?inProgress");
		} else if (request.queryParams("answered") != null) {
			result = communicationHandler.getGETResponse(yodaQAURL + "q/?answered");
		}
		return result;
	}

	/***
	 * Detect third person pronoun in question. If it is presented, it uses concepts from older questions
	 * @param question Text of question
	 * @param request Request from web interface
	 * @return response of yodaQA
	 */
	private String askQuestion(Question question, Request request) {
		ArrayDeque<Concept> concepts;
		concepts= getConceptsIfThirdPersonPronouns(question.getTransformedQuestionText());

		CommunicationHandler communicationHandler = new CommunicationHandler();
		return communicationHandler.getPOSTResponse(yodaQAURL + "/q", request, question.getTransformedQuestionText(), concepts);
	}

	private Question transformQuestion(Question question){
		for(Transformation transformation: TransformationArray.transformationsList){
			question.applyTransformationIfUseful(transformation);
		}
		return question;
	}

	private ArrayDeque<Concept> getConceptsIfThirdPersonPronouns(String question){
		String[] thirdPersonPronouns = {"he", "she", "it", "his", "hers", "him", "her", "they", "them", "their"};
		for (int i = 0; i < thirdPersonPronouns.length; i++) {
			if (isContain(question.toLowerCase(), thirdPersonPronouns[i])) {
				return conceptMemorizer.getConcepts();
			}
		}
		return null;
	}

	private int getQuestionID(String answer){
		return Integer.parseInt(answer.replaceAll("[\\D]", ""));
	}

	private JSONObject transformBack(int id, JSONObject answer){
		Question question = questionMapper.getQuestionByID(id);
		return question.transformBack(answer);
	}
}
