package cz.Communication;
/**
 * Created by Petr Marek on 26.11.2015.
 * Class which handles connection between web interface and yodaQA
 */

import cz.Concepts.ConceptMemorizer;
import org.json.JSONObject;
import spark.Request;
import spark.Response;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static spark.Spark.*;
import static spark.Spark.get;


public class WebInterface implements Runnable {

	private int port;
	private String yodaQAURL;
	private ConceptMemorizer conceptMemorizer;

	private int questionCount = 0;

	public WebInterface(int port, String yodaQAURL) {
		this.port = port;
		this.yodaQAURL = yodaQAURL;
		conceptMemorizer=new ConceptMemorizer();
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
		String question = queryParamsMap.get("question")[0];
		conceptMemorizer.updateConcepts(queryParamsMap, questionCount);
		questionCount++;

		return answerQuestion(question, request);
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
		String id = request.params("id");
		CommunicationHandler communicationHandler = new CommunicationHandler();
		String GETResponse = communicationHandler.getGETResponse(yodaQAURL + "q/" + id);
		JSONObject jsonObj = new JSONObject(GETResponse);
		conceptMemorizer.updateConcepts(jsonObj, questionCount);
		return GETResponse;
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
	private String answerQuestion(String question, Request request) {
		String[] thirdPersonPronouns = {"he", "she", "it", "his", "hers", "him", "her", "they", "them", "their"};
		CommunicationHandler communicationHandler = new CommunicationHandler();

		for (int i = 0; i < thirdPersonPronouns.length; i++) {
			if (isContain(question.toLowerCase(), thirdPersonPronouns[i])) {
				return communicationHandler.getPOSTResponse(yodaQAURL + "/q", request, conceptMemorizer.getConcepts());
			}
		}
		return communicationHandler.getPOSTResponse(yodaQAURL + "/q", request, null);
	}

	/***
	 * Checks if word appears in text
	 * @param text
	 * @param word
	 * @return TRUE if word appears in text, FALSE if word doesn't appear in text
	 */
	private static boolean isContain(String text, String word){
		String pattern = "\\b"+word+"\\b";
		Pattern p=Pattern.compile(pattern);
		Matcher m=p.matcher(text);
		return m.find();
	}
}
