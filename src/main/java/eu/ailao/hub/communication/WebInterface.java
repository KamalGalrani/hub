package eu.ailao.hub.communication;
/**
 * Created by Petr Marek on 26.11.2015.
 * Class which handles connection between web interface and yodaQA
 */

import eu.ailao.hub.AnswerSentenceGenerator;
import eu.ailao.hub.Statics;
import eu.ailao.hub.dialog.Dialog;
import eu.ailao.hub.dialog.DialogMemorizer;
import eu.ailao.hub.questions.Question;
import eu.ailao.hub.questions.QuestionMapper;
import eu.ailao.hub.traffic.QuestionProcessor;
import eu.ailao.hub.traffic.Traffic;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static spark.Spark.*;


public class WebInterface implements Runnable {

	final Logger logger = LoggerFactory.getLogger(WebInterface.class);
	private int port;
	private String yodaQAURL;
	private QuestionMapper questionMapper;
	private AnswerSentenceGenerator answerSentenceGenerator;
	private DialogMemorizer dialogMemorizer;
	private Traffic traffic;
	private ArrayDeque<Question> notYetHandledQuestions;


	private static final String USER_ID = "userID";

	public WebInterface(int port, String yodaQAURL) {
		this.port = port;
		this.yodaQAURL = yodaQAURL;
		this.questionMapper = QuestionMapper.getInstance();
		this.answerSentenceGenerator = new AnswerSentenceGenerator();
		this.dialogMemorizer = new DialogMemorizer();
		this.traffic = new Traffic();
		notYetHandledQuestions = new ArrayDeque<>();
		QuestionProcessor questionProcessor = new QuestionProcessor(traffic, yodaQAURL, this);
		questionProcessor.start();
	}

	/***
	 * Starts getting requests from web interface and sending it to yodaQA
	 */
	public void run() {
		port(port);
		post("/q", ((request, response) -> handleGettingID(request, response)));
		get("/q/*/*", ((request, response) -> handleGettingAnswer(request, response)));
		get("/q/:dID", ((request, response) -> handleGettingDialogsInfo(request, response)));
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

		String questionText = queryParamsMap.get("text")[0];
		QuestionGoogleDocLogger questionGoogleDocLogger = new QuestionGoogleDocLogger();
		questionGoogleDocLogger.log(questionText);
		Question question = new Question(questionText);

		logger.info("Getting id| Question asked: {}", question.getOriginalQuestionText());

		String dialogID = queryParamsMap.get("dialogID")[0];
		Dialog dialog = dialogMemorizer.getDialog(dialogID);
		dialog.addQuestion(question);

		question.setRequest(request);
		question.setDialog(dialog);
		question.setService(Statics.Services.NOT_KNOWN_YET);

		notYetHandledQuestions.add(question);

		int clientQuestionID = questionMapper.addQuestion(question);

		JSONObject answer = new JSONObject();
		answer.put("id", clientQuestionID);
		answer.put("dialogID", dialog.getId());
		logger.info("Getting id| Question text: {}, Dialog id: {}, Question id: {}", questionText, dialog.getId(), question.getServiceQuestionID());
		return answer.toString();
	}

	public ArrayDeque<Question> getNotYetHandledQuestions() {
		return notYetHandledQuestions;
	}

	/***
	 * Reaction to GET request to /q/:id/:dialogID
	 * @param request
	 * @param response
	 * @return
	 */
	private Object handleGettingAnswer(Request request, Response response) {
		response.type("application/json");
		response.header("Access-Control-Allow-Origin", "*");
		response.status(201);

		String stringID = request.splat()[0];
		int id = Integer.parseInt(stringID);

		Question question = questionMapper.getQuestionByClientID(id);

		String dialogID = request.splat()[1];
		Dialog dialog = null;
		if (dialogID.equals("-1")) {
			dialog = question.getDialog();
		} else {
			dialog = dialogMemorizer.getDialog(dialogID);
		}
		if (!dialog.hasQuestionWithId(id)) {
			dialog.addQuestion(question);
		}

		JSONObject answer;
		switch (question.getService()) {
			case YODA_QA:
				answer = getAnswer(id, dialog);
				break;
			case TRAFFIC:
				answer = traffic.getAnswer(id, question);
				break;
			default:
				answer = new JSONObject("{\"summary\":{\"lats\":[],\"concepts\":[]},\"sources\":{},\"answers\":[]," +
						"\"snippets\":{},\"finished\":false,\"artificialConcepts\":[],\"hasOnlyArtificialConcept\":false," +
						"\"gen_sources\":0,\"gen_answers\":0}");
				answer.put("id", question.getClientQuestionID());
				answer.put("text", question.getOriginalQuestionText());
				break;
		}

		answer.put("dialogID", dialog.getId());
		if ((boolean) answer.get("finished")) {
			logger.info("Getting answer| Question id: {}, Dialog id: {}, Question text: {}, Generated answers: {}, Finished: {}", id, dialog.getId(), answer.get("text"), answer.get("gen_answers"), answer.get("finished"));
		}
		return answer.toString();
	}

	/***
	 * Reaction to GET request to /q/:id/
	 * @param request
	 * @param response
	 * @return
	 */
	private Object handleGettingDialogsInfo(Request request, Response response) {
		response.type("application/json");
		response.header("Access-Control-Allow-Origin", "*");
		response.status(201);

		String id = request.params("dID");
		//return dialog
		Dialog dialog = dialogMemorizer.getDialog(id);
		ArrayList<Integer> questions = dialog.getQuestionsIDs();
		JSONArray dialogAnswer = new JSONArray(questions);
		logger.info("Getting dialogs info| Dialog id: {}, Questions ids: {}", dialog.getId(), dialog.getQuestionsIDs());
		return dialogAnswer.toString();
	}

	/**
	 * Get answer from YODAQA
	 * @param id client id
	 * @param dialog dialog in which is
	 * @return answer
	 */
	private JSONObject getAnswer(int id, Dialog dialog) {
		CommunicationHandler communicationHandler = new CommunicationHandler();
		Question question = questionMapper.getQuestionByClientID(id);
		int serviceID = question.getServiceQuestionID();
		String GETResponse = communicationHandler.getGETResponse(yodaQAURL + "q/" + serviceID);
		JSONObject answer = new JSONObject(GETResponse);
		answer = transformBack(id, answer);
		answer.put("id", question.getClientQuestionID());
		dialog.getConceptMemorizer().updateConceptsDuringGettingQuestion(answer);
		dialog.getClueMemorizer().setClue(answer);
		String answerSentence = answerSentenceGenerator.getAnswerSentence(answer);
		if (answerSentence != null) {
			answerSentence = transformBackAnswerSentence(id, answerSentence);
		}
		answer.put("answerSentence", answerSentence);
		return answer;
	}

	/***
	 * Reaction to GET request to /q/
	 * @param request
	 * @param response
	 * @return
	 */
	private Object handleGettingInformation(Request request, Response response) {
		String result = null;
		response.type("application/json");
		response.header("Access-Control-Allow-Origin", "*");
		response.status(201);
		CommunicationHandler communicationHandler = new CommunicationHandler();
		if (request.queryParams("toAnswer") != null) {
			result = communicationHandler.getGETResponse(yodaQAURL + "q/?toAnswer");
			result = translateServiceIDToClientID(result);
		} else if (request.queryParams("inProgress") != null) {
			result = communicationHandler.getGETResponse(yodaQAURL + "q/?inProgress");
			result = translateServiceIDToClientID(result);
		} else if (request.queryParams("answered") != null) {
			result = communicationHandler.getGETResponse(yodaQAURL + "q/?answered");
			result = translateServiceIDToClientID(result);
		} else if (request.queryParams("dialogs") != null) {
			ArrayList dialogs = dialogMemorizer.getDialogs();
			List lastDialogs = dialogs.subList(0 < dialogs.size() - 6 ? dialogs.size() - 6 : 0, dialogs.size());
			JSONArray dialogArray = new JSONArray();
			for (int i = 0; i < lastDialogs.size(); i++) {
				JSONObject oneDialog = new JSONObject();
				oneDialog.put("id", ((Dialog) lastDialogs.get(i)).getId());
				oneDialog.put("dialogQuestions", createDialogAnswer((Dialog) lastDialogs.get(i)));
				if (oneDialog.getJSONArray("dialogQuestions").length() != 0) {
					dialogArray.put(oneDialog);
				}
			}
			result = dialogArray.toString();
		}

		return result;
	}

	private String translateServiceIDToClientID(String result) {
		JSONArray JSONresult = new JSONArray(result);
		for (int i = 0; i < JSONresult.length(); i++) {
			try{
			JSONresult.getJSONObject(i).put("id", questionMapper.getClientIdByServiceID(JSONresult.getJSONObject(i).getInt("id")));}
			catch(Exception e){}
		}
		return JSONresult.toString();
	}


	/***
	 * Transforms answer back
	 * Applies back transformations in reverse order to answer
	 * @param id id of question
	 * @param answer YodaQA's answer
	 * @return Answer transformed back
	 */
	private JSONObject transformBack(int id, JSONObject answer) {
		Question question = questionMapper.getQuestionByClientID(id);
		return question.transformBack(answer);
	}

	/**
	 * Gets the answer sentence and transforms it back.
	 * Example: "Travolta birth date is 64" transforms back by age transformation to "Travolta age is 64"
	 * @param id id of question
	 * @param answerSentence sentence to transform
	 * @return sentence transformed back
	 */
	private String transformBackAnswerSentence(int id, String answerSentence) {
		Question question = questionMapper.getQuestionByClientID(id);
		return question.transformBackAnswerSentence(answerSentence);
	}

	/**
	 * Creates JSON answer for client in form of dialog
	 * @param dialog Dialog to recreate in JSON
	 * @return JSON of dialog
	 */
	private JSONArray createDialogAnswer(Dialog dialog) {
		JSONArray dialogAnswer = new JSONArray();
		ArrayList<Question> questions = dialog.getQuestions();
		for (int i = 0; i < questions.size(); i++) {
			if (questions.get(i) == null) {
				continue;
			}
			JSONObject question = new JSONObject();
			question.put("id", questions.get(i).getClientQuestionID());
			question.put("text", questions.get(i).getOriginalQuestionText());
			dialogAnswer.put(question);
		}
		return dialogAnswer;
	}
}
